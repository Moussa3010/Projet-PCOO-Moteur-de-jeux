package com.mypackage.projet.jeux.vue;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mypackage.projet.jeux.modele.entites.Entite;

/**
 * Interface pour le rendu des entités
 */
public interface RenduEntite {
    
    /**
     * Dessine l'entité
     * @param batch Le SpriteBatch pour dessiner
     * @param entite L'entité à dessiner
     */
    void dessiner(SpriteBatch batch, Entite entite);
    
    /**
     * Libère les ressources utilisées
     */
    void libererRessources();
}


