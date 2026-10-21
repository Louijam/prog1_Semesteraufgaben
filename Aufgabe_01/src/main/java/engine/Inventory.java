package engine;

import java.util.ArrayList;
import java.util.List;


/**
 * Verwaltet die Items, die ein Fahrzeug besitzt.
 *
 * Das Inventar kann Items hinzufügen, entfernen und prüfen,
 * ob ein bestimmter Item-Typ vorhanden ist.
 */
public class Inventory {

    // Liste aller Items im Inventar.
    private ArrayList<Item> items;


    // -----------------------------------------------------------------------
    /**
     * Erzeugt ein leeres Inventar.
     */
    public Inventory() {
        this.items = new ArrayList<>();
    }


    // -----------------------------------------------------------------------
    /**
     * Erzeugt ein Inventar und übernimmt die angegebenen Items.
     *
     * Die übergebene Liste wird kopiert. Änderungen am Inventar
     * wirken sich daher nicht auf die ursprüngliche Liste aus.
     *
     * @param items Items, mit denen das Inventar gefüllt werden soll
     */
    public Inventory(List<Item> items) {
        this.items = new ArrayList<>(items);
    }


    // -----------------------------------------------------------------------
    /**
     * Fügt dem Inventar ein Item hinzu.
     *
     * @param item Item, das hinzugefügt werden soll
     */
    public void add(Item item) {
        this.items.add(item);
    }


    // -----------------------------------------------------------------------
    /**
     * Entfernt das erste Item des angegebenen Typs aus dem Inventar.
     *
     * @param type Typ des zu entfernenden Items
     * @return entferntes Item oder null, wenn kein Item dieses Typs
     *         im Inventar vorhanden ist
     */
    public Item remove(ItemType type) {

        for (Item item : this.items) {
            if (item.getType() == type) {
                items.remove(item);
                return item;
            }
        }

        return null;
    }


    // -----------------------------------------------------------------------
    /**
     * Prüft, ob mindestens ein Item des angegebenen Typs
     * im Inventar vorhanden ist.
     *
     * @param type Typ des gesuchten Items
     * @return true, wenn ein Item dieses Typs vorhanden ist,
     *         sonst false
     */
    public boolean contains(ItemType type) {

        for (Item item : this.items) {
            if (item.getType() == type) {
                return true;
            }
        }

        return false;
    }


    // -----------------------------------------------------------------------
    /**
     * Prüft, ob das Inventar keine Items enthält.
     *
     * @return true, wenn das Inventar leer ist, sonst false
     */
    public boolean isEmpty() {
        return this.items.isEmpty();
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt die Anzahl der Items im Inventar zurück.
     *
     * @return Anzahl der Items
     */
    public int size() {
        return this.items.size();
    }


    // -----------------------------------------------------------------------
    /**
     * Zählt die Anzahl der Items für einen gegebenen Typ.
     *
     * @return Anzahl der Items für einen Typ
     */
    public int count(ItemType type) {
        int count = 0;

        for (Item item : this.items) {
            if (item.getType() == type) {
                count++;
            }
        }

        return count;
    }

}
