package com.mypackage.projet.jeux.modele.entites;

/**
 * Classe représentant un objet que le joueur peut collecter (pièces, power-ups, etc.)
 */
public class ObjetCollectable extends Entite {
    
    private String type;
    private int valeur;
    private boolean collecte;
    
    /**
     * Énumération des types d'objets collectables
     */
    public enum TypeObjet {
        PIECE,
        CHAMPIGNON,
        FLEUR,
        ETOILE
    }
    
    /**
     * Constructeur de l'objet collectable
     * @param x Position X
     * @param y Position Y
     * @param type Type de l'objet
     * @param valeur Valeur de l'objet (points, vie, etc.)
     */
    public ObjetCollectable(float x, float y, String type, int valeur) {
        super(x, y, 24, 24);
        this.type = type;
        this.valeur = valeur;
        this.collecte = false;
    }
    
    @Override
    public void mettreAJour(float deltaTemps) {
        if (!active || collecte) {
            return;
        }
        
        // Animation simple de rotation ou de flottement
        // Peut être étendu selon les besoins
    }
    
    /**
     * Marque l'objet comme collecté
     */
    public void collecter() {
        this.collecte = true;
        this.active = false;
    }
    
    // Getters
    public String getType() {
        return type;
    }
    
    public int getValeur() {
        return valeur;
    }
    
    public boolean estCollecte() {
        return collecte;
    }
}
