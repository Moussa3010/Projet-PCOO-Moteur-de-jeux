package com.mypackage.projet.jeux.vue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mypackage.projet.jeux.modele.entites.Joueur;
import com.mypackage.projet.jeux.modele.niveau.Niveau;

/**
 * HUD refait avec approche "Pixel Perfect" et grille stricte
 * Layout à 3 colonnes : GAUCHE (Score/Vies/Pièces) | CENTRE (World) | DROITE (Time)
 */
public class RenduHUD {
    
    // ========== CONSTANTES DE LAYOUT ==========
    private static final float HUD_HEIGHT = 80f;          // Hauteur de la zone HUD
    private static final float MARGIN_HORIZONTAL = 20f;   // Marge gauche/droite
    private static final float MARGIN_TOP = 15f;          // Marge du haut
    private static final float LINE_SPACING = 30f;        // Espacement entre lignes
    private static final float ICON_SIZE = 24f;           // Taille des icônes
    private static final float ICON_TEXT_SPACING = 5f;   // Espace entre icône et texte
    
    // ========== COMPOSANTS ==========
    private BitmapFont policeLabel;      // Pour MARIO, WORLD, TIME
    private BitmapFont policeValeur;     // Pour les valeurs (score, temps, etc.)
    private OrthographicCamera cameraHUD;
    private ShapeRenderer shapeRenderer;
    private GlyphLayout layout;          // Pour mesurer et centrer les textes
    
    // Icônes
    private Texture iconePiece;
    private Texture iconeVie;
    private Texture iconeEtoile;
    
    /**
     * Constructeur
     */
    public RenduHUD() {
        // Police pour les labels (MARIO, WORLD, TIME) - Grande
        policeLabel = new BitmapFont();
        policeLabel.setColor(Color.WHITE);
        policeLabel.getData().setScale(2.2f);
        
        // Police pour les valeurs (score, temps, pièces, vies) - Moyenne
        policeValeur = new BitmapFont();
        policeValeur.setColor(Color.WHITE);
        policeValeur.getData().setScale(1.8f);
        
        shapeRenderer = new ShapeRenderer();
        layout = new GlyphLayout();
        
        // Caméra fixe pour le HUD (ne suit pas le joueur)
        cameraHUD = new OrthographicCamera();
        cameraHUD.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // Charger les icônes
        chargerIcones();
    }
    
    /**
     * Charge toutes les icônes du HUD
     */
    private void chargerIcones() {
        try {
            iconePiece = new Texture("assets/textures/coin_hud.png");
        } catch (Exception e) {
            Gdx.app.error("RenduHUD", "⚠️ Icône pièce manquante");
        }
        
        try {
            iconeVie = new Texture("assets/textures/hud_life_icon.png");
        } catch (Exception e) {
            Gdx.app.error("RenduHUD", "⚠️ Icône vie manquante");
        }
        
        try {
            iconeEtoile = new Texture("assets/textures/hud_star_icon.png");
        } catch (Exception e) {
            Gdx.app.error("RenduHUD", "⚠️ Icône étoile manquante");
        }
    }
    
    /**
     * Dessine le HUD simple (sans niveau)
     * @param batch Le SpriteBatch
     * @param joueur Le joueur dont on affiche les informations
     */
    public void dessiner(SpriteBatch batch, Joueur joueur) {
        dessinerAvecNiveau(batch, joueur, null, 1);
    }
    
    /**
     * Dessine le HUD avec les informations du niveau (approche Pixel Perfect)
     * @param batch Le SpriteBatch
     * @param joueur Le joueur
     * @param niveau Le niveau actuel (peut être null)
     * @param numeroNiveau Numéro du niveau
     */
    public void dessinerAvecNiveau(SpriteBatch batch, Joueur joueur, Niveau niveau, int numeroNiveau) {
        if (joueur == null) {
            return;
        }
        
        // IMPORTANT : Mettre à jour la caméra HUD avec les dimensions actuelles
        // (pour supporter le plein écran et le redimensionnement)
        float largeurEcran = Gdx.graphics.getWidth();
        float hauteurEcran = Gdx.graphics.getHeight();
        cameraHUD.setToOrtho(false, largeurEcran, hauteurEcran);
        cameraHUD.update();
        
        // 1. Dessiner le fond noir de la Top Bar
        dessinerFondHUD(largeurEcran, hauteurEcran);
        
        // 2. Calculer les coordonnées des 3 colonnes
        float colGaucheX = MARGIN_HORIZONTAL;
        float colCentreX = largeurEcran / 2f;
        float colDroiteX = largeurEcran - MARGIN_HORIZONTAL;
        
        // 3. Calculer les lignes verticales
        float ligneHaute = hauteurEcran - MARGIN_TOP;
        float ligneMoyenne = ligneHaute - LINE_SPACING;
        float ligneBasse = ligneMoyenne - LINE_SPACING;
        
        // 4. Dessiner les 3 colonnes
        batch.setProjectionMatrix(cameraHUD.combined);
        batch.begin();
        
        // ========== COLONNE GAUCHE : MARIO, Score, Pièces, Vies ==========
        dessinerColonneGauche(batch, joueur, colGaucheX, ligneHaute, ligneMoyenne, ligneBasse);
        
        // ========== COLONNE CENTRE : WORLD, Niveau ==========
        dessinerColonneCentre(batch, colCentreX, ligneHaute, ligneMoyenne, numeroNiveau);
        
        // ========== COLONNE DROITE : TIME, Temps ========== 
        // Timer désactivé sur demande de l'utilisateur
        // dessinerColonneDroite(batch, niveau, colDroiteX, ligneHaute, ligneMoyenne);
        
        batch.end();
    }
    
    /**
     * Dessine le fond noir semi-transparent de la zone HUD
     */
    private void dessinerFondHUD(float largeurEcran, float hauteurEcran) {
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.setProjectionMatrix(cameraHUD.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Fond noir semi-transparent
        shapeRenderer.setColor(0, 0, 0, 0.85f);
        shapeRenderer.rect(0, hauteurEcran - HUD_HEIGHT, largeurEcran, HUD_HEIGHT);
        
        // Ligne de séparation blanche
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.rect(0, hauteurEcran - HUD_HEIGHT - 2, largeurEcran, 2);
        
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
    }
    
    /**
     * Dessine la colonne gauche : MARIO, Score, Pièces, Vies
     * Alignement : GAUCHE
     */
    private void dessinerColonneGauche(SpriteBatch batch, Joueur joueur, float x, float y1, float y2, float y3) {
        // Ligne 1 : Label "MARIO"
        policeLabel.setColor(Color.WHITE);
        policeLabel.draw(batch, "MARIO", x, y1);
        
        // Ligne 2 : Score (6 chiffres, jaune)
        String scoreStr = String.format("%06d", joueur.getScore());
        policeValeur.setColor(Color.YELLOW);
        policeValeur.draw(batch, scoreStr, x, y2);
        
        // Ligne 3 : Pièces (icône + texte) ET Vies (icône + texte) sur la même ligne
        // Pièces à gauche
        if (iconePiece != null) {
            float iconY = y3 - ICON_SIZE / 2;
            batch.draw(iconePiece, x, iconY, ICON_SIZE, ICON_SIZE);
            
            String piecesStr = "x" + joueur.getPieces();
            policeValeur.setColor(Color.WHITE);
            policeValeur.draw(batch, piecesStr, x + ICON_SIZE + ICON_TEXT_SPACING, y3);
        }
        
        // Vies à droite (décalées de 100px)
        if (iconeVie != null) {
            float vieX = x + 100f;
            float iconY = y3 - ICON_SIZE / 2;
            batch.draw(iconeVie, vieX, iconY, ICON_SIZE, ICON_SIZE);
            
            String viesStr = "x" + joueur.getVies();
            policeValeur.setColor(Color.WHITE);
            policeValeur.draw(batch, viesStr, vieX + ICON_SIZE + ICON_TEXT_SPACING, y3);
        }
    }
    
    /**
     * Dessine la colonne centre : WORLD, Niveau
     * Alignement : CENTRE (utilise GlyphLayout pour centrage parfait)
     */
    private void dessinerColonneCentre(SpriteBatch batch, float centerX, float y1, float y2, int numeroNiveau) {
        // Ligne 1 : Label "WORLD" (centré)
        policeLabel.setColor(Color.WHITE);
        layout.setText(policeLabel, "WORLD");
        float worldX = centerX - layout.width / 2;
        policeLabel.draw(batch, "WORLD", worldX, y1);
        
        // Ligne 2 : Numéro de niveau "1-1" (centré)
        String niveauStr = "1-" + numeroNiveau;
        policeValeur.setColor(Color.WHITE);
        layout.setText(policeValeur, niveauStr);
        float niveauX = centerX - layout.width / 2;
        policeValeur.draw(batch, niveauStr, niveauX, y2);
    }
    
    /**
     * Dessine la colonne droite : TIME, Temps
     * Alignement : DROITE (utilise GlyphLayout pour alignement à droite)
     */
    private void dessinerColonneDroite(SpriteBatch batch, Niveau niveau, float rightX, float y1, float y2) {
        // Ligne 1 : Label "TIME" (aligné à droite)
        policeLabel.setColor(Color.WHITE);
        layout.setText(policeLabel, "TIME");
        float timeX = rightX - layout.width;
        policeLabel.draw(batch, "TIME", timeX, y1);
        
        // Ligne 2 : Temps (3 chiffres, aligné à droite)
        int tempsAffiche = 999; // Valeur par défaut
        if (niveau != null) {
            int tempsSecondes = (int) niveau.getProgression().getTempsEcoule();
            tempsAffiche = Math.max(0, 999 - tempsSecondes);
        }
        
        String tempsStr = String.format("%03d", tempsAffiche);
        policeValeur.setColor(Color.WHITE);
        layout.setText(policeValeur, tempsStr);
        float tempsX = rightX - layout.width;
        policeValeur.draw(batch, tempsStr, tempsX, y2);
    }
    
    /**
     * Dessine un message d'information temporaire
     * @param batch Le SpriteBatch
     * @param message Le message à afficher
     * @param x Position X
     * @param y Position Y
     */
    public void dessinerMessage(SpriteBatch batch, String message, float x, float y) {
        // Mettre à jour la caméra pour supporter le plein écran
        cameraHUD.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraHUD.update();
        
        batch.setProjectionMatrix(cameraHUD.combined);
        batch.begin();
        policeValeur.setColor(Color.YELLOW);
        policeValeur.draw(batch, message, x, y);
        batch.end();
    }
    
    /**
     * Dessine un écran de fin de niveau avec menu interactif
     * @param batch Le SpriteBatch
     * @param niveauReussi true si le niveau est réussi
     * @param menuFinNiveau Le menu de fin de niveau à dessiner
     */
    public void dessinerEcranFin(SpriteBatch batch, boolean niveauReussi, MenuFinNiveau menuFinNiveau) {
        // Mettre à jour la caméra pour supporter le plein écran
        cameraHUD.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraHUD.update();
        
        batch.setProjectionMatrix(cameraHUD.combined);
        
        if (menuFinNiveau != null) {
            // Utiliser le nouveau menu professionnel
            menuFinNiveau.dessiner(batch);
        } else {
            // Fallback : ancien affichage simple
            dessinerEcranFinSimple(batch, niveauReussi);
        }
    }
    
    /**
     * Dessine un écran de fin simple (fallback)
     * @param batch Le SpriteBatch
     * @param niveauReussi true si le niveau est réussi
     */
    private void dessinerEcranFinSimple(SpriteBatch batch, boolean niveauReussi) {
        // Mettre à jour la caméra pour supporter le plein écran
        float largeur = Gdx.graphics.getWidth();
        float hauteur = Gdx.graphics.getHeight();
        cameraHUD.setToOrtho(false, largeur, hauteur);
        cameraHUD.update();
        
        // Fond noir semi-transparent
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(cameraHUD.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, largeur, hauteur);
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        
        batch.begin();
        
        float y = hauteur / 2 + 50;
        String message = niveauReussi ? "COURSE CLEAR!" : "GAME OVER";
        policeLabel.setColor(niveauReussi ? Color.YELLOW : Color.RED);
        
        layout.setText(policeLabel, message);
        float x = largeur / 2 - layout.width / 2;
        
        policeLabel.draw(batch, message, x, y);
        
        if (niveauReussi && iconeEtoile != null) {
            batch.draw(iconeEtoile, largeur / 2 - 32, y - 80, 64, 64);
        }
        
        batch.end();
    }
    
    /**
     * Libère les ressources
     */
    public void libererRessources() {
        if (policeLabel != null) {
            policeLabel.dispose();
        }
        if (policeValeur != null) {
            policeValeur.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (iconePiece != null) {
            iconePiece.dispose();
        }
        if (iconeVie != null) {
            iconeVie.dispose();
        }
        if (iconeEtoile != null) {
            iconeEtoile.dispose();
    }
}
}
