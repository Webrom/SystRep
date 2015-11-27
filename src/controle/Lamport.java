package controle;

import modele.AbrisLamport;
import modele.InfosMsgAbri;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Created by azank on 27/11/2015.
 */
public class Lamport implements LamportInterface {
    private NoeudCentralBackend noeudCentralBackend;
    private int clock;
    private List<AbrisLamport> listeGestionAbris;
    private boolean abrisSC;

    public Lamport(NoeudCentralBackend noeudCentralBackend) {
        this.noeudCentralBackend = noeudCentralBackend;
        this.clock=0;
        this.abrisSC=false;
        this.listeGestionAbris = new ArrayList<>();
        this.noeudCentralBackend = noeudCentralBackend;
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
    public void demandeSectionCritique(String urlAbri) {
        if(findAbris(urlAbri).getInfo()!=InfosMsgAbri.REQ){
            changeAbriInfo(InfosMsgAbri.REQ,urlAbri);
        }
        if(!abrisSC){
            try {
                noeudCentralBackend.obtientSC(urlAbri);
            }catch (NullPointerException error){
                System.out.println(urlAbri + "est en section critique");
            }
            this.abrisSC = true;
        }
    }

    @Override
    public void finSectionCritique(String urlAbri) {
        changeAbriInfo(InfosMsgAbri.REL,urlAbri);
        AbrisLamport abri = getMinReq();
        if (abri != null){
            try {
                noeudCentralBackend.obtientSC(abri.getUrlAbri());
            }catch (NullPointerException error){
                System.out.println(abri.getUrlAbri() + "est en section critique");
            }
            this.abrisSC = true;
        }else{
            this.abrisSC=false;
        }
    }

    /**
     * Modifie l'info de l'abris
     * @param msg : changement info de l'abri
     * @param urlAbri : numero de l'abri
     */
    private void changeAbriInfo(InfosMsgAbri msg, String urlAbri){
        findAbris(urlAbri).setInfo(msg);
        findAbris(urlAbri).setClock(clock);
        clock++;
    }

    /**
     * Permet de savoir si l'abris est dans notre liste de gestion, si oui alors on renvoit sa reference sinon on ajoute
     * l'abris à la liste
     * @param urlAbris : numero de l'abris
     * @return AbrisLamport
     */
    private AbrisLamport findAbris(String urlAbris){
        AbrisLamport abri = null;
        if(listeGestionAbris.isEmpty()) {
            abri = addAbrisintoList(urlAbris);
        }else {
            Iterator<AbrisLamport> i = this.listeGestionAbris.iterator();
            boolean find = false;
            while (i.hasNext() && !find){
                AbrisLamport tamponAbri = i.next();
                if(tamponAbri.getUrlAbri()==urlAbris){
                    abri = tamponAbri;
                    find = true;
                }
            }
            if(abri==null){
                //L'abris n'est pas présent dans la liste
                abri = addAbrisintoList(urlAbris);
            }
        }
        return abri;
    }

    /**
     * Ajoute un abris à la liste de gestion
     * @param urlAbri : numero de l'abris
     * @return AbrisLamport
     */
    private AbrisLamport addAbrisintoList(String urlAbri){
        AbrisLamport newAbri = new AbrisLamport(urlAbri,InfosMsgAbri.REQ,clock);
        clock++;
        this.listeGestionAbris.add(newAbri);
        return newAbri;
    }

    /**
     * Renvoie l'abris qui a demandé la section critique le plus tot
     * @return AbrisLamport
     */
    private AbrisLamport getMinReq(){
        AbrisLamport a = null;
        int min = clock;
        for(AbrisLamport abri : this.listeGestionAbris){
            min = (abri.getClock()<=min)?abri.getClock():min;
            a = (abri.getInfo()== InfosMsgAbri.REQ && abri.getClock()<=min)?findAbris(abri.getUrlAbri()):null;
        }
        return a;
    }

    public void affiche(){
        System.out.println("liste gestion :");
        for(AbrisLamport abri : this.listeGestionAbris){
            System.out.println(abri.toString());
        }
    }
}
