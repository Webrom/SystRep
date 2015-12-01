/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import modele.*;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public class AbriBackend extends UnicastRemoteObject implements AbriLocalInterface, AbriRemoteInterface {
    
    protected String url;
    protected String controleurUrl;
    protected String noeudCentralUrl;

    protected Abri abri;
    final protected ControleurInterface controleur;
    protected NoeudCentralRemoteInterface noeudCentral;
    
    protected Annuaire abrisDistants;    // Map faisant le lien entre les url et les interfaces RMI des abris distants
    protected ArrayList<String> copains; // Les urls des autres membres du groupe de l'abri courant // Pas dans l'annuaire -> imposer la gestion d'une liste locale aux abris pour les groupes
    
    protected Semaphore semaphore;
    
    public AbriBackend(String _url, Abri _abri) throws RemoteException, MalformedURLException {
        
        this.url = _url;
        this.controleurUrl = _url+"/controleur";
        this.noeudCentralUrl = "";
        
        this.abri = _abri;
        this.controleur = new SimplisteControleur(controleurUrl, this);
        this.noeudCentral = null;
        
        this.abrisDistants = new Annuaire();
        this.copains = new ArrayList<String>();
        
        this.semaphore = new Semaphore(0, true);
    }
    
    /**
     * @throws AbriException
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException
     * @throws Throwable
     */
    @Override
    public void finalize() throws AbriException, RemoteException, NotBoundException, MalformedURLException, Throwable {
        try {
            deconnecterAbri();
            Naming.unbind(url);
        } finally {
            super.finalize();
        }
    }

    @Override
    public String getUrl() {
        return url;
    }
    
    @Override
    public boolean estConnecte() {
        return abri.estConnecte();
    }

    @Override
    public Annuaire getAnnuaire() {
        return abrisDistants;
    }

    @Override
    public void connecterAbri() throws AbriException, RemoteException, MalformedURLException, NotBoundException, InterruptedException {
        // Enregistrer dans l'annuaire RMI
        //TODO modifier la connection d'abri
        Naming.rebind(url, (AbriRemoteInterface) this);
        
        // Enregistrement de tous les autres abris
        // et notification a tous les autres abris
        for (String name : Naming.list(Adresses.archetypeAdresseAbri())) {
            name = "rmi:" + name;
            if (!name.equals(url)) {
                Remote o = Naming.lookup(name);
                if (o instanceof NoeudCentralRemoteInterface) {
                    // Enregistrement du noeud central
                    if (noeudCentral == null) {
                        this.noeudCentralUrl = name;
                        noeudCentral = (NoeudCentralRemoteInterface) o;
                        noeudCentral.enregisterAbri(url);
                        noeudCentral.askSC(url);
                        System.out.println("avant sem");
                        semaphore.acquire();
                        System.out.println("apres sem");
                        noeudCentral.rendSC(url);
                        noeudCentral.connectNewAbri(this.url,this.abri.donnerGroupe());
                        System.out.println("apres connectNewAbri");
                        System.out.println("après rendSC");
                        abri.connecter();
                    }
                    else {
                        throw new AbriException("Plusieurs noeuds centraux semblent exister.");
                    }
                }
            }
        }

    }

    /**
     *
     * @throws AbriException
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    @Override
    public void deconnecterAbri() throws AbriException, RemoteException, MalformedURLException, NotBoundException, InterruptedException {
        noeudCentral.askSC(this.url);
        semaphore.acquire();
        noeudCentral.deconectAbri(this.url);
        noeudCentral.rendSC(this.url);
        // noeudCentral
        noeudCentral.supprimerAbri(url);
        noeudCentralUrl = "";
        noeudCentral = null;
        copains.clear();
        abrisDistants.vider();
        
        // Abri
        abri.deconnecter();
        
        // Annuaire RMI
        Naming.unbind(url);
    }

    /**
     *
     * @param message
     * @throws RemoteException
     * @throws AbriException
     */
    @Override
    public synchronized void recevoirMessage(modele.Message message) throws RemoteException, AbriException {
        if (!message.getUrlDestinataire().contains(url)) {
            throw new AbriException("Message recu par le mauvais destinataire (" + message.getUrlDestinataire().toString() +  " != " + url + ")");
        }
        System.out.println(url + ": \tMessage recu de " + message.getUrlEmetteur() + " \"" + message.getContenu() + "\"");
        abri.ajouterMessage(message);
    }

    @Override
    public void emettreMessage(String message) throws InterruptedException, RemoteException, AbriException, NoeudCentralException {
            noeudCentral.askSC(this.url);
            semaphore.acquire();
            System.out.println(url + ": \tEntree en section critique");
            System.out.println(url + " est dans le groupe " + abri.donnerGroupe());
            System.out.println(url + ": \tEmission vers " + copains.toString() + ": " + message);
            noeudCentral.modifierAiguillage(url, copains);
            noeudCentral.transmettre(new Message(url, copains, message));
            noeudCentral.rendSC(this.url);
            System.out.println(url + ": \tSortie de la section critique");
    }

    @Override
    public void enregistrerAbri(String urlDistant, String groupe) throws AbriException, RemoteException, InterruptedException, MalformedURLException, NotBoundException {
        Remote o = Naming.lookup(urlDistant);
        this.abrisDistants.ajouterAbriDistant(urlDistant,(AbriRemoteInterface) o);
        if (groupe.equals(abri.donnerGroupe()))
        {
            System.out.println("entre dans le if du enregistrerAbri");
            this.copains.add(urlDistant);
            noeudCentral.askSC(this.url);
            semaphore.acquire();
            noeudCentral.replyNewAbri(this.url,urlDistant);
            noeudCentral.rendSC(this.url);
        }
        
        System.out.println(url + ": \tEnregistrement de l'abri " + urlDistant);
    }

    @Override
    public void updateCopains(String urlEmetteur, boolean type) throws RemoteException, AbriException, MalformedURLException, NotBoundException {
        if(!type) {
            Remote o = Naming.lookup(urlEmetteur);
            this.abrisDistants.ajouterAbriDistant(urlEmetteur, (AbriRemoteInterface) o);
            System.out.println("entre dans updateCopains");
            this.copains.add(urlEmetteur);
        }
        else {
            Remote o = Naming.lookup(urlEmetteur);
            this.abrisDistants.retirerAbriDistant(urlEmetteur);
            this.copains.remove(urlEmetteur);
        }
    }

    @Override
    public synchronized void supprimerAbri(String urlDistant) {
        System.out.println(url + ": \tOubli de l'abri " + urlDistant);
        abrisDistants.retirerAbriDistant(urlDistant);
        if (copains.contains(urlDistant))
        {
            copains.remove(urlDistant);
        }
    }
    
    @Override
    public synchronized void supprimerAbri(String urlAbriDistant, String urlControleurDistant) {
        try {
            AbriRemoteInterface o = (AbriRemoteInterface) Naming.lookup(urlAbriDistant);
            AbriBackend.this.supprimerAbri(urlAbriDistant);
            supprimerControleur(urlControleurDistant);
            o.supprimerControleur(controleurUrl);
        } catch (NotBoundException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(AbriBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void enregistrerControleur(String urlDistante, String groupe) {
        controleur.enregistrerControleur(urlDistante, groupe);
    }

    @Override
    public synchronized void supprimerControleur(String urlDistante) {
        controleur.supprimerControleur(urlDistante);
    }

    @Override
    public synchronized void recevoirAutorisation() {
        semaphore.release();
    }

    @Override
    public synchronized void recevoirSC() throws RemoteException {
        semaphore.release();
    }
    
    @Override
    public void changerGroupe(String groupe)
    {
        abri.definirGroupe(groupe);
    }
    
    @Override
    public String signalerGroupe() throws RemoteException
    {
        return abri.donnerGroupe();
    }
    
}
