package com.mypackage.projet.jeux.modele.entites;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Classe abstraite représentant une entité du jeu.
 * Toutes les entités (joueur, ennemis, objets) héritent de cette classe.
 */
public abstract class Entite {
    
    protected Vector2 position;
    protected Vector2 vitesse;
    protected float largeur;
    protected float hauteur;
    protected boolean active;
    protected Rectangle boiteCollision;
    
    /**
     * Constructeur de l'entité
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param largeur Largeur de l'entité
     * @param hauteur Hauteur de l'entité
     */
    public Entite(float x, float y, float largeur, float hauteur) {
        this.position = new Vector2(x, y);
        this.vitesse = new Vector2(0, 0);
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.active = true;
        this.boiteCollision = new Rectangle(x, y, largeur, hauteur);
    }
    
    /**
     * Méthode abstraite de mise à jour de l'entité
     * @param deltaTemps Temps écoulé depuis la dernière frame
     */
    public abstract void mettreAJour(float deltaTemps);
    
    /**
     * Met à jour la boîte de collision en fonction de la position
     */
    protected void mettreAJourBoiteCollision() {
        boiteCollision.setPosition(position.x, position.y);
    }
    
    /**
     * Vérifie si cette entité entre en collision avec une autre
     * @param autre L'autre entité
     * @return true si collision, false sinon
     */
    public boolean entreEnCollisionAvec(Entite autre) {
        return this.boiteCollision.overlaps(autre.getBoiteCollision());
    }
    
    // Getters et Setters
    public Vector2 getPosition() {
        return position;
    }
    
    public void setPosition(float x, float y) {
        this.position.set(x, y);
        mettreAJourBoiteCollision();
    }
    
    public Vector2 getVitesse() {
        return vitesse;
    }
    
    public void setVitesse(float vx, float vy) {
        this.vitesse.set(vx, vy);
    }
    
    public float getLargeur() {
        return largeur;
    }
    
    public float getHauteur() {
        return hauteur;
    }
    
    public boolean estActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Rectangle getBoiteCollision() {
        return boiteCollision;
    }
}
