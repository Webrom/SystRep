package controle;

import modele.AbriException;
import modele.AbrisLamport;
import modele.InfosMsgAbri;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Controleur du noeud central, cette classe gère la section critique
 * Created by azank on 27/11/2015.
 */
public class Lamport {
    private NoeudCentralBackend noeudCentralBackend;
    private int clock;
    private List<AbrisLamport> listeGestionAbris;
    private boolean sc;
    private String abrisSC;

    public Lamport(NoeudCentralBackend noeudCentralBackend) {
        this.noeudCentralBackend = noeudCentralBackend;
        this.clock=0;
        this.sc =false;
        this.listeGestionAbris = new ArrayList<>();
        this.noeudCentralBackend = noeudCentralBackend;
    }

    /**
     * Constructeur utilisé pour les tests
     */
    public Lamport() {
        this.clock=0;
        this.sc =false;
        this.listeGestionAbris = new ArrayList<>();
    }

    /**
     * Receptionne du demande d'entree en section critique du noeud central de la part d'un processus metier
     * @param urlAbri : url de l'abris
     * @throws AbriException
     * @throws RemoteException
     */
    public void demandeSectionCritique(String urlAbri) throws AbriException, RemoteException {
        if(findAbris(urlAbri).getInfo()!=InfosMsgAbri.REQ){
            changeAbriInfo(InfosMsgAbri.REQ,urlAbri);
        }
        if(!sc){
            //utilisé pour les tests
            try {
                noeudCentralBackend.obtientSC(urlAbri);
                System.out.println("LAMPORT--------"+urlAbri + "A LA SC--------");
            }catch (NullPointerException error){
                System.out.println(urlAbri + " est en section critique");
                affiche();
            }
            this.sc = true;
            this.abrisSC = urlAbri;
        }
    }

    /**
     *  Receptionne la notification du noeud central  de la sortie de la section critique d'un processus metier
     * @param urlAbri : url de l'abris
     * @throws AbriException
     * @throws RemoteException
     */
    public void finSectionCritique(String urlAbri) throws AbriException, RemoteException {
        changeAbriInfo(InfosMsgAbri.REL,urlAbri);
        if(Objects.equals(urlAbri, this.abrisSC)){
            System.out.println("LAMPORT--------"+urlAbri + "REND LA SC--------");
            AbrisLamport abri = getMinReq();
            if(abri!=null) {
                this.sc = true;
                this.abrisSC = abri.getUrlAbri();
                try {
                    //utilisé pour les tests
                    noeudCentralBackend.obtientSC(abri.getUrlAbri());
                    System.out.println("LAMPORT--------"+abri.getUrlAbri() + "A LA SC--------");
                } catch (NullPointerException e) {
                    System.out.println(urlAbri + " sort de la sc");
                    affiche();
                    System.out.println(abri.getUrlAbri() + " est en section critique \n");
                }
            }else //personne n'est en attente de la section critique
                sc = false;
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
                if(Objects.equals(tamponAbri.getUrlAbri(), urlAbris)){
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
            if(abri.getClock()<=min && abri.getInfo() == InfosMsgAbri.REQ){
                a = findAbris(abri.getUrlAbri());
                min = abri.getClock();
            }
        }
        return a;
    }

    /**
     * Fonction utilisée pour le test de lamport
     */
    public void affiche(){
        System.out.println("liste gestion :");
        for(AbrisLamport abri : this.listeGestionAbris){
            System.out.println(abri.toString());
        }
    }
}
