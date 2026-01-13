package com.mypackage.projet.jeux.modele.gestionnaires;

import com.badlogic.gdx.math.Rectangle;
import com.mypackage.projet.jeux.modele.entites.Drapeau;
import com.mypackage.projet.jeux.modele.entites.Ennemi;
import com.mypackage.projet.jeux.modele.entites.EnnemiTerrestre;
import com.mypackage.projet.jeux.modele.entites.Joueur;
import com.mypackage.projet.jeux.modele.entites.ObjetCollectable;
import com.mypackage.projet.jeux.modele.entites.Obstacle;
import com.mypackage.projet.jeux.modele.entites.PowerUp;
import com.mypackage.projet.jeux.modele.niveau.Niveau;

/**
 * Classe gérant les collisions entre les différentes entités du jeu
 */
public class GestionnaireCollisions {
    
    private Niveau niveau;
    
    /**
     * Constructeur
     * @param niveau Le niveau dans lequel gérer les collisions
     */
    public GestionnaireCollisions(Niveau niveau) {
        this.niveau = niveau;
    }
    
    /**
     * Vérifie et gère toutes les collisions du niveau
     */
    public void gererCollisions() {
        Joueur joueur = niveau.getJoueur();
        if (joueur == null || !joueur.estActive()) {
            return;
        }
        
        // Collisions avec les obstacles
        gererCollisionsObstacles(joueur);
        
        // Collisions avec les ennemis
        gererCollisionsEnnemis(joueur);
        
        // Collisions avec les objets collectables
        gererCollisionsObjetsCollectables(joueur);
        
        // Collisions avec les power-ups
        gererCollisionsPowerUps(joueur);
        
        // Collisions avec le drapeau (fin de niveau)
        gererCollisionDrapeau(joueur);
        
        // Collisions des ennemis avec les obstacles
        for (Ennemi ennemi : niveau.getEnnemis()) {
            if (ennemi.estActive()) {
                gererCollisionsEnnemiObstacles(ennemi);
            }
        }
        
        // Collisions des power-ups avec les obstacles
        for (PowerUp powerUp : niveau.getPowerUps()) {
            if (powerUp.estActive()) {
                gererCollisionsPowerUpObstacles(powerUp);
            }
        }
    }
    
    /**
     * Gère les collisions du joueur avec les obstacles
     * AMÉLIORATION : Séparation des axes X et Y pour une détection plus précise
     * @param joueur Le joueur
     */
    private void gererCollisionsObstacles(Joueur joueur) {
        Rectangle boiteJoueur = joueur.getBoiteCollision();
        float rayonVerification = 200f;
        
        // Sauvegarder la position et vitesse avant collision
        float posXAvant = joueur.getPosition().x;
        float posYAvant = joueur.getPosition().y;
        float vitesseX = joueur.getVitesse().x;
        float vitesseY = joueur.getVitesse().y;
        
        // ========== PHASE 1 : COLLISIONS HORIZONTALES (Axe X) ==========
        for (Obstacle obstacle : niveau.getObstacles()) {
            if (!obstacle.estActive() || !obstacle.estSolide()) {
                continue;
            }
            
            // OPTIMISATION : Ignorer les obstacles trop éloignés
            float distanceX = Math.abs(obstacle.getPosition().x - posXAvant);
            float distanceY = Math.abs(obstacle.getPosition().y - posYAvant);
            
            if (distanceX > rayonVerification || distanceY > rayonVerification) {
                continue;
            }
            
            Rectangle boiteObstacle = obstacle.getBoiteCollision();
            
            if (boiteJoueur.overlaps(boiteObstacle)) {
                // Calculer les chevauchements
                float overlapGauche = boiteJoueur.x + boiteJoueur.width - boiteObstacle.x;
                float overlapDroite = boiteObstacle.x + boiteObstacle.width - boiteJoueur.x;
                float overlapHaut = boiteJoueur.y + boiteJoueur.height - boiteObstacle.y;
                float overlapBas = boiteObstacle.y + boiteObstacle.height - boiteJoueur.y;
                
                // Trouver le plus petit chevauchement
                float minOverlapX = Math.min(overlapGauche, overlapDroite);
                float minOverlapY = Math.min(overlapHaut, overlapBas);
                
                // Si le chevauchement horizontal est plus petit que le vertical,
                // c'est une collision horizontale
                if (minOverlapX < minOverlapY) {
                    // Collision horizontale détectée
                    if (overlapGauche < overlapDroite) {
                        // Collision à gauche du mur
                        float nouvelleX = boiteObstacle.x - boiteJoueur.width;
                        joueur.forcerPosition(nouvelleX, joueur.getPosition().y);
                    } else {
                        // Collision à droite du mur
                        float nouvelleX = boiteObstacle.x + boiteObstacle.width;
                        joueur.forcerPosition(nouvelleX, joueur.getPosition().y);
                    }
                    
                    // NE PAS annuler la vitesse complètement (permet de continuer à bouger)
                    // On laisse juste le joueur contre le mur
                    
                    // Mettre à jour la boîte de collision après le déplacement
                    boiteJoueur = joueur.getBoiteCollision();
                }
            }
        }
        
        // ========== PHASE 2 : COLLISIONS VERTICALES (Axe Y) ==========
        boolean auSol = false;
        for (Obstacle obstacle : niveau.getObstacles()) {
            if (!obstacle.estActive() || !obstacle.estSolide()) {
                continue;
            }
            
            // OPTIMISATION : Ignorer les obstacles trop éloignés
            float distanceX = Math.abs(obstacle.getPosition().x - joueur.getPosition().x);
            float distanceY = Math.abs(obstacle.getPosition().y - joueur.getPosition().y);
            
            if (distanceX > rayonVerification || distanceY > rayonVerification) {
                continue;
            }
            
            Rectangle boiteObstacle = obstacle.getBoiteCollision();
            
            if (boiteJoueur.overlaps(boiteObstacle)) {
                // Calculer les chevauchements
                float overlapGauche = boiteJoueur.x + boiteJoueur.width - boiteObstacle.x;
                float overlapDroite = boiteObstacle.x + boiteObstacle.width - boiteJoueur.x;
                float overlapHaut = boiteJoueur.y + boiteJoueur.height - boiteObstacle.y;
                float overlapBas = boiteObstacle.y + boiteObstacle.height - boiteJoueur.y;
                
                // Trouver le plus petit chevauchement
                float minOverlapX = Math.min(overlapGauche, overlapDroite);
                float minOverlapY = Math.min(overlapHaut, overlapBas);
                
                // Si le chevauchement vertical est plus petit que l'horizontal,
                // c'est une collision verticale
                if (minOverlapY < minOverlapX) {
                    if (overlapBas < overlapHaut) {
                        // Joueur atterrit sur l'obstacle
                            float nouvelleY = boiteObstacle.y + boiteObstacle.height;
                            joueur.forcerPosition(joueur.getPosition().x, nouvelleY);
                            joueur.setVitesse(joueur.getVitesse().x, 0);
                            joueur.setAuSol(true);
                        auSol = true;
                        
                        // Mettre à jour la boîte de collision
                        boiteJoueur = joueur.getBoiteCollision();
                        
                    } else {
                        // ========== COLLISION PLAFOND : Mario tape sa tête ==========
                        // IMPORTANT : Vérifier que Mario monte vraiment (vitesse.y > 0)
                        if (joueur.getVitesse().y > 0) {
                            // 1. Correction physique : Placer Mario juste EN DESSOUS du bloc
                            float nouvelleY = boiteObstacle.y - boiteJoueur.height;
                            joueur.forcerPosition(joueur.getPosition().x, nouvelleY);
                            
                            // 2. Arrêt de la montée : Mario retombe
                            joueur.setVitesse(joueur.getVitesse().x, 0);
                            
                            // 3. Interaction avec le bloc
                            interagirAvecBloc(obstacle, joueur);
                            
                            // Mettre à jour la boîte de collision
                            boiteJoueur = joueur.getBoiteCollision();
                        }
                    }
                }
            }
        }
        
        // Si aucune collision au sol détectée, le joueur n'est pas au sol
        if (!auSol && vitesseY <= 0) {
            // Vérifier une dernière fois avec une marge de tolérance
            boolean auSolAvecTolerance = false;
            Rectangle boiteJoueurEtendue = new Rectangle(
                boiteJoueur.x + 2, 
                boiteJoueur.y - 2, 
                boiteJoueur.width - 4, 
                2
            );
            
            for (Obstacle obstacle : niveau.getObstacles()) {
                if (obstacle.estActive() && obstacle.estSolide()) {
                    if (boiteJoueurEtendue.overlaps(obstacle.getBoiteCollision())) {
                        auSolAvecTolerance = true;
                        break;
                    }
                }
            }
            
            if (!auSolAvecTolerance) {
                joueur.setAuSol(false);
            }
        }
    }
    
    /**
     * Gère l'interaction quand Mario tape un bloc par en dessous
     * @param bloc Le bloc touché
     * @param joueur Le joueur
     */
    private void interagirAvecBloc(Obstacle bloc, Joueur joueur) {
        String typeBloc = bloc.getType();
        
        if (typeBloc == null) {
            return; // Bloc sans type spécifique
        }
        
        // ========== BLOC MYSTÈRE (Question Block) ==========
        if (typeBloc.equals("BLOC_QUESTION") || typeBloc.contains("question") || typeBloc.contains("mystere")) {
            activerBlocQuestion(bloc, joueur);
        }
        // ========== BRIQUE DESTRUCTIBLE ==========
        else if (typeBloc.equals("BLOC_BRIQUE") || typeBloc.contains("brique") || typeBloc.contains("brick")) {
            activerBrique(bloc, joueur);
        }
        // ========== AUTRES BLOCS ==========
        else {
            // Animation de rebond simple pour les autres blocs
        }
    }
    
    /**
     * Active un bloc mystère (fait apparaître un item)
     * @param bloc Le bloc mystère
     * @param joueur Le joueur
     */
    private void activerBlocQuestion(Obstacle bloc, Joueur joueur) {
        // Vérifier si le bloc a déjà été activé (on peut ajouter un flag plus tard)
        // Pour l'instant, on désactive le bloc après utilisation
        if (!bloc.estSolide()) {
            return; // Déjà utilisé
        }
        
        // Animation du bloc (rebond)
        // TODO: Ajouter une vraie animation de rebond
        
        // Faire apparaître un item
        float blocX = bloc.getPosition().x;
        float blocY = bloc.getPosition().y;
        float blocHauteur = bloc.getHauteur();
        
        // Décider quel item faire apparaître
        // Pour l'instant, on fait apparaître une pièce
        ObjetCollectable piece = new ObjetCollectable(
            blocX,
            blocY + blocHauteur,
            "piece",
            10
        );
        
        niveau.ajouterObjetCollectable(piece);
        
        // Marquer le bloc comme utilisé (changer son apparence)
        // Pour l'instant, on le rend non-solide pour indiquer qu'il est vide
        // TODO: Changer la texture du bloc au lieu de le désactiver
    }
    
    /**
     * Gère l'interaction avec une brique (destruction ou rebond)
     * @param bloc La brique
     * @param joueur Le joueur
     */
    private void activerBrique(Obstacle bloc, Joueur joueur) {
        // Vérifier la transformation de Mario
        Joueur.EtatTransformation transformation = joueur.getTransformation();
        
        // Si Mario est GRAND ou FEU, il peut détruire les briques
        if (transformation == Joueur.EtatTransformation.GRAND || 
            transformation == Joueur.EtatTransformation.FEU) {
            
            // ========== DESTRUCTION DE LA BRIQUE ==========
            // Désactiver la brique
            bloc.setDestructible(true);
            bloc.detruire();
            
            // Ajouter des points au score
            joueur.ajouterScore(50);
            
            // TODO: Ajouter animation de débris/particules de brique
            
        } else {
            // ========== MARIO PETIT : Simple rebond du bloc ==========
            // TODO: Ajouter une petite animation de rebond du bloc
        }
    }
    
    /**
     * Gère les collisions du joueur avec les ennemis
     * AMÉLIORÉ : Détection précise de la direction de collision (Mario-style)
     * @param joueur Le joueur
     */
    private void gererCollisionsEnnemis(Joueur joueur) {
        Rectangle boiteJoueur = joueur.getBoiteCollision();
        
        for (Ennemi ennemi : niveau.getEnnemis()) {
            if (!ennemi.estActive()) {
                continue;
            }
            
            Rectangle boiteEnnemi = ennemi.getBoiteCollision();
            
            if (boiteJoueur.overlaps(boiteEnnemi)) {
                // ========== DÉTECTION DE DIRECTION AMÉLIORÉE ==========
                
                // Calculer les chevauchements sur chaque axe
                float overlapGauche = boiteJoueur.x + boiteJoueur.width - boiteEnnemi.x;
                float overlapDroite = boiteEnnemi.x + boiteEnnemi.width - boiteJoueur.x;
                float overlapHaut = boiteJoueur.y + boiteJoueur.height - boiteEnnemi.y;
                float overlapBas = boiteEnnemi.y + boiteEnnemi.height - boiteJoueur.y;
                
                // Trouver le plus petit chevauchement
                float minOverlapX = Math.min(overlapGauche, overlapDroite);
                float minOverlapY = Math.min(overlapHaut, overlapBas);
                
                // Zone de tolérance pour l'écrasement (plus permissif = plus facile)
                float toleranceEcrasement = 8f;
                
                // ========== ÉCRASEMENT PAR LE HAUT (MARIO SAUTE SUR L'ENNEMI) ==========
                if (minOverlapY < minOverlapX + toleranceEcrasement && 
                    joueur.getVitesse().y <= 0 && 
                    overlapBas < overlapHaut) {
                    
                    // SUCCÈS : Mario écrase l'ennemi ! 🦘
                    ennemi.subirDegats(1);
                    joueur.ajouterScore(ennemi.getPointsScore());
                    
                    // REBOND Mario-style (plus haut si on maintient saut)
                    float rebond = 350f; // Rebond de base
                    joueur.setVitesse(joueur.getVitesse().x, rebond);
                    
                    // Notifier le niveau
                    if (!ennemi.estActive()) {
                        niveau.notifierEnnemiVaincu();
                    }
                    
                // ========== COLLISION LATÉRALE (MARIO SE FAIT TOUCHER) ==========
                } else {
                    // DANGER : Mario touche l'ennemi de côté ou par en dessous ! 💔
                    
                    // ✅ VÉRIFICATION INVINCIBILITÉ : Ne rien faire si Mario clignote
                    if (joueur.estInvincible()) {
                        continue; // Passer à l'ennemi suivant
                    }
                    
                    // Mario n'est pas invincible → Il prend des dégâts
                    joueur.perdreVie();
                    
                    // KNOCKBACK : Repousser Mario dans la direction opposée
                    float directionKnockback = joueur.getPosition().x < ennemi.getPosition().x ? -1 : 1;
                    joueur.setVitesse(directionKnockback * 250, 250);
                    
                    // ✅ L'invincibilité temporaire est automatiquement activée dans perdreVie()
                }
            }
        }
    }
    
    /**
     * Gère les collisions du joueur avec les objets collectables
     * @param joueur Le joueur
     */
    private void gererCollisionsObjetsCollectables(Joueur joueur) {
        for (ObjetCollectable objet : niveau.getObjetsCollectables()) {
            if (!objet.estActive()) {
                continue;
            }
            
            if (joueur.entreEnCollisionAvec(objet)) {
                objet.collecter();
                joueur.ajouterScore(objet.getValeur());
                
                // Notifier le niveau qu'une pièce a été collectée
                if ("PIECE".equalsIgnoreCase(objet.getType())) {
                    joueur.ajouterPieces(1); // +1 pièce, +10 points, bonus de vie tous les 100
                    niveau.notifierPieceCollectee();
                }
            }
        }
    }
    
    /**
     * Gère les collisions d'un ennemi avec les obstacles
     * @param ennemi L'ennemi
     */
    private void gererCollisionsEnnemiObstacles(Ennemi ennemi) {
        for (Obstacle obstacle : niveau.getObstacles()) {
            if (!obstacle.estActive() || !obstacle.estSolide()) {
                continue;
            }
            
            if (ennemi.entreEnCollisionAvec(obstacle)) {
                // Si l'ennemi touche un mur, il inverse sa direction
                if (ennemi instanceof EnnemiTerrestre) {
                    ennemi.inverserDirection();
                    EnnemiTerrestre ennemiTerrestre = (EnnemiTerrestre) ennemi;
                    
                    // Vérifier si collision verticale (au sol)
                    if (ennemi.getPosition().y <= obstacle.getPosition().y + obstacle.getHauteur()) {
                        ennemiTerrestre.placerAuSol(obstacle.getPosition().y + obstacle.getHauteur());
                    }
                }
            }
        }
    }
    
    /**
     * Gère les collisions du joueur avec les power-ups
     * @param joueur Le joueur
     */
    private void gererCollisionsPowerUps(Joueur joueur) {
        for (PowerUp powerUp : niveau.getPowerUps()) {
            if (!powerUp.estActive() || powerUp.estEnApparition()) {
                continue;
            }
            
            if (joueur.entreEnCollisionAvec(powerUp)) {
                powerUp.collecter();
                joueur.appliquerPowerUp(powerUp.getTypePowerUp());
            }
        }
    }
    
    /**
     * Gère les collisions des power-ups avec les obstacles
     * @param powerUp Le power-up
     */
    private void gererCollisionsPowerUpObstacles(PowerUp powerUp) {
        Rectangle boitePowerUp = powerUp.getBoiteCollision();
        
        for (Obstacle obstacle : niveau.getObstacles()) {
            if (!obstacle.estActive() || !obstacle.estSolide()) {
                continue;
            }
            
            Rectangle boiteObstacle = obstacle.getBoiteCollision();
            
            if (boitePowerUp.overlaps(boiteObstacle)) {
                // Calculer les chevauchements
                float overlapHaut = boitePowerUp.y + boitePowerUp.height - boiteObstacle.y;
                float overlapBas = boiteObstacle.y + boiteObstacle.height - boitePowerUp.y;
                float overlapGauche = boitePowerUp.x + boitePowerUp.width - boiteObstacle.x;
                float overlapDroite = boiteObstacle.x + boiteObstacle.width - boitePowerUp.x;
                
                float minOverlapX = Math.min(overlapGauche, overlapDroite);
                float minOverlapY = Math.min(overlapHaut, overlapBas);
                
                if (minOverlapY < minOverlapX) {
                    // Collision verticale (au sol)
                    if (overlapBas < overlapHaut && powerUp.getVitesse().y <= 0) {
                        powerUp.placerAuSol(boiteObstacle.y + boiteObstacle.height);
                    }
                } else {
                    // Collision horizontale (mur)
                    powerUp.inverserDirection();
                }
            }
        }
    }
    
    
    /**
     * Gère la collision avec le drapeau de fin de niveau
     * @param joueur Le joueur
     */
    private void gererCollisionDrapeau(Joueur joueur) {
        Drapeau drapeau = niveau.getDrapeau();
        
        if (drapeau == null) {
            return;
        }
        
        Rectangle boiteJoueur = joueur.getBoiteCollision();
        Rectangle boiteDrapeau = drapeau.getBoiteCollision();
        
        if (boiteJoueur.overlaps(boiteDrapeau)) {
            if (!drapeau.estTouche()) {
                // Le joueur touche le drapeau pour la première fois
                drapeau.marquerTouche();
                
                // NE PAS terminer le niveau immédiatement
                // La séquence sera gérée par le ControleurJeu
                // qui détectera que le drapeau a été touché
            }
        }
    }
}
