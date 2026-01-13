package com.mypackage.projet.jeux.controleur;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mypackage.projet.jeux.modele.entites.Joueur;
import com.mypackage.projet.jeux.modele.niveau.Niveau;

/**
 * Classe gérant les entrées clavier pour contrôler le joueur
 */
public class ControleurEntrees {
    
    /**
     * Constructeur
     */
    public ControleurEntrees() {
    }
    
    /**
     * Gère les entrées utilisateur pour le joueur
     * @param joueur Le joueur à contrôler
     */
    public void gererEntrees(Joueur joueur) {
        gererEntrees(joueur, null);
    }
    
    /**
     * Gère les entrées utilisateur pour le joueur
     * @param joueur Le joueur à contrôler
     * @param niveau Le niveau actuel
     */
    public void gererEntrees(Joueur joueur, Niveau niveau) {
        if (joueur == null || !joueur.estActive()) {
            return;
        }
        
        boolean deplacementGauche = false;
        boolean deplacementDroite = false;
        
        // Déplacement à gauche
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.Q)) {
            joueur.deplacerGauche();
            deplacementGauche = true;
        }
        
        // Déplacement à droite
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            joueur.deplacerDroite();
            deplacementDroite = true;
        }
        
        // Si aucun déplacement horizontal, arrêter le mouvement
        if (!deplacementGauche && !deplacementDroite) {
            joueur.arreterDeplacementHorizontal();
        }
        
        // Saut - NOUVEAU : Appui
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || 
            Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            joueur.sauter();
        }
        
        // NOUVEAU : Relâchement du saut (pour saut variable Mario-style)
        if (!Gdx.input.isKeyPressed(Input.Keys.SPACE) && 
            !Gdx.input.isKeyPressed(Input.Keys.UP) &&
            !Gdx.input.isKeyPressed(Input.Keys.Z)) {
            joueur.relacherSaut();
        }
        
    }
}


