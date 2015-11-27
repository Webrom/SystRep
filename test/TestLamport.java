import controle.Lamport;

/**
 * Created by azank on 27/11/2015.
 */
public class TestLamport {
    public static void main(String[] args) {
        System.out.println("test lamport");
        Lamport lamport = new Lamport();
        lamport.demandeSectionCritique("45");
        lamport.demandeSectionCritique("31");
        lamport.demandeSectionCritique("45");
        lamport.finSectionCritique("45");
        lamport.affiche();
    }
}


