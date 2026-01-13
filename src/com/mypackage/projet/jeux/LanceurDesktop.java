package com.mypackage.projet.jeux;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Classe de lancement pour la version Desktop du jeu
 */
public class LanceurDesktop {
    
    /**
     * Point d'entrée principal de l'application
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        // Configuration de la fenêtre
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        
        // Titre de la fenêtre
        config.setTitle("Jeu de Plateforme - Moteur 2D avec LibGDX");
        
        // Dimensions de la fenêtre
        config.setWindowedMode(800, 600);
        
        // Activer la synchronisation verticale (limite automatiquement les FPS)
        config.useVsync(true);
        
        // Lancer l'application
        new Lwjgl3Application(new JeuPlateforme(), config);
    }
}


