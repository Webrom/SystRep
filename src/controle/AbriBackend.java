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
    protected NoeudCentralRemoteInterface noeudCentral;
    
    protected Annuaire abrisDistants;    // Map faisant le lien entre les url et les interfaces RMI des abris distants
    protected ArrayList<String> copains; // Les urls des autres membres du groupe de l'abri courant // Pas dans l'annuaire -> imposer la gestion d'une liste locale aux abris pour les groupes
    
    protected Semaphore semaphore,canAskSC;
    
    public AbriBackend(String _url, Abri _abri) throws RemoteException, MalformedURLException {
        
        this.url = _url;
        this.controleurUrl = _url+"/controleur";
        this.noeudCentralUrl = "";
        
        this.abri = _abri;
        this.noeudCentral = null;
        
        this.abrisDistants = new Annuaire();
        this.copains = new ArrayList<String>();
        
        this.semaphore = new Semaphore(0, true);
        this.canAskSC = new Semaphore(1,true);
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

    /**
     * Permet de connecter un abri.
     * Récupère une interface RMI du noeud central.
     * Demande la SC
     * L'obtient
     * Rend la SC (pour éviter un interblocage avec la réponse des autres)
     * Demande au noeud central d'envoyer un message à tous les autres pour dire qu'il est connecté.
     * @throws AbriException
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     * @throws InterruptedException
     */
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
                        this.askSC();
                        System.out.println("avant sem");
                        semaphore.acquire();
                        System.out.println("apres sem");
                        this.rendreSC();
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
     * Permet de déconnecter l'abri.
     * Demain la SC
     * L'obtient
     * Il demande au noeud central d'envoyer à tous le monde qu'il se déconnecte
     * Rend la SC
     * Dit au noeud central de le supprimer de l'annuaire
     * Le noeud central devient nul (vu que l'abri est déconnecté)
     * Vide la l'annuaire et la liste de copain.
     * @throws AbriException
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    @Override
    public void deconnecterAbri() throws AbriException, RemoteException, MalformedURLException, NotBoundException, InterruptedException {
        this.askSC();
        semaphore.acquire();
        noeudCentral.deconectAbri(this.url);
        this.rendreSC();
        // noeudCentral
        noeudCentral.supprimerAbri(url);
        noeudCentralUrl = "";
        noeudCentral = null;
        //Dans le code fourni au départ, il n'était pas prévu de vider la liste de copains. Du coup en changeant de groupe on continuait à envoyer au précédent groupe
        copains.clear();
        abrisDistants.vider();
        
        // Abri
        abri.deconnecter();
        
        // Annuaire RMI
        Naming.unbind(url);
    }

    /**
     * Implémenté dans le code fourni
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

    /**
     * Nous avons gardé le code d'origine.
     * Nous avons juste ajouté le fait de demander la SC avant d'émettre le message.
     * @param message
     * @throws InterruptedException
     * @throws RemoteException
     * @throws AbriException
     * @throws NoeudCentralException
     */
    @Override
    public void emettreMessage(String message) throws InterruptedException, RemoteException, AbriException, NoeudCentralException {
            this.askSC();
            semaphore.acquire();
            System.out.println(url + ": \tEntree en section critique");
            System.out.println(url + " est dans le groupe " + abri.donnerGroupe());
            System.out.println(url + ": \tEmission vers " + copains.toString() + ": " + message);
            noeudCentral.modifierAiguillage(url, copains);
            noeudCentral.transmettre(new Message(url, copains, message));
            this.rendreSC();
            System.out.println(url + ": \tSortie de la section critique");
    }

    /**
     * Cette fonction est appelé lorsque le noeud central nous informe qu'un nouvel abri est connecté.
     * Si le nouvel abri est dans le même groupe que nous, alors :
     * Nous l'ajoutons à la liste de copain
     * Nous l'ajoutons à l'annuaire local (nous le faisons uniquement pour la vue, autrement ça nous sert à rien)
     * Demande de la SC
     * Utilise le sémaphore
     * Envoie au noeud central un message de réponse avec son url, pour que le nouvel abri le connaisse
     * Rend la SC
     * @param urlDistant
     * @param groupe
     * @throws AbriException
     * @throws RemoteException
     * @throws InterruptedException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    @Override
    public void enregistrerAbri(String urlDistant, String groupe) throws AbriException, RemoteException, InterruptedException, MalformedURLException, NotBoundException {

        if (groupe.equals(abri.donnerGroupe()))
        {
            //TODO Maybe problème semaphore
            Remote o = Naming.lookup(urlDistant);
            this.abrisDistants.ajouterAbriDistant(urlDistant,(AbriRemoteInterface) o);
            System.out.println("entre dans le if du enregistrerAbri");
            this.copains.add(urlDistant);
            System.out.println("ajouter à copain passé");
            this.askSC();
            System.out.println("askSC passé");
            semaphore.acquire();
            System.out.println("semaphore.acquire passé");
            noeudCentral.replyNewAbri(this.url,urlDistant);
            this.rendreSC();
            System.out.println("rendre SC passé");
        }
        
        System.out.println(url + ": \tEnregistrement de l'abri " + urlDistant);
    }

    /**
     * Permet de mettre à jour la liste de copain.
     * Selon le type, ça signifie que c'est une réponse de connexion (donc à ajouter) ou une déconnexion (à supprimer).
     * @param urlEmetteur
     * @param type
     * @throws RemoteException
     * @throws AbriException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
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

    /**
     * Appelé par le noeud central.
     * Permet d'indiquer que l'on obtient la SC, et donc imcrémenter de 1 le sémaphore.
     * @throws RemoteException
     */
    @Override
    public synchronized void recevoirSC() throws RemoteException {
        semaphore.release();
    }

    /**
     * Utilisée par la vue pour mettre à jour le groupe de l'abri lorsque nous choisissons avant de se connecter.
     * @param groupe
     */
    @Override
    public void changerGroupe(String groupe)
    {
        //TODO modifier à la connexion
        abri.definirGroupe(groupe);
    }

    @Override
    public void askSC() throws InterruptedException, AbriException, RemoteException {

        System.out.println("entre dans askSC");
        System.out.println(canAskSC.toString());
        canAskSC.acquire();
        System.out.println("a passé canAskSC.acquire");
        noeudCentral.askSC(this.url);
        System.out.println("demande au noeud central envoyé");
    }

    @Override
    public void rendreSC() throws AbriException, RemoteException {
        noeudCentral.rendSC(this.url);
        System.out.println("rendSC passé");
        System.out.println(canAskSC.toString());
        canAskSC.release();
        System.out.println(canAskSC.toString());
        System.out.println("canAskSC.release passé");
    }
}
