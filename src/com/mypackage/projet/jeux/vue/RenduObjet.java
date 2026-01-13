package com.mypackage.projet.jeux.vue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mypackage.projet.jeux.modele.entites.Entite;
import com.mypackage.projet.jeux.modele.entites.ObjetCollectable;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsable du rendu des objets collectables avec animations
 */
public class RenduObjet implements RenduEntite {
    
    private Map<String, Texture> texturesObjets;
    private Animation<TextureRegion> animationPiece;
    private Texture spritesheetPiece;
    private float tempsAnimation;
    
    /**
     * Constructeur
     */
    public RenduObjet() {
        texturesObjets = new HashMap<>();
        tempsAnimation = 0;
    }
    
    /**
     * Charge une texture pour un type d'objet
     * @param type Type de l'objet
     * @param cheminTexture Chemin vers la texture
     */
    public void chargerTexture(String type, String cheminTexture) {
        try {
            // Si c'est une pièce, charger l'animation
            if ("PIECE".equalsIgnoreCase(type)) {
                chargerAnimationPiece();
            } else {
            Texture texture = new Texture(cheminTexture);
            texturesObjets.put(type, texture);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la texture de l'objet " + type + " : " + e.getMessage());
        }
    }
    
    /**
     * Charge l'animation de la pièce (rotation)
     */
    private void chargerAnimationPiece() {
        try {
            spritesheetPiece = new Texture("assets/textures/coin_spritesheet.png");
            
            // Découper la spritesheet (4 frames de 24x24)
            TextureRegion[][] tmp = TextureRegion.split(spritesheetPiece, 24, 24);
            Array<TextureRegion> frames = new Array<>();
            for (int i = 0; i < 4; i++) {
                frames.add(tmp[0][i]);
            }
            
            // Créer l'animation (0.15s par frame = rotation fluide)
            animationPiece = new Animation<>(0.15f, frames, Animation.PlayMode.LOOP);
        } catch (Exception e) {
            System.err.println("⚠️  Erreur lors du chargement de l'animation de pièce : " + e.getMessage());
        }
    }
    
    @Override
    public void dessiner(SpriteBatch batch, Entite entite) {
        if (!(entite instanceof ObjetCollectable)) {
            return;
        }
        
        ObjetCollectable objet = (ObjetCollectable) entite;
        if (!objet.estActive()) {
            return;
        }
        
        tempsAnimation += 0.016f; // ~60 FPS
        
        // Si c'est une pièce, utiliser l'animation
        if ("PIECE".equalsIgnoreCase(objet.getType()) && animationPiece != null) {
            TextureRegion frame = animationPiece.getKeyFrame(tempsAnimation, true);
            
            // Effet de flottement vertical (comme dans Mario)
            float offsetY = (float) Math.sin(tempsAnimation * 2) * 3f;
            
            batch.draw(frame,
                      objet.getPosition().x,
                      objet.getPosition().y + offsetY,
                      objet.getLargeur(),
                      objet.getHauteur());
        } else {
            // Autres objets : rendu normal
        Texture texture = texturesObjets.get(objet.getType());
        if (texture != null) {
            batch.draw(texture,
                      objet.getPosition().x,
                      objet.getPosition().y,
                      objet.getLargeur(),
                      objet.getHauteur());
            }
        }
    }
    
    @Override
    public void libererRessources() {
        for (Texture texture : texturesObjets.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        if (spritesheetPiece != null) {
            spritesheetPiece.dispose();
        }
        texturesObjets.clear();
    }
}


