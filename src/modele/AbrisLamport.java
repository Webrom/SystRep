package modele;

/**
 * Created by azank on 27/11/2015.
 */
public class AbrisLamport {
    private int numeroAbris;
    private InfosMsgAbri info;
    private int clock;

    public AbrisLamport(int numeroAbris, InfosMsgAbri info, int clock) {
        this.numeroAbris = numeroAbris;
        this.info = info;
        this.clock = clock;
    }

    public int getNumeroAbris() {
        return numeroAbris;
    }

    public InfosMsgAbri getInfo() {
        return info;
    }

    public void setInfo(InfosMsgAbri info) {
        this.info = info;
    }

    public int getClock() {
        return clock;
    }

    public void setClock(int clock) {this.clock = clock;}

    @Override
    public String toString() {
        return "AbrisLamport{" +
                "numeroAbris=" + numeroAbris +
                ", info=" + info +
                ", clock=" + Integer.toString(clock) +
                '}';
    }
}
