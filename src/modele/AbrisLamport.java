package modele;

/**
 * Created by azank on 27/11/2015.
 */
public class AbrisLamport {
    private String urlAbri;
    private InfosMsgAbri info;
    private int clock;

    public AbrisLamport(String urlAbri, InfosMsgAbri info, int clock) {
        this.urlAbri = urlAbri;
        this.info = info;
        this.clock = clock;
    }

    public String getUrlAbri() {
        return urlAbri;
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
                "urlAbri=" + urlAbri +
                ", info=" + info +
                ", clock=" + Integer.toString(clock) +
                '}';
    }
}
