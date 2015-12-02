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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public class NoeudCentralBackend extends UnicastRemoteObject implements NoeudCentralRemoteInterface {

    protected String url;
    protected NoeudCentral noeudCentral;
    protected Annuaire abris;
    protected Lamport lamport;

    public NoeudCentralBackend(String _url) throws RemoteException, MalformedURLException {
        this.url = _url;
        noeudCentral = new NoeudCentral();
        abris = new Annuaire();
        Naming.rebind(url, (NoeudCentralRemoteInterface) this);
        this.lamport =  new Lamport(this);
    }

    @Override
    public void finalize() throws RemoteException, NotBoundException, MalformedURLException, Throwable {
        try {
            Naming.unbind(url);
        } finally {
            super.finalize();
        }
    }

    public NoeudCentral getNoeudCentral() {
        return noeudCentral;
    }

    public Annuaire getAnnuaire() {
        return abris;
    }

    @Override
    public synchronized void modifierAiguillage(String depuisUrl, ArrayList<String> versUrl) throws RemoteException, NoeudCentralException {
        
        System.out.print(url + ": \tReconfiguration du reseau de " + depuisUrl + " vers ");
        Iterator<String> itr = versUrl.iterator();
        while (itr.hasNext()) {
            System.out.print(itr.next());
        }
        System.out.print("\n");
        
        noeudCentral.reconfigurerAiguillage(depuisUrl, versUrl);
    }

    @Override
    public synchronized void transmettre(Message message, String url) throws RemoteException, AbriException, NoeudCentralException {
        try {
            noeudCentral.demarrerTransmission();
            
            System.out.println(this.url + ": \tTransmission du message \"" + message.toString() + "\"");
            
            ArrayList<String> abrisCible = noeudCentral.getVersUrl();
            Iterator<String> itr = abrisCible.iterator();
            
            while (itr.hasNext()) {
                AbriRemoteInterface c = abris.chercherUrl(itr.next());
                c.recevoirMessage(message);
            }
        } catch (RemoteException ex) {
            throw ex;
        } catch (AbriException ex) {
            throw ex;
        } finally {
            noeudCentral.stopperTransmission();
            abris.chercherUrl(url).rendreSC(); //On rend la section critique une fois que la transmission est terminée
        }
    }

    @Override
    public void enregisterAbri(String urlAbriDistant) throws RemoteException, NotBoundException, MalformedURLException {
        System.out.println(url + ": \tEnregistrement de l'abri dans l'annuaire " + urlAbriDistant);
        AbriRemoteInterface abriDistant = (AbriRemoteInterface) Naming.lookup(urlAbriDistant);
        abris.ajouterAbriDistant(urlAbriDistant, abriDistant);
    }

    @Override
    public void supprimerAbri(String urlAbriDistant) throws RemoteException {
        System.out.println(url + ": \tSuppression de l'abri de l'annuaire " + urlAbriDistant);
        abris.retirerAbriDistant(urlAbriDistant);
    }

    /**
     * Permet de prévenir un abri qu'il obtient la SC
     * @param urlAbri numéro de l'abri à informer
     */
    public void obtientSC(String urlAbri) throws AbriException, RemoteException {
        System.out.println(urlAbri+" vient d'obtenir la section critique");
        abris.chercherUrl(urlAbri).recevoirSC();
    }

    /**
     * Lorsqu'un abris demande la SC, nous retransmettons la demande à l'algorithme de Lamport
     * @param urlAbri
     */
    @Override
    public void askSC(String urlAbri) throws RemoteException, AbriException {
        System.out.println(urlAbri+" demande la SC");
        lamport.demandeSectionCritique(urlAbri);
    }

    /**
     * Lorsqu'un abri rend la SC. Nous informons l'algo
     * @param url url de l'abri qui rend la SC
     * @throws AbriException
     * @throws RemoteException
     */
    @Override
    public void rendSC(String url) throws AbriException, RemoteException{
        System.out.println(url+" demande à rendre la SC");
        lamport.finSectionCritique(url);
    }

    /**
     * Permet d'envoyer à tous les abris l'informations qu'un nouvel abri s'est connecté
     * @param url url du nouvel abri
     * @param groupe groupe du nouvel abri
     * @throws RemoteException
     * @throws AbriException
     * @throws InterruptedException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    @Override
    public void connectNewAbri(String url, String groupe) throws RemoteException, AbriException, InterruptedException, MalformedURLException, NotBoundException {
        for (Map.Entry<String, AbriRemoteInterface> entry:abris.getAbrisDistants().entrySet()) {
            if(!Objects.equals(entry.getKey(), url)) {
                entry.getValue().enregistrerAbri(url, groupe);
            }
        }
    }

    /**
     * Lorsqu'un abri répond à une nouvel connexion, pour lui informer qu'il est dans son groupe
     * @param urlEmetteur url de l'abri qui répond
     * @param urlDistinataire url de l'abri qui doit ajouter l'abri émetteur
     * @throws RemoteException
     * @throws AbriException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    @Override
    public void replyNewAbri(String urlEmetteur, String urlDistinataire) throws RemoteException, AbriException, MalformedURLException, NotBoundException {
        abris.chercherUrl(urlDistinataire).updateCopains(urlEmetteur,false);
    }

    /**
     * Permet d'informer tous les abris qu'il se déconnecte
     * @param urlEmetteur url de l'abri qui se déconnecte
     * @throws RemoteException
     * @throws AbriException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    @Override
    public void deconectAbri(String urlEmetteur) throws RemoteException, AbriException, MalformedURLException, NotBoundException {
        for (Map.Entry<String, AbriRemoteInterface> entry:abris.getAbrisDistants().entrySet()) {
            if(!Objects.equals(entry.getKey(), urlEmetteur)) {
                entry.getValue().updateCopains(urlEmetteur,true);
            }
        }
    }

}
