package com.mypackage.projet.jeux.vue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Menu de fin de niveau professionnel style Super Mario
 * Utilise les coordonnées relatives à la caméra pour un affichage correct
 */
public class MenuFinNiveau {
    
    /**
     * Types de boutons disponibles
     */
    public enum TypeBouton {
        REJOUER,
        QUITTER,
        NIVEAU_SUIVANT
    }
    
    /**
     * Classe représentant un bouton cliquable
     */
    public static class Bouton {
        public String texte;
        public Rectangle zone;
        public TypeBouton type;
        public boolean survole;
        
        public Bouton(String texte, float x, float y, float largeur, float hauteur, TypeBouton type) {
            this.texte = texte;
            this.zone = new Rectangle(x, y, largeur, hauteur);
            this.type = type;
            this.survole = false;
        }
    }
    
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private BitmapFont policeTitre;
    private BitmapFont policeBouton;
    private GlyphLayout layout;
    
    Bouton[] boutons; // Package-private pour RenduTransition
    private boolean niveauReussi;
    
    // Dimensions du panneau
    private static final float LARGEUR_PANNEAU = 500;
    private static final float HAUTEUR_PANNEAU = 350;
    
    // Dimensions des boutons
    private static final float LARGEUR_BOUTON = 220;
    private static final float HAUTEUR_BOUTON = 60;
    private static final float ESPACEMENT_BOUTONS = 20;
    
    /**
     * Constructeur
     * @param camera La caméra du jeu (pour les coordonnées relatives)
     * @param niveauReussi true si le niveau est réussi, false si Game Over
     * @param aDesNiveauxRestants true s'il reste des niveaux à jouer
     */
    public MenuFinNiveau(OrthographicCamera camera, boolean niveauReussi, boolean aDesNiveauxRestants) {
        this.camera = camera;
        this.niveauReussi = niveauReussi;
        this.shapeRenderer = new ShapeRenderer();
        this.layout = new GlyphLayout();
        
        // Police pour le titre (grande)
        policeTitre = new BitmapFont();
        policeTitre.getData().setScale(4.0f);
        
        // Police pour les boutons
        policeBouton = new BitmapFont();
        policeBouton.getData().setScale(2.5f);
        
        initialiserBoutons(aDesNiveauxRestants);
    }
    
    /**
     * Initialise les boutons selon le contexte
     * Les positions seront recalculées à chaque frame pour suivre la caméra
     */
    private void initialiserBoutons(boolean aDesNiveauxRestants) {
        if (niveauReussi && aDesNiveauxRestants) {
            // 3 boutons : NIVEAU SUIVANT, REJOUER, QUITTER
            boutons = new Bouton[3];
            boutons[0] = new Bouton("NIVEAU SUIVANT", 0, 0, LARGEUR_BOUTON, HAUTEUR_BOUTON, TypeBouton.NIVEAU_SUIVANT);
            boutons[1] = new Bouton("REJOUER", 0, 0, LARGEUR_BOUTON, HAUTEUR_BOUTON, TypeBouton.REJOUER);
            boutons[2] = new Bouton("QUITTER", 0, 0, LARGEUR_BOUTON, HAUTEUR_BOUTON, TypeBouton.QUITTER);
        } else {
            // 2 boutons : REJOUER, QUITTER
            boutons = new Bouton[2];
            boutons[0] = new Bouton("REJOUER", 0, 0, LARGEUR_BOUTON, HAUTEUR_BOUTON, TypeBouton.REJOUER);
            boutons[1] = new Bouton("QUITTER", 0, 0, LARGEUR_BOUTON, HAUTEUR_BOUTON, TypeBouton.QUITTER);
        }
    }
    
    /**
     * Met à jour les positions des boutons relativement à la caméra
     */
    private void mettreAJourPositionsBoutons() {
        // Centre de la caméra
        float centerX = camera.position.x;
        float centerY = camera.position.y;
        
        // Position de départ pour les boutons (au centre du panneau)
        float startY = centerY - 50;
        
        // Calculer l'espacement total
        float espacementTotal = (boutons.length - 1) * (HAUTEUR_BOUTON + ESPACEMENT_BOUTONS);
        float premierBoutonY = startY + espacementTotal / 2;
        
        // Positionner chaque bouton
        for (int i = 0; i < boutons.length; i++) {
            Bouton bouton = boutons[i];
            bouton.zone.x = centerX - LARGEUR_BOUTON / 2;
            bouton.zone.y = premierBoutonY - i * (HAUTEUR_BOUTON + ESPACEMENT_BOUTONS);
        }
    }
    
    /**
     * Met à jour l'état de survol des boutons selon la position de la souris
     * Utilise camera.unproject pour convertir les coordonnées écran en coordonnées monde
     */
    public void mettreAJourSurvol() {
        // Convertir les coordonnées écran en coordonnées monde
        Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPoint);
        
        // Mettre à jour les positions des boutons avant de vérifier le survol
        mettreAJourPositionsBoutons();
        
        // Vérifier quel bouton est survolé
        for (Bouton bouton : boutons) {
            bouton.survole = bouton.zone.contains(touchPoint.x, touchPoint.y);
        }
    }
    
    /**
     * Vérifie si un bouton a été cliqué
     * @return Le type de bouton cliqué, ou null si aucun
     */
    public TypeBouton verifierClic() {
        // Convertir les coordonnées écran en coordonnées monde
        Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPoint);
        
        // Mettre à jour les positions des boutons avant de vérifier le clic
        mettreAJourPositionsBoutons();
        
        // Vérifier quel bouton a été cliqué
        for (Bouton bouton : boutons) {
            if (bouton.zone.contains(touchPoint.x, touchPoint.y)) {
                return bouton.type;
            }
        }
        return null;
    }
    
    /**
     * Dessine le menu complet
     * @param batch Le SpriteBatch pour le texte
     */
    public void dessiner(SpriteBatch batch) {
        // Mettre à jour les positions des boutons à chaque frame
        mettreAJourPositionsBoutons();
        
        dessinerFondEtBordure();
        dessinerTitre(batch);
        dessinerBoutons(batch);
    }
    
    /**
     * Dessine le fond noir semi-transparent et la bordure blanche
     */
    private void dessinerFondEtBordure() {
        // Centre de la caméra
        float centerX = camera.position.x;
        float centerY = camera.position.y;
        
        // Position du panneau (centré sur la caméra)
        float panneauX = centerX - LARGEUR_PANNEAU / 2;
        float panneauY = centerY - HAUTEUR_PANNEAU / 2;
        
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Fond noir semi-transparent plein écran (pour assombrir le jeu)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.75f);
        // Dessiner un grand rectangle centré sur la caméra
        shapeRenderer.rect(centerX - camera.viewportWidth / 2, 
                          centerY - camera.viewportHeight / 2,
                          camera.viewportWidth, 
                          camera.viewportHeight);
        shapeRenderer.end();
        
        // Panneau principal noir
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.95f);
        shapeRenderer.rect(panneauX, panneauY, LARGEUR_PANNEAU, HAUTEUR_PANNEAU);
        shapeRenderer.end();
        
        // Bordure blanche épaisse
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(5);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(panneauX, panneauY, LARGEUR_PANNEAU, HAUTEUR_PANNEAU);
        shapeRenderer.end();
        
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        Gdx.gl.glLineWidth(1); // Réinitialiser l'épaisseur de ligne
    }
    
    /**
     * Dessine le titre (GAME OVER ou COURSE CLEAR!)
     */
    private void dessinerTitre(SpriteBatch batch) {
        // Centre de la caméra
        float centerX = camera.position.x;
        float centerY = camera.position.y;
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        String titre;
        Color couleur;
        
        if (niveauReussi) {
            titre = "COURSE CLEAR!";
            couleur = new Color(1.0f, 0.84f, 0.0f, 1.0f); // Or/Jaune
        } else {
            titre = "GAME OVER";
            couleur = Color.RED;
        }
        
        policeTitre.setColor(couleur);
        
        // Utiliser GlyphLayout pour centrer parfaitement le texte
        layout.setText(policeTitre, titre);
        float x = centerX - layout.width / 2;
        float y = centerY + HAUTEUR_PANNEAU / 2 - 50;
        
        policeTitre.draw(batch, titre, x, y);
        
        batch.end();
    }
    
    /**
     * Dessine tous les boutons avec leur état (normal ou survolé)
     */
    private void dessinerBoutons(SpriteBatch batch) {
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        for (Bouton bouton : boutons) {
            // Dessiner le fond du bouton
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            
            if (bouton.survole) {
                // Fond jaune doré si survolé
                shapeRenderer.setColor(1.0f, 0.84f, 0.0f, 0.9f);
            } else {
                // Fond gris foncé normal
                shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.9f);
            }
            
            shapeRenderer.rect(bouton.zone.x, bouton.zone.y, bouton.zone.width, bouton.zone.height);
            shapeRenderer.end();
            
            // Bordure blanche
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            Gdx.gl.glLineWidth(3);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(bouton.zone.x, bouton.zone.y, bouton.zone.width, bouton.zone.height);
            shapeRenderer.end();
        }
        
        // Dessiner le texte des boutons
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        for (Bouton bouton : boutons) {
            if (bouton.survole) {
                policeBouton.setColor(Color.BLACK); // Texte noir si survolé
            } else {
                policeBouton.setColor(Color.WHITE); // Texte blanc normal
            }
            
            // Utiliser GlyphLayout pour centrer parfaitement le texte
            layout.setText(policeBouton, bouton.texte);
            float textX = bouton.zone.x + (bouton.zone.width - layout.width) / 2;
            float textY = bouton.zone.y + bouton.zone.height / 2 + layout.height / 2;
            
            policeBouton.draw(batch, bouton.texte, textX, textY);
        }
        
        batch.end();
        
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        Gdx.gl.glLineWidth(1); // Réinitialiser l'épaisseur de ligne
    }
    
    /**
     * Libère les ressources
     */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (policeTitre != null) {
            policeTitre.dispose();
        }
        if (policeBouton != null) {
            policeBouton.dispose();
        }
    }
}
