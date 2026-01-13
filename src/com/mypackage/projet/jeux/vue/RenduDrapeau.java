package com.mypackage.projet.jeux.vue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mypackage.projet.jeux.modele.entites.Drapeau;
import com.mypackage.projet.jeux.modele.entites.Entite;

/**
 * Classe responsable du rendu du drapeau de fin de niveau
 */
public class RenduDrapeau implements RenduEntite {
    
    private Texture textureDrapeau;
    private Texture textureMat;
    private Texture textureBase;
    private Texture textureChateau;
    
    /**
     * Constructeur
     */
    public RenduDrapeau() {
        chargerTextures();
    }
    
    /**
     * Charge toutes les textures du drapeau
     */
    private void chargerTextures() {
        try {
            textureDrapeau = new Texture("assets/textures/flag.png");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors du chargement de la texture du drapeau : " + e.getMessage());
        }
        
        try {
            textureMat = new Texture("assets/textures/flag_pole.png");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors du chargement de la texture du mât");
        }
        
        try {
            textureBase = new Texture("assets/textures/flag_pole_base.png");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors du chargement de la texture de la base");
        }
        
        try {
            textureChateau = new Texture("assets/textures/castle.png");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors du chargement de la texture du château");
        }
    }
    
    @Override
    public void dessiner(SpriteBatch batch, Entite entite) {
        if (!(entite instanceof Drapeau)) {
            return;
        }
        
        Drapeau drapeau = (Drapeau) entite;
        
        float x = drapeau.getPosition().x;
        float y = drapeau.getPosition().y;
        float hauteurMat = drapeau.getHauteurMat();
        
        // ========== ORDRE DE RENDU (du fond au premier plan) ==========
        
        // NE PAS DESSINER LE CHÂTEAU ICI - il sera dessiné séparément pour le Z-index
        
        // 2️⃣ MÂT (Plan intermédiaire)
        if (textureMat != null) {
            batch.draw(textureMat, 
                      x + 12, // Centrer le mât
                      y, 
                      8, 
                      hauteurMat);
        }
        
        // 3️⃣ BASE DU MÂT (Plan intermédiaire)
        if (textureBase != null) {
            batch.draw(textureBase, 
                      x + 8, 
                      y - 8, 
                      16, 
                      16);
        }
        
        // 4️⃣ DRAPEAU (Premier plan - dessiné EN DERNIER pour être AU-DESSUS)
        float drapeauX = x + 20; // À droite du mât
        float drapeauY;
        
        if (drapeau.estTouche()) {
            // Animation de descente du drapeau
            float tempsAnimation = drapeau.getTempsAnimation();
            float progressionDescente = Math.min(1.0f, tempsAnimation / 2.0f); // 2 secondes pour descendre
            drapeauY = y + hauteurMat - 32 - (progressionDescente * (hauteurMat - 40));
        } else {
            // Drapeau en haut du mât
            drapeauY = y + hauteurMat - 32;
        }
        
        // Dessiner le drapeau avec effet de flottement
        if (textureDrapeau != null) {
            float offsetFlottement = (float) Math.sin(System.currentTimeMillis() / 200.0) * 2;
            batch.draw(textureDrapeau, 
                      drapeauX + offsetFlottement, 
                      drapeauY, 
                      32, 
                      32);
        }
    }
    
    /**
     * Dessine le château (à appeler séparément pour contrôler le Z-index)
     * @param batch Le SpriteBatch
     * @param drapeau Le drapeau (pour obtenir la position)
     */
    public void dessinerChateau(SpriteBatch batch, Drapeau drapeau) {
        if (drapeau == null || textureChateau == null) {
            return;
        }
        
        float x = drapeau.getPosition().x;
        float y = drapeau.getPosition().y;
        
        // Dessiner le château à gauche du drapeau
        batch.draw(textureChateau, 
                  x - 150, // À gauche du drapeau
                  y, 
                  128, 
                  96);
    }
    
    @Override
    public void libererRessources() {
        if (textureDrapeau != null) {
            textureDrapeau.dispose();
        }
        if (textureMat != null) {
            textureMat.dispose();
        }
        if (textureBase != null) {
            textureBase.dispose();
        }
        if (textureChateau != null) {
            textureChateau.dispose();
        }
    }
}

