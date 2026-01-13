package com.mypackage.projet.jeux.controleur;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mypackage.projet.jeux.modele.entites.Joueur;
import com.mypackage.projet.jeux.modele.gestionnaires.GestionnaireCollisions;
import com.mypackage.projet.jeux.modele.gestionnaires.GestionnaireNiveaux;
import com.mypackage.projet.jeux.modele.niveau.Niveau;
import com.mypackage.projet.jeux.utilitaires.SauvegardeProgression;
import com.mypackage.projet.jeux.modele.niveau.ProgressionNiveau;

import java.util.Map;

/**
 * Contrôleur principal du jeu, gère la logique du jeu et les entrées utilisateur
 */
public class ControleurJeu {
    
    private GestionnaireNiveaux gestionnaireNiveaux;
    private GestionnaireCollisions gestionnaireCollisions;
    private ControleurEntrees controleurEntrees;
    private EtatJeu etatActuel;
    private boolean sauvegardeAutomatique;
    
    // Variables pour la séquence de fin de niveau
    private EtapeSequenceFin etapeSequenceFin;
    private float tempsSequence;
    private float positionChateauX;
    
    /**
     * Énumération des états possibles du jeu
     */
    public enum EtatJeu {
        MENU,
        EN_JEU,
        PAUSE,
        GAME_OVER,
        VICTOIRE,
        TRANSITION_NIVEAU,
        RECAPITULATIF_NIVEAU,
        SEQUENCE_FIN_NIVEAU  // Nouvelle séquence animée
    }
    
    /**
     * Énumération des étapes de la séquence de fin de niveau
     */
    public enum EtapeSequenceFin {
        GLISSADE_DRAPEAU,      // Mario descend le long du mât
        MARCHE_VERS_CHATEAU,   // Mario marche vers le château
        ENTREE_CHATEAU,        // Mario entre et disparaît
        ATTENTE_MENU           // Attente avant d'afficher le menu
    }
    
    /**
     * Constructeur
     */
    public ControleurJeu() {
        this.gestionnaireNiveaux = new GestionnaireNiveaux();
        this.controleurEntrees = new ControleurEntrees();
        this.etatActuel = EtatJeu.MENU;
        this.sauvegardeAutomatique = false; // Désactivé pour toujours démarrer au niveau 1
        
        // Initialiser les variables de séquence
        this.etapeSequenceFin = null;
        this.tempsSequence = 0;
        this.positionChateauX = 0;
    }
    
    /**
     * Initialise le jeu
     */
    public void initialiser() {
        // Tenter de charger une sauvegarde existante
        if (sauvegardeAutomatique && SauvegardeProgression.existe()) {
            if (chargerProgression()) {
                etatActuel = EtatJeu.EN_JEU;
                return;
            }
        }
        
        // Sinon, charger le premier niveau
        gestionnaireNiveaux.chargerPremierNiveau();
        
        Niveau niveauActuel = gestionnaireNiveaux.getNiveauActuel();
        if (niveauActuel != null) {
            gestionnaireCollisions = new GestionnaireCollisions(niveauActuel);
        }
        
        etatActuel = EtatJeu.EN_JEU;
    }
    
    /**
     * Met à jour le jeu
     * @param deltaTemps Temps écoulé depuis la dernière frame
     */
    public void mettreAJour(float deltaTemps) {
        // Toujours mettre à jour le gestionnaire de niveaux pour les transitions
        gestionnaireNiveaux.mettreAJour(deltaTemps);
        
        switch (etatActuel) {
            case EN_JEU:
                mettreAJourJeu(deltaTemps);
                break;
            case PAUSE:
                gererPause();
                break;
            case GAME_OVER:
                gererGameOver();
                break;
            case VICTOIRE:
                gererVictoire();
                break;
            case MENU:
                gererMenu();
                break;
            case TRANSITION_NIVEAU:
                gererTransitionNiveau();
                break;
            case RECAPITULATIF_NIVEAU:
                gererRecapitulatifNiveau();
                break;
            case SEQUENCE_FIN_NIVEAU:
                gererSequenceFinNiveau(deltaTemps);
                break;
        }
    }
    
    /**
     * Met à jour la logique du jeu
     * @param deltaTemps Temps écoulé
     */
    private void mettreAJourJeu(float deltaTemps) {
        Niveau niveauActuel = gestionnaireNiveaux.getNiveauActuel();
        if (niveauActuel == null) {
            return;
        }
        
        Joueur joueur = niveauActuel.getJoueur();
        if (joueur == null) {
            return;
        }
        
        // Gérer les entrées du joueur
        controleurEntrees.gererEntrees(joueur, niveauActuel);
        
        // Mettre à jour le niveau
        niveauActuel.mettreAJour(deltaTemps);
        
        // Empêcher le joueur de sortir des limites du niveau
        limiterPositionJoueur(joueur, niveauActuel);
        
        // Gérer les collisions
        if (gestionnaireCollisions != null) {
            gestionnaireCollisions.gererCollisions();
        }
        
        // Nettoyer les entités inactives
        niveauActuel.nettoyerEntitesInactives();
        
        // Vérifier les conditions de fin de niveau
        verifierConditionsFinNiveau(joueur);
        
        // Vérifier la touche pause
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            etatActuel = EtatJeu.PAUSE;
        }
    }
    
    /**
     * Vérifie les conditions de fin de niveau
     * @param joueur Le joueur
     */
    private void verifierConditionsFinNiveau(Joueur joueur) {
        // Vérifier si le joueur est mort
        if (joueur.getVies() <= 0) {
            etatActuel = EtatJeu.GAME_OVER;
            if (sauvegardeAutomatique) {
                sauvegarderProgression();
            }
            return;
        }
        
        // Vérifier si le drapeau a été touché (démarrer la séquence)
        Niveau niveauActuel = gestionnaireNiveaux.getNiveauActuel();
        com.mypackage.projet.jeux.modele.entites.Drapeau drapeau = niveauActuel.getDrapeau();
        
        if (drapeau != null && drapeau.estTouche() && etatActuel == EtatJeu.EN_JEU) {
            // Démarrer la séquence de fin de niveau
            gagnerNiveau();
            return;
        }
        
        // Vérifier si le niveau est terminé (après la séquence)
        if (niveauActuel.estTermine() && !gestionnaireNiveaux.estEnTransition()) {
            // Afficher le récapitulatif du niveau
            etatActuel = EtatJeu.RECAPITULATIF_NIVEAU;
            
            if (sauvegardeAutomatique) {
                sauvegarderProgression();
            }
        }
    }
    
    /**
     * Gère l'état de pause
     */
    private void gererPause() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            etatActuel = EtatJeu.EN_JEU;
        }
    }
    
    /**
     * Gère l'état de game over (clavier + souris)
     */
    private void gererGameOver() {
        // Support clavier (ENTRÉE ou R pour recommencer)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || 
            Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            
            gestionnaireNiveaux.rechargerNiveauActuel();
            gestionnaireCollisions = new GestionnaireCollisions(gestionnaireNiveaux.getNiveauActuel());
            reinitialiserVariablesSequence();
            etatActuel = EtatJeu.EN_JEU;
            
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.M) || 
                   Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            etatActuel = EtatJeu.MENU;
        }
        
        // Support souris (clics sur les boutons du menu)
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            gererClicSouris(false, false);
        }
    }
    
    /**
     * Gère l'état de victoire (clavier + souris)
     */
    private void gererVictoire() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            // Afficher le récapitulatif complet
            gestionnaireNiveaux.afficherRecapitulatif();
            etatActuel = EtatJeu.MENU;
        }
        
        // Support souris
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            gererClicSouris(true, false);
        }
    }
    
    /**
     * Gère la transition entre niveaux
     */
    private void gererTransitionNiveau() {
        // Les transitions sont gérées automatiquement par le GestionnaireNiveaux
        if (!gestionnaireNiveaux.estEnTransition()) {
            etatActuel = EtatJeu.EN_JEU;
        }
    }
    
    /**
     * Gère l'affichage du récapitulatif de niveau (clavier + souris)
     */
    private void gererRecapitulatifNiveau() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || 
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            passerAuNiveauSuivant();
        }
        
        // Support souris
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            gererClicSouris(true, gestionnaireNiveaux.aDesNiveauxRestants());
        }
    }
    
    /**
     * Passe au niveau suivant ou affiche la victoire finale
     */
    private void passerAuNiveauSuivant() {
            // Passer au niveau suivant
            if (gestionnaireNiveaux.aDesNiveauxRestants()) {
                boolean success = gestionnaireNiveaux.niveauSuivant();
                if (success) {
                    Niveau nouveauNiveau = gestionnaireNiveaux.getNiveauActuel();
                    
                    // Créer un nouveau joueur pour le nouveau niveau
                    if (nouveauNiveau.getJoueur() == null) {
                        com.mypackage.projet.jeux.modele.entites.Joueur joueur = 
                            new com.mypackage.projet.jeux.modele.entites.Joueur(100, 64);
                        nouveauNiveau.setJoueur(joueur);
                    }
                    
                    gestionnaireCollisions = new GestionnaireCollisions(nouveauNiveau);
                    reinitialiserVariablesSequence();
                    etatActuel = EtatJeu.TRANSITION_NIVEAU;
                }
            } else {
                // Tous les niveaux terminés
                etatActuel = EtatJeu.VICTOIRE;
            }
        }
    
    /**
     * Gère un clic de bouton du menu
     * @param typeBouton Le type de bouton cliqué
     */
    public void gererClicBouton(com.mypackage.projet.jeux.vue.MenuFinNiveau.TypeBouton typeBouton) {
        if (typeBouton == null) {
            return;
        }
        
        switch (typeBouton) {
            case REJOUER:
                gestionnaireNiveaux.rechargerNiveauActuel();
                gestionnaireCollisions = new GestionnaireCollisions(gestionnaireNiveaux.getNiveauActuel());
                reinitialiserVariablesSequence();
                etatActuel = EtatJeu.EN_JEU;
                break;
                
            case QUITTER:
                Gdx.app.exit();
                break;
                
            case NIVEAU_SUIVANT:
                passerAuNiveauSuivant();
                break;
        }
    }
    
    /**
     * Gère les clics souris sur les boutons du menu de fin
     * @param niveauReussi true si victoire, false si game over
     * @param aDesNiveauxRestants true s'il reste des niveaux
     */
    private void gererClicSouris(boolean niveauReussi, boolean aDesNiveauxRestants) {
        // Cette méthode est maintenant obsolète car la gestion des clics
        // se fait directement dans JeuPlateforme avec gererClicBouton()
    }
    
    /**
     * Gère le menu
     */
    private void gererMenu() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            initialiser();
        }
    }
    
    // Getters
    public GestionnaireNiveaux getGestionnaireNiveaux() {
        return gestionnaireNiveaux;
    }
    
    public EtatJeu getEtatActuel() {
        return etatActuel;
    }
    
    public void setEtatActuel(EtatJeu etat) {
        this.etatActuel = etat;
    }
    
    /**
     * Sauvegarde la progression actuelle
     * @return true si succès, false sinon
     */
    public boolean sauvegarderProgression() {
        SauvegardeProgression.Sauvegarde sauvegarde = new SauvegardeProgression.Sauvegarde();
        sauvegarde.niveauActuelIndex = gestionnaireNiveaux.getNiveauActuelIndex();
        sauvegarde.scoreTotal = gestionnaireNiveaux.getScoreTotal();
        
        // Sauvegarder les progressions de tous les niveaux
        Map<String, ProgressionNiveau> historique = gestionnaireNiveaux.getHistoriqueProgressions();
        for (Map.Entry<String, ProgressionNiveau> entry : historique.entrySet()) {
            SauvegardeProgression.DonneesProgression donnees = 
                new SauvegardeProgression.DonneesProgression(entry.getValue());
            sauvegarde.progressions.put(entry.getKey(), donnees);
        }
        
        return SauvegardeProgression.sauvegarder(sauvegarde);
    }
    
    /**
     * Charge la progression sauvegardée
     * @return true si succès, false sinon
     */
    public boolean chargerProgression() {
        SauvegardeProgression.Sauvegarde sauvegarde = SauvegardeProgression.charger();
        if (sauvegarde == null) {
            return false;
        }
        
        // Charger le niveau sauvegardé
        if (gestionnaireNiveaux.chargerNiveau(sauvegarde.niveauActuelIndex)) {
            Niveau niveauActuel = gestionnaireNiveaux.getNiveauActuel();
            if (niveauActuel != null) {
                gestionnaireCollisions = new GestionnaireCollisions(niveauActuel);
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Active ou désactive la sauvegarde automatique
     * @param activer true pour activer, false pour désactiver
     */
    public void setSauvegardeAutomatique(boolean activer) {
        this.sauvegardeAutomatique = activer;
    }
    
    /**
     * Retourne si la sauvegarde automatique est active
     * @return true si active, false sinon
     */
    public boolean isSauvegardeAutomatique() {
        return sauvegardeAutomatique;
    }
    
    /**
     * Limite la position du joueur aux bordures du niveau
     * @param joueur Le joueur
     * @param niveau Le niveau actuel
     */
    private void limiterPositionJoueur(Joueur joueur, Niveau niveau) {
        float posX = joueur.getPosition().x;
        float posY = joueur.getPosition().y;
        float largeurJoueur = joueur.getLargeur();
        float hauteurJoueur = joueur.getHauteur();
        
        // Limiter horizontalement (gauche et droite)
        if (posX < 0) {
            joueur.forcerPosition(0, posY);
            joueur.setVitesse(0, joueur.getVitesse().y);
        } else if (posX + largeurJoueur > niveau.getLargeur()) {
            joueur.forcerPosition(niveau.getLargeur() - largeurJoueur, posY);
            joueur.setVitesse(0, joueur.getVitesse().y);
        }
        
        // Vérifier si le joueur tombe en dehors du niveau (mort)
        if (posY < -100) {
            joueur.perdreVie();
            // Réinitialiser la position du joueur au début du niveau
            joueur.forcerPosition(64, 384);
            joueur.setVitesse(0, 0);
        }
    }
    
    /**
     * Démarre la séquence de fin de niveau (appelé quand le joueur touche le drapeau)
     */
    public void gagnerNiveau() {
        Niveau niveauActuel = gestionnaireNiveaux.getNiveauActuel();
        if (niveauActuel == null) {
            return;
        }
        
        com.mypackage.projet.jeux.modele.entites.Drapeau drapeau = niveauActuel.getDrapeau();
        if (drapeau == null) {
            return;
        }
        
        Joueur joueur = niveauActuel.getJoueur();
        if (joueur == null) {
            return;
        }
        
        // Calculer la position du château (à gauche du drapeau - 150px selon RenduDrapeau.java)
        positionChateauX = drapeau.getPosition().x - 150 + 64; // Centre de la porte du château
        
        // Positionner Mario au centre du mât du drapeau
        float matX = drapeau.getPosition().x + 12; // Position du mât (décalage de 12px)
        joueur.forcerPosition(matX - joueur.getLargeur() / 2, joueur.getPosition().y);
        
        // Initialiser la séquence
        etatActuel = EtatJeu.SEQUENCE_FIN_NIVEAU;
        etapeSequenceFin = EtapeSequenceFin.GLISSADE_DRAPEAU;
        tempsSequence = 0;
        joueur.setVisible(true); // S'assurer que le joueur est visible
        joueur.setAlpha(1.0f); // S'assurer que le joueur est opaque
    }
    
    /**
     * Gère la séquence de fin de niveau en 4 étapes
     * @param deltaTemps Temps écoulé
     */
    private void gererSequenceFinNiveau(float deltaTemps) {
        Niveau niveauActuel = gestionnaireNiveaux.getNiveauActuel();
        if (niveauActuel == null) {
            return;
        }
        
        Joueur joueur = niveauActuel.getJoueur();
        com.mypackage.projet.jeux.modele.entites.Drapeau drapeau = niveauActuel.getDrapeau();
        
        if (joueur == null || drapeau == null) {
            return;
        }
        
        // Mettre à jour le drapeau (pour l'animation de descente)
        if (drapeau.estActive()) {
            drapeau.mettreAJour(deltaTemps);
        }
        
        tempsSequence += deltaTemps;
        
        switch (etapeSequenceFin) {
            case GLISSADE_DRAPEAU:
                gererGlissadeDrapeau(joueur, drapeau, deltaTemps);
                break;
                
            case MARCHE_VERS_CHATEAU:
                gererMarcheVersChateau(joueur, deltaTemps);
                break;
                
            case ENTREE_CHATEAU:
                gererEntreeChateau(joueur, deltaTemps);
                break;
                
            case ATTENTE_MENU:
                gererAttenteMenu(niveauActuel);
                break;
        }
    }
    
    /**
     * ÉTAPE 1 : Mario glisse le long du mât du drapeau
     */
    private void gererGlissadeDrapeau(Joueur joueur, com.mypackage.projet.jeux.modele.entites.Drapeau drapeau, float deltaTemps) {
        // Mario descend doucement
        float vitesseDescente = -150f;
        joueur.setVitesse(0, vitesseDescente);
        
        // Mise à jour manuelle de la position
        float nouvelleY = joueur.getPosition().y + vitesseDescente * deltaTemps;
        joueur.forcerPosition(joueur.getPosition().x, nouvelleY);
        
        // Vérifier si Mario touche le sol (position Y du drapeau)
        float solY = drapeau.getPosition().y;
        if (joueur.getPosition().y <= solY) {
            // Mario a atteint le sol, passer à l'étape suivante
            joueur.forcerPosition(joueur.getPosition().x, solY);
            joueur.setVitesse(0, 0);
            joueur.setAuSol(true);
            
            etapeSequenceFin = EtapeSequenceFin.MARCHE_VERS_CHATEAU;
            tempsSequence = 0;
        }
    }
    
    /**
     * ÉTAPE 2 : Mario marche vers le château
     */
    private void gererMarcheVersChateau(Joueur joueur, float deltaTemps) {
        // Mario avance automatiquement vers la droite
        float vitesseMarche = 100f;
        joueur.setVitesse(vitesseMarche, 0);
        
        // Mise à jour manuelle de la position
        float nouvelleX = joueur.getPosition().x + vitesseMarche * deltaTemps;
        joueur.forcerPosition(nouvelleX, joueur.getPosition().y);
        
        // Vérifier si Mario atteint le centre de la porte du château
        if (joueur.getPosition().x >= positionChateauX) {
            etapeSequenceFin = EtapeSequenceFin.ENTREE_CHATEAU;
            tempsSequence = 0;
        }
    }
    
    /**
     * ÉTAPE 3 : Mario entre dans le château et disparaît progressivement
     */
    private void gererEntreeChateau(Joueur joueur, float deltaTemps) {
        // Arrêter Mario
        joueur.setVitesse(0, 0);
        
        // Faire disparaître Mario progressivement (fade out sur 0.5 seconde)
        float dureeFadeOut = 0.5f;
        if (tempsSequence < dureeFadeOut) {
            // Réduire l'alpha progressivement de 1.0 à 0.0
            float progression = tempsSequence / dureeFadeOut;
            float alpha = 1.0f - progression;
            joueur.setAlpha(alpha);
        } else {
            // Complètement invisible après le fade out
            joueur.setAlpha(0.0f);
        }
        
        // Attendre 2 secondes au total avant de passer au menu
        if (tempsSequence >= 2.0f) {
            etapeSequenceFin = EtapeSequenceFin.ATTENTE_MENU;
            tempsSequence = 0;
        }
    }
    
    /**
     * ÉTAPE 4 : Afficher le menu de fin de niveau
     */
    private void gererAttenteMenu(Niveau niveau) {
        Joueur joueur = niveau.getJoueur();
        
        // Marquer le niveau comme terminé
        niveau.terminerNiveau(true);
        
        // Afficher le récapitulatif
        etatActuel = EtatJeu.RECAPITULATIF_NIVEAU;
        
        // Réinitialiser les variables de séquence
        reinitialiserVariablesSequence();
        
        // Réinitialiser la visibilité et la transparence du joueur
        if (joueur != null) {
            joueur.setVisible(true);
            joueur.setAlpha(1.0f);
        }
    }
    
    /**
     * Réinitialise les variables de la séquence de fin de niveau
     */
    private void reinitialiserVariablesSequence() {
        etapeSequenceFin = null;
        tempsSequence = 0;
        positionChateauX = 0;
    }
}


