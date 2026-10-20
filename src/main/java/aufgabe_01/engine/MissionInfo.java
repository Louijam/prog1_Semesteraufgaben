package aufgabe_01.engine;

/**
 * Stellt einem Agenten Informationen über die aktuelle Mission zur Verfügung.
 *
 * MissionInfo enthält ausschließlich Informationen, die ein Agent für seine
 * eigene Entscheidungslogik verwenden darf. Die eigentliche Prüfung, ob
 * eine Mission erfüllt wurde, erfolgt weiterhin durch Mission.
 */
public interface MissionInfo {

    // -----------------------------------------------------------------------
    /**
     * Gibt die Anzahl der Items zurück, die für die aktuelle Mission
     * mindestens benötigt werden.
     *
     * @param type Itemtyp
     * @return benötigte Anzahl; 0, wenn kein Item dieses Typs benötigt wird
     */
    public int getRequiredItemCount(ItemType type);

    
    // -----------------------------------------------------------------------
    /**
     * Parameter, ob Mission ein Pattern zeichnen muss oder nciht
     * 
     * @return true, wenn ein Pattern gezeichnet werden muss
     */
    boolean getDrawPattern();


    // -----------------------------------------------------------------------
    /**
     * Prüft, ob die aktuelle Mission Items dieses Typs benötigt.
     *
     * @param type Itemtyp
     * @return true, wenn mindestens ein Item dieses Typs benötigt wird
     */
    default boolean requiresItem(ItemType type) {
        return getRequiredItemCount(type) > 0;
    }
}
