package engine;

/**
 * Beschreibt eine Mission, die im Spiel erfüllt werden muss.
 *
 * Eine Mission wird beim Start des Spiels mit den teilnehmenden
 * Agenten und deren technischen Entitäten initialisiert.
 * Anschließend kann die Spiel-Engine prüfen, ob die Mission
 * bereits vollständig erfüllt wurde.
 */
public interface Mission {

    // -----------------------------------------------------------------------
    /**
     * Gibt die für Agenten relevanten Informationen über die aktuelle Mission zurück.
     * Die zurückgegebenen Informationen dienen ausschließlich dazu,
     * dass ein Agent sein Verhalten an die Missionsanforderungen anpassen kann.
     *
     * @return für Agenten relevante Informationen über die Mission
     */
    MissionInfo getInfo();
}
