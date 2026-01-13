package com.mypackage.projet.jeux.modele.niveau;

/**
 * Classe représentant les objectifs d'un niveau
 */
public class ObjectifNiveau {
    
    /**
     * Type d'objectif
     */
    public enum TypeObjectif {
        ATTEINDRE_FIN,          // Atteindre un point précis
        COLLECTER_PIECES,       // Collecter un certain nombre de pièces
        VAINCRE_ENNEMIS,        // Vaincre tous les ennemis
        SURVIVRE_TEMPS,         // Survivre pendant un certain temps
        COLLECTER_CLE           // Trouver une clé spéciale
    }
    
    private TypeObjectif type;
    private int valeurCible;    // Valeur nécessaire pour compléter l'objectif
    private int valeurActuelle;  // Progression actuelle
    private String description;
    private boolean accompli;
    
    /**
     * Constructeur
     * @param type Type d'objectif
     * @param valeurCible Valeur à atteindre
     * @param description Description de l'objectif
     */
    public ObjectifNiveau(TypeObjectif type, int valeurCible, String description) {
        this.type = type;
        this.valeurCible = valeurCible;
        this.description = description;
        this.valeurActuelle = 0;
        this.accompli = false;
    }
    
    /**
     * Met à jour la progression de l'objectif
     * @param valeur Nouvelle valeur
     */
    public void mettreAJourProgression(int valeur) {
        this.valeurActuelle = valeur;
        verifierAccomplissement();
    }
    
    /**
     * Incrémente la progression
     * @param increment Valeur à ajouter
     */
    public void incrementerProgression(int increment) {
        this.valeurActuelle += increment;
        verifierAccomplissement();
    }
    
    /**
     * Vérifie si l'objectif est accompli
     */
    private void verifierAccomplissement() {
        if (valeurActuelle >= valeurCible) {
            this.accompli = true;
        }
    }
    
    /**
     * Réinitialise l'objectif
     */
    public void reinitialiser() {
        this.valeurActuelle = 0;
        this.accompli = false;
    }
    
    /**
     * Retourne la progression en pourcentage
     * @return Pourcentage (0-100)
     */
    public float getProgressionPourcentage() {
        if (valeurCible == 0) return 100;
        return Math.min(100, (valeurActuelle * 100.0f) / valeurCible);
    }
    
    /**
     * Retourne une description formatée de la progression
     * @return Description
     */
    public String getDescriptionProgression() {
        return String.format("%s (%d/%d)", description, valeurActuelle, valeurCible);
    }
    
    // Getters et Setters
    public TypeObjectif getType() {
        return type;
    }
    
    public int getValeurCible() {
        return valeurCible;
    }
    
    public void setValeurCible(int valeurCible) {
        this.valeurCible = valeurCible;
    }
    
    public int getValeurActuelle() {
        return valeurActuelle;
    }
    
    public void setValeurActuelle(int valeurActuelle) {
        this.valeurActuelle = valeurActuelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean estAccompli() {
        return accompli;
    }
    
    public void setAccompli(boolean accompli) {
        this.accompli = accompli;
    }
}

