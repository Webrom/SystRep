import controle.Lamport;
import controle.NoeudCentralBackend;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

/**
 * Created by azank on 27/11/2015.
 */
public class TestLamport {
    public static void main(String[] args) {
        System.out.println("test lamport");
        Lamport lamport = new Lamport();
        lamport.demandeSectionCritique(42);
        lamport.toString();
    }
}


