package engine;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.gurkenlabs.litiengine.Game;


/**
 * Repräsentiert das Spielfeld.
 *
 * Das Spielfeld besteht aus einer Kachel-basierten Map.
 * Das Layout des Spielfeldes wird als Textdatei eingelesen.
 * Jede Position im Layout entspricht genau einer Kachel.
 *
 * Die Kacheln beschreiben den Untergrund:
 *
 *     - -> EMPTY
 *     . -> TRACK
 *     ! -> HAZARD
 *     # -> WALL
 *
 * Zusätzlich können auf einer Kachel weitere Elemente liegen:
 *
 *     S -> Startposition
 *     G -> Ziel
 *     Items
 *
 * Ziel und Startposition sind keine eigenen Tile-Typen.
 * Beispielsweise ist das Ziel weiterhin eine TRACK-Kachel.
 * 
 * Die Koordinaten der Map werden als Tile-Koordinaten (x, y) angegeben.
 * Die technische Position der Agenten wird dagegen in Weltkoordinaten
 * (Pixel) angegeben. Das Spielfeld übernimmt die Umrechnung zwischen
 * diesen beiden Koordinatensystemen.
 *
 */
public class GameMap {

    // Größe einer Kachel in Weltkoordinaten (Pixel).
    // Die tatsächliche Größe wird abhängig von der Größe des Spielfelds
    // und der Anzahl der Kacheln im Konstruktor berechnet
    private int tileSize = 20;

    // Position des Ziel-Tiles in Tile-Koordinaten.
    private Point goal;

    // Position des Start-Tiles in Tile-Koordinaten.
    private Point start;

    // Die eigentliche Map.
    // Erste Dimension: y -> Zeile
    // Zweite Dimension: x -> Spalte
    // Zugriff: map[y][x]
    private Tile[][] map;

    // Items auf den einzelnen Kacheln.
    // Die Indizes entsprechen denen der Map: items[y][x]
    private Item[][] items;


    // -----------------------------------------------------------------------
    /**
     * Erzeugt ein neues Spielfeld.
     *
     * Zunächst werden die Listen für Wände und Track erzeugt
     * erzeugt. Anschließend wird das Layout geladen und daraus
     * die Map und die Items erstellt.
     * 
     * @param layoutFile Name bzw. Pfad der Layout-Datei
     * @param gameWidth Breite des Spielfelds in Pixeln
     * @param gameHeight Höhe des Spielfelds in Pixeln
     */
    public GameMap(String layoutFile, int gameWidth, int gameHeight) {

        // Layout des Spielfeldes.
        // Jede Zeile des Arrays entspricht einer Zeile der Map.
        String[] layout = loadLayout(layoutFile);

        // Aus dem Layout die Tile-Map erstellen.
        createMap(layout);

        // Die Kachelgröße so berechnen, dass die Map in die verfügbare Breite und Höhe des Spielfelds passt.
        int tileWidth = gameWidth / map[0].length;
        int tileHeight = gameHeight / map.length;
        tileSize = Math.min(tileWidth, tileHeight);    
    }


    // -----------------------------------------------------------------------
    /**
     * Prüft, ob der Agent auf der Ziel-Kachel steht
     *
     * Der Mittelpunkt des Agenten wird in eine Tile-Koordinate
     * umgerechnet. Anschließend wird geprüft, ob diese Koordinate
     * der gespeicherten Zielposition entspricht.
     *
     * @param controller das zu überprüfende v
     * @return true, wenn sich der Agent auf der Ziel-Kachel befindet
     */
    public boolean isOnGoal(AgentEntity controller) {

        int x = worldToTileX(controller.getCenter().getX());
        int y = worldToTileY(controller.getCenter().getY());

        return goal != null
            && goal.getX() == x
            && goal.getY() == y;
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt die Position des Ziel-Tiles zurück.
     *
     * Die Position wird in Kachel-Koordinaten angegeben.
     *
     * @return Position des Ziels
     */
    public Point getGoal() {
        return goal;
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt die Startposition des Agenten in Weltkoordinaten zurück.
     *
     * Die Startposition wird im Layout als Kachel-Koordinate gespeichert.
     * Für die Engine wird diese anschließend in Pixel umgerechnet.
     *
     * Beispiel:
     *
     *     Tile (8, 7)
     *         ↓
     *     Weltposition (160, 140)
     *
     * @return Startposition in Weltkoordinaten
     */
    public Point2D getStartWorldPosition() {

        return new Point2D.Double(
            start.getX() * tileSize,
            start.getY() * tileSize
        );
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt das Tile an einer bestimmten Position der Map zurück.
     *
     * x bezeichnet die Spalte.
     * y bezeichnet die Zeile.
     *
     * Da ein zweidimensionales Array in der Form
     *
     *     map[y][x]
     *
     * verwendet wird, wird y zuerst angegeben.
     *
     * Beispiel:
     *
     *     getTile(8, 7)
     *
     * liefert das Tile in Spalte 8 und Zeile 7.
     *
     * @param col Spalte
     * @param row Zeile
     * @return Kachel an der angegebenen Position (oder OUT_OF_BOUNDS)
     */
    public Tile getTile(int col, int row) {
        if ( row<map.length && col<map[0].length ){
            return map[row][col];
        }
        return Tile.OUT_OF_BOUNDS;
    }


    // -----------------------------------------------------------------------
    /**
     * Prüft, ob sich der Agent außerhalb des Spielfelds befindet.
     *
     * Hier wird die komplette Collision Box des Agenten betrachtet.
     * Dadurch wird auch erkannt, wenn ein Teil des Agenten
     * das Spielfeld verlässt.
     *
     * @param controller der zu überprüfende Agent
     * @return true, wenn sich der Agent außerhalb der Spielfeldgrenzen befindet
     */
    public boolean isOutOfBounds(AgentEntity controller) {

        Rectangle2D box = controller.getCollisionBox();

        return box.getMinX() < Game.physics().getBounds().getMinX()
            || box.getMinY() < Game.physics().getBounds().getMinY()
            || box.getMaxX() > Game.physics().getBounds().getMaxX()
            || box.getMaxY() > Game.physics().getBounds().getMaxY();
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt die Breite der Map in Tiles zurück.
     *
     * @return Anzahl der Spalten
     */
    public int getMapWidth() {
        return map[0].length;
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt die Höhe der Map in Tiles zurück.
     *
     * @return Anzahl der Zeilen
     */
    public int getMapHeight() {
        return map.length;
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt die Größe eines Tiles in Pixeln zurück.
     *
     * @return Tile-Größe in Pixeln
     */
    public int getTileSize() {
        return tileSize;
    }


    // -----------------------------------------------------------------------
    /**
     * Wandelt eine X-Weltkoordinate in eine Tile-X-Koordinate um.
     *
     * Weltkoordinaten werden in Pixeln angegeben.
     * Tilekoordinaten geben dagegen die Spalte der Map an.
     *
     * Beispiel bei TILE_SIZE = 20:
     *
     *     x = 45
     *
     *     45 / 20 = 2
     *
     * Das entspricht Spalte 2.
     *
     * @param x X-Koordinate in Pixeln
     * @return Spalte der Map
     */
    public int worldToTileX(double x) {
        return (int) (x / tileSize);
    }


    // -----------------------------------------------------------------------
    /**
     * Wandelt eine Y-Weltkoordinate in eine Tile-Y-Koordinate um.
     *
     * Die Tile-Y-Koordinate entspricht der Zeile der Map.
     *
     * @param y Y-Koordinate in Pixeln
     * @return Zeile der Map
     */
    public int worldToTileY(double y) {
        return (int) (y / tileSize);
    }


    // -----------------------------------------------------------------------
    /**
     * Ermittelt das Tile an einer Position in Weltkoordinaten.
     *
     * Zunächst werden die Pixelkoordinaten in Tilekoordinaten
     * umgerechnet. Anschließend wird das entsprechende Tile
     * aus der Map gelesen.
     *
     * @param x X-Koordinate in Pixeln
     * @param y Y-Koordinate in Pixeln
     * @return Tile an der angegebenen Weltposition
     */
    public Tile getTileAtWorldPosition(double x, double y) {

        int tileX = worldToTileX(x);
        int tileY = worldToTileY(y);

        return getTile(tileX, tileY);
    }


    // -----------------------------------------------------------------------
    /**
     * Ermittelt die Kachel in Fahrtrichtung des Agenten.
     *
     * distance wird in Tiles angegeben:
     *
     *     look(1) -> eine Kachel voraus
     *     look(2) -> zwei Kacheln voraus
     *
     * Die AgentEntity kennt die aktuelle Fahrtrichtung und
     * berechnet daraus eine Weltposition vor dem Agenten.
     *
     * @param agent Agenten, von dem aus geschaut wird
     * @param distance Anzahl der Kacheln in Fahrtrichtung
     * @return Kacheltyp in Fahrtrichtung
     */
    public Tile getTileAhead(AgentEntity agent, int distance) {
        // Weltposition vor dem Agenten bestimmen.
        Point2D nextPosition = agent.getPositionInFront(distance*tileSize);

        // Weltposition in Tile-Koordinaten umwandeln.
        return getTileAtWorldPosition(nextPosition.getX(), nextPosition.getY());
    }

 
    // -----------------------------------------------------------------------
    /**
     * Ermittelt das Tile rechts vom Agenten.
     *
     * @param agent Agent, von dem aus geschaut wird
     * @return Tile rechts vom Agenten
     */
    public Tile getTileRight(AgentEntity agent) {
        Point2D nextPosition = agent.getPositionRight(tileSize);
        return getTileAtWorldPosition(nextPosition.getX(), nextPosition.getY());
    }


    // -----------------------------------------------------------------------
    /**
     * Ermittelt das Tile links vom Agenten.
     *Agenten
     * @param agent Agent, von dem aus geschaut wird
     * @return Tile links vom Agenten
     */
    public Tile getTileLeft(AgentEntity agent) {
        Point2D nextPosition = agent.getPositionLeft(tileSize);
        return getTileAtWorldPosition(nextPosition.getX(), nextPosition.getY());
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt das Item auf der angegebenen Kachel zurück.
     *
     * Befindet sich auf der Kachel kein Item, wird null zurückgegeben.
     *
     * @param col Spalte der Kachel
     * @param row Zeile der Kachel
     * @return Item auf der Kachel oder null
     */
    public Item getItem(int col, int row) {
        return items[row][col];
    }


    // -----------------------------------------------------------------------
    /**
     * Prüft, ob sich auf der angegebenen Kachel ein Item befindet.
     *
     * @param col Spalte der Kachel
     * @param row Zeile der Kachel
     * @return true, wenn sich ein Item auf der Kachel befindet,
     *         sonst false
     */
    public boolean isItemOnTile(int col, int row) {
        return items[row][col] != null;
    }


    // -----------------------------------------------------------------------
    /**
     * Entfernt das Item von der angegebenen Kachel und gibt es zurück.
     *
     * Befindet sich auf der Kachel kein Item, wird null zurückgegeben.
     *
     * @param col Spalte der Kachel
     * @param row Zeile der Kachel
     * @return entferntes Item oder null
     */
    public Item pickUp(int col, int row) {
        Item item = items[row][col];
        items[row][col] = null;
        return item;
    }


    // -----------------------------------------------------------------------
    /**
     * Legt ein Item auf der angegebenen Kachel ab.
     *
     * Ist die Kachel bereits mit einem Item belegt, wird das neue
     * Item nicht abgelegt.
     *
     * @param col Spalte der Kachel
     * @param row Zeile der Kachel
     * @param item abzulegendes Item
     * @return true, wenn das Item erfolgreich abgelegt wurde,
     *         sonst false
     */
    public boolean drop(int col, int row, Item item) {
        if (this.items[row][col] != null) {
            return false;
        }
        this.items[row][col] = item;

        if (this.map[row][col] == Tile.HAZARD && item.getType() == ItemType.SAMPLE) {
            this.map[row][col] = Tile.PATH;
            // item wird verbraucht
            this.items[row][col] = null;
        }        

        return true;
    }


    // -----------------------------------------------------------------------
    /**
     * Legt ein Marker-Item auf der angegebenen Kachel ab.
     * Items, die schon auf der Kachel liegen, werden überschrieben
     *
     * @param col Spalte der Kachel
     * @param row Zeile der Kachel
     * @return true, wenn das Item erfolgreich abgelegt wurde,
     *         sonst false
     */
    public boolean placeMarker(int col, int row) {
        this.items[row][col] = new Item(ItemType.MARKER);
        return true;
    }

    // -----------------------------------------------------------------------
    /**
     * Zählt die Anzahl der Gefahrenstellen auf dem Spielfeld.
     * 
     * @return Anzahl der {@code HAZARD}-Felder auf der Map
     */
    public int countHazards() {
        int count = 0;

        // Jede Zeile der Map durchlaufen.
        for (int y = 0; y < map.length; y++) {
            // Jede Spalte der Map durchlaufen.
            for (int x = 0; x < map[0].length; x++) {
                if (this.map[y][x] == Tile.HAZARD) {
                    count++;
                }
            }
        }
        return count;
    }


    // -----------------------------------------------------------------------
    /**
     * Erstellt die Kachel-Map aus dem String-Layout.
     *
     * Jeder Buchstabe des Layouts wird in einen Kachel-Typ
     * umgewandelt.
     *
     *     # -> WALL
     *     . -> TRACK
     *     ! -> HAZARD
     *     G -> TRACK + GOAL
     *     S -> TRACK + Startposition
     *
     * Start und Ziel sind keine eigenen Tile-Typen.
     * Sie werden zusätzlich zur eigentlichen Kachel gespeichert.
     *
     * @param layout String-Array mit dem Spielfeld-Layout
     */
    private void createMap(String[] layout) {
        int height = layout.length;
        int width = layout[0].length();

        this.map = new Tile[height][width];
        this.items = new Item[getMapHeight()][getMapWidth()];

        // Jede Zeile der Map durchlaufen.
        for (int y = 0; y < height; y++) {

            // Jede Spalte der aktuellen Zeile durchlaufen.
            for (int x = 0; x < width; x++) {

                char symbol = layout[y].charAt(x);

                switch (symbol) {

                    // Blockiert
                    case '#':
                        this.map[y][x] = Tile.BLOCKED;
                        break;

                    // Befahrbare Strecke
                    case '.':
                        this.map[y][x] = Tile.PATH;
                        break;

                    // Befahrbare Strecke, aber gefährlich
                    case '!':
                        this.map[y][x] = Tile.HAZARD;
                        break;

                    // Startposition
                    // Die Kachel selbst bleibt PATH und ist daher befahrbar.
                    // Zusätzlich wird die Startposition gespeichert.
                    case 'S':
                        this.map[y][x] = Tile.PATH;
                        this.start = new Point(x, y);
                        break;

                    // Ziel
                    // Die Kachel selbst bleibt PATH und ist daher befahrbar.
                    // Zusätzlich wird die Position des Ziels gespeichert.
                    case 'G':
                        this.map[y][x] = Tile.PATH;
                        this.goal = new Point(x, y);
                        break;

                    // Leeres Feld
                    case '-':
                        this.map[y][x] = Tile.EMPTY;
                        break;

                    // Items
                    // Die Kachel selbst bleibt PATH und ist daher befahrbar.
                    default:
                        this.map[y][x] = Tile.PATH;
                        Item item = null;
                        // Prüfen, ob das Symbol zu einem Item-Typ gehört.
                        for (ItemType type : ItemType.values()) {
                            if (type.getSymbol() == symbol) {
                                item = new Item(type);
                            }
                        }
                        // Item speichern.
                        if (item != null) {
                            this.items[y][x] = item;
                        }
                        break;
                }
            }
        }
    }


    // -----------------------------------------------------------------------
    /**
     * Lade das Layout des Spielfeldes.
     *
     * Jede Zeichenposition entspricht genau einer Kachel.
     */
    private String[] loadLayout(String layoutName) {
        List<String> lines = new ArrayList<>();

        // Zeilen aus einer Datei lesen und in Array speichern
        try (InputStream input = getClass().getResourceAsStream("/" + layoutName)) {
            if (input == null) {
                throw new IllegalArgumentException("Layout nicht gefunden: " + layoutName);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {

                    if (line.equals("[MISSION]")) {
                        break;
                    }
                    if (!line.isEmpty()) {
                        lines.add(line);
                    }                    
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Laden des Spielfeldes", e);
        }

        // maximale Länge über alle Zeilen bestimmen
        int maxLength = Collections.max(lines, Comparator.comparing(String::length)).length();
        // Zeilen sollen alle gleich lang sein. Also kurze Zeilen auffüllen
        for (int i = 0; i < lines.size(); i++) {
            String aktuelleZeile = lines.get(i);
            int anzahl = maxLength - aktuelleZeile.length();
            lines.set(i, aktuelleZeile + "-".repeat(anzahl));
        }
        
        // Fügt ein Leerzeichen an den Anfang jeder Zeile ein
        lines.replaceAll(s -> "-" + s);

        int laengeString = lines.get(0).length();
        String text = "-".repeat(laengeString);
        // oben eine leere Zeile einfügen
        lines.add(0,text);
        // unten eine leere Zeile einfügen
        lines.add(text);

        return lines.toArray(new String[0]);
    }
}