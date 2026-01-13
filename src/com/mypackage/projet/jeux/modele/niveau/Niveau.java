package com.mypackage.projet.jeux.modele.niveau;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.mypackage.projet.jeux.modele.entites.Drapeau;
import com.mypackage.projet.jeux.modele.entites.Ennemi;
import com.mypackage.projet.jeux.modele.entites.Joueur;
import com.mypackage.projet.jeux.modele.entites.ObjetCollectable;
import com.mypackage.projet.jeux.modele.entites.Obstacle;
import com.mypackage.projet.jeux.modele.entites.PowerUp;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un niveau du jeu
 */
public class Niveau {
    
    private String nom;
    private TiledMap carte;
    private Joueur joueur;
    private List<Ennemi> ennemis;
    private List<ObjetCollectable> objetsCollectables;
    private List<Obstacle> obstacles;
    private List<PowerUp> powerUps;
    private Drapeau drapeau; // Drapeau de fin de niveau
    private float largeur;
    private float hauteur;
    private boolean termine;
    
    // Nouveaux attributs pour la gestion avancée
    private ProgressionNiveau progression;
    private List<ObjectifNiveau> objectifs;
    private int totalPiecesInitial;
    private int totalEnnemisInitial;
    private float positionFinX; // Position X pour atteindre la fin du niveau
    private float positionFinY; // Position Y pour atteindre la fin du niveau
    private boolean conditionVictoireAtteinte;
    
    /**
     * Constructeur du niveau
     * @param nom Nom du niveau
     */
    public Niveau(String nom) {
        this.nom = nom;
        this.ennemis = new ArrayList<>();
        this.objetsCollectables = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.drapeau = null;
        this.termine = false;
        this.conditionVictoireAtteinte = false;
        
        // Initialiser les nouveaux systèmes
        this.progression = new ProgressionNiveau(nom);
        this.objectifs = new ArrayList<>();
        this.totalPiecesInitial = 0;
        this.totalEnnemisInitial = 0;
        this.positionFinX = -1;
        this.positionFinY = -1;
        
        // Ajouter un objectif par défaut (atteindre la fin)
        ajouterObjectif(new ObjectifNiveau(
            ObjectifNiveau.TypeObjectif.ATTEINDRE_FIN,
            1,
            "Atteindre la fin du niveau"
        ));
    }
    
    /**
     * Met à jour tous les éléments du niveau
     * @param deltaTemps Temps écoulé depuis la dernière frame
     */
    public void mettreAJour(float deltaTemps) {
        if (termine) {
            return;
        }
        
        // Mettre à jour la progression du temps (désactivé)
        // progression.mettreAJourTemps(deltaTemps);
        
        // Mettre à jour le joueur
        if (joueur != null) {
            joueur.mettreAJour(deltaTemps);
            verifierConditionsVictoire();
        }
        
        // Mettre à jour les ennemis
        for (Ennemi ennemi : ennemis) {
            if (ennemi.estActive()) {
                ennemi.mettreAJour(deltaTemps);
            }
        }
        
        // Mettre à jour les objets collectables
        for (ObjetCollectable objet : objetsCollectables) {
            if (objet.estActive()) {
                objet.mettreAJour(deltaTemps);
            }
        }
        
        // Mettre à jour les power-ups
        for (PowerUp powerUp : powerUps) {
            if (powerUp.estActive()) {
                powerUp.mettreAJour(deltaTemps);
            }
        }
        
        // Mettre à jour le drapeau
        if (drapeau != null) {
            drapeau.mettreAJour(deltaTemps);
        }
        
        // Mettre à jour la progression des objectifs
        mettreAJourObjectifs();
    }
    
    /**
     * Vérifie les conditions de victoire du niveau
     */
    private void verifierConditionsVictoire() {
        if (joueur == null || conditionVictoireAtteinte) {
            return;
        }
        
        // Vérifier si tous les objectifs sont accomplis
        boolean tousObjectifsAccomplis = true;
        for (ObjectifNiveau objectif : objectifs) {
            if (!objectif.estAccompli()) {
                tousObjectifsAccomplis = false;
                break;
            }
        }
        
        if (tousObjectifsAccomplis) {
            conditionVictoireAtteinte = true;
            terminerNiveau(true);
        }
    }
    
    /**
     * Met à jour la progression des objectifs
     */
    private void mettreAJourObjectifs() {
        for (ObjectifNiveau objectif : objectifs) {
            switch (objectif.getType()) {
                case ATTEINDRE_FIN:
                    if (positionFinX > 0 && joueur != null) {
                        float distanceX = Math.abs(joueur.getPosition().x - positionFinX);
                        float distanceY = Math.abs(joueur.getPosition().y - positionFinY);
                        
                        // DEBUG: Afficher la distance toutes les 2 secondes
                        // Progression tracking (silent)
                        if ((int)(progression.getTempsEcoule()) % 2 == 0 && progression.getTempsEcoule() > 0) {
                            // Position tracking removed
                        }
                        
                        // Zone de fin assez large (100px de rayon)
                        if (distanceX < 100 && distanceY < 200) {
                            if (!objectif.estAccompli()) {
                                objectif.setAccompli(true);
                            }
                        }
                    }
                    break;
                    
                case COLLECTER_PIECES:
                    int anciennePieces = objectif.getValeurActuelle();
                    objectif.mettreAJourProgression(progression.getPieces());
                    break;
                    
                case VAINCRE_ENNEMIS:
                    int ancienEnnemis = objectif.getValeurActuelle();
                    objectif.mettreAJourProgression(progression.getEnnemisVaincus());
                    break;
                    
                case SURVIVRE_TEMPS:
                    objectif.mettreAJourProgression((int)progression.getTempsEcoule());
                    break;
            }
        }
    }
    
    /**
     * Termine le niveau
     * @param victoire true si victoire, false si défaite
     */
    public void terminerNiveau(boolean victoire) {
        this.termine = true;
        
        if (victoire && joueur != null) {
            progression.marquerTermine(
                totalPiecesInitial,
                totalEnnemisInitial,
                joueur.getVies()
            );
        }
    }
    
    /**
     * Ajoute un ennemi au niveau
     * @param ennemi L'ennemi à ajouter
     */
    public void ajouterEnnemi(Ennemi ennemi) {
        ennemis.add(ennemi);
        totalEnnemisInitial++;
        
        // Si le drapeau est déjà défini, appliquer les limites immédiatement
        if (drapeau != null) {
            float positionChateau = drapeau.getPosition().x - 150;
            ennemi.definirLimites(0, positionChateau);
        }
    }
    
    /**
     * Ajoute un objectif au niveau
     * @param objectif L'objectif à ajouter
     */
    public void ajouterObjectif(ObjectifNiveau objectif) {
        objectifs.add(objectif);
    }
    
    /**
     * Définit la position de fin du niveau
     * @param x Position X
     * @param y Position Y
     */
    public void definirPositionFin(float x, float y) {
        this.positionFinX = x;
        this.positionFinY = y;
    }
    
    /**
     * Réinitialise le niveau
     */
    public void reinitialiser() {
        termine = false;
        conditionVictoireAtteinte = false;
        progression.reinitialiser();
        
        for (ObjectifNiveau objectif : objectifs) {
            objectif.reinitialiser();
        }
        
        // Réactiver tous les ennemis
        for (Ennemi ennemi : ennemis) {
            ennemi.setActive(true);
            ennemi.setPointsVie(ennemi.getPointsVie()); // Reset HP
        }
        
        // Réactiver tous les objets collectables
        for (ObjetCollectable objet : objetsCollectables) {
            objet.setActive(true);
        }
        
        // Réinitialiser le drapeau si présent
        if (drapeau != null) {
            drapeau.reinitialiser();
        }
        
        // Réinitialiser le joueur si présent
        if (joueur != null) {
            // Utiliser la nouvelle méthode complète de réinitialisation
            joueur.reinitialiser(100, 64);
        }
    }
    
    /**
     * Ajoute un objet collectable au niveau
     * @param objet L'objet à ajouter
     */
    public void ajouterObjetCollectable(ObjetCollectable objet) {
        objetsCollectables.add(objet);
        if ("PIECE".equalsIgnoreCase(objet.getType())) {
            totalPiecesInitial++;
        }
    }
    
    /**
     * Notifie qu'une pièce a été collectée
     */
    public void notifierPieceCollectee() {
        progression.ajouterPiece();
    }
    
    /**
     * Notifie qu'un ennemi a été vaincu
     */
    public void notifierEnnemiVaincu() {
        progression.ennemiVaincu();
    }
    
    /**
     * Ajoute un obstacle au niveau
     * @param obstacle L'obstacle à ajouter
     */
    public void ajouterObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }
    
    /**
     * Nettoie les entités inactives
     */
    public void nettoyerEntitesInactives() {
        ennemis.removeIf(ennemi -> !ennemi.estActive());
        objetsCollectables.removeIf(objet -> !objet.estActive());
        obstacles.removeIf(obstacle -> !obstacle.estActive());
        powerUps.removeIf(powerUp -> !powerUp.estActive());
    }
    
    /**
     * Ajoute un power-up au niveau
     * @param powerUp Le power-up à ajouter
     */
    public void ajouterPowerUp(PowerUp powerUp) {
        powerUps.add(powerUp);
    }
    
    
    // Getters et Setters
    public String getNom() {
        return nom;
    }
    
    public TiledMap getCarte() {
        return carte;
    }
    
    public void setCarte(TiledMap carte) {
        this.carte = carte;
    }
    
    public Joueur getJoueur() {
        return joueur;
    }
    
    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }
    
    public List<Ennemi> getEnnemis() {
        return ennemis;
    }
    
    public List<ObjetCollectable> getObjetsCollectables() {
        return objetsCollectables;
    }
    
    public List<Obstacle> getObstacles() {
        return obstacles;
    }
    
    public float getLargeur() {
        return largeur;
    }
    
    public void setLargeur(float largeur) {
        this.largeur = largeur;
    }
    
    public float getHauteur() {
        return hauteur;
    }
    
    public void setHauteur(float hauteur) {
        this.hauteur = hauteur;
    }
    
    public boolean estTermine() {
        return termine;
    }
    
    public void setTermine(boolean termine) {
        this.termine = termine;
    }
    
    public ProgressionNiveau getProgression() {
        return progression;
    }
    
    public List<ObjectifNiveau> getObjectifs() {
        return objectifs;
    }
    
    public int getTotalPiecesInitial() {
        return totalPiecesInitial;
    }
    
    public int getTotalEnnemisInitial() {
        return totalEnnemisInitial;
    }
    
    public float getPositionFinX() {
        return positionFinX;
    }
    
    public float getPositionFinY() {
        return positionFinY;
    }
    
    public boolean isConditionVictoireAtteinte() {
        return conditionVictoireAtteinte;
    }
    
    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
    
    
    /**
     * Définit le drapeau de fin de niveau
     * @param drapeau Le drapeau à ajouter
     */
    public void setDrapeau(Drapeau drapeau) {
        this.drapeau = drapeau;
        
        // Calculer la position du château (à gauche du drapeau)
        if (drapeau != null) {
            float positionChateau = drapeau.getPosition().x - 150;
            
            // Appliquer cette limite à tous les ennemis
            // Les ennemis ne peuvent pas dépasser le château
            for (Ennemi ennemi : ennemis) {
                // Limite droite = position du château
                // Limite gauche = début du niveau (0)
                ennemi.definirLimites(0, positionChateau);
            }
        }
    }
    
    /**
     * Obtient le drapeau de fin de niveau
     * @return Le drapeau ou null s'il n'y en a pas
     */
    public Drapeau getDrapeau() {
        return drapeau;
    }
}


