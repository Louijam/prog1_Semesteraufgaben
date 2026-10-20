package aufgabe_01.engine;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.gurkenlabs.litiengine.entities.Creature;


/**
 * Technische Repräsentation eines Agenten in der Engine.
 *
 * AgentEntity kümmert sich um die konkrete Position, Größe
 * Bewegung und Kollision des Agenten.
 */
public class AgentEntity extends Creature {

    /*
     * Fahrtrichtung des Agenten.
     *
     * 0 = rechts
     * 1 = unten
     * 2 = links
     * 3 = oben
     */
    private int direction = 0;

    // ist true, wenn Fahrzeug gestoppt wurde
    private boolean stopped = false;

    // Enthält die Grafiken des Spielers für die vier möglichen Fahrtrichtungen.
    // Index 0 = rechts, 1 = unten, 2 = links, 3 = oben.
    private BufferedImage[] images;


    // -----------------------------------------------------------------------
    /**
     * Erzeugt einen neuen Agenten .
     *
     * @param posX  X-Position des Agenten
     * @param posY  Y-Position des Agenten
     * @param color Farbe des Agenten
     */
    public AgentEntity(double posX, double posY, int tileSize) {

        super();

        // Position
        setLocation(posX, posY);

        // Größe des Agenten
        setSize(tileSize, tileSize);

        // Collision Box
        setCollisionBoxWidth(tileSize);
        setCollisionBoxHeight(tileSize);

        // Collision aktivieren
        setCollision(true);

        // Bilder laden
        images = new BufferedImage[4];
        try {
            images[0] = ImageIO.read(getClass().getResource("/images/right.png"));
            images[1] = ImageIO.read(getClass().getResource("/images/down.png"));
            images[2] = ImageIO.read(getClass().getResource("/images/left.png"));
            images[3] = ImageIO.read(getClass().getResource("/images/up.png"));

        } catch (IOException e) {
            throw new RuntimeException("Bilder konnten nicht geladen werden.", e);
        }        
    }

    // -----------------------------------------------------------------------
    /**
     * Bewegt den Agenten um einen Schritt in die aktuelle Bewegungsrichtung.
     *
     * Die Methode wird nicht direkt von den Studierenden aufgerufen.
     * Über Agent steht dafür die Methode move() zur Verfügung.
     */
    public void move(double distance) {

        if (stopped) {
            return;
        }

        double x = getX();
        double y = getY();

        if (this.direction == 0) {
            x += distance;
        } else if (this.direction == 1) {
            y += distance;
        } else if (this.direction == 2) {
            x -= distance;
        } else if (this.direction == 3) {
            y -= distance;
        }

        setLocation(x, y);
    }

    // -----------------------------------------------------------------------
    /**
     * Dreht den Agenten um 90 Grad nach links.
     */
    public void turnLeft() {

        this.direction--;

        if (this.direction < 0) {
            this.direction = 3;
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Dreht den Agenten um 90 Grad nach rechts.
     */
    public void turnRight() {

        this.direction++;

        if (this.direction > 3) {
            this.direction = 0;
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Stoppt den Agenten.
     */
    public void stop() {

        this.stopped = true;
    }

    // -----------------------------------------------------------------------
    /**
     * Lässt den Agenten sich weiterbewegen.
     */
    public void resume() {

        this.stopped = false;
    }

    // -----------------------------------------------------------------------
    /**
     * Berechnet die Kollisionsbox des Agenten nach einer Bewegung
     * um die angegebene Entfernung.
     *
     * Die aktuelle Fahrtrichtung des Agenten wird bei der Berechnung
     * berücksichtigt. Die bestehende Kollisionsbox wird dabei nicht verändert.
     *
     * @param distance Entfernung, um die der Agent bewegt wird
     * @return Kollisionsbox an der berechneten Position
     */
    public Rectangle2D getCollisionBoxAfterMove(double distance) {
        Rectangle2D box = getCollisionBox();
        double x = box.getCenterX();
        double y = box.getCenterY();

        if (this.direction == 0) {
            x += distance;
        } else if (this.direction == 1) {
            y += distance;
        } else if (this.direction == 2) {
            x -= distance;
        } else if (this.direction == 3) {
            y -= distance;
        }

        return new Rectangle2D.Double(x, y, box.getWidth(), box.getHeight());
    }

    // -----------------------------------------------------------------------
    /**
     * Berechnet eine Position in Bewegungsrichtung.
     *
     * Die aktuelle Position des Agenten wird dabei nicht verändert.
     *
     * @param distance Anzahl der Bewegungsschritte vor dem Agenten
     * @return Position vor dem Agenten
     */
    public Point2D getPositionInFront(double distance) {
        Rectangle2D box = getCollisionBox();

        double x = box.getCenterX();
        double y = box.getCenterY();

        switch (this.direction) {
            case 0: // Agent fährt nach rechts -> vorne ist rechts
                x += distance;
                break;
            case 1: // Agent fährt nach unten -> vorne ist unten
                y += distance;
                break;
            case 2: // Agent fährt nach links -> vorne ist links
                x -= distance;
                break;
            case 3: // Agent fährt nach oben -> vorne ist oben
                y -= distance;
                break;
        }

        return new Point2D.Double(x, y);
    }

    // -----------------------------------------------------------------------
    /**
     * Berechnet die Position rechts neben dem Agenten in der angegebenen
     * Entfernung.
     * 
     * Die Position wird abhängig von der aktuellen Bewegungsrichtung berechnet.
     * Dabei wird der Mittelpunkt der Kollisionsbox des Agenten als
     * Ausgangspunkt verwendet.
     * 
     * @param distance Entfernung vom Agenten
     * @return Position rechts neben dem Agenten
     */
    public Point2D getPositionRight(double distance) {
        double x = getCollisionBox().getCenterX();
        double y = getCollisionBox().getCenterY();

        switch (this.direction) {
            case 0: // Agent fährt nach rechts -> rechts ist unten
                y += distance;
                break;
            case 1: // Agent fährt nach unten -> rechts ist links
                x -= distance;
                break;
            case 2: // Agent fährt nach links -> rechts ist oben
                y -= distance;
                break;
            case 3: // Agent fährt nach oben -> rechts ist rechts
                x += distance;
                break;
        }

        return new Point2D.Double(x, y);
    }

    // -----------------------------------------------------------------------
    /**
     * Berechnet die Position links neben dem Agenten in der angegebenen
     * Entfernung.
     * 
     * Die Position wird abhängig von der aktuellen Bewegungsrichtung berechnet.
     * Dabei wird der Mittelpunkt der Kollisionsbox des Agenten als
     * Ausgangspunkt verwendet.
     * 
     * @param distance Entfernung vom Agenten
     * @return Position links neben dem Agenten
     */
    public Point2D getPositionLeft(double distance) {
        double x = getCollisionBox().getCenterX();
        double y = getCollisionBox().getCenterY();

        switch (this.direction) {
            case 0: // Agent fährt nach rechts -> links ist oben
                y -= distance;
                break;
            case 1: // Agent fährt nach unten -> links ist rechts
                x += distance;
                break;
            case 2: // Agent fährt nach links -> links ist unten
                y += distance;
                break;
            case 3: // Agent fährt nach oben -> links ist links
                x -= distance;
                break;
        }

        return new Point2D.Double(x, y);
    }

    // -----------------------------------------------------------------------
    /**
     * Gibt die aktuelle Bewegungsrichtung des Agenten zurück.
     *
     * @return aktuelle Bewegungsrichtung
     */
    public int getDirection() {
        return this.direction;
    }

    // -----------------------------------------------------------------------
    /**
     * Gibt das aktuelle Erscheinungsbild des Agenten zurück
     * Das Bild wird anhand der aktuellen Blickrichtung ausgewählt.
     * 
     * @return Bild, das zur aktuellen Blickrichtung des Agenten gehört
     */
    public BufferedImage getAppearance() {
        return this.images[getDirection()];
    }

}