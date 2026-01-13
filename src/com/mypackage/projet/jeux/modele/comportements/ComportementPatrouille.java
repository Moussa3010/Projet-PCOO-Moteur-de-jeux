package com.mypackage.projet.jeux.modele.comportements;

import com.mypackage.projet.jeux.modele.entites.Ennemi;

/**
 * Comportement de patrouille : l'ennemi se déplace de gauche à droite
 */
public class ComportementPatrouille implements ComportementEnnemi {
    
    private float distancePatrouille;
    private float positionXInitiale;
    private boolean initialisee;
    
    /**
     * Constructeur
     * @param distancePatrouille Distance de patrouille de chaque côté
     */
    public ComportementPatrouille(float distancePatrouille) {
        this.distancePatrouille = distancePatrouille;
        this.initialisee = false;
    }
    
    @Override
    public void executer(Ennemi ennemi, float deltaTemps) {
        // Initialiser la position de départ
        if (!initialisee) {
            positionXInitiale = ennemi.getPosition().x;
            initialisee = true;
        }
        
        // Vérifier si l'ennemi a atteint les limites de patrouille
        float distanceParcourue = Math.abs(ennemi.getPosition().x - positionXInitiale);
        if (distanceParcourue >= distancePatrouille) {
            ennemi.inverserDirection();
        }
    }
}
