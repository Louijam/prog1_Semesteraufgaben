package engine;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.screens.GameScreen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;


/**
 * Spielbildschirm des Robot Race.
 *
 * Der Screen ist für die Darstellung des Spiels zuständig.
 * Die Spiellogik wird von anderen Klassen übernommen.
 *
 * Die wichtigsten Klassen haben dabei folgende Aufgaben:
 *
 *     Screen  - Darstellung
 *     Race    - Spielablauf
 *     GameMap - Spielfeld
 *     Agent   - Verhalten und Steuerung
 */
public class Screen extends GameScreen {

    // Größe des Spielfensters in Pixeln.
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;

    // Das aktuelle Spiel.
    private GameController gameController;
    
    // Bild für Item PACKAGE
    private BufferedImage itemImage_package;
    // Bild für Item SAMPLE
    private BufferedImage itemImage_sample;
    // Bild für Item SAMPLE
    private BufferedImage itemImage_marker;
    // Bild für Item ENERGY
    private BufferedImage itemImage_energy;
    // Bild für das Ziel
    private BufferedImage itemImage_goal;


    // -----------------------------------------------------------------------
    /**
     * Erzeugt den Spielbildschirm und startet ein neues Rennen.
     *
     * Der Name "GameScreen" ist der interne Name des Screens.
     * Der sichtbare Titel des Fensters wird anschließend über
     * den Namen des Rennens gesetzt.
     */
    public Screen(String layoutFile, List<Agent> agents) {
        super("GameScreen");

        gameController = new GameController(WIDTH, HEIGHT, layoutFile, agents);

        // Titel des Spielfensters setzen.
        Game.window().setTitle(layoutFile);

        try {
            itemImage_package = ImageIO.read(getClass().getResource("/images/package.png"));
            itemImage_marker = ImageIO.read(getClass().getResource("/images/marker.png"));
            itemImage_sample = ImageIO.read(getClass().getResource("/images/sample.png"));
            itemImage_energy = ImageIO.read(getClass().getResource("/images/energy.png"));
            itemImage_goal = ImageIO.read(getClass().getResource("/images/goal.png"));
        } catch (IOException e) {
            throw new RuntimeException("Bilder konnten nicht geladen werden.", e);
        }

    }


    // -----------------------------------------------------------------------
    /**
     * Zeichnet den aktuellen Zustand des Spiels.
     *
     * Die einzelnen Bestandteile werden nacheinander gezeichnet.
     */
    @Override
    public void render(Graphics2D g) {
        renderBackground(g);
        renderGameMap(g);
        renderAgents(g);
        renderStatus(g);
    }


    // -----------------------------------------------------------------------
    /**
     * Bereitet den Screen vor.
     *
     * Die Methode wird von der LitiEngine aufgerufen, bevor der Screen aktiv wird.
     *
     * Hier wird race.update() an die Game-Loop angehängt.
     * Dadurch wird die Spiellogik regelmäßig aktualisiert.
     *
     * gameController.update() ruft wiederum agent.act() auf.
     * Die Klasse Player muss act() daher nicht selbst aufrufen.
     */
    @Override
    public void prepare() {
        super.prepare();

        Game.loop().attach(() -> {
            gameController.update();
        });
    }


    // -----------------------------------------------------------------------
    /**
     * Zeichnet den Hintergrund des Spielfelds.
     */
    private void renderBackground(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }


    // -----------------------------------------------------------------------
    /**
     * Zeichnet das Spielfeld.
     *
     * Die Map besteht aus einzelnen Kacheln.
     * Für jeden Eintrag der Map wird abhängig vom Tile-Typ
     * eine Farbe ausgewählt und ein entsprechendes Rechteck gezeichnet
     *
     * Die Position einer Kachel wird aus seiner Spalte (x),
     * seiner Zeile (y) und der Größe einer Kachel berechnet.
     *
     *     x * tileSize -> Position auf der X-Achse
     *     y * tileSize -> Position auf der Y-Achse
     */
    private void renderGameMap(Graphics2D g) {
        GameMap gameMap = gameController.getGameMap();

        int tileSize = gameMap.getTileSize();
        int padding = tileSize / 8;

        // Alle Zeilen der Map durchlaufen.
        for (int y = 0; y < gameMap.getMapHeight(); y++) {

            // Alle Spalten der aktuellen Zeile durchlaufen.
            for (int x = 0; x < gameMap.getMapWidth(); x++) {

                // Kachel an der Position x/y aus der Map holen.
                Tile tile = gameMap.getTile(x, y);

                // Farbe abhängig vom Tile-Typ auswählen.
                switch (tile) {
                    case EMPTY:
                        g.setColor(Color.WHITE);
                        break;
                    case PATH:
                        g.setColor(new Color(193,199,201));
                        break;
                    case HAZARD:
                        g.setColor(new Color(206,175,112));
                        break;
                    case BLOCKED:
                        g.setColor(new Color(69,140,245));
                        break;
                }
                // Das Tile als gefülltes Quadrat zeichnen.
                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);

                // -----------------------------
                // Item zeichnen
                // -----------------------------

                Item item = gameMap.getItem(x, y);
                if (item != null) {
                    switch (item.getType()) {
                        case ItemType.PACKAGE:
                            g.drawImage(itemImage_package, x* tileSize + padding, y* tileSize + padding, tileSize - 2 * padding, tileSize - 2 * padding, null);
                            break;
                        case ItemType.ENERGY:
                            g.drawImage(itemImage_energy, x* tileSize + padding, y* tileSize + padding, tileSize - 2 * padding, tileSize - 2 * padding, null);
                            break;                    
                        case ItemType.MARKER:
                            g.drawImage(itemImage_marker, x* tileSize + padding, y* tileSize + padding, tileSize - 2 * padding, tileSize - 2 * padding, null);
                            break;
                        case ItemType.SAMPLE:
                            g.drawImage(itemImage_sample, x* tileSize + padding, y* tileSize + padding, tileSize - 2 * padding, tileSize - 2 * padding, null);
                            break;                    
                        case ItemType.COIN:
                        default:
                            g.setColor(new Color(238,243,53));
                            // Item als Kreis zeichnen
                            g.fillOval(x* tileSize + padding, y* tileSize + padding, tileSize - 2 * padding, tileSize - 2 * padding); 
                            break;
                    }
                }
            }
        }

        // -- Ziel zeichnen
        Point goal = gameMap.getGoal();
        if (goal != null) {
            g.drawImage(itemImage_goal, goal.x* tileSize, goal.y* tileSize, tileSize, tileSize, null);
        }
    }


    // -----------------------------------------------------------------------
    /**
     * Zeichnet alle Agenten.
     *
     * Die Position und Größe der Agenten werden von der
     * AgentEntity bereitgestellt.
     *
     * Für die Darstellung wird die Collision Box verwendet.
     */
    private void renderAgents(Graphics2D g) {
        List <AgentEntity> entities = gameController.getAgentEntities();

        for (AgentEntity entity : entities) {
            Rectangle2D box = entity.getCollisionBox();

            g.drawImage(
                entity.getAppearance(),
                (int) box.getX(),
                (int) box.getY(),
                (int) box.getWidth(),
                (int) box.getHeight(),
                null
            );            
        }
    }


    // -----------------------------------------------------------------------
    /**
     * Zeichnet Informationen über den aktuellen Spielzustand.
     */
    private void renderStatus(Graphics2D g) {

        g.setColor(Color.BLACK);

        if (gameController.isGameOver()) {
            // "Game Over" auf dem Bildschirm angezeigt.
            g.setColor(new Color(255,80,80));
            g.fillRect(100, 250, 300, 80);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", 150, 300);
        }
    }
}