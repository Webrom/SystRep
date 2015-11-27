package controle;

/**
 * Created by azank on 27/11/2015.
 */
public interface LamportInterface {
    /**
     * Receptionne du demande d'entree en section critique du noeud central de la part d'un processus metier
     * @param numeroAbris
     */
    void demandeSectionCritique(int numeroAbris);

    /**
     * Receptionne la notification du noeud central a sa sortie de la section critique d'un processus metier
     * @param numeroAbris
     */
    void finSectionCritique(int numeroAbris);
}
