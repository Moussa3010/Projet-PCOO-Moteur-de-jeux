package com.mypackage.projet.jeux.modele.entites;

import com.mypackage.projet.jeux.modele.comportements.ComportementEnnemi;

/**
 * Classe abstraite représentant un ennemi dans le jeu
 */
public abstract class Ennemi extends Entite {
    
    protected int pointsVie;
    protected int degats;
    protected int pointsScore;
    protected ComportementEnnemi comportement;
    protected String type;
    protected float limiteGauche;
    protected float limiteDroite;
    
    /**
     * Constructeur de l'ennemi
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param largeur Largeur de l'ennemi
     * @param hauteur Hauteur de l'ennemi
     * @param type Type de l'ennemi
     */
    public Ennemi(float x, float y, float largeur, float hauteur, String type) {
        super(x, y, largeur, hauteur);
        this.type = type;
        this.pointsVie = 1;
        this.degats = 1;
        this.pointsScore = 100;
        // Pas de limites par défaut
        this.limiteGauche = Float.NEGATIVE_INFINITY;
        this.limiteDroite = Float.POSITIVE_INFINITY;
    }
    
    @Override
    public void mettreAJour(float deltaTemps) {
        if (!active) {
            return;
        }
        
        // Exécuter le comportement de l'ennemi
        if (comportement != null) {
            comportement.executer(this, deltaTemps);
        }
        
        // Calculer la nouvelle position
        float nouvelleX = position.x + vitesse.x * deltaTemps;
        
        // Vérifier les limites de déplacement (empêcher de dépasser le château)
        if (nouvelleX < limiteGauche) {
            nouvelleX = limiteGauche;
            inverserDirection();
        } else if (nouvelleX > limiteDroite) {
            nouvelleX = limiteDroite;
            inverserDirection();
        }
        
        // Mettre à jour la position
        position.x = nouvelleX;
        position.y += vitesse.y * deltaTemps;
        
        // Mettre à jour la boîte de collision
        mettreAJourBoiteCollision();
    }
    
    /**
     * Inflige des dégâts à l'ennemi
     * @param degats Nombre de points de vie à retirer
     */
    public void subirDegats(int degats) {
        this.pointsVie -= degats;
        if (this.pointsVie <= 0) {
            mourir();
        }
    }
    
    /**
     * Gère la mort de l'ennemi
     */
    protected void mourir() {
        this.active = false;
    }
    
    /**
     * Inverse la direction de l'ennemi
     */
    public void inverserDirection() {
        vitesse.x = -vitesse.x;
    }
    
    // Getters et Setters
    public int getPointsVie() {
        return pointsVie;
    }
    
    public void setPointsVie(int pointsVie) {
        this.pointsVie = pointsVie;
    }
    
    public int getDegats() {
        return degats;
    }
    
    public void setDegats(int degats) {
        this.degats = degats;
    }
    
    public int getPointsScore() {
        return pointsScore;
    }
    
    public void setPointsScore(int pointsScore) {
        this.pointsScore = pointsScore;
    }
    
    public ComportementEnnemi getComportement() {
        return comportement;
    }
    
    public void setComportement(ComportementEnnemi comportement) {
        this.comportement = comportement;
    }
    
    public String getType() {
        return type;
    }
    
    /**
     * Définit les limites de déplacement de l'ennemi
     * @param limiteGauche Position X minimale
     * @param limiteDroite Position X maximale
     */
    public void definirLimites(float limiteGauche, float limiteDroite) {
        this.limiteGauche = limiteGauche;
        this.limiteDroite = limiteDroite;
    }
    
    public float getLimiteGauche() {
        return limiteGauche;
    }
    
    public void setLimiteGauche(float limiteGauche) {
        this.limiteGauche = limiteGauche;
    }
    
    public float getLimiteDroite() {
        return limiteDroite;
    }
    
    public void setLimiteDroite(float limiteDroite) {
        this.limiteDroite = limiteDroite;
    }
}
