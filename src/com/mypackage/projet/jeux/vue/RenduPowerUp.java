package com.mypackage.projet.jeux.vue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mypackage.projet.jeux.modele.entites.Entite;
import com.mypackage.projet.jeux.modele.entites.PowerUp;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe de rendu des power-ups
 */
public class RenduPowerUp implements RenduEntite {
    
    private Map<PowerUp.TypePowerUp, Texture> texturesPowerUps;
    private float tempsAnimation;
    
    public RenduPowerUp() {
        texturesPowerUps = new HashMap<>();
        tempsAnimation = 0;
    }
    
    /**
     * Charge les textures des power-ups
     */
    public void chargerTextures() {
        try {
            texturesPowerUps.put(PowerUp.TypePowerUp.CHAMPIGNON_MAGIQUE, 
                new Texture("assets/textures/mushroom_super.png"));
            texturesPowerUps.put(PowerUp.TypePowerUp.FLEUR_DE_FEU, 
                new Texture("assets/textures/fire_flower.png"));
            texturesPowerUps.put(PowerUp.TypePowerUp.CHAMPIGNON_1UP, 
                new Texture("assets/textures/mushroom_1up.png"));
            texturesPowerUps.put(PowerUp.TypePowerUp.SUPER_ETOILE, 
                new Texture("assets/textures/star.png"));
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors du chargement des textures power-ups : " + e.getMessage());
            // Utiliser des textures par défaut ou créer des sprites de base
        }
    }
    
    @Override
    public void dessiner(SpriteBatch batch, Entite entite) {
        if (!(entite instanceof PowerUp)) return;
        PowerUp powerUp = (PowerUp) entite;
        
        if (!powerUp.estActive() || powerUp.estCollecte()) {
            return;
        }
        
        tempsAnimation += 0.016f; // ~60 FPS
        
        Texture texture = texturesPowerUps.get(powerUp.getTypePowerUp());
        if (texture != null) {
            // Animation de flottement pour les power-ups
            float offsetY = 0;
            if (powerUp.getTypePowerUp() == PowerUp.TypePowerUp.FLEUR_DE_FEU || 
                powerUp.getTypePowerUp() == PowerUp.TypePowerUp.SUPER_ETOILE) {
                offsetY = (float) Math.sin(tempsAnimation * 3) * 3f;
            }
            
            // Animation de rotation pour l'étoile
            if (powerUp.getTypePowerUp() == PowerUp.TypePowerUp.SUPER_ETOILE) {
                float rotation = tempsAnimation * 120f; // Rotation rapide
                batch.draw(texture,
                    powerUp.getPosition().x,
                    powerUp.getPosition().y + offsetY,
                    powerUp.getLargeur() / 2, // Point de rotation au centre
                    powerUp.getHauteur() / 2,
                    powerUp.getLargeur(),
                    powerUp.getHauteur(),
                    1f, 1f,
                    rotation,
                    0, 0,
                    texture.getWidth(),
                    texture.getHeight(),
                    false, false);
            } else {
                batch.draw(texture,
                    powerUp.getPosition().x,
                    powerUp.getPosition().y + offsetY,
                    powerUp.getLargeur(),
                    powerUp.getHauteur());
            }
        } else {
            // Rendu de secours si texture non chargée
            // (sera dessiné en couleur par le debug renderer si activé)
        }
    }
    
    @Override
    public void libererRessources() {
        for (Texture texture : texturesPowerUps.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        texturesPowerUps.clear();
    }
}

