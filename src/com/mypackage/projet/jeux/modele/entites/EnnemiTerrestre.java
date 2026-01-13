package com.mypackage.projet.jeux.modele.entites;

/**
 * Classe représentant un ennemi qui se déplace au sol (type Goomba)
 */
public class EnnemiTerrestre extends Ennemi {
    
    private static final float VITESSE_DEFAUT = 50f;
    private static final float GRAVITE = -800f;
    
    private boolean auSol;
    
    /**
     * Constructeur de l'ennemi terrestre
     * @param x Position X initiale
     * @param y Position Y initiale
     */
    public EnnemiTerrestre(float x, float y) {
        super(x, y, 32, 32, "terrestre");
        this.vitesse.x = -VITESSE_DEFAUT; // Se déplace vers la gauche par défaut
        this.auSol = false;
    }
    
    @Override
    public void mettreAJour(float deltaTemps) {
        if (!active) {
            return;
        }
        
        // Appliquer la gravité
        vitesse.y += GRAVITE * deltaTemps;
        
        // Appeler la mise à jour de la classe parent
        super.mettreAJour(deltaTemps);
    }
    
    /**
     * Place l'ennemi au sol
     * @param y Position Y du sol
     */
    public void placerAuSol(float y) {
        position.y = y;
        vitesse.y = 0;
        auSol = true;
    }
    
    public boolean estAuSol() {
        return auSol;
    }
    
    public void setAuSol(boolean auSol) {
        this.auSol = auSol;
    }
}
