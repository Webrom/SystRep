package modele;

/**
 * Created by azank on 27/11/2015.
 */
public class AbrisLamport {
    private int numeroAbris;
    private InfosMsgAbri info;
    private int horloge;

    public AbrisLamport(int numeroAbris, InfosMsgAbri info) {
        this.numeroAbris = numeroAbris;
        this.info = info;
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

    public int getHorloge() {
        return horloge;
    }

    public void setHorloge(int horloge) {this.horloge = horloge;}

    @Override
    public String toString() {
        return "AbrisLamport{" +
                "numeroAbris=" + numeroAbris +
                ", info=" + info +
                ", horloge=" + Integer.toString(horloge) +
                '}';
    }
}
