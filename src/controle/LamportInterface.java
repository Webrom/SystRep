package controle;

/**
 * Created by azank on 27/11/2015.
 */
public interface LamportInterface {
    /**
     * Receptionne du demande d'entree en section critique du noeud central de la part d'un processus metier
     * @param urlAbri
     */
    void demandeSectionCritique(String urlAbri);

    /**
     * Receptionne la notification du noeud central a sa sortie de la section critique d'un processus metier
     * @param urlAbri
     */
    void finSectionCritique(String urlAbri);
}
