import controle.Lamport;

/**
 * Created by azank on 27/11/2015.
 */
public class TestLamport {
    public static void main(String[] args) {
        System.out.println("test lamport");
        Lamport lamport = new Lamport();
        lamport.demandeSectionCritique(42);
        lamport.affiche();
    }
}


