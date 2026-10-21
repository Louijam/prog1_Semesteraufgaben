package game;

import engine.Agent;

/**
 * Konkreter Agent.
 *
 * Die Klasse erbt die Bewegungs- und Sensor-Methoden von {@link Agent}.
 * Die eigentliche Bewegungsstrategie wird in {@link #act()} implementiert.
 */
public class Player extends Agent {

    /**
     * Wird von der Spiel-Engine bei jedem Update aufgerufen.
     *
     * In dieser Methode kann  festgelegt werden, wie sich der 
     * Agent abhängig von seiner Umgebung verhält.
     */
    @Override
    public void act() {
        // ...
    }
}
