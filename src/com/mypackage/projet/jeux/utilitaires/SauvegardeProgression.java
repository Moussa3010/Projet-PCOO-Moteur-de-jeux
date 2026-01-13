package com.mypackage.projet.jeux.utilitaires;

import com.mypackage.projet.jeux.modele.niveau.ProgressionNiveau;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitaire pour sauvegarder et charger la progression du joueur
 */
public class SauvegardeProgression {
    
    private static final String FICHIER_SAUVEGARDE = "sauvegarde_progression.dat";
    private static final String DOSSIER_SAUVEGARDE = "saves";
    
    /**
     * Classe représentant une sauvegarde complète
     */
    public static class Sauvegarde implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public int niveauActuelIndex;
        public int scoreTotal;
        public Map<String, DonneesProgression> progressions;
        public long dateCreation;
        public String nomJoueur;
        
        public Sauvegarde() {
            this.progressions = new HashMap<>();
            this.dateCreation = System.currentTimeMillis();
            this.nomJoueur = "Joueur";
        }
    }
    
    /**
     * Données de progression sérialisables
     */
    public static class DonneesProgression implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public String nomNiveau;
        public int score;
        public int pieces;
        public int ennemisVaincus;
        public float tempsEcoule;
        public boolean termine;
        public boolean parfait;
        public int tentatives;
        public int etoiles;
        
        public DonneesProgression() {}
        
        public DonneesProgression(ProgressionNiveau prog) {
            this.nomNiveau = prog.getNomNiveau();
            this.score = prog.getScore();
            this.pieces = prog.getPieces();
            this.ennemisVaincus = prog.getEnnemisVaincus();
            this.tempsEcoule = prog.getTempsEcoule();
            this.termine = prog.estTermine();
            this.parfait = prog.estParfait();
            this.tentatives = prog.getTentatives();
            this.etoiles = prog.getEtoiles();
        }
        
        public ProgressionNiveau versProgressionNiveau() {
            ProgressionNiveau prog = new ProgressionNiveau(nomNiveau);
            prog.setScore(score);
            prog.setPieces(pieces);
            prog.setEnnemisVaincus(ennemisVaincus);
            prog.setTempsEcoule(tempsEcoule);
            prog.setTermine(termine);
            prog.setParfait(parfait);
            prog.setTentatives(tentatives);
            prog.setEtoiles(etoiles);
            return prog;
        }
    }
    
    /**
     * Sauvegarde la progression dans un fichier
     * @param sauvegarde Données à sauvegarder
     * @param nomFichier Nom du fichier (optionnel, utilise le nom par défaut si null)
     * @return true si succès, false sinon
     */
    public static boolean sauvegarder(Sauvegarde sauvegarde, String nomFichier) {
        try {
            // Créer le dossier de sauvegarde s'il n'existe pas
            File dossier = new File(DOSSIER_SAUVEGARDE);
            if (!dossier.exists()) {
                dossier.mkdirs();
            }
            
            String fichier = nomFichier != null ? nomFichier : FICHIER_SAUVEGARDE;
            String cheminComplet = DOSSIER_SAUVEGARDE + File.separator + fichier;
            
            // Écrire la sauvegarde
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(cheminComplet))) {
                oos.writeObject(sauvegarde);
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la sauvegarde : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sauvegarde avec le nom par défaut
     * @param sauvegarde Données à sauvegarder
     * @return true si succès, false sinon
     */
    public static boolean sauvegarder(Sauvegarde sauvegarde) {
        return sauvegarder(sauvegarde, null);
    }
    
    /**
     * Charge la progression depuis un fichier
     * @param nomFichier Nom du fichier (optionnel, utilise le nom par défaut si null)
     * @return La sauvegarde chargée ou null
     */
    public static Sauvegarde charger(String nomFichier) {
        try {
            String fichier = nomFichier != null ? nomFichier : FICHIER_SAUVEGARDE;
            String cheminComplet = DOSSIER_SAUVEGARDE + File.separator + fichier;
            
            File file = new File(cheminComplet);
            if (!file.exists()) {
                return null;
            }
            
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(cheminComplet))) {
                Sauvegarde sauvegarde = (Sauvegarde) ois.readObject();
                return sauvegarde;
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Erreur lors du chargement : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Charge avec le nom par défaut
     * @return La sauvegarde chargée ou null
     */
    public static Sauvegarde charger() {
        return charger(null);
    }
    
    /**
     * Supprime une sauvegarde
     * @param nomFichier Nom du fichier (optionnel)
     * @return true si supprimé, false sinon
     */
    public static boolean supprimer(String nomFichier) {
        try {
            String fichier = nomFichier != null ? nomFichier : FICHIER_SAUVEGARDE;
            String cheminComplet = DOSSIER_SAUVEGARDE + File.separator + fichier;
            
            File file = new File(cheminComplet);
            if (file.exists()) {
                boolean resultat = file.delete();
                return resultat;
            }
            return false;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Vérifie si une sauvegarde existe
     * @param nomFichier Nom du fichier (optionnel)
     * @return true si elle existe, false sinon
     */
    public static boolean existe(String nomFichier) {
        String fichier = nomFichier != null ? nomFichier : FICHIER_SAUVEGARDE;
        String cheminComplet = DOSSIER_SAUVEGARDE + File.separator + fichier;
        return new File(cheminComplet).exists();
    }
    
    /**
     * Vérifie si la sauvegarde par défaut existe
     * @return true si elle existe, false sinon
     */
    public static boolean existe() {
        return existe(null);
    }
    
    /**
     * Liste tous les fichiers de sauvegarde
     * @return Tableau des noms de fichiers
     */
    public static String[] listerSauvegardes() {
        File dossier = new File(DOSSIER_SAUVEGARDE);
        if (!dossier.exists() || !dossier.isDirectory()) {
            return new String[0];
        }
        
        return dossier.list((dir, name) -> name.endsWith(".dat"));
    }
}

