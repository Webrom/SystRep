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
    public void demandeSectionCritique(int numeroAbri) {
        if(findAbris(numeroAbri).getInfo()!=InfosMsgAbri.REQ){
            changeAbriInfo(InfosMsgAbri.REQ,numeroAbri);
        }
        if(!abrisSC){
            try {
                noeudCentralBackend.obtientSC(numeroAbri);
            }catch (NullPointerException error){
                System.out.println(numeroAbri + "est en section critique");
            }
            this.abrisSC = true;
        }
    }

    @Override
    public void finSectionCritique(int numeroAbri) {
        changeAbriInfo(InfosMsgAbri.REL,numeroAbri);
        AbrisLamport abri = getMinReq(numeroAbri);
        if (abri != null){
            try {
                noeudCentralBackend.obtientSC(abri.getNumeroAbris());
            }catch (NullPointerException error){
                System.out.println(abri.getNumeroAbris() + "est en section critique");
            }
            this.abrisSC = true;
        }else{
            this.abrisSC=false;
        }
    }

    /**
     * Modifie l'info de l'abris
     * @param msg
     * @param numeroAbri
     */
    private void changeAbriInfo(InfosMsgAbri msg, int numeroAbri){
        findAbris(numeroAbri).setInfo(msg);
        findAbris(numeroAbri).setClock(clock);
        clock++;
    }

    /**
     * Permet de savoir si l'abris est dans notre liste de gestion, si oui alors on renvoit sa reference sinon on ajoute
     * l'abris à la liste
     * @param numeroAbri
     * @return
     */
    private AbrisLamport findAbris(int numeroAbri){
        AbrisLamport abri = null;
        if(listeGestionAbris.isEmpty()) {
            abri = addAbrisintoList(numeroAbri);
        }else {
            Iterator<AbrisLamport> i = this.listeGestionAbris.iterator();
            boolean find = false;
            while (i.hasNext() && !find){
                AbrisLamport tamponAbri = i.next();
                if(tamponAbri.getNumeroAbris()==numeroAbri){
                    abri = tamponAbri;
                    find = true;
                }
            }
            if(abri==null){
                //L'abris n'est pas présent dans la liste
                abri = addAbrisintoList(numeroAbri);
            }
        }
        return abri;
    }

    /**
     * Ajoute un abris à la liste de gestion
     * @param numeroAbri
     * @return
     */
    private AbrisLamport addAbrisintoList(int numeroAbri){
        AbrisLamport newAbri = new AbrisLamport(numeroAbri,InfosMsgAbri.REQ,clock);
        clock++;
        this.listeGestionAbris.add(newAbri);
        return newAbri;
    }

    /**
     * Renvoie l'abris qui a demandé la section critique le plus tot
     * @param numeroAbri
     * @return
     */
    private AbrisLamport getMinReq(int numeroAbri){
        AbrisLamport a = null;
        int min = clock;
        for(AbrisLamport abri : this.listeGestionAbris){
            min = (abri.getClock()<=min)?abri.getClock():min;
            a = (abri.getInfo()== InfosMsgAbri.REQ && abri.getClock()<=min)?findAbris(abri.getNumeroAbris()):null;
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
