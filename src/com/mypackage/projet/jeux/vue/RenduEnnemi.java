package com.mypackage.projet.jeux.vue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mypackage.projet.jeux.modele.entites.Ennemi;
import com.mypackage.projet.jeux.modele.entites.Entite;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsable du rendu des ennemis avec animations
 * Utilise les sprites classiques de Mario : Goomba
 */
public class RenduEnnemi implements RenduEntite {
    
    private Map<String, Texture> texturesEnnemis;
    private Map<String, Animation<TextureRegion>> animationsEnnemis;
    private float tempsEcoule = 0f;
    
    /**
     * Constructeur
     */
    public RenduEnnemi() {
        texturesEnnemis = new HashMap<>();
        animationsEnnemis = new HashMap<>();
    }
    
    /**
     * Charge une texture pour un type d'ennemi et crée son animation
     * @param type Type de l'ennemi
     * @param cheminTexture Chemin vers la texture
     */
    public void chargerTexture(String type, String cheminTexture) {
        try {
            Texture texture = new Texture(cheminTexture);
            texturesEnnemis.put(type, texture);
            
            // Créer une animation simple de marche
            Array<TextureRegion> frames = new Array<>();
            TextureRegion region = new TextureRegion(texture);
            frames.add(region);
            
            // Animation avec légère oscillation (simule la marche)
            Animation<TextureRegion> animation = new Animation<>(0.15f, frames, Animation.PlayMode.LOOP);
            animationsEnnemis.put(type, animation);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de l'ennemi " + type + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void dessiner(SpriteBatch batch, Entite entite) {
        if (!(entite instanceof Ennemi)) {
            return;
        }
        
        Ennemi ennemi = (Ennemi) entite;
        if (!ennemi.estActive()) {
            return;
        }
        
        // Mettre à jour le temps d'animation
        tempsEcoule += 0.016f; // ~60 FPS
        
        Animation<TextureRegion> animation = animationsEnnemis.get(ennemi.getType());
        if (animation != null) {
            TextureRegion frame = animation.getKeyFrame(tempsEcoule, true);
            
            // Déterminer la direction de l'ennemi pour le flip horizontal
            boolean regardeAGauche = ennemi.getVitesse().x < 0;
            
            // Flip si nécessaire
            if (regardeAGauche && !frame.isFlipX()) {
                frame.flip(true, false);
            } else if (!regardeAGauche && frame.isFlipX()) {
                frame.flip(true, false);
            }
            
            // Effet de légère oscillation verticale pour simuler la marche
            float offsetY = (float) Math.sin(tempsEcoule * 8) * 1.5f;
            
            batch.draw(frame,
                      ennemi.getPosition().x,
                      ennemi.getPosition().y + offsetY,
                      ennemi.getLargeur(),
                      ennemi.getHauteur());
        }
    }
    
    @Override
    public void libererRessources() {
        for (Texture texture : texturesEnnemis.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        texturesEnnemis.clear();
    }
}


