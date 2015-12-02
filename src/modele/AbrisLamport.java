package modele;

/**
 * Classe utilisée par Lamport pour stocker les informations de demande et fin SC des abris
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

    /**
     * @return String : url de l'abris
     */
    public String getUrlAbri() {
        return urlAbri;
    }

    /**
     * @return InfoMsgAbri : message d'information
     */
    public InfosMsgAbri getInfo() {
        return info;
    }

    /**
     * Change le message d'information
     * @param info : InfosMsgAbri
     */
    public void setInfo(InfosMsgAbri info) {
        this.info = info;
    }

    /**
     * @return int : horloge de lamport
     */
    public int getClock() {
        return clock;
    }

    /**
     * Change l'horloge de lamport
     * @param clock : int
     */
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
