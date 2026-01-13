package com.mypackage.projet.jeux;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mypackage.projet.jeux.controleur.ControleurJeu;
import com.mypackage.projet.jeux.modele.gestionnaires.GestionnaireNiveaux;
import com.mypackage.projet.jeux.modele.niveau.Niveau;
import com.mypackage.projet.jeux.utilitaires.ChargeurNiveau;
import com.mypackage.projet.jeux.utilitaires.ConfigurationJeu;
import com.mypackage.projet.jeux.utilitaires.GestionnaireRessources;
import com.mypackage.projet.jeux.vue.RenduNiveau;
import com.mypackage.projet.jeux.vue.RenduTransition;
import com.mypackage.projet.jeux.vue.RenduHUD;

/**
 * Classe principale du jeu de plateforme
 * Point d'entrée de l'application LibGDX
 */
public class JeuPlateforme extends ApplicationAdapter {
    
    private ConfigurationJeu configuration;
    private ControleurJeu controleurJeu;
    private RenduNiveau rendeurNiveau;
    private RenduTransition rendeurTransition;
    private OrthographicCamera camera;
    private Viewport viewport;
    private GestionnaireRessources gestionnaireRessources;
    private ChargeurNiveau chargeurNiveau;
    
    /**
     * Méthode appelée au démarrage de l'application
     */
    @Override
    public void create() {
        Gdx.app.log("JeuPlateforme", "=== Démarrage du Jeu de Plateforme ===");
        
        // Charger la configuration
        configuration = ConfigurationJeu.chargerDepuisFichier("config/configuration.json");
        
        // Initialiser la caméra avec un viewport fixe pour maintenir le ratio d'aspect
        camera = new OrthographicCamera();
        viewport = new FitViewport(configuration.getLargeurFenetre(), configuration.getHauteurFenetre(), camera);
        viewport.apply();
        camera.position.set(configuration.getLargeurFenetre() / 2f, configuration.getHauteurFenetre() / 2f, 0);
        
        // Initialiser le gestionnaire de ressources
        gestionnaireRessources = new GestionnaireRessources();
        gestionnaireRessources.chargerRessources();
        
        // Initialiser le chargeur de niveaux
        chargeurNiveau = new ChargeurNiveau();
        
        // Initialiser le contrôleur de jeu
        controleurJeu = new ControleurJeu();
        
        // Charger les niveaux depuis la configuration
        chargerNiveaux();
        
        // Initialiser le rendu
        rendeurNiveau = new RenduNiveau(camera);
        rendeurTransition = new RenduTransition(camera);
        
        // Initialiser le jeu
        controleurJeu.initialiser();
        
        // Initialiser le rendu pour le niveau actuel
        Niveau niveauActuel = controleurJeu.getGestionnaireNiveaux().getNiveauActuel();
        if (niveauActuel != null) {
            rendeurNiveau.initialiserPourNiveau(niveauActuel);
            
            // Créer un joueur par défaut si aucun n'a été chargé
            if (niveauActuel.getJoueur() == null) {
                // Placer le joueur au dessus du sol
                // Le sol est à y=0, on place le joueur à y=64 (2 tiles au dessus)
                com.mypackage.projet.jeux.modele.entites.Joueur joueur = 
                    new com.mypackage.projet.jeux.modele.entites.Joueur(100, 64);
                niveauActuel.setJoueur(joueur);
                Gdx.app.log("JeuPlateforme", "✅ Joueur créé par défaut à (100, 64) avec " + joueur.getVies() + " vies");
            } else {
                // Le joueur a été chargé depuis Tiled
                com.mypackage.projet.jeux.modele.entites.Joueur joueur = niveauActuel.getJoueur();
                Gdx.app.log("JeuPlateforme", "✅ Joueur chargé depuis Tiled à (" + joueur.getPosition().x + 
                                 ", " + joueur.getPosition().y + ") avec " + joueur.getVies() + " vies");
            }
        }
        
        // Forcer le jeu à démarrer (pas en pause)
        controleurJeu.setEtatActuel(ControleurJeu.EtatJeu.EN_JEU);
        
        Gdx.app.log("JeuPlateforme", "=== Jeu initialisé avec succès ===");
        Gdx.app.log("JeuPlateforme", "🎮 État du jeu: " + controleurJeu.getEtatActuel());
        Gdx.app.log("JeuPlateforme", "🎮 Utilisez les FLÈCHES ← → pour bouger, ESPACE pour sauter");
    }
    
    /**
     * Charge tous les niveaux définis dans la configuration
     */
    private void chargerNiveaux() {
        GestionnaireNiveaux gestionnaireNiveaux = controleurJeu.getGestionnaireNiveaux();
        
        // Si des niveaux sont définis dans la configuration, les charger
        if (configuration.getNiveaux() != null && !configuration.getNiveaux().isEmpty()) {
            int numeroNiveau = 1;
            for (String cheminNiveau : configuration.getNiveaux()) {
                Niveau niveau = chargeurNiveau.chargerNiveau(cheminNiveau);
                if (niveau != null) {
                    // Configurer les objectifs et la position de fin
                    configurerNiveau(niveau, numeroNiveau);
                    gestionnaireNiveaux.ajouterNiveau(niveau);
                    Gdx.app.log("JeuPlateforme", "✅ Niveau " + numeroNiveau + " chargé : " + cheminNiveau);
                    numeroNiveau++;
                }
            }
        } else {
            // Charger un niveau par défaut
            Gdx.app.log("JeuPlateforme", "Aucun niveau défini dans la configuration, chargement du niveau par défaut");
            Niveau niveauDefaut = chargeurNiveau.chargerNiveau("assets/cartes/niveau1.tmx");
            if (niveauDefaut != null) {
                configurerNiveau(niveauDefaut, 1);
                gestionnaireNiveaux.ajouterNiveau(niveauDefaut);
            }
        }
    }
    
    /**
     * Configure les objectifs et paramètres d'un niveau
     * @param niveau Le niveau à configurer
     * @param numero Numéro du niveau
     */
    private void configurerNiveau(Niveau niveau, int numero) {
        // Définir la position de fin du niveau - Progression comme dans Mario
        // Niveau 1: 50% - Tutoriel/Facile
        // Niveau 2: 70% - Moyen
        // Niveau 3: 85% - Difficile
        // Niveau 4: 95% - Boss Final
        float pourcentage;
        String difficulte;
        
        switch (numero) {
            case 1:
                pourcentage = 0.50f;
                difficulte = "★☆☆☆ FACILE";
                break;
            case 2:
                pourcentage = 0.70f;
                difficulte = "★★☆☆ MOYEN";
                break;
            case 3:
                pourcentage = 0.85f;
                difficulte = "★★★☆ DIFFICILE";
                break;
            case 4:
                pourcentage = 0.95f;
                difficulte = "★★★★ BOSS FINAL";
                break;
            default:
                pourcentage = 0.95f;
                difficulte = "★★★★ NIVEAU BONUS";
                break;
        }
        
        float posFinX = niveau.getLargeur() * pourcentage;
        float posFinY = 100; // À 100 pixels du sol
        niveau.definirPositionFin(posFinX, posFinY);
        
        Gdx.app.log("JeuPlateforme", "\n╔═════════════════════════════════════════════════╗");
        Gdx.app.log("JeuPlateforme", "║  🎮 MONDE 1-" + numero + " : " + niveau.getNom());
        Gdx.app.log("JeuPlateforme", "╠═════════════════════════════════════════════════╣");
        Gdx.app.log("JeuPlateforme", "║  Difficulté : " + difficulte);
        Gdx.app.log("JeuPlateforme", "║  Distance : " + (int)posFinX + "px (" + (int)(pourcentage * 100) + "% du niveau)");
        Gdx.app.log("JeuPlateforme", "║  Objectifs : " + niveau.getObjectifs().size() + " objectif(s)");
        Gdx.app.log("JeuPlateforme", "║  Pièces : " + niveau.getTotalPiecesInitial());
        Gdx.app.log("JeuPlateforme", "║  Ennemis : " + niveau.getTotalEnnemisInitial());
        Gdx.app.log("JeuPlateforme", "╚═════════════════════════════════════════════════╝");
        Gdx.app.log("JeuPlateforme", "🎯 Mission : Atteindre X=" + (int)posFinX + " puis appuyez sur ENTRÉE\n");
    }
    
    /**
     * Méthode appelée à chaque frame pour le rendu
     */
    @Override
    public void render() {
        // Calculer le temps écoulé
        float deltaTemps = Gdx.graphics.getDeltaTime();
        
        // Mettre à jour le jeu
        controleurJeu.mettreAJour(deltaTemps);
        
        // Gérer les clics souris sur les menus de fin
        gererClicsMenu();
        
        // Faire suivre le joueur par la caméra
        Niveau niveauActuel = controleurJeu.getGestionnaireNiveaux().getNiveauActuel();
        if (niveauActuel != null && niveauActuel.getJoueur() != null) {
            com.mypackage.projet.jeux.modele.entites.Joueur joueur = niveauActuel.getJoueur();
            
            // Centrer la caméra sur le joueur (horizontalement)
            float targetX = joueur.getPosition().x + joueur.getLargeur() / 2;
            
            // Pour la caméra verticale, garder une position fixe pour voir le niveau
            float halfWidth = camera.viewportWidth / 2;
            float halfHeight = camera.viewportHeight / 2;
            
            // Centrer horizontalement sur le joueur
            camera.position.x = Math.max(halfWidth, Math.min(targetX, niveauActuel.getLargeur() - halfWidth));
            
            // Caméra fixe verticalement à mi-hauteur de l'écran pour bien voir le niveau
            camera.position.y = halfHeight;
            
            // DEBUG: Afficher la position toutes les 60 frames (1 fois par seconde à 60 FPS)
            if (Gdx.graphics.getFrameId() % 60 == 0) {
                Gdx.app.debug("JeuPlateforme", String.format("Joueur: (%.1f, %.1f) | Vitesse: (%.1f, %.1f) | auSol: %b | État: %s",
                    joueur.getPosition().x, 
                    joueur.getPosition().y,
                    joueur.getVitesse().x,
                    joueur.getVitesse().y,
                    joueur.estAuSol(),
                    joueur.getEtat()
                ));
            }
        }
        
        // Effacer l'écran
        Gdx.gl.glClearColor(0.53f, 0.81f, 0.92f, 1); // Couleur de ciel bleu
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Mettre à jour la caméra
        camera.update();
        
        // Dessiner le niveau
        if (niveauActuel != null) {
            // Dessiner le niveau de base (carte, entités, etc.)
            rendeurNiveau.dessiner(niveauActuel);
            
            // Dessiner le HUD amélioré si en jeu ou pendant la séquence de fin
            if (controleurJeu.getEtatActuel() == ControleurJeu.EtatJeu.EN_JEU ||
                controleurJeu.getEtatActuel() == ControleurJeu.EtatJeu.SEQUENCE_FIN_NIVEAU) {
                rendeurNiveau.dessinerHUD(
                    niveauActuel,
                    controleurJeu.getGestionnaireNiveaux().getNiveauActuelIndex() + 1
                );
            }
        }
        
        // Dessiner les transitions et écrans spéciaux
        GestionnaireNiveaux gestionnaire = controleurJeu.getGestionnaireNiveaux();
        switch (controleurJeu.getEtatActuel()) {
            case TRANSITION_NIVEAU:
                if (gestionnaire.estEnTransition()) {
                    switch (gestionnaire.getEtatTransition()) {
                        case DEBUT_NIVEAU:
                            rendeurTransition.dessinerDebutNiveau(
                                gestionnaire, 
                                gestionnaire.getProgressionTransition()
                            );
                            break;
                        case FIN_NIVEAU:
                            rendeurTransition.dessinerFinNiveau(
                                gestionnaire,
                                gestionnaire.getProgressionTransition()
                            );
                            break;
                        case TRANSITION_EN_COURS:
                            rendeurTransition.dessinerTransitionNiveau(
                                gestionnaire,
                                gestionnaire.getProgressionTransition()
                            );
                            break;
                    }
                }
                break;
                
            case RECAPITULATIF_NIVEAU:
                // Utiliser le menu de victoire interactif avec boutons (NIVEAU SUIVANT, REJOUER, QUITTER)
                if (niveauActuel != null) {
                    rendeurTransition.dessinerRecapitulatifNiveau(gestionnaire);
                }
                break;
                
            case GAME_OVER:
                rendeurTransition.dessinerGameOver(
                    controleurJeu.getGestionnaireNiveaux(), 
                    controleurJeu.getGestionnaireNiveaux().getNiveauActuelIndex() + 1
                );
                break;
                
            case VICTOIRE:
                rendeurTransition.dessinerVictoire(gestionnaire);
                break;
        }
        
        // Afficher les FPS dans le titre (pour le debug)
        Gdx.graphics.setTitle(configuration.getTitre() + " - FPS: " + Gdx.graphics.getFramesPerSecond());
    }
    
    /**
     * Méthode appelée lors du redimensionnement de la fenêtre
     * Le FitViewport maintient automatiquement le ratio d'aspect
     */
    @Override
    public void resize(int largeur, int hauteur) {
        viewport.update(largeur, hauteur, true);
        Gdx.app.log("JeuPlateforme", "🖥️  Fenêtre redimensionnée : " + largeur + "x" + hauteur);
    }
    
    /**
     * Méthode appelée lors de la mise en pause de l'application
     */
    @Override
    public void pause() {
        Gdx.app.log("JeuPlateforme", "Jeu en pause");
    }
    
    /**
     * Méthode appelée lors de la reprise de l'application
     */
    @Override
    public void resume() {
        Gdx.app.log("JeuPlateforme", "Jeu repris");
    }
    
    /**
     * Gère les clics souris sur les menus de fin (Game Over, Victoire, Récapitulatif)
     */
    private void gererClicsMenu() {
        // Vérifier si on est dans un état où un menu est affiché
        ControleurJeu.EtatJeu etat = controleurJeu.getEtatActuel();
        if (etat != ControleurJeu.EtatJeu.GAME_OVER && 
            etat != ControleurJeu.EtatJeu.VICTOIRE && 
            etat != ControleurJeu.EtatJeu.RECAPITULATIF_NIVEAU) {
            return;
        }
        
        // Vérifier si un clic vient d'être effectué
        if (!Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            return;
        }
        
        // Récupérer le menu approprié et vérifier quel bouton a été cliqué
        com.mypackage.projet.jeux.vue.MenuFinNiveau menu = null;
        
        if (etat == ControleurJeu.EtatJeu.GAME_OVER) {
            menu = rendeurTransition.getMenuGameOver();
        } else if (etat == ControleurJeu.EtatJeu.VICTOIRE) {
            menu = rendeurTransition.getMenuVictoire(false);
        } else if (etat == ControleurJeu.EtatJeu.RECAPITULATIF_NIVEAU) {
            boolean aDesNiveauxRestants = controleurJeu.getGestionnaireNiveaux().aDesNiveauxRestants();
            menu = rendeurTransition.getMenuVictoire(aDesNiveauxRestants);
        }
        
        if (menu != null) {
            // Le menu gère maintenant la conversion des coordonnées en interne
            com.mypackage.projet.jeux.vue.MenuFinNiveau.TypeBouton boutonClique = menu.verifierClic();
            
            if (boutonClique != null) {
                controleurJeu.gererClicBouton(boutonClique);
            }
        }
    }
    
    /**
     * Méthode appelée à la fermeture de l'application
     */
    @Override
    public void dispose() {
        Gdx.app.log("JeuPlateforme", "=== Fermeture du jeu ===");
        
        // Libérer les ressources
        if (rendeurNiveau != null) {
            rendeurNiveau.libererRessources();
        }
        if (rendeurTransition != null) {
            rendeurTransition.libererRessources();
        }
        if (gestionnaireRessources != null) {
            gestionnaireRessources.libererRessources();
        }
        
        Gdx.app.log("JeuPlateforme", "=== Ressources libérées ===");
    }
}


