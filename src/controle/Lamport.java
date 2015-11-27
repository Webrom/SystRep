package controle;

import modele.AbrisLamport;
import modele.InfosMsgAbris;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by azank on 27/11/2015.
 */
public class Lamport implements LamportInterface {
    private NoeudCentralRemoteInterface noeudCentralBackend;
    private int clock;
    private List<AbrisLamport> listeGestionAbris;
    private boolean abrisSC;

    public Lamport(NoeudCentralBackend noeudCentralBackend) {
        this.noeudCentralBackend = noeudCentralBackend;
        this.clock=0;
        this.abrisSC=false;
        this.listeGestionAbris = new ArrayList<>();
    }

    /**
     * Constructeur utilisé pour les tests
     */
    public Lamport() {
        this.clock=0;
        this.abrisSC=false;
        this.listeGestionAbris = new ArrayList<>();
    }

    @Override
    public void demandeSectionCritique(int numeroAbris) {
        changeAbrisInfo(InfosMsgAbris.REQ,numeroAbris);
        if(!abrisSC){
            //todo envoyer sc à l'abris wesh
            this.abrisSC = true;
        }
    }

    @Override
    public void finSectionCritique(int numeroAbris) {
        changeAbrisInfo(InfosMsgAbris.REL,numeroAbris);
        AbrisLamport abris = getMinReq(numeroAbris);
        if (abris != null){
            //todo envoyer SC a abris
            this.abrisSC = true;
        }else{
            this.abrisSC=false;
        }
    }

    /**
     * Modifie l'info de l'abris
     * @param msg
     * @param numeroAbris
     */
    private void changeAbrisInfo(InfosMsgAbris msg,int numeroAbris){
        findAbris(numeroAbris).setInfo(msg);
        findAbris(numeroAbris).setHorloge(clock);
        clock++;
    }

    /**
     * Permet de savoir si l'abris est dans notre liste de gestion, si oui alors on renvoit sa reference sinon on ajoute
     * l'abris à la liste
     * @param numeroAbris
     * @return
     */
    private AbrisLamport findAbris(int numeroAbris){
        AbrisLamport abris = null;
        if(listeGestionAbris.isEmpty()) {
            abris = addAbrisintoList(numeroAbris);
        }else {
            for (AbrisLamport liste_abris : this.listeGestionAbris) {
                abris = (liste_abris.getNumeroAbris()==numeroAbris)?liste_abris:null;
            }
            if(abris==null){
                //L'abris n'est pas présent dans la liste
                abris = addAbrisintoList(numeroAbris);
            }
        }
        return abris;
    }

    /**
     * Ajoute un abris à la liste de gestion
     * @param numeroAbris
     * @return
     */
    private AbrisLamport addAbrisintoList(int numeroAbris){
        AbrisLamport newAbris = new AbrisLamport(numeroAbris,"req",clock);
        clock++;
        System.out.println(clock);
        return newAbris;
    }

    /**
     * Renvoie l'abris qui a demandé la section critique le plus tot
     * @param numeroAbris
     * @return
     */
    private AbrisLamport getMinReq(int numeroAbris){
        AbrisLamport a = null;
        int min = clock;
        for(AbrisLamport abris : this.listeGestionAbris){
            min = (abris.getHorloge()<=min)?abris.getHorloge():min;
            a = (abris.getInfo()==InfosMsgAbris.REQ && abris.getHorloge()<=min)?findAbris(abris.getNumeroAbris()):null;
        }
        return a;
    }

    @Override
    public String toString() {
        return "Lamport{" +
                "listeGestionAbris=" + listeGestionAbris +
                '}';
    }
}
