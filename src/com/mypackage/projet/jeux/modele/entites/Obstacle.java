package com.mypackage.projet.jeux.modele.entites;

/**
 * Classe représentant un obstacle statique (blocs, plateformes, etc.)
 */
public class Obstacle extends Entite {
    
    private String type;
    private boolean destructible;
    private boolean solide;
    
    /**
     * Énumération des types d'obstacles
     */
    public enum TypeObstacle {
        BLOC_NORMAL,
        BLOC_QUESTION,
        BLOC_BRIQUE,
        PLATEFORME
    }
    
    /**
     * Constructeur de l'obstacle
     * @param x Position X
     * @param y Position Y
     * @param largeur Largeur de l'obstacle
     * @param hauteur Hauteur de l'obstacle
     * @param type Type de l'obstacle
     */
    public Obstacle(float x, float y, float largeur, float hauteur, String type) {
        super(x, y, largeur, hauteur);
        this.type = type;
        this.destructible = false;
        this.solide = true;
    }
    
    @Override
    public void mettreAJour(float deltaTemps) {
        // Les obstacles sont généralement statiques
        // Cette méthode peut être étendue pour des obstacles mobiles
    }
    
    /**
     * Détruit l'obstacle s'il est destructible
     */
    public void detruire() {
        if (destructible) {
            this.active = false;
        }
    }
    
    // Getters et Setters
    public String getType() {
        return type;
    }
    
    public boolean estDestructible() {
        return destructible;
    }
    
    public void setDestructible(boolean destructible) {
        this.destructible = destructible;
    }
    
    public boolean estSolide() {
        return solide;
    }
    
    public void setSolide(boolean solide) {
        this.solide = solide;
    }
}
