package controle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azank on 27/11/2015.
 */
public class Lamport implements LamportInterface {
    private int clock;
    private List<AbrisLamport> listeGestionAbris;
    private boolean abrisSC;

    public Lamport() {
        this.clock=0;
        this.abrisSC=false;
        this.listeGestionAbris = new ArrayList<>();
    }

    @Override
    public void demandeSectionCritique(int numeroAbris) {
        if(listeGestionAbris.isEmpty()) {
            addAbrisintoList(numeroAbris);
        }else{
            if (!abrisIsPresent(numeroAbris)){
                addAbrisintoList(numeroAbris);
            }
        }

        
    }

    @Override
    public void finSectionCritique(int numeroAbris) {

    }

    private boolean abrisIsPresent(int numeroAbris){
        boolean isPresent=false;
        for (AbrisLamport abris : this.listeGestionAbris) {
            isPresent = (abris.getNumeroAbris() == numeroAbris);
        }
        return isPresent;
    }

    private void addAbrisintoList(int numeroAbris){
        AbrisLamport newAbris = new AbrisLamport(numeroAbris,"req",clock);
        clock++;
        System.out.println(clock);
    }
}
