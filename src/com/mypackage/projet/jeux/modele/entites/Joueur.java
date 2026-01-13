package com.mypackage.projet.jeux.modele.entites;

/**
 * Classe représentant le joueur (personnage principal contrôlé par l'utilisateur)
 */
public class Joueur extends Entite {
    
    // ========== CONSTANTES PHYSIQUES MARIO-LIKE AMÉLIORÉES ==========
    private static final float VITESSE_MAX = 250f;          // Vitesse maximale
    private static final float ACCELERATION = 1200f;        // Accélération au sol
    private static final float DECELERATION = 1500f;        // Décélération/friction
    private static final float ACCELERATION_AIR = 900f;     // Accélération dans l'air (AMÉLIORÉE: plus de contrôle)
    
    // SAUT AMÉLIORÉ : Plus de hauteur, meilleur contrôle
    private static final float VITESSE_SAUT = 700f;         // Vitesse initiale (AUGMENTÉE pour sauts plus hauts)
    private static final float SAUT_MIN_HAUTEUR = 250f;     // Hauteur min (RÉDUITE pour petits sauts plus courts)
    private static final float GRAVITE = -1100f;            // Gravité montée (LÉGÈREMENT réduite pour float)
    private static final float GRAVITE_CHUTE_RAPIDE = -2000f; // Gravité chute (AUGMENTÉE pour tombées rapides)
    private static final float GRAVITE_SAUT_RELACHE = -1600f; // Gravité si on relâche en montant
    
    // ========== TIMERS MARIO-LIKE ==========
    private static final float COYOTE_TIME = 0.12f;         // Coyote time (AUGMENTÉE: plus tolérant)
    private static final float JUMP_BUFFER_TIME = 0.15f;    // Jump buffer (AUGMENTÉE: plus tolérant)
    
    private boolean auSol;
    private int vies;
    private int score;
    private int pieces; // Compteur de pièces (séparé du score)
    private boolean peutSauter;
    private EtatJoueur etat;
    private EtatTransformation transformation;
    private boolean invincibleTemporaire; // Après avoir pris un dégât
    private float tempsInvincibilite;
    private static final float DUREE_INVINCIBILITE = 2.0f;
    private boolean visible; // Pour la séquence de fin de niveau
    private float alpha; // Transparence (1.0 = opaque, 0.0 = invisible)
    
    // Variables pour physique améliorée
    private float tempsHorsSol;         // Temps depuis qu'on a quitté le sol (pour coyote time)
    private float tempsSautBuffer;      // Temps du buffer de saut
    private boolean sautMaintenu;       // Si la touche saut est maintenue
    private float directionVoulue;      // Direction demandée (-1, 0, 1)
    
    /**
     * Énumération représentant les différents états du joueur
     */
    public enum EtatJoueur {
        IMMOBILE,
        MARCHE,
        SAUTE,
        TOMBE,
        MORT
    }
    
    /**
     * Énumération représentant l'état de transformation de Mario
     */
    public enum EtatTransformation {
        PETIT,      // Mario normal (petit)
        GRAND,      // Mario après Champignon Magique (grand, peut encaisser 1 coup)
        FEU         // Mario après Fleur de Feu
    }
    
    /**
     * Constructeur du joueur
     * @param x Position X initiale
     * @param y Position Y initiale
     */
    public Joueur(float x, float y) {
        super(x, y, 32, 48); // Taille typique d'un personnage Mario-like
        this.vies = 3;
        this.score = 0;
        this.pieces = 0;
        this.auSol = false;
        this.peutSauter = true;
        this.etat = EtatJoueur.IMMOBILE;
        this.transformation = EtatTransformation.GRAND; // Commence en mode GRAND
        this.invincibleTemporaire = false;
        this.tempsInvincibilite = 0;
        this.visible = true; // Le joueur est visible par défaut
        this.alpha = 1.0f; // Opaque par défaut
        
        // Initialiser les variables de physique améliorée
        this.tempsHorsSol = 0;
        this.tempsSautBuffer = 0;
        this.sautMaintenu = false;
        this.directionVoulue = 0;
    }
    
    @Override
    public void mettreAJour(float deltaTemps) {
        if (!active || etat == EtatJoueur.MORT) {
            return;
        }
        
        // Gérer l'invincibilité temporaire
        if (invincibleTemporaire) {
            tempsInvincibilite += deltaTemps;
            if (tempsInvincibilite >= DUREE_INVINCIBILITE) {
                invincibleTemporaire = false;
                tempsInvincibilite = 0;
            }
        }
        
        // ========== PHYSIQUE MARIO-LIKE ==========
        
        // 1. ACCÉLÉRATION HORIZONTALE PROGRESSIVE
        float accelerationActuelle = auSol ? ACCELERATION : ACCELERATION_AIR;
        
        if (directionVoulue != 0) {
            // Accélérer dans la direction voulue
            vitesse.x += directionVoulue * accelerationActuelle * deltaTemps;
            // Limiter à la vitesse max
            if (Math.abs(vitesse.x) > VITESSE_MAX) {
                vitesse.x = Math.signum(vitesse.x) * VITESSE_MAX;
            }
        } else {
            // Décélération/friction
            if (Math.abs(vitesse.x) > 0) {
                float decelerationActuelle = auSol ? DECELERATION : DECELERATION * 0.5f;
                float nouvelleVitesse = vitesse.x - Math.signum(vitesse.x) * decelerationActuelle * deltaTemps;
                // S'arrêter si on change de signe (éviter l'oscillation)
                if (Math.signum(nouvelleVitesse) != Math.signum(vitesse.x)) {
                    vitesse.x = 0;
                } else {
                    vitesse.x = nouvelleVitesse;
                }
            }
        }
        
        // 2. GRAVITÉ VARIABLE AMÉLIORÉE (3 états pour plus de contrôle)
        float graviteActuelle;
        if (vitesse.y < 0) {
            // CHUTE : Gravité forte pour tomber rapidement
            graviteActuelle = GRAVITE_CHUTE_RAPIDE;
        } else if (!sautMaintenu && vitesse.y > 0) {
            // SAUT RELÂCHÉ : Gravité intermédiaire pour arrêter la montée plus vite
            graviteActuelle = GRAVITE_SAUT_RELACHE;
        } else {
            // SAUT MAINTENU : Gravité normale pour flotter plus longtemps
            graviteActuelle = GRAVITE;
        }
        vitesse.y += graviteActuelle * deltaTemps;
        
        // 3. SAUT VARIABLE (coupure nette si on relâche tôt)
        if (!sautMaintenu && vitesse.y > SAUT_MIN_HAUTEUR) {
            vitesse.y = SAUT_MIN_HAUTEUR;
        }
        
        // Limiter la vitesse de chute maximale
        if (vitesse.y < -600f) {
            vitesse.y = -600f;
        }
        
        // 4. COYOTE TIME - Incrémenter si pas au sol
        if (!auSol) {
            tempsHorsSol += deltaTemps;
        } else {
            tempsHorsSol = 0;
        }
        
        // 5. JUMP BUFFER - Décrémenter le timer
        if (tempsSautBuffer > 0) {
            tempsSautBuffer -= deltaTemps;
        }
        
        // Appliquer le saut si buffer actif et on vient de toucher le sol
        if (tempsSautBuffer > 0 && auSol) {
            effectuerSaut();
            tempsSautBuffer = 0;
        }
        
        // Mettre à jour la position
        position.x += vitesse.x * deltaTemps;
        position.y += vitesse.y * deltaTemps;
        
        // SOL DE SÉCURITÉ
        if (position.y <= 32) {
            position.y = 32;
            vitesse.y = 0;
            auSol = true;
            peutSauter = true;
        }
        
        // Mettre à jour l'état
        mettreAJourEtat();
        
        // Mettre à jour la boîte de collision
        mettreAJourBoiteCollision();
        
        // Reset de la direction (sera mise à jour par les entrées)
        directionVoulue = 0;
    }
    
    /**
     * Met à jour l'état du joueur en fonction de sa vitesse et position
     */
    private void mettreAJourEtat() {
        if (vitesse.y > 0) {
            etat = EtatJoueur.SAUTE;
            auSol = false;
        } else if (vitesse.y < 0 && !auSol) {
            etat = EtatJoueur.TOMBE;
        } else if (Math.abs(vitesse.x) > 0) {
            etat = EtatJoueur.MARCHE;
        } else {
            etat = EtatJoueur.IMMOBILE;
        }
    }
    
    /**
     * Fait se déplacer le joueur vers la gauche (NOUVELLE PHYSIQUE)
     */
    public void deplacerGauche() {
        directionVoulue = -1;
    }
    
    /**
     * Fait se déplacer le joueur vers la droite (NOUVELLE PHYSIQUE)
     */
    public void deplacerDroite() {
        directionVoulue = 1;
    }
    
    /**
     * Arrête le déplacement horizontal du joueur (NOUVELLE PHYSIQUE)
     */
    public void arreterDeplacementHorizontal() {
        directionVoulue = 0;
    }
    
    /**
     * Fait sauter le joueur - AMÉLIORÉ avec Coyote Time et Jump Buffer
     */
    public void sauter() {
        // COYOTE TIME : Peut sauter même légèrement hors du sol
        if ((auSol || tempsHorsSol < COYOTE_TIME) && peutSauter) {
            effectuerSaut();
        } else if (!auSol) {
            // JUMP BUFFER : Mémoriser la tentative de saut
            tempsSautBuffer = JUMP_BUFFER_TIME;
        }
        
        sautMaintenu = true;
    }
    
    /**
     * Relâcher la touche de saut (pour saut variable)
     */
    public void relacherSaut() {
        sautMaintenu = false;
    }
    
    /**
     * Effectue réellement le saut
     */
    private void effectuerSaut() {
        vitesse.y = VITESSE_SAUT;
        auSol = false;
        tempsHorsSol = COYOTE_TIME; // Empêcher re-saut immédiat
        etat = EtatJoueur.SAUTE;
        sautMaintenu = true;
    }
    
    /**
     * Place le joueur au sol
     * @param y Position Y du sol
     */
    public void placerAuSol(float y) {
        // Petite correction pour éviter les micro-oscillations
        if (Math.abs(position.y - y) > 0.1f) {
            position.y = y;
        }
        vitesse.y = 0;
        auSol = true;
        peutSauter = true;
    }
    
    /**
     * Force la position du joueur (pour les corrections de collision)
     * @param x Position X
     * @param y Position Y
     */
    public void forcerPosition(float x, float y) {
        position.x = x;
        position.y = y;
        mettreAJourBoiteCollision();
    }
    
    /**
     * Fait perdre une vie au joueur (avec système de transformation Mario-like)
     * Gère les transformations : FEU → GRAND → PETIT → MORT
     */
    public void perdreVie() {
        // Si invincible, ignorer les dégâts
        if (invincibleTemporaire) {
            return;
        }
        
        // Système de transformation : rétrograder avant de perdre une vie
        if (transformation == EtatTransformation.FEU) {
            // ========== FEU → GRAND ==========
            transformation = EtatTransformation.GRAND;
            invincibleTemporaire = true;
            tempsInvincibilite = 0;
            
        } else if (transformation == EtatTransformation.GRAND) {
            // ========== GRAND → PETIT ==========
            rapetisser();
            invincibleTemporaire = true;
            tempsInvincibilite = 0;
            
        } else {
            // ========== PETIT → MORT ==========
            vies--;
            invincibleTemporaire = true;
            tempsInvincibilite = 0;
            
            if (vies <= 0) {
                mourir();
            }
        }
    }
    
    /**
     * Fait rapetisser Mario (GRAND → PETIT)
     * Gère correctement le redimensionnement de la hitbox
     */
    private void rapetisser() {
        if (transformation != EtatTransformation.GRAND) {
            return; // Déjà petit ou autre état
        }
        
        // Sauvegarder l'ancienne hauteur
        float ancienneHauteur = this.hauteur;
        
        // Nouvelle taille (Mario Petit)
        float nouvelleHauteur = 32f;
        float nouvelleLargeur = 32f;
        
        // Mettre à jour la transformation et les dimensions
        this.transformation = EtatTransformation.PETIT;
        this.hauteur = nouvelleHauteur;
        this.largeur = nouvelleLargeur;
        
        // CRITIQUE : Ajuster la position Y pour ne pas flotter dans l'air
        // Mario rapetisse "vers le bas", donc on descend sa position
        float differenceHauteur = ancienneHauteur - nouvelleHauteur;
        this.position.y -= differenceHauteur;
        
        // Mettre à jour la boîte de collision
        mettreAJourBoiteCollision();
    }
    
    /**
     * Tue Mario (animation de mort)
     */
    private void mourir() {
        etat = EtatJoueur.MORT;
        active = false;
        
        // Animation de mort : petit saut vers le haut
        vitesse.y = 400f;
    }
    
    /**
     * Applique un power-up à Mario avec gestion correcte de la transformation
     * @param type Type de power-up
     */
    public void appliquerPowerUp(PowerUp.TypePowerUp type) {
        switch (type) {
            case CHAMPIGNON_MAGIQUE:
                if (transformation == EtatTransformation.PETIT) {
                    // TRANSFORMATION : PETIT → GRAND
                    grandir();
                } else {
                    // Si déjà grand, donner des points bonus
                    ajouterScore(1000);
                }
                break;
                
            case FLEUR_DE_FEU:
                if (transformation != EtatTransformation.FEU) {
                    // Si PETIT, grandir d'abord puis devenir FEU
                    if (transformation == EtatTransformation.PETIT) {
                        grandir();
                    }
                    transformation = EtatTransformation.FEU;
                } else {
                    ajouterScore(1000);
                }
                break;
                
            case CHAMPIGNON_1UP:
                vies++;
                ajouterScore(500);
                break;
                
            case SUPER_ETOILE:
                // Activer l'invincibilité temporaire
                invincibleTemporaire = true;
                tempsInvincibilite = 0;
                ajouterScore(1000);
                break;
        }
    }
    
    /**
     * Fait grandir Mario (PETIT → GRAND)
     * Gère correctement le redimensionnement de la hitbox et l'ajustement de position
     */
    private void grandir() {
        if (transformation != EtatTransformation.PETIT) {
            return; // Déjà grand
        }
        
        // Sauvegarder l'ancienne hauteur
        float ancienneHauteur = this.hauteur;
        
        // Nouvelle taille (Mario Grand)
        float nouvelleHauteur = 48f;
        float nouvelleLargeur = 32f;
        
        // Mettre à jour la transformation et les dimensions
        this.transformation = EtatTransformation.GRAND;
        this.hauteur = nouvelleHauteur;
        this.largeur = nouvelleLargeur;
        
        // CRITIQUE : Ajuster la position Y pour ne pas s'enfoncer dans le sol
        // Mario grandit "vers le haut", donc on monte sa position de la différence de hauteur
        float differenceHauteur = nouvelleHauteur - ancienneHauteur;
        this.position.y += differenceHauteur;
        
        // Mettre à jour la boîte de collision
        mettreAJourBoiteCollision();
    }
    
    /**
     * Ajoute des points au score
     * @param points Nombre de points à ajouter
     */
    public void ajouterScore(int points) {
        this.score += points;
    }
    
    /**
     * Ajoute des pièces et met à jour le score
     * Bonus de vie tous les 100 pièces (comme dans Mario)
     * @param nombrePieces Nombre de pièces à ajouter
     */
    public void ajouterPieces(int nombrePieces) {
        int ancienTotal = this.pieces;
        this.pieces += nombrePieces;
        this.score += nombrePieces * 10; // Chaque pièce vaut 10 points
        
        // Bonus de vie tous les 100 pièces
        int ancien100 = ancienTotal / 100;
        int nouveau100 = this.pieces / 100;
        if (nouveau100 > ancien100) {
            this.vies++;
        }
    }
    
    /**
     * Réinitialise complètement le joueur (pour recommencer un niveau)
     * @param x Position X de départ
     * @param y Position Y de départ
     */
    public void reinitialiser(float x, float y) {
        // Réinitialiser position et vitesse
        this.position.set(x, y);
        this.vitesse.set(0, 0);
        mettreAJourBoiteCollision();
        
        // Réinitialiser l'état
        this.vies = 3;
        this.pieces = 0; // Réinitialiser les pièces aussi
        this.auSol = false;
        this.peutSauter = true;
        this.etat = EtatJoueur.IMMOBILE;
        this.active = true;
        this.visible = true; // Réinitialiser la visibilité
        this.alpha = 1.0f; // Réinitialiser la transparence (opaque)
        
        // Réinitialiser les variables de physique améliorée
        this.tempsHorsSol = 0;
        this.tempsSautBuffer = 0;
        this.sautMaintenu = false;
        this.directionVoulue = 0;
    }
    
    // Getters et Setters
    public boolean estAuSol() {
        return auSol;
    }
    
    public void setAuSol(boolean auSol) {
        this.auSol = auSol;
    }
    
    public int getVies() {
        return vies;
    }
    
    public void setVies(int vies) {
        this.vies = vies;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getPieces() {
        return pieces;
    }
    
    public void setPieces(int pieces) {
        this.pieces = pieces;
    }
    
    public EtatJoueur getEtat() {
        return etat;
    }
    
    public EtatTransformation getTransformation() {
        return transformation;
    }
    
    public void setTransformation(EtatTransformation transformation) {
        this.transformation = transformation;
    }
    
    public boolean estInvincible() {
        return invincibleTemporaire;
    }
    
    
    public boolean estVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public float getAlpha() {
        return alpha;
    }
    
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
    }
}
