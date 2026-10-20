package aufgabe_01.engine;

/**
 * Beschreibt den Untergrund einer Kachel des Spielfelds.
 *
 * Eine Kachel repräsentiert einen Bereich des Kurses.
 * Die Position einer Kachel wird über ihre X- und Y-Koordinaten
 * in der Map bestimmt.
 *
 * Zusätzliche Elemente wie Ziel, Items oder die Startposition
 * werden nicht als eigene Tile-Typen gespeichert. Sie können
 * auf einer PATH-Kachel liegen.
 */
public enum Tile {

    // Befahrbarer Teil des Kurses.
    PATH('.'),

    // Nicht passierbares Hindernis bzw. Wand.
    BLOCKED('#'),

    // Passierbar, aber gefährlich.
    HAZARD('!'),

    // Leere Kachel.
    EMPTY('-'),

    // Außerhalb des Spielfelds.
    OUT_OF_BOUNDS('_');


    // Zeichen, mit dem der Tile-Typ in der Level-Datei dargestellt wird.
    private char symbol;


    // -----------------------------------------------------------------------
    /**
     * Erzeugt einen Tile-Typ mit dem angegebenen Symbol.
     *
     * @param symbol Zeichen, mit dem der Tile-Typ in der Level-Datei dargestellt wird
     */
    Tile(char symbol) {
        this.symbol = symbol;
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt das Symbol dieses Tile-Typs zurück.
     *
     * @return Symbol des Tile-Typs
     */
    public char getSymbol() {
        return symbol;
    }
}
