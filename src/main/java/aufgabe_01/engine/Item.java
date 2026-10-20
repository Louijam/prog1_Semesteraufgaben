package engine;

/**
 * Repräsentiert ein Item, das sich entweder auf dem Kurs
 * oder im Inventar eines Fahrzeugs befinden kann.
 *
 * Ein Item besitzt einen Typ, der über {@link ItemType} festgelegt wird.
 */
public class Item {

    // Typ des Items, z. B. PACKAGE, FUEL oder COIN.
    private ItemType type;


    // -----------------------------------------------------------------------
    /**
     * Erzeugt ein neues Item des angegebenen Typs.
     *
     * @param type Typ des Items
     */
    public Item(ItemType type) {
        this.type = type;
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt den Typ dieses Items zurück.
     *
     * @return Typ des Items
     */
    public ItemType getType() {
        return type;
    }
}
