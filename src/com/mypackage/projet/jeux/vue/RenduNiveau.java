package com.mypackage.projet.jeux.vue;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mypackage.projet.jeux.modele.entites.Drapeau;
import com.mypackage.projet.jeux.modele.entites.Ennemi;
import com.mypackage.projet.jeux.modele.entites.ObjetCollectable;
import com.mypackage.projet.jeux.modele.entites.PowerUp;
import com.mypackage.projet.jeux.modele.niveau.Niveau;

/**
 * Classe responsable du rendu d'un niveau complet
 */
public class RenduNiveau {
    
    private OrthogonalTiledMapRenderer rendeurCarte;
    private RenduJoueur rendeurJoueur;
    private RenduEnnemi rendeurEnnemi;
    private RenduObjet rendeurObjet;
    private RenduPowerUp rendeurPowerUp;
    private RenduDrapeau rendeurDrapeau;
    private RenduHUD rendeurHUD;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    
    /**
     * Constructeur
     * @param camera La caméra du jeu
     */
    public RenduNiveau(OrthographicCamera camera) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.rendeurJoueur = new RenduJoueur("assets/textures/joueur.png");
        this.rendeurEnnemi = new RenduEnnemi();
        this.rendeurObjet = new RenduObjet();
        this.rendeurPowerUp = new RenduPowerUp();
        this.rendeurDrapeau = new RenduDrapeau();
        this.rendeurHUD = new RenduHUD();
        
        // Charger les textures des ennemis classiques de Mario
        rendeurEnnemi.chargerTexture("terrestre", "assets/textures/goomba.png");
        
        // Charger les textures des objets
        rendeurObjet.chargerTexture("PIECE", "assets/textures/piece.png");
        rendeurObjet.chargerTexture("CHAMPIGNON", "assets/textures/champignon.png");
        
        // Charger les textures des power-ups
        rendeurPowerUp.chargerTextures();
    }
    
    /**
     * Initialise le rendu pour un niveau donné
     * @param niveau Le niveau à rendre
     */
    public void initialiserPourNiveau(Niveau niveau) {
        TiledMap carte = niveau.getCarte();
        if (carte != null) {
            rendeurCarte = new OrthogonalTiledMapRenderer(carte);
        }
    }
    
    /**
     * Dessine le niveau complet
     * OPTIMISATION : Culling de la caméra - ne dessine que ce qui est visible
     * @param niveau Le niveau à dessiner
     */
    public void dessiner(Niveau niveau) {
        // NOTE : La caméra est mise à jour dans JeuPlateforme.java avec clamping aux limites
        // Ne pas la mettre à jour ici pour éviter de montrer du vide aux bords du niveau
        
        // Dessiner la carte Tiled
        if (rendeurCarte != null) {
            rendeurCarte.setView(camera);
            rendeurCarte.render();
        }
        
        // Calculer les limites de la caméra pour le culling
        float cameraLeft = camera.position.x - camera.viewportWidth / 2;
        float cameraRight = camera.position.x + camera.viewportWidth / 2;
        float cameraBottom = camera.position.y - camera.viewportHeight / 2;
        float cameraTop = camera.position.y + camera.viewportHeight / 2;
        
        // Dessiner les entités
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Dessiner le drapeau de fin (en arrière-plan)
        if (niveau.getDrapeau() != null) {
            rendeurDrapeau.dessiner(batch, niveau.getDrapeau());
        }
        
        // Dessiner le joueur
        if (niveau.getJoueur() != null) {
            rendeurJoueur.dessiner(batch, niveau.getJoueur());
        }
        
        // Dessiner les ennemis (CULLING : seulement si visibles)
        for (Ennemi ennemi : niveau.getEnnemis()) {
            if (ennemi.estActive() && estVisible(ennemi, cameraLeft, cameraRight, cameraBottom, cameraTop)) {
                rendeurEnnemi.dessiner(batch, ennemi);
            }
        }
        
        // Dessiner les objets collectables (CULLING : seulement si visibles)
        for (ObjetCollectable objet : niveau.getObjetsCollectables()) {
            if (objet.estActive() && estVisible(objet, cameraLeft, cameraRight, cameraBottom, cameraTop)) {
                rendeurObjet.dessiner(batch, objet);
            }
        }
        
        // Dessiner les power-ups (CULLING : seulement si visibles)
        for (PowerUp powerUp : niveau.getPowerUps()) {
            if (powerUp.estActive() && estVisible(powerUp, cameraLeft, cameraRight, cameraBottom, cameraTop)) {
                rendeurPowerUp.dessiner(batch, powerUp);
            }
        }
        
        // Dessiner le CHÂTEAU en dernier (PREMIER PLAN) pour qu'il passe devant Mario
        if (niveau.getDrapeau() != null) {
            rendeurDrapeau.dessinerChateau(batch, niveau.getDrapeau());
        }
        
        batch.end();
        
        // RENDU DE DEBUG DÉSACTIVÉ : Les vrais sprites sont maintenant visibles !
        // Les rectangles de couleur cachaient les sprites Mario et Goombas
        /* DEBUG MODE OFF
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Rectangle ROUGE pour le joueur
        if (niveau.getJoueur() != null) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(
                niveau.getJoueur().getPosition().x,
                niveau.getJoueur().getPosition().y,
                niveau.getJoueur().getLargeur(),
                niveau.getJoueur().getHauteur()
            );
        }
        
        // Rectangles VERTS pour les ennemis
        shapeRenderer.setColor(Color.GREEN);
        for (Ennemi ennemi : niveau.getEnnemis()) {
            if (ennemi.estActive()) {
                shapeRenderer.rect(
                    ennemi.getPosition().x,
                    ennemi.getPosition().y,
                    ennemi.getLargeur(),
                    ennemi.getHauteur()
                );
            }
        }
        
        // Rectangles JAUNES pour les objets
        shapeRenderer.setColor(Color.YELLOW);
        for (ObjetCollectable objet : niveau.getObjetsCollectables()) {
            if (objet.estActive()) {
                shapeRenderer.rect(
                    objet.getPosition().x,
                    objet.getPosition().y,
                    objet.getLargeur(),
                    objet.getHauteur()
                );
            }
        }
        
        shapeRenderer.end();
        */
    }
    
    /**
     * Dessine le HUD pour le niveau
     * @param niveau Le niveau
     * @param numeroNiveau Le numéro du niveau
     */
    public void dessinerHUD(Niveau niveau, int numeroNiveau) {
        if (niveau.getJoueur() != null) {
            rendeurHUD.dessinerAvecNiveau(batch, niveau.getJoueur(), niveau, numeroNiveau);
        }
    }
    
    /**
     * Vérifie si une entité est visible dans la caméra (pour le culling)
     * @param entite L'entité à vérifier
     * @param left Limite gauche de la caméra
     * @param right Limite droite de la caméra
     * @param bottom Limite basse de la caméra
     * @param top Limite haute de la caméra
     * @return true si l'entité est visible
     */
    private boolean estVisible(com.mypackage.projet.jeux.modele.entites.Entite entite, 
                                float left, float right, float bottom, float top) {
        float entityRight = entite.getPosition().x + entite.getLargeur();
        float entityTop = entite.getPosition().y + entite.getHauteur();
        
        // Vérifier si l'entité est dans les limites de la caméra
        return !(entityRight < left || entite.getPosition().x > right ||
                 entityTop < bottom || entite.getPosition().y > top);
    }
    
    /**
     * Retourne le SpriteBatch pour d'autres rendus
     * @return Le SpriteBatch
     */
    public SpriteBatch getBatch() {
        return batch;
    }
    
    /**
     * Retourne le rendu HUD
     * @return Le RenduHUD
     */
    public RenduHUD getRendeurHUD() {
        return rendeurHUD;
    }
    
    /**
     * Libère toutes les ressources
     */
    public void libererRessources() {
        if (batch != null) {
            batch.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (rendeurCarte != null) {
            rendeurCarte.dispose();
        }
        if (rendeurJoueur != null) {
            rendeurJoueur.libererRessources();
        }
        if (rendeurEnnemi != null) {
            rendeurEnnemi.libererRessources();
        }
        if (rendeurObjet != null) {
            rendeurObjet.libererRessources();
        }
        if (rendeurPowerUp != null) {
            rendeurPowerUp.libererRessources();
        }
        if (rendeurDrapeau != null) {
            rendeurDrapeau.libererRessources();
        }
        if (rendeurHUD != null) {
            rendeurHUD.libererRessources();
        }
    }
}


