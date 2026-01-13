package com.mypackage.projet.jeux.vue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mypackage.projet.jeux.modele.gestionnaires.GestionnaireNiveaux;
import com.mypackage.projet.jeux.modele.niveau.Niveau;
import com.mypackage.projet.jeux.modele.niveau.ObjectifNiveau;
import com.mypackage.projet.jeux.modele.niveau.ProgressionNiveau;

/**
 * Classe responsable du rendu des transitions entre niveaux
 */
public class RenduTransition {
    
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont fontGrand;
    private OrthographicCamera camera;
    private OrthographicCamera cameraUI; // Caméra fixe pour l'UI
    
    // Menu de fin de niveau
    private MenuFinNiveau menuGameOver;
    private MenuFinNiveau menuVictoire;
    
    /**
     * Constructeur
     * @param camera Caméra pour le rendu
     */
    public RenduTransition(OrthographicCamera camera) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.fontGrand = new BitmapFont();
        
        // Créer une caméra fixe pour l'UI (ne suit pas le joueur)
        this.cameraUI = new OrthographicCamera();
        this.cameraUI.setToOrtho(false, camera.viewportWidth, camera.viewportHeight);
        
        // Configurer les polices
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
        
        fontGrand.setColor(Color.YELLOW);
        fontGrand.getData().setScale(3.0f);
    }
    
    /**
     * Dessine la transition de début de niveau
     * @param gestionnaire Gestionnaire de niveaux
     * @param progression Progression de la transition (0 à 1)
     */
    public void dessinerDebutNiveau(GestionnaireNiveaux gestionnaire, float progression) {
        Niveau niveau = gestionnaire.getNiveauActuel();
        if (niveau == null) return;
        
        float largeurEcran = camera.viewportWidth;
        float hauteurEcran = camera.viewportHeight;
        
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Fond semi-transparent qui disparaît progressivement
        float alpha = 1.0f - progression;
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, alpha * 0.8f);
        shapeRenderer.rect(0, 0, largeurEcran, hauteurEcran);
        shapeRenderer.end();
        
        // Texte du niveau qui glisse depuis le haut
        batch.begin();
        
        float offsetY = (1.0f - progression) * 200 - 100;
        String texteNiveau = "NIVEAU " + (gestionnaire.getNiveauActuelIndex() + 1);
        float largeurTexte = fontGrand.getRegions().get(0).getRegionWidth() * texteNiveau.length() * 3.0f / 2;
        float x = (largeurEcran - largeurTexte) / 2;
        float y = hauteurEcran / 2 + offsetY + 50;
        
        fontGrand.setColor(1, 1, 0, alpha);
        fontGrand.draw(batch, texteNiveau, x, y);
        
        // Nom du niveau
        String nomNiveau = niveau.getNom();
        float largeurNom = font.getRegions().get(0).getRegionWidth() * nomNiveau.length() * 1.5f / 2;
        float xNom = (largeurEcran - largeurNom) / 2;
        float yNom = y - 60;
        
        font.setColor(1, 1, 1, alpha);
        font.draw(batch, nomNiveau, xNom, yNom);
        
        // Objectifs du niveau
        float yObjectif = yNom - 80;
        font.getData().setScale(1.2f);
        font.draw(batch, "OBJECTIFS:", 100, yObjectif);
        
        yObjectif -= 40;
        for (ObjectifNiveau objectif : niveau.getObjectifs()) {
            font.draw(batch, "• " + objectif.getDescription(), 120, yObjectif);
            yObjectif -= 30;
        }
        
        font.getData().setScale(1.5f);
        batch.end();
        
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
    }
    
    /**
     * Dessine la transition de fin de niveau
     * @param gestionnaire Gestionnaire de niveaux
     * @param progression Progression de la transition (0 à 1)
     */
    public void dessinerFinNiveau(GestionnaireNiveaux gestionnaire, float progression) {
        Niveau niveau = gestionnaire.getNiveauActuel();
        if (niveau == null) return;
        
        ProgressionNiveau prog = niveau.getProgression();
        float largeurEcran = camera.viewportWidth;
        float hauteurEcran = camera.viewportHeight;
        
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Fond qui apparaît progressivement
        float alpha = progression * 0.9f;
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, alpha);
        shapeRenderer.rect(0, 0, largeurEcran, hauteurEcran);
        shapeRenderer.end();
        
        // Panneau de résultats
        if (progression > 0.3f) {
            float panelAlpha = Math.min(1.0f, (progression - 0.3f) / 0.7f);
            
            batch.begin();
            
            float y = hauteurEcran / 2 + 150;
            
            // Titre
            fontGrand.setColor(0, 1, 0, panelAlpha);
            String titre = "NIVEAU TERMINÉ !";
            float largeurTitre = fontGrand.getRegions().get(0).getRegionWidth() * titre.length() * 3.0f / 2;
            fontGrand.draw(batch, titre, (largeurEcran - largeurTitre) / 2, y);
            
            y -= 80;
            
            // Statistiques
            font.setColor(1, 1, 1, panelAlpha);
            font.getData().setScale(1.8f);
            
            float x = largeurEcran / 2 - 150;
            
            font.draw(batch, "Score:", x, y);
            font.draw(batch, String.valueOf(prog.getScore()), x + 200, y);
            y -= 50;
            
            font.draw(batch, "Pièces:", x, y);
            font.draw(batch, prog.getPieces() + "/" + niveau.getTotalPiecesInitial(), x + 200, y);
            y -= 50;
            
            font.draw(batch, "Ennemis:", x, y);
            font.draw(batch, String.valueOf(prog.getEnnemisVaincus()), x + 200, y);
            y -= 50;
            
            font.draw(batch, "Temps:", x, y);
            font.draw(batch, String.format("%.1fs", prog.getTempsEcoule()), x + 200, y);
            y -= 50;
            
            // Étoiles
            font.getData().setScale(2.5f);
            font.setColor(1, 0.84f, 0, panelAlpha); // Couleur or
            String etoiles = "";
            for (int i = 0; i < prog.getEtoiles(); i++) {
                etoiles += "★ ";
            }
            for (int i = prog.getEtoiles(); i < 3; i++) {
                etoiles += "☆ ";
            }
            float largeurEtoiles = font.getRegions().get(0).getRegionWidth() * etoiles.length() * 2.5f / 2;
            font.draw(batch, etoiles, (largeurEcran - largeurEtoiles) / 2, y);
            
            // Niveau parfait ?
            if (prog.estParfait()) {
                y -= 60;
                font.getData().setScale(1.5f);
                font.setColor(1, 0, 1, panelAlpha); // Magenta
                String parfait = "★ NIVEAU PARFAIT ! ★";
                float largeurParfait = font.getRegions().get(0).getRegionWidth() * parfait.length() * 1.5f / 2;
                font.draw(batch, parfait, (largeurEcran - largeurParfait) / 2, y);
            }
            
            // Instructions - Plus visible
            y -= 80;
            font.getData().setScale(1.8f);
            font.setColor(1, 1, 0, panelAlpha); // Jaune pour plus de visibilité
            String instruction = "▶ ENTRÉE = Niveau Suivant ◀";
            float largeurInstruction = font.getRegions().get(0).getRegionWidth() * instruction.length() * 1.8f / 2;
            font.draw(batch, instruction, (largeurEcran - largeurInstruction) / 2, y);
            
            // Clignotement pour attirer l'attention
            y -= 40;
            font.getData().setScale(1.3f);
            if ((int)(System.currentTimeMillis() / 500) % 2 == 0) {
                font.setColor(1, 1, 1, panelAlpha);
                String espace = "(ou ESPACE)";
                float largeurEspace = font.getRegions().get(0).getRegionWidth() * espace.length() * 1.3f / 2;
                font.draw(batch, espace, (largeurEcran - largeurEspace) / 2, y);
            }
            
            font.getData().setScale(1.5f);
            batch.end();
        }
        
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
    }
    
    /**
     * Dessine la transition entre deux niveaux
     * @param gestionnaire Gestionnaire de niveaux
     * @param progression Progression (0 à 1)
     */
    public void dessinerTransitionNiveau(GestionnaireNiveaux gestionnaire, float progression) {
        float largeurEcran = camera.viewportWidth;
        float hauteurEcran = camera.viewportHeight;
        
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Effet de rideau qui se ferme puis s'ouvre
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        
        float largeurRideau;
        if (progression < 0.5f) {
            // Rideau se ferme
            largeurRideau = (progression * 2) * largeurEcran / 2;
        } else {
            // Rideau s'ouvre
            largeurRideau = (2 - progression * 2) * largeurEcran / 2;
        }
        
        // Rideau gauche
        shapeRenderer.rect(0, 0, largeurRideau, hauteurEcran);
        // Rideau droit
        shapeRenderer.rect(largeurEcran - largeurRideau, 0, largeurRideau, hauteurEcran);
        
        shapeRenderer.end();
        
        // Message au milieu
        if (progression > 0.4f && progression < 0.6f) {
            batch.begin();
            fontGrand.setColor(1, 1, 1, 1);
            String message = "NIVEAU " + (gestionnaire.getNiveauActuelIndex() + 1);
            float largeurMessage = fontGrand.getRegions().get(0).getRegionWidth() * message.length() * 3.0f / 2;
            fontGrand.draw(batch, message, (largeurEcran - largeurMessage) / 2, hauteurEcran / 2);
            batch.end();
        }
        
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
    }
    
    /**
     * Initialise ou récupère le menu de Game Over
     * @return Le menu de Game Over
     */
    public MenuFinNiveau getMenuGameOver() {
        if (menuGameOver == null) {
            menuGameOver = new MenuFinNiveau(camera, false, false);
        }
        return menuGameOver;
    }
    
    /**
     * Initialise ou récupère le menu de Victoire
     * @param aDesNiveauxRestants true s'il reste des niveaux
     * @return Le menu de Victoire
     */
    public MenuFinNiveau getMenuVictoire(boolean aDesNiveauxRestants) {
        if (menuVictoire == null || menuVictoire.boutons.length != (aDesNiveauxRestants ? 3 : 2)) {
            // Recréer le menu si le nombre de boutons a changé
            if (menuVictoire != null) {
                menuVictoire.dispose();
            }
            menuVictoire = new MenuFinNiveau(camera, true, aDesNiveauxRestants);
        }
        return menuVictoire;
    }
    
    /**
     * Dessine l'écran de Game Over simple (sans gestionnaire)
     */
    public void dessinerGameOver() {
        dessinerGameOver(null, 1);
    }
    
    /**
     * Dessine l'écran de Game Over amélioré avec menu interactif
     * @param gestionnaire Gestionnaire de niveaux (peut être null)
     * @param numeroNiveau Numéro du niveau actuel
     */
    public void dessinerGameOver(GestionnaireNiveaux gestionnaire, int numeroNiveau) {
        // Mettre à jour la caméra
        camera.update();
        
        // Utiliser le nouveau menu professionnel
        MenuFinNiveau menu = getMenuGameOver();
        
        // Mettre à jour le survol de la souris
        menu.mettreAJourSurvol();
        
        // Dessiner le menu
        menu.dessiner(batch);
    }
    
    /**
     * Dessine l'écran de récapitulatif de niveau (fin de niveau réussie)
     * Affiche le menu interactif avec NIVEAU SUIVANT, REJOUER, QUITTER
     * @param gestionnaire Gestionnaire de niveaux
     */
    public void dessinerRecapitulatifNiveau(GestionnaireNiveaux gestionnaire) {
        // Mettre à jour la caméra
        camera.update();
        
        // Vérifier s'il reste des niveaux pour afficher le bon menu
        boolean aDesNiveauxRestants = gestionnaire.aDesNiveauxRestants();
        MenuFinNiveau menu = getMenuVictoire(aDesNiveauxRestants);
        
        // Mettre à jour le survol de la souris
        menu.mettreAJourSurvol();
        
        // Dessiner le menu
        menu.dessiner(batch);
    }
    
    /**
     * Dessine l'écran de victoire finale avec menu interactif
     * (Tous les niveaux ont été terminés)
     * @param gestionnaire Gestionnaire de niveaux
     */
    public void dessinerVictoire(GestionnaireNiveaux gestionnaire) {
        // Mettre à jour la caméra
        camera.update();
        
        // Utiliser le nouveau menu professionnel (victoire finale = pas de niveaux restants)
        MenuFinNiveau menu = getMenuVictoire(false);
        
        // Mettre à jour le survol de la souris
        menu.mettreAJourSurvol();
        
        // Dessiner le menu
        menu.dessiner(batch);
    }
    
    /**
     * Libère les ressources
     */
    public void libererRessources() {
        if (batch != null) {
            batch.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (fontGrand != null) {
            fontGrand.dispose();
        }
        if (menuGameOver != null) {
            menuGameOver.dispose();
        }
        if (menuVictoire != null) {
            menuVictoire.dispose();
        }
    }
}

