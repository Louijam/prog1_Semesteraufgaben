package aufgabe_01;

import aufgabe_01.engine.Screen;
import aufgabe_01.engine.Agent;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import aufgabe_01.game.Player;
import de.gurkenlabs.litiengine.Game;


/**
 * Startet das Spiel.
 */
public class Program {
       
    // -----------------------------------------------------------------------
    /**
     * Einstiegspunkt des Spiels.
     * - initialisiert die Engine, 
     * - konfiguriert das Spielfenster,
     * - und startet anschließend das Spiel.
     */
    public static void main(String[] args) {

        // Level-Datei für das Spielfeld und die Mission festlegen.
        // Dateiname kann beim Start als Argument übergeben werden
        String levelFile = "level/reachGoal_1.txt";
        if (args.length > 0) {
            levelFile = args[0];
        }   

        // Agenten für das Spiel erzeugen.
        Agent agent = new Player();

        // Agenten in einer Liste zusammenfassen.
        // Die Liste kann später mehrere Agenten enthalten.
        List<Agent> agents = new ArrayList<>();
        agents.add(agent);

        // Engine initialisieren.
        Game.init();

        // Größe des Spielfensters festlegen.
        Game.config().graphics().setResolutionWidth(1000);
        Game.config().graphics().setResolutionHeight(600);

        // Bereich festlegen, in dem sich die Spielfiguren bewegen dürfen.
        Game.physics().setBounds(
            new Rectangle2D.Double(0, 0, 1000, 600)
        );

        // Spielbildschirm erzeugen und bei der Engine registrieren.
        // Der Screen erhält das Level, die Agenten und die Mission.
        Game.screens().add(
            new Screen(levelFile, agents)
        );

        // Spielgeschwindigkeit reduzieren, um die Bewegungen
        // der Agenten während der Entwicklung besser beobachten zu können.
        Game.loop().setTimeScale(0.1f);

        // Game-Loop starten.
        Game.start();
    }
}