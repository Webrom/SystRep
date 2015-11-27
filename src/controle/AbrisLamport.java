package controle;

/**
 * Created by azank on 27/11/2015.
 */
public class AbrisLamport {
    private int numeroAbris;
    private String info;
    private int horloge;

    public AbrisLamport(int numeroAbris, String message, int horloge) {
        this.numeroAbris = numeroAbris;
        this.info = info;
        this.horloge = horloge;
    }

    public int getNumeroAbris() {
        return numeroAbris;
    }

    public void setNumeroAbris(int numeroAbris) {
        this.numeroAbris = numeroAbris;
    }

    public String getinfo() {
        return info;
    }

    public void setinfo(String info) {
        this.info = info;
    }

    public int getHorloge() {
        return horloge;
    }

    public void setHorloge(int horloge) {
        this.horloge = horloge;
    }
}