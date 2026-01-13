package com.mypackage.projet.jeux.modele.niveau;

/**
 * Classe représentant la progression et les statistiques d'un niveau
 */
public class ProgressionNiveau {
    
    private String nomNiveau;
    private int score;
    private int pieces;
    private int ennemisVaincus;
    private float tempsEcoule;
    private boolean termine;
    private boolean parfait; // Niveau terminé sans perdre de vie
    private int tentatives;
    private int etoiles; // 0 à 3 étoiles selon la performance
    
    /**
     * Constructeur
     * @param nomNiveau Nom du niveau
     */
    public ProgressionNiveau(String nomNiveau) {
        this.nomNiveau = nomNiveau;
        this.score = 0;
        this.pieces = 0;
        this.ennemisVaincus = 0;
        this.tempsEcoule = 0;
        this.termine = false;
        this.parfait = false;
        this.tentatives = 0;
        this.etoiles = 0;
    }
    
    /**
     * Réinitialise la progression du niveau
     */
    public void reinitialiser() {
        this.score = 0;
        this.pieces = 0;
        this.ennemisVaincus = 0;
        this.tempsEcoule = 0;
        this.termine = false;
        this.parfait = false;
        this.tentatives++;
    }
    
    /**
     * Ajoute du score
     * @param points Points à ajouter
     */
    public void ajouterScore(int points) {
        this.score += points;
    }
    
    /**
     * Ajoute une pièce collectée
     */
    public void ajouterPiece() {
        this.pieces++;
        ajouterScore(10);
    }
    
    /**
     * Enregistre la défaite d'un ennemi
     */
    public void ennemiVaincu() {
        this.ennemisVaincus++;
        ajouterScore(50);
    }
    
    /**
     * Met à jour le temps écoulé
     * @param deltaTemps Temps écoulé
     */
    public void mettreAJourTemps(float deltaTemps) {
        this.tempsEcoule += deltaTemps;
    }
    
    /**
     * Calcule le nombre d'étoiles selon la performance
     * @param totalPieces Nombre total de pièces dans le niveau
     * @param totalEnnemis Nombre total d'ennemis dans le niveau
     * @param viesRestantes Nombre de vies restantes du joueur
     * @return Nombre d'étoiles (0-3)
     */
    public int calculerEtoiles(int totalPieces, int totalEnnemis, int viesRestantes) {
        int etoiles = 1; // Au moins 1 étoile pour avoir terminé
        
        // +1 étoile si au moins 50% des pièces collectées
        if (totalPieces > 0 && (pieces * 100.0 / totalPieces) >= 50) {
            etoiles++;
        }
        
        // +1 étoile si aucune vie perdue
        if (viesRestantes >= 3) {
            etoiles++;
        }
        
        this.etoiles = etoiles;
        return etoiles;
    }
    
    /**
     * Marque le niveau comme terminé
     * @param totalPieces Nombre total de pièces
     * @param totalEnnemis Nombre total d'ennemis
     * @param viesRestantes Vies restantes
     */
    public void marquerTermine(int totalPieces, int totalEnnemis, int viesRestantes) {
        this.termine = true;
        this.parfait = (viesRestantes >= 3);
        calculerEtoiles(totalPieces, totalEnnemis, viesRestantes);
        
        // Bonus de temps désactivé (timer retiré du jeu)
        /*
        if (tempsEcoule < 60) {
            ajouterScore(500);
        } else if (tempsEcoule < 120) {
            ajouterScore(300);
        } else if (tempsEcoule < 180) {
            ajouterScore(100);
        }
        */
    }
    
    /**
     * Retourne un résumé de la performance
     * @return Résumé formaté
     */
    public String obtenirResume() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Niveau : ").append(nomNiveau).append(" ===\n");
        sb.append("Score : ").append(score).append("\n");
        sb.append("Pièces : ").append(pieces).append("\n");
        sb.append("Ennemis vaincus : ").append(ennemisVaincus).append("\n");
        sb.append("Temps : ").append(String.format("%.1f", tempsEcoule)).append("s\n");
        sb.append("Étoiles : ").append(etoiles).append("/3\n");
        sb.append("Tentatives : ").append(tentatives).append("\n");
        if (parfait) {
            sb.append("🏆 NIVEAU PARFAIT !\n");
        }
        return sb.toString();
    }
    
    // Getters et Setters
    public String getNomNiveau() {
        return nomNiveau;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getPieces() {
        return pieces;
    }
    
    public void setPieces(int pieces) {
        this.pieces = pieces;
    }
    
    public int getEnnemisVaincus() {
        return ennemisVaincus;
    }
    
    public void setEnnemisVaincus(int ennemisVaincus) {
        this.ennemisVaincus = ennemisVaincus;
    }
    
    public float getTempsEcoule() {
        return tempsEcoule;
    }
    
    public void setTempsEcoule(float tempsEcoule) {
        this.tempsEcoule = tempsEcoule;
    }
    
    public boolean estTermine() {
        return termine;
    }
    
    public void setTermine(boolean termine) {
        this.termine = termine;
    }
    
    public boolean estParfait() {
        return parfait;
    }
    
    public void setParfait(boolean parfait) {
        this.parfait = parfait;
    }
    
    public int getTentatives() {
        return tentatives;
    }
    
    public void setTentatives(int tentatives) {
        this.tentatives = tentatives;
    }
    
    public int getEtoiles() {
        return etoiles;
    }
    
    public void setEtoiles(int etoiles) {
        this.etoiles = etoiles;
    }
}

