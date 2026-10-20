package engine;

import java.util.EnumMap;
import java.util.Map;

/**
 * Standardimplementierung von MissionInfo.
 *
 * Speichert die für die aktuelle Mission benötigten Mengen
 * der einzelnen Itemtypen.
 *
 * Diese Klasse wird von der Spiel-Engine verwendet und ist
 * für die Implementierung eines Players nicht erforderlich.
 */
final class DefaultMissionInfo implements MissionInfo {

    private final Map<ItemType, Integer> requiredItems;

    private boolean drawPattern = false;


    // -----------------------------------------------------------------------
    /**
     * Erzeugt Informationen über die benötigten Items einer Mission.
     *
     * @param requiredItems Zuordnung von Itemtypen zu den jeweils
     *                      benötigten Mengen
     */
    DefaultMissionInfo(Map<ItemType, Integer> requiredItems, boolean drawPattern) {
        this.requiredItems = new EnumMap<>(ItemType.class);
        this.requiredItems.putAll(requiredItems);
        this.drawPattern = drawPattern;
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt die für die Mission benötigte Anzahl eines Itemtyps zurück.
     *
     * @param type Itemtyp
     * @return benötigte Anzahl; 0, wenn kein Item dieses Typs benötigt wird
     */
    @Override
    public int getRequiredItemCount(ItemType type) {
        return requiredItems.getOrDefault(type, 0);
    }


    // -----------------------------------------------------------------------
    /**
     * Parameter, ob Mission ein Pattern zeichnen muss oder nciht
     * 
     * @return true, wenn ein Pattern gezeichnet werden muss
     */
    @Override
    public boolean getDrawPattern() {
        return this.drawPattern;
    }
}
