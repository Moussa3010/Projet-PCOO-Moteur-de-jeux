package com.mypackage.projet.jeux.utilitaires;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.mypackage.projet.jeux.modele.comportements.ComportementPatrouille;
import com.mypackage.projet.jeux.modele.entites.Drapeau;
import com.mypackage.projet.jeux.modele.entites.Ennemi;
import com.mypackage.projet.jeux.modele.entites.EnnemiTerrestre;
import com.mypackage.projet.jeux.modele.entites.Joueur;
import com.mypackage.projet.jeux.modele.entites.ObjetCollectable;
import com.mypackage.projet.jeux.modele.entites.Obstacle;
import com.mypackage.projet.jeux.modele.entites.PowerUp;
import com.mypackage.projet.jeux.modele.niveau.Niveau;

/**
 * Classe utilitaire pour charger les niveaux depuis des fichiers Tiled (.tmx)
 */
public class ChargeurNiveau {
    
    private TmxMapLoader chargeurCarte;
    
    /**
     * Constructeur
     */
    public ChargeurNiveau() {
        this.chargeurCarte = new TmxMapLoader();
    }
    
    /**
     * Charge un niveau depuis un fichier TMX
     * @param cheminFichier Chemin vers le fichier .tmx
     * @return Le niveau chargé
     */
    public Niveau chargerNiveau(String cheminFichier) {
        try {
            // Charger la carte Tiled
            TiledMap carte = chargeurCarte.load(cheminFichier);
            
            // Extraire le nom du niveau
            String nomNiveau = extraireNomFichier(cheminFichier);
            Niveau niveau = new Niveau(nomNiveau);
            niveau.setCarte(carte);
            
            // Définir les dimensions du niveau
            MapProperties proprietes = carte.getProperties();
            int largeurCarte = proprietes.get("width", Integer.class);
            int hauteurCarte = proprietes.get("height", Integer.class);
            int tailleTuile = proprietes.get("tilewidth", Integer.class);
            
            niveau.setLargeur(largeurCarte * tailleTuile);
            niveau.setHauteur(hauteurCarte * tailleTuile);
            
            // Charger les différentes couches d'objets
            chargerCoucheJoueur(carte, niveau);
            chargerCoucheEnnemis(carte, niveau);
            chargerCoucheObjets(carte, niveau);
            chargerCouchePowerUps(carte, niveau);
            chargerCoucheDrapeau(carte, niveau);
            chargerCoucheObstacles(carte, niveau);
            
            return niveau;
            
        } catch (Exception e) {
            Gdx.app.error("ChargeurNiveau", "Erreur lors du chargement du niveau : " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Charge la couche contenant le joueur
     * @param carte La carte Tiled
     * @param niveau Le niveau à remplir
     */
    private void chargerCoucheJoueur(TiledMap carte, Niveau niveau) {
        MapLayer couche = carte.getLayers().get("Joueur");
        if (couche == null) {
            Gdx.app.log("ChargeurNiveau", "Avertissement : Aucune couche 'Joueur' trouvée");
            return;
        }
        
        for (MapObject objet : couche.getObjects()) {
            if (objet instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) objet).getRectangle();
                // CORRECTION: Tiled utilise Y=0 en haut, LibGDX Y=0 en bas
                // Pas besoin de convertir car TmxMapLoader fait déjà la conversion
                Joueur joueur = new Joueur(rect.x, rect.y);
                Gdx.app.log("ChargeurNiveau", "✅ Joueur chargé depuis Tiled à position: (" + rect.x + ", " + rect.y + ")");
                niveau.setJoueur(joueur);
                break; // Un seul joueur par niveau
            }
        }
    }
    
    /**
     * Charge la couche contenant les ennemis
     * @param carte La carte Tiled
     * @param niveau Le niveau à remplir
     */
    private void chargerCoucheEnnemis(TiledMap carte, Niveau niveau) {
        MapLayer couche = carte.getLayers().get("Ennemis");
        if (couche == null) {
            Gdx.app.log("ChargeurNiveau", "Avertissement : Aucune couche 'Ennemis' trouvée");
            return;
        }
        
        int nbEnnemis = 0;
        for (MapObject objet : couche.getObjects()) {
            if (objet instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) objet).getRectangle();
                MapProperties props = objet.getProperties();
                
                // Récupérer le type d'ennemi depuis les propriétés (avec valeur par défaut sécurisée)
                String type = props.containsKey("type") ? props.get("type", String.class) : "terrestre";
                
                Ennemi ennemi = creerEnnemi(type, rect.x, rect.y, props);
                if (ennemi != null) {
                    niveau.ajouterEnnemi(ennemi);
                    nbEnnemis++;
                    Gdx.app.debug("ChargeurNiveau", "  → Ennemi " + type + " chargé à (" + rect.x + ", " + rect.y + ")");
                }
            }
        }
        Gdx.app.log("ChargeurNiveau", "✅ " + nbEnnemis + " ennemi(s) chargé(s)");
    }
    
    /**
     * Crée un ennemi selon son type
     * @param type Type de l'ennemi
     * @param x Position X
     * @param y Position Y
     * @param props Propriétés additionnelles
     * @return L'ennemi créé
     */
    private Ennemi creerEnnemi(String type, float x, float y, MapProperties props) {
        Ennemi ennemi;
        
        switch (type.toLowerCase()) {
            case "terrestre":
            default:
                ennemi = new EnnemiTerrestre(x, y);
                break;
        }
        
        // Configurer le comportement si spécifié (avec valeur par défaut sécurisée)
        String comportement = props.containsKey("comportement") ? props.get("comportement", String.class) : "patrouille";
        if ("patrouille".equals(comportement)) {
            float distance = props.containsKey("distancePatrouille") ? props.get("distancePatrouille", Float.class) : 100f;
            ennemi.setComportement(new ComportementPatrouille(distance));
        }
        
        // Configurer les points de vie si spécifiés (sécurisé contre null)
        if (props.containsKey("pointsVie")) {
        Integer pv = props.get("pointsVie", Integer.class);
        if (pv != null) {
            ennemi.setPointsVie(pv);
            }
        }
        
        return ennemi;
    }
    
    /**
     * Charge la couche contenant les objets collectables
     * @param carte La carte Tiled
     * @param niveau Le niveau à remplir
     */
    private void chargerCoucheObjets(TiledMap carte, Niveau niveau) {
        MapLayer couche = carte.getLayers().get("Objets");
        if (couche == null) {
            Gdx.app.log("ChargeurNiveau", "Avertissement : Aucune couche 'Objets' trouvée");
            return;
        }
        
        int nbObjets = 0;
        for (MapObject objet : couche.getObjects()) {
            if (objet instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) objet).getRectangle();
                MapProperties props = objet.getProperties();
                
                // Récupération sécurisée des propriétés
                String type = props.containsKey("type") ? props.get("type", String.class) : "PIECE";
                int valeur = props.containsKey("valeur") ? props.get("valeur", Integer.class) : 10;
                
                ObjetCollectable objetCollectable = new ObjetCollectable(rect.x, rect.y, type, valeur);
                niveau.ajouterObjetCollectable(objetCollectable);
                nbObjets++;
            }
        }
        Gdx.app.log("ChargeurNiveau", "✅ " + nbObjets + " objet(s) collectable(s) chargé(s)");
    }
    
    /**
     * Charge la couche contenant les power-ups
     * @param carte La carte Tiled
     * @param niveau Le niveau à remplir
     */
    private void chargerCouchePowerUps(TiledMap carte, Niveau niveau) {
        MapLayer couche = carte.getLayers().get("PowerUps");
        if (couche == null) {
            Gdx.app.debug("ChargeurNiveau", "ℹ️  Aucune couche 'PowerUps' trouvée (optionnel)");
            return;
        }
        
        int nbPowerUps = 0;
        for (MapObject objet : couche.getObjects()) {
            if (objet instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) objet).getRectangle();
                MapProperties props = objet.getProperties();
                
                // Récupération sécurisée du type
                String type = props.containsKey("type") ? props.get("type", String.class) : "CHAMPIGNON_MAGIQUE";
                PowerUp.TypePowerUp typePowerUp;
                
                // Convertir le type string en enum (avec gestion d'erreur)
                try {
                    switch (type.toUpperCase()) {
                        case "CHAMPIGNON_MAGIQUE":
                        case "MUSHROOM":
                            typePowerUp = PowerUp.TypePowerUp.CHAMPIGNON_MAGIQUE;
                            break;
                        case "FLEUR_DE_FEU":
                        case "FIRE_FLOWER":
                            typePowerUp = PowerUp.TypePowerUp.FLEUR_DE_FEU;
                            break;
                        case "CHAMPIGNON_1UP":
                        case "1UP":
                            typePowerUp = PowerUp.TypePowerUp.CHAMPIGNON_1UP;
                            break;
                        case "SUPER_ETOILE":
                        case "STAR":
                            typePowerUp = PowerUp.TypePowerUp.SUPER_ETOILE;
                            break;
                        default:
                            Gdx.app.log("ChargeurNiveau", "Type de power-up inconnu: " + type + ", utilisation de CHAMPIGNON_MAGIQUE par défaut");
                            typePowerUp = PowerUp.TypePowerUp.CHAMPIGNON_MAGIQUE;
                            break;
                    }
                } catch (Exception e) {
                    Gdx.app.error("ChargeurNiveau", "Erreur lors du parsing du type de power-up: " + type, e);
                    typePowerUp = PowerUp.TypePowerUp.CHAMPIGNON_MAGIQUE;
                }
                
                PowerUp powerUp = new PowerUp(rect.x, rect.y, typePowerUp);
                niveau.ajouterPowerUp(powerUp);
                nbPowerUps++;
                Gdx.app.debug("ChargeurNiveau", "  → Power-up " + typePowerUp + " chargé à (" + rect.x + ", " + rect.y + ")");
            }
        }
        Gdx.app.log("ChargeurNiveau", "✅ " + nbPowerUps + " power-up(s) chargé(s)");
    }
    
    /**
     * Charge la couche contenant les obstacles
     * @param carte La carte Tiled
     * @param niveau Le niveau à remplir
     */
    private void chargerCoucheObstacles(TiledMap carte, Niveau niveau) {
        MapLayer couche = carte.getLayers().get("Obstacles");
        if (couche == null) {
            Gdx.app.log("ChargeurNiveau", "Avertissement : Aucune couche 'Obstacles' trouvée");
            return;
        }
        
        int nbObstacles = 0;
        for (MapObject objet : couche.getObjects()) {
            if (objet instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) objet).getRectangle();
                MapProperties props = objet.getProperties();
                
                // Récupération sécurisée des propriétés
                String type = props.containsKey("type") ? props.get("type", String.class) : "BLOC_NORMAL";
                boolean destructible = props.containsKey("destructible") && props.get("destructible", Boolean.class);
                
                Obstacle obstacle = new Obstacle(rect.x, rect.y, rect.width, rect.height, type);
                obstacle.setDestructible(destructible);
                niveau.ajouterObstacle(obstacle);
                nbObstacles++;
                Gdx.app.debug("ChargeurNiveau", "  → Obstacle chargé à (" + rect.x + ", " + rect.y + ") taille " + rect.width + "x" + rect.height);
            }
        }
        Gdx.app.log("ChargeurNiveau", "✅ " + nbObstacles + " obstacles chargés");
    }
    
    /**
     * Charge la couche contenant le drapeau de fin
     * @param carte La carte Tiled
     * @param niveau Le niveau à remplir
     */
    private void chargerCoucheDrapeau(TiledMap carte, Niveau niveau) {
        MapLayer couche = carte.getLayers().get("Drapeau");
        if (couche == null) {
            Gdx.app.debug("ChargeurNiveau", "ℹ️  Aucune couche 'Drapeau' trouvée (optionnel)");
            return;
        }
        
        for (MapObject objet : couche.getObjects()) {
            if (objet instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) objet).getRectangle();
                MapProperties props = objet.getProperties();
                
                // Hauteur du mât (défaut : 160 pixels) - Récupération sécurisée
                float hauteur = 160f;
                if (props.containsKey("hauteur")) {
                    Float hauteurProp = props.get("hauteur", Float.class);
                    if (hauteurProp != null) {
                        hauteur = hauteurProp;
                    }
                }
                
                // Créer le drapeau
                Drapeau drapeau = new Drapeau(rect.x, rect.y, hauteur);
                niveau.setDrapeau(drapeau);
                
                Gdx.app.log("ChargeurNiveau", "  → Drapeau de fin chargé à (" + rect.x + ", " + rect.y + ") hauteur=" + hauteur);
                break; // Un seul drapeau par niveau
            }
        }
    }
    
    /**
     * Extrait le nom du fichier sans l'extension
     * @param cheminFichier Chemin complet du fichier
     * @return Le nom du fichier
     */
    private String extraireNomFichier(String cheminFichier) {
        String nom = cheminFichier;
        int dernierSlash = Math.max(nom.lastIndexOf('/'), nom.lastIndexOf('\\'));
        if (dernierSlash >= 0) {
            nom = nom.substring(dernierSlash + 1);
        }
        int dernierPoint = nom.lastIndexOf('.');
        if (dernierPoint > 0) {
            nom = nom.substring(0, dernierPoint);
        }
        return nom;
    }
}


