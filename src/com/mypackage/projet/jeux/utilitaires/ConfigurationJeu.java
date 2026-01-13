package com.mypackage.projet.jeux.utilitaires;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour charger et gérer la configuration du jeu depuis un fichier JSON
 */
public class ConfigurationJeu {
    
    private String titre;
    private int largeurFenetre;
    private int hauteurFenetre;
    private boolean pleinEcran;
    private List<String> niveaux;
    
    /**
     * Constructeur par défaut
     */
    public ConfigurationJeu() {
        this.titre = "Jeu de Plateforme";
        this.largeurFenetre = 800;
        this.hauteurFenetre = 600;
        this.pleinEcran = false;
        this.niveaux = new ArrayList<>();
    }
    
    /**
     * Charge la configuration depuis un fichier JSON
     * @param cheminFichier Chemin vers le fichier de configuration
     * @return La configuration chargée
     */
    public static ConfigurationJeu chargerDepuisFichier(String cheminFichier) {
        try {
            FileHandle fichier = Gdx.files.internal(cheminFichier);
            if (!fichier.exists()) {
                return new ConfigurationJeu();
            }
            
            Json json = new Json();
            ConfigurationJeu config = json.fromJson(ConfigurationJeu.class, fichier);
            return config;
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la configuration : " + e.getMessage());
            return new ConfigurationJeu();
        }
    }
    
    /**
     * Sauvegarde la configuration dans un fichier JSON
     * @param cheminFichier Chemin où sauvegarder
     */
    public void sauvegarderDansFichier(String cheminFichier) {
        try {
            Json json = new Json();
            String jsonString = json.prettyPrint(this);
            
            FileHandle fichier = Gdx.files.local(cheminFichier);
            fichier.writeString(jsonString, false);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de la configuration : " + e.getMessage());
        }
    }
    
    // Getters et Setters
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public int getLargeurFenetre() {
        return largeurFenetre;
    }
    
    public void setLargeurFenetre(int largeurFenetre) {
        this.largeurFenetre = largeurFenetre;
    }
    
    public int getHauteurFenetre() {
        return hauteurFenetre;
    }
    
    public void setHauteurFenetre(int hauteurFenetre) {
        this.hauteurFenetre = hauteurFenetre;
    }
    
    public boolean isPleinEcran() {
        return pleinEcran;
    }
    
    public void setPleinEcran(boolean pleinEcran) {
        this.pleinEcran = pleinEcran;
    }
    
    public List<String> getNiveaux() {
        return niveaux;
    }
    
    public void setNiveaux(List<String> niveaux) {
        this.niveaux = niveaux;
    }
}


