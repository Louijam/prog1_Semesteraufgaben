package game;

import engine.*;

/**
 * Konkreter Agent.
 *
 * Die Klasse erbt die Bewegungs- und Sensor-Methoden von {@link Agent}.
 * Die eigentliche Bewegungsstrategie wird in {@link #act()} implementiert.
 */
public class Player extends Agent {
    private boolean straight, left, right = false;


    /**
     * Wird von der Spiel-Engine bei jedem Update aufgerufen.
     *
     * In dieser Methode kann  festgelegt werden, wie sich der 
     * Agent abhängig von seiner Umgebung verhält.
     */
    @Override
    public void act() {
        if (!isOnGoal()){
            scan();

            if(right){
                turnRight();
                move();
            }

            else if (straight){
                move();
            }

            else if(left){
                turnLeft();
                move();
            }

            else {
                flip();
            }

        }


    }
    private void flip(){
        turnLeft();
        turnLeft();
    }

    private void scan(){
        straight = look(1) == Tile.PATH;
        left = lookLeft() == Tile.PATH;
        right = lookRight() == Tile.PATH;

    }

}
