package aufgabe_01.engine;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Steuert den Ablauf einer einzelnen Session.
 *
 * GameController verbindet die verschiedenen Bestandteile des Spiels:
 *
 *   GameController
 *    - GameMap     - Spielfeld
 *    - AgentEntity - technische Darstellung eines Agenten
 *    - Agent       - Bewegungslogik
 *
 * GameController ist außerdem dafür verantwortlich festzustellen, ob das Spiel
 * beendet wurde oder der Agentdas Spielfeld verlassen hat.
 */
public class GameController {
    // logger
    Logger logger = Logger.getLogger(getClass().getName());
    
    // Technisches Agenten-Objekte für Darstellung und Bewegung.
    private List<AgentEntity> agentEntites = new ArrayList<>();

    // Agenten mit der eigentlichen Bewegungssrategie.
    private List<Agent> agents;

    // Spielfeld, auf dem die Aufgabe stattfindet.
    private GameMap gameMap;

    // aktueller Status
    private GameStatus status = GameStatus.RUNNING;
    
    // aktuelle Mission.
    Mission mission;



    // -----------------------------------------------------------------------
    /**
     * Erzeugt eine neue Aufgabe.
     *
     * Dabei werden das Spielfeld und der Agent erzeugt und miteinander verbunden.
     */
    public GameController(int gameWidth, int gameHeight, String levelFile, List<Agent> agents) {

        // Spielwelt aus der angegebenen Layout-Datei erzeugen.
        gameMap = new GameMap(levelFile, gameWidth, gameHeight);

        // Gemeinsame Startposition der Agenten auf dem Spielfeld ermitteln.
        Point2D start = gameMap.getStartWorldPosition();

        // Liste der vom Programm bereitgestellten Agenten übernehmen.
        this.agents = agents;

        // Mission initialisieren basierend auf Informationen in der Level-Datei.
        this.mission = loadMission(levelFile);

        // Für jeden Agenten eine technische AgentEntity erzeugen und
        // Agent und AgentEntity miteinander sowie mit dem Spielfeld verbinden.
        for (Agent agent : agents) {

            // Technische Darstellung und Bewegung des Agenten erzeugen.
            AgentEntity agentEntity = new AgentEntity(
                start.getX(),
                start.getY(),
                gameMap.getTileSize()
            );

            // AgentEntity für die spätere Verwendung durch die Engine speichern.
            this.agentEntites.add(agentEntity);

            // Agent mit seiner technischen AgentEntity verbinden.
            agent.attach(agentEntity);

            // Agent mit dem Spielfeld verbinden, damit er die Spielwelt
            // wahrnehmen und mit ihr interagieren kann.
            agent.setGameMap(gameMap);

            // Agent mit MissionInfo verbinden
            agent.setMissionInfo(mission.getInfo());
        }
    }


    // -----------------------------------------------------------------------
    /**
     * Aktualisiert den Zustand des aktuellen Spiels.
     *
     * Diese Methode wird regelmäßig von der Game-Loop aufgerufen.
     */
    public void update() {

        // Nach dem Ende keine weiteren Aktionen ausführen.
        if (isGameOver()) {
            return;
        }

        for (int i = 0; i < agents.size(); i++) {
            // Prüfen, ob der Agent das Spielfeld verlassen hat.
            if ( gameMap.isOutOfBounds(this.agentEntites.get(i))) {
                this.status = GameStatus.OUT_OF_BOUNDS;
                return;
            }
            // Prüfen, ob der Agent auf einer blockierten Kachel steht
            if (this.agents.get(i).getCurrentTile()==Tile.BLOCKED) {
                this.status = GameStatus.RULE_VIOLATION;
                return;                
            }

            // Die Bewegungsstrategie des Agenten ausführen.
            // Bei Player wird hier Player.act() aufgerufen.
            this.agents.get(i).act();
        }        
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt das aktuelle Spielfeld zurück.
     *
     * Wird unter anderem vom Screen verwendet, um das Spielfeld darzustellen.
     */
    public GameMap getGameMap() {
        return gameMap;
    }


    // -----------------------------------------------------------------------
    /**
     * Gibt das technische Agentenobjekt zurück.
     *
     * Wird vom Screen für die Darstellung des Agenten verwendet.
     */
    public List<AgentEntity> getAgentEntities() {
        return this.agentEntites;
    }


    // -----------------------------------------------------------------------
    /**
     * Prüft, ob das Spiel beendet wurde.
     *
     * @return true, wenn das Spiel beendet ist, sonst false
     */
    public boolean isGameOver() {
        return this.status != GameStatus.RUNNING;
    }

 
    // -----------------------------------------------------------------------
    /**
     * Lädt die Mission aus der angegebenen Level-Datei.
     *
     * Der Missionstyp und die zugehörigen Missionsanforderungen
     * werden aus dem Abschnitt {@code [MISSION]} gelesen.
     *
     * @param levelFile Name der Level-Datei
     * @return die konfigurierte Mission
     */
    private Mission loadMission(String levelFile) {

        int numOfHazards = 0;
        boolean drawPattern = false;

        Map<ItemType, Integer> requiredItems = new EnumMap<>(ItemType.class);

        // Zeilen aus einer Datei lesen
        try (InputStream input = getClass().getResourceAsStream("/" + levelFile)) {
            if (input == null) {
                throw new IllegalArgumentException("Level nicht gefunden: " + levelFile);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                boolean inMissionSection = false;
                while ((line = reader.readLine()) != null) {
                    // Leerzeichen löschen
                    line = line.trim();

                    if (line.equals("[MISSION]")) {
                        inMissionSection = true;
                        continue;
                    }
                    if (!inMissionSection || line.isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split("=");

                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Ungültige Missionszeile: " + line);
                    }

                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    if (key.equals("HAZARD")) {
                        numOfHazards = Integer.parseInt(value);

                    } else if (key.equals("PATTERN")) {
                        drawPattern = Boolean.parseBoolean(value);

                    } else {
                        try {
                            ItemType type = ItemType.valueOf(key);
                            int amount = Integer.parseInt(value);

                            if (amount < 0) {
                                throw new IllegalArgumentException("Anzahl darf nicht negativ sein: " + line);
                            }

                            requiredItems.put(type, amount);

                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Ungültige Missionsangabe: " + line, e);
                        }
                    }

                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Laden der Mission", e);
        }

        MissionInfo info = new DefaultMissionInfo(requiredItems, drawPattern);

        return new DefaultMission(info);
    }

}
