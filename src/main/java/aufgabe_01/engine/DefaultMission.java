package engine;

/**
 * Mission, bei der ein Agent eine bestimmte Anzahl an Items sammeln
 * und anschließend das Ziel erreichen muss.
 *
 * In dieser konkreten Mission müssen fünf Coins gesammelt werden.
 */
public class DefaultMission implements Mission {

    // Infos zur Mission.
    private MissionInfo info;


    // -----------------------------------------------------------------------
    /**
     * Erstellt eine neue Mission mit den angegebenen Missionsinformationen.
     *
     * @param info Informationen und Anforderungen der Mission
     */
    public DefaultMission(MissionInfo info) {
        this.info = info;
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt die für Agenten relevanten Informationen dieser Mission zurück.
     *
     * @return Informationen über die Missionsanforderungen
     */
    @Override
    public MissionInfo getInfo() {
        return this.info;
    }

}
