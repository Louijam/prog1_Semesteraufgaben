package aufgabe_01.engine;

/**
 * Beschreibt den aktuellen Zustand einer Spielsitzung.
 */
public enum GameStatus {

    /**
     * Das Spiel läuft noch.
     */
    RUNNING,

    /**
     * Das Missionsziel wurde erreicht.
     */
    MISSION_COMPLETE,

    /**
     * Eine Spielregel wurde verletzt.
     */
    RULE_VIOLATION,

    /**
     * Das Fahrzeug hat das Spielfeld verlassen.
     */
    OUT_OF_BOUNDS,

    /**
     * Die maximal erlaubte Anzahl an Ticks wurde erreicht.
     */
    TIMEOUT
}
