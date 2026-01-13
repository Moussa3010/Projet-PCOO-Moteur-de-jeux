package com.mypackage.projet.jeux.vue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mypackage.projet.jeux.modele.entites.Entite;
import com.mypackage.projet.jeux.modele.entites.Joueur;

/**
 * Classe responsable du rendu du joueur avec animations fluides
 */
public class RenduJoueur implements RenduEntite {
    
    // ========== TEXTURES MARIO PETIT ==========
    private Texture texturePetitIdle;
    private Texture texturePetitJump;
    private Texture texturePetitRun1;
    private Texture texturePetitRun2;
    
    // ========== TEXTURES MARIO GRAND ==========
    private Texture textureGrandIdle;
    private Texture textureGrandJump;
    private Texture textureGrandRun1;
    private Texture textureGrandRun2;
    
    // ========== ANIMATIONS MARIO PETIT ==========
    private Animation<TextureRegion> animationPetitImmobile;
    private Animation<TextureRegion> animationPetitCourse;
    private Animation<TextureRegion> animationPetitSaut;
    
    // ========== ANIMATIONS MARIO GRAND ==========
    private Animation<TextureRegion> animationGrandImmobile;
    private Animation<TextureRegion> animationGrandCourse;
    private Animation<TextureRegion> animationGrandSaut;
    
    // Textures legacy (pour compatibilité)
    private Texture textureSpritesheet;
    
    // Temps d'animation
    private float tempsEcoule = 0f;
    
    // Direction du joueur (pour le flip horizontal)
    private boolean regardeADroite = true;
    
    /**
     * Constructeur
     * @param cheminTexture Chemin vers la texture du joueur (non utilisé, on charge les sprites Mario)
     */
    public RenduJoueur(String cheminTexture) {
        chargerTextures();
        creerAnimations();
    }
    
    /**
     * Charge toutes les textures de Mario (PETIT et GRAND)
     */
    private void chargerTextures() {
        try {
            // ========== CHARGER MARIO GRAND (versions "_big") ==========
            textureGrandIdle = new Texture("assets/textures/mario_idle_big.png");
            textureGrandJump = new Texture("assets/textures/mario_jump_big.png");
            textureGrandRun1 = new Texture("assets/textures/mario_run1_big.png");
            textureGrandRun2 = new Texture("assets/textures/mario_run2_big.png");
            
            // ========== CHARGER MARIO PETIT (versions sans "_big") ==========
            // Note : Si les fichiers n'existent pas, on utilisera les versions "_big" en fallback
            try {
                texturePetitIdle = new Texture("assets/textures/mario_idle.png");
                texturePetitJump = new Texture("assets/textures/mario_jump.png");
                texturePetitRun1 = new Texture("assets/textures/mario_run1.png");
                texturePetitRun2 = new Texture("assets/textures/mario_run2.png");
            } catch (Exception e) {
                // Fallback : Utiliser les textures GRAND pour PETIT (redimensionnées)
                texturePetitIdle = textureGrandIdle;
                texturePetitJump = textureGrandJump;
                texturePetitRun1 = textureGrandRun1;
                texturePetitRun2 = textureGrandRun2;
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des textures Mario : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crée les animations pour chaque état du joueur (PETIT et GRAND)
     */
    private void creerAnimations() {
        try {
            // ========== ANIMATIONS MARIO GRAND ==========
            Array<TextureRegion> framesGrandImmobile = new Array<>();
            framesGrandImmobile.add(new TextureRegion(textureGrandIdle));
            animationGrandImmobile = new Animation<>(0.2f, framesGrandImmobile, Animation.PlayMode.LOOP);
            
            Array<TextureRegion> framesGrandCourse = new Array<>();
            framesGrandCourse.add(new TextureRegion(textureGrandRun1));
            framesGrandCourse.add(new TextureRegion(textureGrandRun2));
            framesGrandCourse.add(new TextureRegion(textureGrandRun1));
            framesGrandCourse.add(new TextureRegion(textureGrandIdle));
            animationGrandCourse = new Animation<>(0.1f, framesGrandCourse, Animation.PlayMode.LOOP);
            
            Array<TextureRegion> framesGrandSaut = new Array<>();
            framesGrandSaut.add(new TextureRegion(textureGrandJump));
            animationGrandSaut = new Animation<>(0.2f, framesGrandSaut, Animation.PlayMode.NORMAL);
            
            // ========== ANIMATIONS MARIO PETIT ==========
            Array<TextureRegion> framesPetitImmobile = new Array<>();
            framesPetitImmobile.add(new TextureRegion(texturePetitIdle));
            animationPetitImmobile = new Animation<>(0.2f, framesPetitImmobile, Animation.PlayMode.LOOP);
            
            Array<TextureRegion> framesPetitCourse = new Array<>();
            framesPetitCourse.add(new TextureRegion(texturePetitRun1));
            framesPetitCourse.add(new TextureRegion(texturePetitRun2));
            framesPetitCourse.add(new TextureRegion(texturePetitRun1));
            framesPetitCourse.add(new TextureRegion(texturePetitIdle));
            animationPetitCourse = new Animation<>(0.1f, framesPetitCourse, Animation.PlayMode.LOOP);
            
            Array<TextureRegion> framesPetitSaut = new Array<>();
            framesPetitSaut.add(new TextureRegion(texturePetitJump));
            animationPetitSaut = new Animation<>(0.2f, framesPetitSaut, Animation.PlayMode.NORMAL);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création des animations : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void dessiner(SpriteBatch batch, Entite entite) {
        if (!(entite instanceof Joueur)) {
            return;
        }
        
        Joueur joueur = (Joueur) entite;
        if (!joueur.estActive()) {
            return;
        }
        
        // Vérifier si le joueur est visible (pour la séquence de fin de niveau)
        if (!joueur.estVisible()) {
            return;
        }
        
        // Mettre à jour le temps d'animation
        tempsEcoule += 0.016f; // ~60 FPS
        
        // Déterminer la direction du joueur
        if (joueur.getVitesse().x > 0.1f) {
            regardeADroite = true;
        } else if (joueur.getVitesse().x < -0.1f) {
            regardeADroite = false;
        }
        
        // Obtenir la frame actuelle de l'animation
        TextureRegion frameActuelle = obtenirFrameActuelle(joueur);
        
        if (frameActuelle != null) {
            // Flip horizontal si le joueur regarde à gauche
            if (!regardeADroite && !frameActuelle.isFlipX()) {
                frameActuelle.flip(true, false);
            } else if (regardeADroite && frameActuelle.isFlipX()) {
                frameActuelle.flip(true, false);
            }
        
            // ========== EFFET DE CLIGNOTEMENT (Invincibilité) ==========
            // Si Mario est invincible, le faire clignoter
            boolean dessinerSprite = true;
            if (joueur.estInvincible()) {
                // Clignoter 10 fois par seconde (100ms on, 100ms off)
                float tempsClignotement = (System.currentTimeMillis() % 200) / 200f;
                dessinerSprite = tempsClignotement < 0.5f; // Visible 50% du temps
            }
            
            // Ne dessiner que si le sprite doit être visible
            if (dessinerSprite) {
                // Utiliser l'alpha du joueur pour la transparence
                float alpha = joueur.getAlpha();
                
                // Appliquer la couleur avec alpha
                batch.setColor(1, 1, 1, alpha);
                
                // Dessiner Mario avec une taille légèrement ajustée pour mieux correspondre
                float largeur = joueur.getLargeur();
                float hauteur = joueur.getHauteur();
                
                batch.draw(frameActuelle, 
                          joueur.getPosition().x, 
                          joueur.getPosition().y,
                          largeur,
                          hauteur);
                
                // Réinitialiser la couleur (important pour ne pas affecter les autres sprites)
                batch.setColor(1, 1, 1, 1);
            }
        }
    }
    
    /**
     * Obtient la frame actuelle de l'animation selon l'état ET la transformation du joueur
     * @param joueur Le joueur
     * @return La frame actuelle de l'animation
     */
    private TextureRegion obtenirFrameActuelle(Joueur joueur) {
        Animation<TextureRegion> animationActuelle;
        
        // Choisir entre PETIT ou GRAND selon la transformation
        boolean estGrandOuFeu = (joueur.getTransformation() == Joueur.EtatTransformation.GRAND ||
                                  joueur.getTransformation() == Joueur.EtatTransformation.FEU);
        
        // Sélectionner l'animation selon l'état du joueur
        switch (joueur.getEtat()) {
            case SAUTE:
            case TOMBE:
                animationActuelle = estGrandOuFeu ? animationGrandSaut : animationPetitSaut;
                break;
            case MARCHE:
                animationActuelle = estGrandOuFeu ? animationGrandCourse : animationPetitCourse;
                break;
            case IMMOBILE:
            default:
                animationActuelle = estGrandOuFeu ? animationGrandImmobile : animationPetitImmobile;
                break;
        }
        
        if (animationActuelle != null) {
            return animationActuelle.getKeyFrame(tempsEcoule, true);
        }
        
        return null;
    }
    
    @Override
    public void libererRessources() {
        // Libérer textures GRAND
        if (textureGrandIdle != null) textureGrandIdle.dispose();
        if (textureGrandJump != null) textureGrandJump.dispose();
        if (textureGrandRun1 != null) textureGrandRun1.dispose();
        if (textureGrandRun2 != null) textureGrandRun2.dispose();
        
        // Libérer textures PETIT (si elles sont différentes des GRAND)
        if (texturePetitIdle != null && texturePetitIdle != textureGrandIdle) {
            texturePetitIdle.dispose();
        }
        if (texturePetitJump != null && texturePetitJump != textureGrandJump) {
            texturePetitJump.dispose();
        }
        if (texturePetitRun1 != null && texturePetitRun1 != textureGrandRun1) {
            texturePetitRun1.dispose();
        }
        if (texturePetitRun2 != null && texturePetitRun2 != textureGrandRun2) {
            texturePetitRun2.dispose();
        }
        
        // Legacy
        if (textureSpritesheet != null) textureSpritesheet.dispose();
    }
}



