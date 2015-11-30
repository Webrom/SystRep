import controle.Lamport;
import modele.AbriException;

/**
 * Created by azank on 27/11/2015.
 */
public class TestLamport {
    public static void main(String[] args) throws AbriException {
        System.out.println("test lamport");
        Lamport lamport = new Lamport();
        for (int i = 0; i <10; i++) {
            lamport.demandeSectionCritique("process"+i);
            if(i==3){
                lamport.finSectionCritique("process0");
                lamport.affiche();
                lamport.demandeSectionCritique("process0");
                lamport.affiche();
            }else if(i==4){
                lamport.finSectionCritique("process1");
            }
        }
        lamport.finSectionCritique("process9");
        lamport.finSectionCritique("process2");
        lamport.affiche();
    }
}


