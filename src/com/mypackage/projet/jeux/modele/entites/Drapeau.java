package com.mypackage.projet.jeux.modele.entites;

import com.badlogic.gdx.math.Vector2;

/**
 * Classe représentant le drapeau de fin de niveau
 * Quand Mario touche le drapeau, le niveau est terminé
 */
public class Drapeau extends Entite {
    
    private boolean touche;
    private float hauteurMat;
    private float tempsAnimation;
    
    /**
     * Constructeur
     * @param x Position X du drapeau
     * @param y Position Y de la base du mât
     * @param hauteur Hauteur du mât
     */
    public Drapeau(float x, float y, float hauteur) {
        super(x, y, 32, hauteur);
        this.hauteurMat = hauteur;
        this.touche = false;
        this.tempsAnimation = 0;
    }
    
    /**
     * Constructeur par défaut (mât de 160 pixels)
     */
    public Drapeau(float x, float y) {
        this(x, y, 160);
    }
    
    @Override
    public void mettreAJour(float deltaTemps) {
        if (touche) {
            tempsAnimation += deltaTemps;
        }
    }
    
    /**
     * Marque le drapeau comme touché
     */
    public void marquerTouche() {
        this.touche = true;
        this.tempsAnimation = 0;
    }
    
    /**
     * Vérifie si le drapeau a été touché
     */
    public boolean estTouche() {
        return touche;
    }
    
    /**
     * Obtient le temps depuis que le drapeau a été touché
     */
    public float getTempsAnimation() {
        return tempsAnimation;
    }
    
    /**
     * Obtient la hauteur du mât
     */
    public float getHauteurMat() {
        return hauteurMat;
    }
    
    /**
     * Obtient la position du sommet du mât
     */
    public Vector2 getPositionSommet() {
        return new Vector2(position.x + largeur/2, position.y + hauteurMat);
    }
    
    /**
     * Réinitialise le drapeau
     */
    public void reinitialiser() {
        this.touche = false;
        this.tempsAnimation = 0;
    }
    
    @Override
    public String toString() {
        return "Drapeau{" +
                "position=" + position +
                ", hauteur=" + hauteurMat +
                ", touche=" + touche +
                '}';
    }
}

