package com.mypackage.projet.jeux.modele.entites;

/**
 * Classe représentant un power-up (Champignon, Fleur de Feu, etc.)
 */
public class PowerUp extends Entite {
    
    private TypePowerUp type;
    private boolean collecte;
    private boolean apparait; // Animation d'apparition depuis un bloc
    private float tempsApparition;
    private static final float DUREE_APPARITION = 0.5f; // Durée de l'animation d'apparition
    private static final float VITESSE_DEPLACEMENT = 80f;
    private static final float GRAVITE = -800f;
    
    /**
     * Énumération des types de power-ups
     */
    public enum TypePowerUp {
        CHAMPIGNON_MAGIQUE,  // Fait grandir Mario + 1 vie
        FLEUR_DE_FEU,        // Transforme en Mario Feu
        SUPER_ETOILE,        // Invincibilité temporaire
        CHAMPIGNON_1UP       // Uniquement +1 vie
    }
    
    /**
     * Constructeur du power-up
     * @param x Position X
     * @param y Position Y
     * @param type Type de power-up
     */
    public PowerUp(float x, float y, TypePowerUp type) {
        super(x, y, 32, 32); // Taille standard d'un power-up
        this.type = type;
        this.collecte = false;
        this.apparait = false;
        this.tempsApparition = 0;
        
        // Les champignons se déplacent, les fleurs restent sur place
        if (type == TypePowerUp.CHAMPIGNON_MAGIQUE || type == TypePowerUp.CHAMPIGNON_1UP) {
            this.vitesse.x = VITESSE_DEPLACEMENT;
        }
    }
    
    @Override
    public void mettreAJour(float deltaTemps) {
        if (!active || collecte) {
            return;
        }
        
        // Animation d'apparition
        if (apparait) {
            tempsApparition += deltaTemps;
            if (tempsApparition < DUREE_APPARITION) {
                // Monter lentement du bloc
                position.y += (32 / DUREE_APPARITION) * deltaTemps;
                mettreAJourBoiteCollision();
                return; // Ne pas appliquer la physique pendant l'apparition
            } else {
                apparait = false; // Fin de l'apparition
            }
        }
        
        // Physique pour les champignons (se déplacent)
        if (type == TypePowerUp.CHAMPIGNON_MAGIQUE || type == TypePowerUp.CHAMPIGNON_1UP) {
            // Gravité
            vitesse.y += GRAVITE * deltaTemps;
            
            // Mise à jour position
            position.x += vitesse.x * deltaTemps;
            position.y += vitesse.y * deltaTemps;
            
            // Sol de sécurité
            if (position.y <= 32) {
                position.y = 32;
                vitesse.y = 0;
            }
        }
        
        mettreAJourBoiteCollision();
    }
    
    /**
     * Marque le power-up comme collecté
     */
    public void collecter() {
        this.collecte = true;
        this.active = false;
    }
    
    /**
     * Démarre l'animation d'apparition
     */
    public void commencerApparition() {
        this.apparait = true;
        this.tempsApparition = 0;
    }
    
    /**
     * Inverse la direction du power-up (collision avec obstacle)
     */
    public void inverserDirection() {
        vitesse.x = -vitesse.x;
    }
    
    /**
     * Place le power-up au sol
     * @param y Position Y du sol
     */
    public void placerAuSol(float y) {
        position.y = y;
        vitesse.y = 0;
    }
    
    // Getters
    public TypePowerUp getTypePowerUp() {
        return type;
    }
    
    public boolean estCollecte() {
        return collecte;
    }
    
    public boolean estEnApparition() {
        return apparait;
    }
}

