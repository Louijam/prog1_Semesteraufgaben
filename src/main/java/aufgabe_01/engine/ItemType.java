package aufgabe_01.engine;

/**
 * Beschreibt die verschiedenen Arten von Items, die im Spiel vorkommen können.
 *
 * Jedes Item besitzt ein Symbol, über das es in der Level-/Parcours-Datei
 * dargestellt werden kann.
 *
 * Beispiel:
 * 'P' -> PACKAGE
 */
public enum ItemType {
    // Paket
    PACKAGE('P'),
    // Energie
    ENERGY('E'),
    // Marker
    MARKER('M'),
    // Probe
    SAMPLE('L'),
    // Münze
    COIN('C');


    // Symbol, mit dem der Item-Typ in der Level-Datei dargestellt wird.
    private char symbol;


    // -----------------------------------------------------------------------
    /**
     * Erzeugt einen Item-Typ mit dem angegebenen Symbol.
     *
     * @param symbol Zeichen, mit dem das Item in der Level-Datei dargestellt wird
     */
    ItemType(char symbol) {
        this.symbol = symbol;
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt das Symbol dieses Item-Typs zurück.
     *
     * @return Symbol des Item-Typs
     */
    public char getSymbol() {
        return symbol;
    }
}
