package engine;

/**
 * Abstrakte Basisklasse für alle steuerbaren Agenten im Spiel.
 *
 * Agent stellt die allgemeine API zur Verfügung, die ein Agent
 * zum Steuern und zur Wahrnehmung seiner Umgebung benötigt.
 *
 * Die konkrete Strategie wird von einer Unterklasse, z. B. Player,
 * in der Methode act() implementiert.
 *
 * Die technische Umsetzung der Bewegung übernimmt {@link AgentEntity}.
 * Informationen über das Spielfeld werden von {@link GameMap} bereitgestellt.
 */
public abstract class Agent {

    // Die Umsetzung von Bewegungen auf dem Spielfeld.
    private AgentEntity actor;

    // Das Spielfeld, auf dem sich der Agent bewegt.
    private GameMap gameMap;

    // Inventar des Agenten.
    private Inventory inventory = new Inventory();

    // Informationen zur aktuellen Mission.
    private MissionInfo missionInfo;


    // =======================================================================
    // -- Bewegung
    // =======================================================================

    // -----------------------------------------------------------------------
    /**
     * Wird von der Spiel-Engine bei jedem Update aufgerufen.
     * Die konkrete Bewegungsstrategie wird von der Unterklasse (z. B. Player) implementiert. 
     */
    public abstract void act();

    // -----------------------------------------------------------------------
    /**
     * Bewegt den Agenten in seine aktuelle Bewegungsrichtung.
     * 
     * Die Größe einer Kachel wird von GameMap bereitgestellt.
     * Die technische Bewegung übernimmt AgentEntity.
     */
    protected void move() {
        this.actor.move(this.gameMap.getTileSize());
    }

    // -----------------------------------------------------------------------
    /**
     * Dreht den Agenten nach links.
     */
    protected void turnLeft() {
        this.actor.turnLeft();
    }

    // -----------------------------------------------------------------------
    /**
     * Dreht den Agenten nach rechts.
     */
    protected void turnRight() {
        this.actor.turnRight();
    }

    // -----------------------------------------------------------------------
    /**
     * Stoppt den Agenten.
     */
    protected void stop() {
        this.actor.stop();
    }


    // =======================================================================
    // -- Umgebung
    // =======================================================================

    // -----------------------------------------------------------------------
    /**
     * Gibt den Typ an der aktuellen Kachel zurück.
     * 
     * @return aktueller Kacheltyp
     */
    protected Tile getCurrentTile() {
        return gameMap.getTile(getTileX(), getTileY());
    }

    // -----------------------------------------------------------------------
    /**
     * Gibt den Typ der Kachel zurück, die sich in der angegebenen Entfernung vor dem Agenten befindet.
     * 
     * distance = 1 -> nächste Kachel
     * distance = 2 -> übernächste Kachel
     * ...  
     * Die Berechnung der Position und die Umrechnung in eine Kachel übernimmt GameMap. 
     * 
     * @param distance Anzahl der Tiles vor dem Agenten
     * @return Kacheltyp in der angegebenen Entfernung vor dem Agenten
     */
    protected Tile look(int distance) {
        return gameMap.getTileAhead(actor, distance);
    }    

    // -----------------------------------------------------------------------
    /**
     * Gibt den Typ der Kachel zurück, die sich rechts vom Agenten befindet.
     * 
     * @return Kacheltyp rechts vom Agenten
     */
    protected Tile lookRight() {
        return gameMap.getTileRight(actor);
    }

    // -----------------------------------------------------------------------
    /**
     * Gibt den Typ der Kachel zurück, die sich links vom Agenten befindet.
     * 
     * @return Kacheltyp links vom Agenten
     */
    protected Tile lookLeft() {
        return gameMap.getTileLeft(actor);
    }

    // -----------------------------------------------------------------------
    /**
     * Prüft, ob der Agent auf der Ziel-Kachel steht.
     * 
     * @return true, wenn sich der Agent auf dem Ziel befindet.
     */
    protected boolean isOnGoal() {
        return this.gameMap.isOnGoal(this.actor);
    }

    // -----------------------------------------------------------------------
    /**
     * Ermittelt die X-Kachel-Koordinate des aktuellen Standorts.
     * Die Umrechnung von Weltkoordinaten (Pixel) in Kachel-Koordinaten übernimmt GameMap.
     * 
     * @return X-Koordinate der aktuellen Kachel
     */
    private int getTileX() {
        return gameMap.worldToTileX(actor.getCenter().getX());
    }

    // -----------------------------------------------------------------------
    /**
     * Ermittelt die Y-Kachel-Koordinate des aktuellen Standorts.
     * Die Umrechnung von Weltkoordinaten (Pixel) in kachel-Koordinaten übernimmt GameMap.
     * 
     * @return Y-Koordinate der aktuellen Kachel
     */
    private int getTileY() {
        return gameMap.worldToTileY(actor.getCenter().getY());
    }


    // =======================================================================
    // -- Items
    // =======================================================================


    // -----------------------------------------------------------------------
    /**
     * Prüft, ob auf der aktuellen Kachel ein Item liegt.
     *
     * @return true, wenn auf der aktuellen Kachel ein Item liegt
     */
    protected boolean isItemOnTile() {
        return gameMap.isItemOnTile(getTileX(), getTileY());
    }

    // -----------------------------------------------------------------------
    /**
     * Gibt die Anzahl der Items mit einem bestimmten Typ im Inventar zurück.
     *
     * @return Anzahl der Items für einen bestimmten Typ im Inventar
     */
    protected int getItemCount(ItemType type) {
        return inventory.count(type);
    }
    
    // -----------------------------------------------------------------------
    /**
     * Gibt die Anzahl der erforderlichen Items mit einem bestimmten Typ zurück.
     *
     * @return Anzahl erforderlichen Items für einen bestimmten Typ
     */
    protected int getRequiredItemCount(ItemType type) {
        return missionInfo.getRequiredItemCount(type);
    }

    // -----------------------------------------------------------------------
    /**
     * Nimmt das Item von der aktuellen Kachel auf und
     * legt es in das Inventar des Agenten.
     *
     * Befindet sich kein Item auf der aktuellen Kachel,
     * wird null zurückgegeben.
     *
     * @return aufgenommenes Item oder null, wenn kein Item vorhanden 
     */
    protected Item pickUp() {
        Item item = gameMap.pickUp(getTileX(), getTileY());
        if (item!=null) {
            inventory.add(item);
        }
        return item;
    }

    // -----------------------------------------------------------------------
    /**
     * Legt ein Item aus dem Inventar auf der aktuellen Kachel ab.
     *
     * Das Item wird nur dann aus dem Inventar entfernt,
     * wenn es erfolgreich auf dem Spielfeld abgelegt werden konnte.
     *
     * @param item Item, das abgelegt werden soll
     * @return true, wenn das Item erfolgreich abgelegt wurde
     */
    protected boolean drop(ItemType type) {
        if (type==ItemType.MARKER) {
            return gameMap.placeMarker(getTileX(), getTileY());
        }
        
        Item item = inventory.remove(type);
        if (item == null) {
                return false;
        }

        if (!gameMap.drop(getTileX(), getTileY(), item)) {
            // Kachel ist schon belegt
            inventory.add(item);
            return false;
        }
        return true;
    }

    // -----------------------------------------------------------------------
    /**
     * Prüft, ob sich mindestens ein Item des angegebenen Typs
     * im Inventar des Agenten befindet.
     *
     * @param type Typ des gesuchten Items
     * @return true, wenn sich mindestens ein Item dieses Typs
     *         im Inventar befindet, sonst false
     */
    protected boolean hasItem(ItemType type) {
        return inventory.contains(type);
    }

    // -----------------------------------------------------------------------
    /**
     * Ermittelt die Anzahl der aktuell noch vorhandenen Gefahrenstellen
     * auf der Spielkarte.
     * 
     * @return Anzahl der Gefahrenstellen
     */
    protected int getRemainingHazards() {
        return gameMap.countHazards();
    }


    // =======================================================================
    // -- Engine
    // =======================================================================

    // -----------------------------------------------------------------------
    /**
     * Verbindet Agent mit der technischen AgentEntity.
     * Die Studierenden müssen AgentEntity dadurch nicht direkt verwenden.
     */
    void attach(AgentEntity controller) {
        this.actor = controller;
    }

    // -----------------------------------------------------------------------
    /**
     * Setzt das Spielfeld, auf dem sich der Agent befindet.
     */
    void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    // -----------------------------------------------------------------------
    /**
     * Setzt die Missioninfo.
     */
    void setMissionInfo(MissionInfo info) {
        this.missionInfo = info;
    }
}