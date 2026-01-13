package com.mypackage.projet.jeux.utilitaires;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;

/**
 * Classe pour gérer le chargement et la libération des ressources du jeu
 */
public class GestionnaireRessources {
    
    private AssetManager gestionnaire;
    
    /**
     * Constructeur
     */
    public GestionnaireRessources() {
        this.gestionnaire = new AssetManager();
    }
    
    /**
     * Charge toutes les ressources nécessaires au jeu
     */
    public void chargerRessources() {
        // Charger les textures
        chargerTexture("assets/textures/joueur.png");
        chargerTexture("assets/textures/ennemi_terrestre.png");
        chargerTexture("assets/textures/piece.png");
        chargerTexture("assets/textures/champignon.png");
        
        // Attendre que toutes les ressources soient chargées
        gestionnaire.finishLoading();
    }
    
    /**
     * Charge une texture
     * @param chemin Chemin vers la texture
     */
    public void chargerTexture(String chemin) {
        try {
            gestionnaire.load(chemin, Texture.class);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la texture " + chemin + " : " + e.getMessage());
        }
    }
    
    /**
     * Récupère une texture chargée
     * @param chemin Chemin de la texture
     * @return La texture
     */
    public Texture obtenirTexture(String chemin) {
        if (gestionnaire.isLoaded(chemin)) {
            return gestionnaire.get(chemin, Texture.class);
        }
        System.err.println("Texture non chargée : " + chemin);
        return null;
    }
    
    /**
     * Met à jour le gestionnaire (pour le chargement asynchrone)
     * @return true si le chargement est terminé
     */
    public boolean mettreAJour() {
        return gestionnaire.update();
    }
    
    /**
     * Obtient le progrès du chargement
     * @return Valeur entre 0 et 1
     */
    public float obtenirProgresChargement() {
        return gestionnaire.getProgress();
    }
    
    /**
     * Libère toutes les ressources
     */
    public void libererRessources() {
        gestionnaire.dispose();
    }
    
    /**
     * Libère une ressource spécifique
     * @param chemin Chemin de la ressource
     */
    public void libererRessource(String chemin) {
        if (gestionnaire.isLoaded(chemin)) {
            gestionnaire.unload(chemin);
        }
    }
}


