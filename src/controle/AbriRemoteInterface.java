/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import modele.AbriException;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Gwenole Lecorve
 * @author David Guennec
 */
public interface AbriRemoteInterface extends Remote {

    public void enregistrerAbri(String urlAbriDistant, String groupe) throws RemoteException, AbriException, InterruptedException, MalformedURLException, NotBoundException;

    public void recevoirMessage(modele.Message transmission) throws RemoteException, AbriException;

    public void recevoirSC() throws RemoteException;

    public void updateCopains(String urlEmetteur, boolean type) throws RemoteException, AbriException, MalformedURLException, NotBoundException;

    public void rendreSC() throws AbriException, RemoteException;
}
