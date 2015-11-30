/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import modele.AbriException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public interface AbriRemoteInterface extends Remote {

    public void enregistrerAbri(String urlAbriDistant, String groupe) throws RemoteException, AbriException, InterruptedException;
    
    public void supprimerAbri(String urlAbriDistant, String urlControleurDistant) throws RemoteException;

    public void enregistrerControleur(String urlControleurDistant, String groupe) throws RemoteException;

    public void supprimerControleur(String urlControleurDistant) throws RemoteException;

    public void recevoirMessage(modele.Message transmission) throws RemoteException, AbriException;

    public String signalerGroupe() throws RemoteException;

    public void recevoirSC() throws RemoteException;

    public void updateCopains(String urlEmetteur) throws RemoteException, AbriException;
}
