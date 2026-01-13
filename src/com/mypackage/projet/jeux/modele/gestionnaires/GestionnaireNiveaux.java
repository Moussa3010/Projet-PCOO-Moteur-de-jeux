package com.mypackage.projet.jeux.modele.gestionnaires;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mypackage.projet.jeux.modele.niveau.Niveau;
import com.mypackage.projet.jeux.modele.niveau.ProgressionNiveau;
import com.mypackage.projet.jeux.utilitaires.ChargeurNiveau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire centralisé des niveaux du jeu.
 * 
 * <p>Responsabilités :</p>
 * <ul>
 *   <li>Chargement des niveaux depuis fichiers TMX</li>
 *   <li>Gestion des transitions entre niveaux</li>
 *   <li>Suivi de la progression globale</li>
 *   <li>Sauvegarde de l'historique</li>
 * </ul>
 * 
 * <p>Pattern : Singleton (recommandé pour usage futur)</p>
 * 
 * @author Projet MIAGE
 * @version 2.0
 * @since 2026-01-05
 */
public class GestionnaireNiveaux {
    
    /**
     * État de transition entre niveaux
     */
    public enum EtatTransition {
        AUCUNE,
        DEBUT_NIVEAU,
        FIN_NIVEAU,
        TRANSITION_EN_COURS
    }
    
    private List<Niveau> niveaux;
    private int niveauActuelIndex;
    private Niveau niveauActuel;
    private Map<String, ProgressionNiveau> historiqueProgressions;
    private EtatTransition etatTransition;
    private float tempsTransition;
    private static final float DUREE_TRANSITION = 2.0f; // 2 secondes
    private int scoreTotal;
    
    // Chargeur de niveaux (utilise l'ancien système éprouvé)
    private ChargeurNiveau chargeurNiveau;
    
    // Liste des chemins vers les fichiers TMX
    private List<String> cheminsNiveaux;
    
    /**
     * Constructeur par défaut.
     * Initialise le gestionnaire avec les 4 niveaux standards.
     */
    public GestionnaireNiveaux() {
        this.niveaux = new ArrayList<>();
        this.niveauActuelIndex = 0;
        this.historiqueProgressions = new HashMap<>();
        this.etatTransition = EtatTransition.AUCUNE;
        this.tempsTransition = 0;
        this.scoreTotal = 0;
        this.chargeurNiveau = new ChargeurNiveau();
        this.cheminsNiveaux = new ArrayList<>();
        
        // Configurer les niveaux par défaut
        configurerNiveauxParDefaut();
    }
    
    /**
     * Configure les chemins des 4 niveaux standards du jeu.
     */
    private void configurerNiveauxParDefaut() {
        cheminsNiveaux.add("assets/cartes/niveau1.tmx");
        cheminsNiveaux.add("assets/cartes/niveau2.tmx");
        cheminsNiveaux.add("assets/cartes/niveau3.tmx");
        cheminsNiveaux.add("assets/cartes/niveau4.tmx");
    }
    
    /**
     * Ajoute un niveau à la liste
     * @param niveau Le niveau à ajouter
     */
    public void ajouterNiveau(Niveau niveau) {
        niveaux.add(niveau);
    }
    
    /**
     * Charge le premier niveau depuis son fichier TMX.
     * Utilise le {@link ChargeurNiveau} pour parser le fichier Tiled.
     * 
     * @return Le niveau chargé, ou null en cas d'erreur
     */
    public Niveau chargerPremierNiveau() {
        if (cheminsNiveaux.isEmpty()) {
            return null;
        }
        
        niveauActuelIndex = 0;
        niveauActuel = chargerNiveauDepuisFichier(cheminsNiveaux.get(0));
        
        if (niveauActuel != null) {
            niveaux.clear();
            niveaux.add(niveauActuel);
            demarrerTransitionDebutNiveau();
        }
        
        return niveauActuel;
    }
    
    /**
     * Charge un niveau depuis un fichier TMX.
     * 
     * @param cheminFichier Chemin relatif ou absolu vers le fichier .tmx
     * @return Le niveau chargé, ou null en cas d'erreur
     */
    public Niveau chargerNiveauDepuisFichier(String cheminFichier) {
        try {
            Niveau niveau = chargeurNiveau.chargerNiveau(cheminFichier);
            return niveau;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Charge tous les niveaux configurés.
     * Utile pour le précalcul ou les menus de sélection de niveau.
     * 
     * @return Le nombre de niveaux chargés avec succès
     */
    public int chargerTousLesNiveaux() {
        niveaux.clear();
        int compteur = 0;
        
        for (String chemin : cheminsNiveaux) {
            Niveau niveau = chargerNiveauDepuisFichier(chemin);
            if (niveau != null) {
                niveaux.add(niveau);
                compteur++;
            }
        }
        
        if (!niveaux.isEmpty()) {
            niveauActuel = niveaux.get(0);
            niveauActuelIndex = 0;
        }
        
        return compteur;
    }
    
    /**
     * Met à jour le gestionnaire de niveaux
     * @param deltaTemps Temps écoulé
     */
    public void mettreAJour(float deltaTemps) {
        if (etatTransition != EtatTransition.AUCUNE) {
            mettreAJourTransition(deltaTemps);
        }
    }
    
    /**
     * Met à jour la transition en cours
     * @param deltaTemps Temps écoulé
     */
    private void mettreAJourTransition(float deltaTemps) {
        tempsTransition += deltaTemps;
        
        if (tempsTransition >= DUREE_TRANSITION) {
            terminerTransition();
        }
    }
    
    /**
     * Démarre une transition de début de niveau
     */
    private void demarrerTransitionDebutNiveau() {
        etatTransition = EtatTransition.DEBUT_NIVEAU;
        tempsTransition = 0;
    }
    
    /**
     * Démarre une transition de fin de niveau
     */
    private void demarrerTransitionFinNiveau() {
        etatTransition = EtatTransition.FIN_NIVEAU;
        tempsTransition = 0;
        
        // Sauvegarder la progression du niveau
        if (niveauActuel != null) {
            ProgressionNiveau prog = niveauActuel.getProgression();
            historiqueProgressions.put(niveauActuel.getNom(), prog);
            scoreTotal += prog.getScore();
        }
    }
    
    /**
     * Termine la transition en cours
     */
    private void terminerTransition() {
        etatTransition = EtatTransition.AUCUNE;
        tempsTransition = 0;
    }
    
    /**
     * Passe au niveau suivant.
     * Charge dynamiquement le prochain fichier TMX si nécessaire.
     * 
     * @return true si un niveau suivant existe et a été chargé, false sinon
     */
    public boolean niveauSuivant() {
        // Vérifier s'il reste des niveaux
        if (niveauActuelIndex >= cheminsNiveaux.size() - 1) {
            return false;
        }
        
            // Sauvegarder la progression du niveau actuel
            demarrerTransitionFinNiveau();
            
            // Passer au niveau suivant
            niveauActuelIndex++;
        
        // Charger le nouveau niveau depuis son fichier TMX
        if (niveauActuelIndex < cheminsNiveaux.size()) {
            String cheminProchainNiveau = cheminsNiveaux.get(niveauActuelIndex);
            niveauActuel = chargerNiveauDepuisFichier(cheminProchainNiveau);
            
            if (niveauActuel != null) {
                // S'assurer que la liste contient le niveau
                if (niveauActuelIndex >= niveaux.size()) {
                    niveaux.add(niveauActuel);
                } else {
                    niveaux.set(niveauActuelIndex, niveauActuel);
                }
            
            // Démarrer la transition du nouveau niveau
            etatTransition = EtatTransition.TRANSITION_EN_COURS;
            tempsTransition = 0;
            
            return true;
            } else {
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * Recharge le niveau actuel
     */
    public void rechargerNiveauActuel() {
        if (niveauActuel != null) {
            niveauActuel.reinitialiser();
            demarrerTransitionDebutNiveau();
        }
    }
    
    /**
     * Retourne au niveau précédent
     * @return true si possible, false sinon
     */
    public boolean niveauPrecedent() {
        if (niveauActuelIndex > 0) {
            niveauActuelIndex--;
            niveauActuel = niveaux.get(niveauActuelIndex);
            demarrerTransitionDebutNiveau();
            return true;
        }
        return false;
    }
    
    /**
     * Charge un niveau spécifique par son index
     * @param index Index du niveau
     * @return true si chargé avec succès, false sinon
     */
    public boolean chargerNiveau(int index) {
        if (index >= 0 && index < niveaux.size()) {
            niveauActuelIndex = index;
            niveauActuel = niveaux.get(niveauActuelIndex);
            demarrerTransitionDebutNiveau();
            return true;
        }
        return false;
    }
    
    /**
     * Charge un niveau par son nom
     * @param nom Nom du niveau
     * @return true si trouvé et chargé, false sinon
     */
    public boolean chargerNiveauParNom(String nom) {
        for (int i = 0; i < niveaux.size(); i++) {
            if (niveaux.get(i).getNom().equals(nom)) {
                return chargerNiveau(i);
            }
        }
        return false;
    }
    
    /**
     * Retourne la progression d'un niveau spécifique
     * @param nomNiveau Nom du niveau
     * @return La progression ou null
     */
    public ProgressionNiveau getProgressionNiveau(String nomNiveau) {
        return historiqueProgressions.get(nomNiveau);
    }
    
    /**
     * Retourne le score total de tous les niveaux
     * @return Score total
     */
    public int getScoreTotal() {
        return scoreTotal;
    }
    
    /**
     * Retourne le nombre total d'étoiles obtenues
     * @return Nombre d'étoiles
     */
    public int getTotalEtoiles() {
        int total = 0;
        for (ProgressionNiveau prog : historiqueProgressions.values()) {
            total += prog.getEtoiles();
        }
        return total;
    }
    
    /**
     * Retourne le nombre maximum d'étoiles possibles
     * @return Nombre maximum d'étoiles
     */
    public int getMaxEtoiles() {
        return niveaux.size() * 3;
    }
    
    /**
     * Affiche un récapitulatif complet de la progression
     */
    public void afficherRecapitulatif() {
        // Méthode vide - le récapitulatif est géré par l'UI
    }
    
    /**
     * Vérifie s'il reste des niveaux
     * @return true s'il reste des niveaux, false sinon
     */
    public boolean aDesNiveauxRestants() {
        return niveauActuelIndex < niveaux.size() - 1;
    }
    
    // Getters
    public Niveau getNiveauActuel() {
        return niveauActuel;
    }
    
    public int getNiveauActuelIndex() {
        return niveauActuelIndex;
    }
    
    public List<Niveau> getNiveaux() {
        return niveaux;
    }
    
    public EtatTransition getEtatTransition() {
        return etatTransition;
    }
    
    public float getTempsTransition() {
        return tempsTransition;
    }
    
    public float getProgressionTransition() {
        return Math.min(1.0f, tempsTransition / DUREE_TRANSITION);
    }
    
    public Map<String, ProgressionNiveau> getHistoriqueProgressions() {
        return historiqueProgressions;
    }
    
    /**
     * Vérifie si une transition est en cours.
     * 
     * @return true si en transition, false sinon
     */
    public boolean estEnTransition() {
        return etatTransition != EtatTransition.AUCUNE;
    }
    
    /**
     * Ajoute un chemin de niveau personnalisé.
     * Permet d'étendre le jeu avec des niveaux custom.
     * 
     * @param cheminFichier Chemin vers le fichier .tmx
     */
    public void ajouterCheminNiveau(String cheminFichier) {
        cheminsNiveaux.add(cheminFichier);
    }
    
    /**
     * Retourne le nombre total de niveaux configurés.
     * 
     * @return Nombre de niveaux
     */
    public int getNombreTotalNiveaux() {
        return cheminsNiveaux.size();
    }
    
    /**
     * Retourne le chemin du niveau actuel.
     * 
     * @return Chemin du fichier TMX, ou null si aucun niveau actuel
     */
    public String getCheminNiveauActuel() {
        if (niveauActuelIndex >= 0 && niveauActuelIndex < cheminsNiveaux.size()) {
            return cheminsNiveaux.get(niveauActuelIndex);
        }
        return null;
    }
    
    /**
     * Réinitialise complètement le gestionnaire.
     * Efface l'historique et recharge depuis le début.
     */
    public void reinitialiserComplet() {
        niveaux.clear();
        historiqueProgressions.clear();
        scoreTotal = 0;
        niveauActuelIndex = 0;
        niveauActuel = null;
        etatTransition = EtatTransition.AUCUNE;
        tempsTransition = 0;
    }
    
    /**
     * Libère les ressources (cartes Tiled).
     * À appeler lors de la fermeture du jeu.
     */
    public void libererRessources() {
        for (Niveau niveau : niveaux) {
            if (niveau != null && niveau.getCarte() != null) {
                niveau.getCarte().dispose();
            }
        }
        niveaux.clear();
    }
}
