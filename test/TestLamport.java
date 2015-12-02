import controle.Lamport;
import modele.AbriException;

import java.rmi.RemoteException;

/**
 * Created by azank on 27/11/2015.
 */
public class TestLamport {
    public static void main(String[] args) throws AbriException, RemoteException {
        final String process = "process";
        System.out.println("test lamport");
        Lamport lamport = new Lamport();
        for (int i = 0; i <10; i++) {
            System.out.println("Ajout d'une demande de processus");
            lamport.demandeSectionCritique(process+i);
            if(i==3){
                lamport.finSectionCritique(process+"0");
                lamport.demandeSectionCritique(process+"0");
            }else if(i==4){
                lamport.finSectionCritique(process+"1");
                lamport.finSectionCritique(process+"3");
            }
        }
        lamport.finSectionCritique(process+"9");
        lamport.finSectionCritique(process+"2");
        lamport.finSectionCritique(process+"0");
        lamport.affiche();
    }
}


