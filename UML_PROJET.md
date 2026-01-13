# Diagramme UML du Projet - Moteur de Jeu 2D

## Vue d'Ensemble de l'Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         ARCHITECTURE MVC                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│   ┌───────────────┐     ┌───────────────┐     ┌──────────────┐ │
│   │   MODÈLE      │◄────│  CONTRÔLEUR   │────►│     VUE      │ │
│   │  (Logique)    │     │ (Coordination)│     │  (Affichage) │ │
│   └───────────────┘     └───────────────┘     └──────────────┘ │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## Diagramme de Classes Complet

### Package: modele.entites

```
┌─────────────────────────────────────────┐
│           <<abstract>>                   │
│              Entite                      │
├─────────────────────────────────────────┤
│ - position : Vector2                    │
│ - vitesse : Vector2                     │
│ - largeur : float                       │
│ - hauteur : float                       │
│ - active : boolean                      │
│ - boiteCollision : Rectangle            │
├─────────────────────────────────────────┤
│ + mettreAJour(deltaTemps : float)       │
│ + mettreAJourBoiteCollision()           │
│ + entreEnCollisionAvec(autre : Entite)  │
│ + getPosition() : Vector2               │
│ + setPosition(x : float, y : float)     │
│ + estActive() : boolean                 │
│ + setActive(active : boolean)           │
└─────────────────────────────────────────┘
                    △
                    │
        ┌───────────┼───────────┬───────────────┬────────────┐
        │           │           │               │            │
        │           │           │               │            │
┌───────▼──────┐  ┌─▼─────┐  ┌─▼──────────┐  ┌─▼────────┐ ┌▼────────┐
│    Joueur    │  │ Ennemi │  │ PowerUp    │  │ Obstacle │ │ Drapeau │
├──────────────┤  │abstract│  ├────────────┤  ├──────────┤ ├─────────┤
│- etat        │  ├────────┤  │- type      │  │- type    │ │- touche │
│- transform.  │  │- comp. │  │- direction │  │- estSol. │ │- hauteur│
│- vies        │  │- type  │  ├────────────┤  ├──────────┤ ├─────────┤
│- score       │  ├────────┤  │+ appliquer │  │+ estSol. │ │+ marquer│
│- invincible  │  │+ setComp│ │  Effet()   │  │  ide()   │ │  Fin()  │
│- auSol       │  │+ eliminer│ │+ mettreA  │  └──────────┘ └─────────┘
├──────────────┤  └────────┘  │  Jour()    │
│+ deplacerG() │       △       └────────────┘       ┌──────────────────┐
│+ deplacerD() │       │                            │ ObjetCollectable │
│+ sauter()    │       │                            ├──────────────────┤
│+ agrandir()  │  ┌────▼────────┐                  │- type : String   │
│+ rapetisser()│  │ EnnemiTerr. │                  │- valeur : int    │
│+ subirDegats│  ├─────────────┤                  ├──────────────────┤
│+ estInvinc. │  │- limiteG    │                  │+ collecter()     │
└──────────────┘  │- limiteD    │                  └──────────────────┘
                  ├─────────────┤
                  │+ patrouiller│
                  └─────────────┘
```

#### Énumérations

```
┌─────────────────────┐     ┌──────────────────────┐     ┌─────────────────────┐
│   EtatJoueur        │     │ EtatTransformation   │     │   TypePowerUp       │
├─────────────────────┤     ├──────────────────────┤     ├─────────────────────┤
│ IMMOBILE            │     │ PETIT                │     │ CHAMPIGNON_MAGIQUE  │
│ MARCHE              │     │ GRAND                │     │ FLEUR_DE_FEU        │
│ SAUTE               │     │ FEU                  │     │ SUPER_ETOILE        │
│ TOMBE               │     └──────────────────────┘     │ CHAMPIGNON_1UP      │
│ MORT                │                                   └─────────────────────┘
└─────────────────────┘

┌─────────────────────┐     ┌──────────────────────┐
│   TypeObstacle      │     │   TypeObjet          │
├─────────────────────┤     ├──────────────────────┤
│ BLOC_NORMAL         │     │ PIECE                │
│ BLOC_QUESTION       │     │ CHAMPIGNON           │
│ BLOC_BRIQUE         │     │ FLEUR                │
│ PLATEFORME          │     │ ETOILE               │
└─────────────────────┘     └──────────────────────┘
```

---

### Package: modele.comportements

```
┌─────────────────────────────────────┐
│      <<interface>>                   │
│     ComportementEnnemi               │
├─────────────────────────────────────┤
│ + executer(ennemi : Ennemi,         │
│            deltaTemps : float)       │
└─────────────────────────────────────┘
                △
                │
                │ implements
                │
┌───────────────▼─────────────────────┐
│    ComportementPatrouille            │
├─────────────────────────────────────┤
│ + executer(ennemi : Ennemi,         │
│            deltaTemps : float)       │
└─────────────────────────────────────┘
```

---

### Package: modele.gestionnaires

```
┌────────────────────────────────────────────────────┐
│         GestionnaireCollisions                      │
├────────────────────────────────────────────────────┤
│ - niveau : Niveau                                  │
├────────────────────────────────────────────────────┤
│ + gererCollisions()                                │
│ + gererCollisionsObstacles(joueur : Joueur)       │
│ + gererCollisionsEnnemis(joueur : Joueur)         │
│ + gererCollisionsObjetsCollectables(joueur)       │
│ + gererCollisionsPowerUps(joueur : Joueur)        │
│ + gererCollisionDrapeau(joueur : Joueur)          │
│ + gererCollisionsEnnemiObstacles(ennemi)          │
│ + gererCollisionsPowerUpObstacles(powerUp)        │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│         GestionnaireNiveaux                         │
├────────────────────────────────────────────────────┤
│ - niveaux : List<String>                           │
│ - niveauActuel : Niveau                            │
│ - indexNiveauActuel : int                          │
│ - historique : Map<String, ProgressionNiveau>      │
│ - etatTransition : EtatTransition                  │
│ - tempsTransition : float                          │
├────────────────────────────────────────────────────┤
│ + chargerPremierNiveau()                           │
│ + chargerNiveauDepuisFichier(chemin : String)     │
│ + niveauSuivant()                                  │
│ + niveauPrecedent()                                │
│ + rechargerNiveauActuel()                          │
│ + mettreAJour(deltaTemps : float)                  │
│ + demarrerTransitionDebutNiveau()                  │
│ + demarrerTransitionFinNiveau()                    │
│ + getProgressionNiveau(nomNiveau : String)         │
│ + getScoreTotal() : int                            │
│ + getTotalEtoiles() : int                          │
└────────────────────────────────────────────────────┘

┌─────────────────────┐
│  EtatTransition     │
├─────────────────────┤
│ AUCUNE              │
│ DEBUT_NIVEAU        │
│ FIN_NIVEAU          │
│ TRANSITION_EN_COURS │
└─────────────────────┘
```

---

### Package: modele.niveau

```
┌────────────────────────────────────────────────────┐
│                  Niveau                             │
├────────────────────────────────────────────────────┤
│ - carte : TiledMap                                 │
│ - joueur : Joueur                                  │
│ - ennemis : List<Ennemi>                           │
│ - powerUps : List<PowerUp>                         │
│ - objetsCollectables : List<ObjetCollectable>      │
│ - obstacles : List<Obstacle>                       │
│ - drapeau : Drapeau                                │
│ - progression : ProgressionNiveau                  │
│ - objectifs : List<ObjectifNiveau>                 │
│ - termine : boolean                                │
├────────────────────────────────────────────────────┤
│ + mettreAJour(deltaTemps : float)                  │
│ + ajouterEnnemi(ennemi : Ennemi)                   │
│ + ajouterPowerUp(powerUp : PowerUp)                │
│ + ajouterObjetCollectable(objet)                   │
│ + ajouterObstacle(obstacle : Obstacle)             │
│ + setDrapeau(drapeau : Drapeau)                    │
│ + notifierPieceCollectee()                         │
│ + notifierEnnemiVaincu()                           │
│ + terminer()                                       │
│ + estTermine() : boolean                           │
└────────────────────────────────────────────────────┘
                    │ 1
                    │
                    │ 1
                    ▼
┌────────────────────────────────────────────────────┐
│            ProgressionNiveau                        │
├────────────────────────────────────────────────────┤
│ - score : int                                      │
│ - piecesCollectees : int                           │
│ - ennemisVaincus : int                             │
│ - tempsEcoule : float                              │
│ - etoilesObtenues : int                            │
├────────────────────────────────────────────────────┤
│ + incrementerScore(points : int)                   │
│ + incrementerPieces()                              │
│ + incrementerEnnemisVaincus()                      │
│ + mettreAJourTemps(deltaTemps : float)             │
│ + calculerEtoiles() : int                          │
│ + getScore() : int                                 │
│ + getPieces() : int                                │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│            ObjectifNiveau                           │
├────────────────────────────────────────────────────┤
│ - type : TypeObjectif                              │
│ - valeurCible : int                                │
│ - valeurActuelle : int                             │
│ - accompli : boolean                               │
├────────────────────────────────────────────────────┤
│ + verifierAccomplissement() : boolean              │
│ + incrementerValeur()                              │
│ + estAccompli() : boolean                          │
└────────────────────────────────────────────────────┘

┌─────────────────────┐
│   TypeObjectif      │
├─────────────────────┤
│ ATTEINDRE_DISTANCE  │
│ COLLECTER_PIECES    │
│ VAINCRE_ENNEMIS     │
│ TEMPS_LIMITE        │
└─────────────────────┘
```

---

### Package: controleur

```
┌────────────────────────────────────────────────────┐
│              ControleurJeu                          │
├────────────────────────────────────────────────────┤
│ - niveau : Niveau                                  │
│ - gestionnaireNiveaux : GestionnaireNiveaux        │
│ - gestionnaireCollisions : GestionnaireCollisions  │
│ - controleurEntrees : ControleurEntrees            │
│ - etatJeu : EtatJeu                                │
│ - etatSequenceFin : EtatSequenceFin                │
│ - tempsSequence : float                            │
│ - camera : OrthographicCamera                      │
├────────────────────────────────────────────────────┤
│ + mettreAJour(deltaTemps : float)                  │
│ + demarrerJeu()                                    │
│ + mettreEnPause()                                  │
│ + reprendre()                                      │
│ + terminerNiveau()                                 │
│ + rejouerNiveau()                                  │
│ + niveauSuivant()                                  │
│ + gameOver()                                       │
│ + quitter()                                        │
│ + gererSequenceFinNiveau(deltaTemps : float)       │
└────────────────────────────────────────────────────┘

┌─────────────────────┐     ┌─────────────────────┐
│     EtatJeu         │     │ EtatSequenceFin     │
├─────────────────────┤     ├─────────────────────┤
│ MENU                │     │ GLISSADE            │
│ EN_JEU              │     │ MARCHE              │
│ PAUSE               │     │ ENTREE_CHATEAU      │
│ SEQUENCE_FIN_NIVEAU │     │ MENU                │
│ MENU_FIN_NIVEAU     │     └─────────────────────┘
│ GAME_OVER           │
│ VICTOIRE            │
│ TRANSITION_NIVEAU   │
└─────────────────────┘

┌────────────────────────────────────────────────────┐
│            ControleurEntrees                        │
├────────────────────────────────────────────────────┤
│ - touchesEnfoncees : Set<Integer>                  │
├────────────────────────────────────────────────────┤
│ + gererEntrees(joueur : Joueur, niveau : Niveau)  │
│ + estToucheEnfoncee(codeTouch : int) : boolean    │
│ + gaucheEnfoncee() : boolean                       │
│ + droiteEnfoncee() : boolean                       │
│ + sautEnfonce() : boolean                          │
│ + pauseEnfoncee() : boolean                        │
└────────────────────────────────────────────────────┘
```

---

### Package: vue

```
┌────────────────────────────────────────────────────┐
│          <<interface>>                              │
│            RenduEntite                              │
├────────────────────────────────────────────────────┤
│ + chargerTextures()                                │
│ + dessiner(entite : Entite, batch : SpriteBatch)   │
│ + libererRessources()                              │
└────────────────────────────────────────────────────┘
                        △
                        │ implements
        ┌───────────────┼───────────────┬───────────┐
        │               │               │           │
┌───────▼──────┐  ┌────▼─────┐  ┌──────▼────┐  ┌──▼────────┐
│ RenduJoueur  │  │RenduEnne.│  │RenduPower │  │RenduObjet │
├──────────────┤  ├──────────┤  ├───────────┤  ├───────────┤
│- textures    │  │- textures│  │- textures │  │- textures │
│- animations  │  │- anim.   │  │- anim.    │  │- anim.    │
├──────────────┤  ├──────────┤  ├───────────┤  ├───────────┤
│+ dessiner()  │  │+ dessiner│  │+ dessiner │  │+ dessiner │
└──────────────┘  └──────────┘  └───────────┘  └───────────┘

        ┌────────────┐
        │RenduDrapeau│
        ├────────────┤
        │- textures  │
        ├────────────┤
        │+ dessiner()│
        └────────────┘

┌────────────────────────────────────────────────────┐
│              RenduNiveau                            │
├────────────────────────────────────────────────────┤
│ - rendeurCarte : OrthogonalTiledMapRenderer        │
│ - rendeurJoueur : RenduJoueur                      │
│ - rendeurEnnemi : RenduEnnemi                      │
│ - rendeurPowerUp : RenduPowerUp                    │
│ - rendeurObjet : RenduObjet                        │
│ - rendeurDrapeau : RenduDrapeau                    │
│ - batch : SpriteBatch                              │
├────────────────────────────────────────────────────┤
│ + chargerRessources()                              │
│ + dessiner(niveau : Niveau, camera : Camera)       │
│ + dessinerCarte()                                  │
│ + dessinerEntites()                                │
│ + libererRessources()                              │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│                RenduHUD                             │
├────────────────────────────────────────────────────┤
│ - batch : SpriteBatch                              │
│ - font : BitmapFont                                │
│ - textureVie : Texture                             │
│ - texturePiece : Texture                           │
├────────────────────────────────────────────────────┤
│ + dessiner(score : int, vies : int,                │
│            pieces : int, temps : float)            │
│ + libererRessources()                              │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│            MenuFinNiveau                            │
├────────────────────────────────────────────────────┤
│ - batch : SpriteBatch                              │
│ - font : BitmapFont                                │
│ - boutons : List<BoutonMenu>                       │
├────────────────────────────────────────────────────┤
│ + dessiner()                                       │
│ + gererClic(x : int, y : int) : ActionMenu        │
│ + libererRessources()                              │
└────────────────────────────────────────────────────┘

┌─────────────────────┐
│    ActionMenu       │
├─────────────────────┤
│ REJOUER             │
│ NIVEAU_SUIVANT      │
│ QUITTER             │
└─────────────────────┘

┌────────────────────────────────────────────────────┐
│            RenduTransition                          │
├────────────────────────────────────────────────────┤
│ - alpha : float                                    │
│ - duree : float                                    │
│ - shapeRenderer : ShapeRenderer                    │
├────────────────────────────────────────────────────┤
│ + dessinerFadeIn(progression : float)              │
│ + dessinerFadeOut(progression : float)             │
│ + libererRessources()                              │
└────────────────────────────────────────────────────┘
```

---

### Package: utilitaires

```
┌────────────────────────────────────────────────────┐
│            ChargeurNiveau                           │
├────────────────────────────────────────────────────┤
│ + charger(cheminFichier : String) : Niveau        │
│ - chargerCoucheJoueur(niveau, carte)              │
│ - chargerCoucheEnnemis(niveau, carte)             │
│ - chargerCouchePowerUps(niveau, carte)            │
│ - chargerCoucheObjets(niveau, carte)              │
│ - chargerCoucheObstacles(niveau, carte)           │
│ - chargerCoucheDrapeau(niveau, carte)             │
│ - creerEnnemiDepuisObjet(objet) : Ennemi          │
│ - creerPowerUpDepuisObjet(objet) : PowerUp        │
│ - creerObstacleDepuisObjet(objet) : Obstacle      │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│         SauvegardeProgression                       │
├────────────────────────────────────────────────────┤
│ - FICHIER_SAUVEGARDE : String                      │
├────────────────────────────────────────────────────┤
│ + sauvegarder(progression : Map)                   │
│ + charger() : Map                                  │
│ + supprimerSauvegarde()                            │
│ + sauvegardeExiste() : boolean                     │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│          ConfigurationJeu                           │
├────────────────────────────────────────────────────┤
│ - FICHIER_CONFIG : String                          │
│ - largeurEcran : int                               │
│ - hauteurEcran : int                               │
│ - volumeMusique : float                            │
│ - volumeEffets : float                             │
│ - niveaux : List<String>                           │
├────────────────────────────────────────────────────┤
│ + chargerConfiguration()                           │
│ + sauvegarderConfiguration()                       │
│ + getNiveaux() : List<String>                      │
│ + getLargeurEcran() : int                          │
│ + getHauteurEcran() : int                          │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│        GestionnaireRessources                       │
├────────────────────────────────────────────────────┤
│ - textures : Map<String, Texture>                  │
│ - animations : Map<String, Animation>              │
├────────────────────────────────────────────────────┤
│ + chargerTexture(chemin : String) : Texture        │
│ + obtenirTexture(chemin : String) : Texture        │
│ + chargerAnimation(nom : String) : Animation       │
│ + obtenirAnimation(nom : String) : Animation       │
│ + libererTout()                                    │
└────────────────────────────────────────────────────┘
```

---

### Classe Principale

```
┌────────────────────────────────────────────────────┐
│            JeuPlateforme                            │
│            implements ApplicationAdapter            │
├────────────────────────────────────────────────────┤
│ - controleurJeu : ControleurJeu                    │
│ - rendeuNiveau : RenduNiveau                       │
│ - rendeuHUD : RenduHUD                             │
│ - menuFinNiveau : MenuFinNiveau                    │
│ - rendeuTransition : RenduTransition               │
│ - batch : SpriteBatch                              │
│ - camera : OrthographicCamera                      │
├────────────────────────────────────────────────────┤
│ + create()                                         │
│ + render()                                         │
│ + resize(width : int, height : int)                │
│ + pause()                                          │
│ + resume()                                         │
│ + dispose()                                        │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│            LanceurDesktop                           │
├────────────────────────────────────────────────────┤
│ + main(args : String[])                            │
└────────────────────────────────────────────────────┘
```

---

## Diagramme de Relations Principales

```
┌─────────────────┐
│ LanceurDesktop  │
└────────┬────────┘
         │ crée
         ▼
┌─────────────────┐
│ JeuPlateforme   │
└────────┬────────┘
         │
         ├─────────► ControleurJeu ────────► ControleurEntrees
         │                │
         │                ├─────────► GestionnaireNiveaux
         │                │                    │
         │                │                    ▼
         │                │              ┌──────────┐
         │                │              │  Niveau  │
         │                │              └──────────┘
         │                │                    │
         │                │         ┌──────────┼──────────┐
         │                │         │          │          │
         │                │    ┌────▼────┐ ┌──▼────┐ ┌──▼─────┐
         │                │    │ Joueur  │ │Ennemis│ │PowerUps│
         │                │    └─────────┘ └───────┘ └────────┘
         │                │
         │                └─────────► GestionnaireCollisions
         │                                    │
         │                                    └────► détecte collisions
         │
         ├─────────► RenduNiveau ────────► RenduJoueur
         │                │                  RenduEnnemi
         │                │                  RenduPowerUp
         │                │                  ...
         │
         ├─────────► RenduHUD
         ├─────────► MenuFinNiveau
         └─────────► RenduTransition
```

---

## Diagramme de Séquence: Boucle de Jeu

```
┌──────────┐  ┌─────────────┐  ┌────────┐  ┌─────────────────────┐  ┌──────────┐
│JeuPlate  │  │ControleurJeu│  │ Niveau │  │GestionnaireCollisions│  │RenduNiveau│
└────┬─────┘  └──────┬──────┘  └───┬────┘  └──────────┬──────────┘  └─────┬─────┘
     │               │              │                  │                    │
     │ render()      │              │                  │                    │
     ├──────────────►│              │                  │                    │
     │               │              │                  │                    │
     │               │ mettreAJour()│                  │                    │
     │               ├─────────────►│                  │                    │
     │               │              │                  │                    │
     │               │              │ mettreAJour()    │                    │
     │               │              │ (joueur, ennemis)│                    │
     │               │              │                  │                    │
     │               │ gererCollisions()               │                    │
     │               ├────────────────────────────────►│                    │
     │               │              │                  │                    │
     │               │              │◄─────────────────┤                    │
     │               │              │  appliquer effets│                    │
     │               │              │                  │                    │
     │               │◄─────────────┤                  │                    │
     │               │              │                  │                    │
     │ dessiner()    │              │                  │                    │
     ├──────────────────────────────────────────────────────────────────────►│
     │               │              │                  │                    │
     │               │              │                  │                    │◄──┐
     │               │              │                  │                    │   │ dessine
     │               │              │                  │                    │   │ toutes
     │               │              │                  │                    │───┘ entités
     │               │              │                  │                    │
     │◄──────────────────────────────────────────────────────────────────────┤
     │               │              │                  │                    │
```

---

## Diagramme de Séquence: Collision Joueur-Ennemi

```
┌────────┐  ┌─────────────────────┐  ┌────────┐  ┌────────┐
│ Joueur │  │GestionnaireCollisions│  │ Ennemi │  │ Niveau │
└───┬────┘  └──────────┬──────────┘  └───┬────┘  └───┬────┘
    │                  │                 │           │
    │  deplacer()      │                 │           │
    │◄─────────────────┤                 │           │
    │                  │                 │           │
    │                  │ getBoiteCollision()         │
    │                  ├────────────────►│           │
    │                  │                 │           │
    │                  │◄────────────────┤           │
    │                  │   Rectangle     │           │
    │                  │                 │           │
    │  entreEnCollisionAvec(ennemi)     │           │
    │◄─────────────────┤                 │           │
    │                  │                 │           │
    │────────────────►│                 │           │
    │    true          │                 │           │
    │                  │                 │           │
    │                  │◄────────────────┐           │
    │                  │ calculer direction          │
    │                  │                 │           │
    │                  │                 │           │
    │  si collision par dessus :         │           │
    │                  │                 │           │
    │                  │ setActive(false)│           │
    │                  ├────────────────►│           │
    │                  │                 │           │
    │  rebondir()      │                 │           │
    │◄─────────────────┤                 │           │
    │                  │                 │           │
    │                  │ notifierEnnemiVaincu()      │
    │                  ├────────────────────────────►│
    │                  │                 │           │
    │  si collision latérale :           │           │
    │                  │                 │           │
    │  subirDegats()   │                 │           │
    │◄─────────────────┤                 │           │
    │                  │                 │           │
```

---

## Diagramme d'États: État du Jeu

```
                    ┌──────────┐
                    │   MENU   │
                    └─────┬────┘
                          │ démarrer
                          ▼
                    ┌──────────┐
              ┌─────┤  EN_JEU  │◄────┐
              │     └─────┬────┘     │
              │ pause     │          │ reprendre
              │           │ fin      │
              ▼           ▼          │
        ┌──────────┐  ┌───────────────────┐
        │  PAUSE   │  │ SEQUENCE_FIN_NIVEAU│
        └──────────┘  └─────────┬─────────┘
              │                 │
              │ quitter         │ terminé
              │                 ▼
              │           ┌──────────────┐
              │           │MENU_FIN_NIVEAU│
              │           └──────┬───────┘
              │                  │
              │         ┌────────┼────────┐
              │         │ rejouer│ suivant│ quitter
              │         ▼        ▼        │
              │    ┌──────────┐  │        │
              │    │TRANSITION│  │        │
              │    │ _NIVEAU  │  │        │
              │    └─────┬────┘  │        │
              │          │        │        │
              │          └───────►│        │
              │                   │        │
              └───────────────────┴────────┘
                          │
                          ▼
                    ┌──────────┐
                    │GAME_OVER │
                    └──────────┘
```

---

## Diagramme d'États: Séquence Fin de Niveau

```
┌────────────┐
│  (Joueur   │
│   touche   │
│  drapeau)  │
└─────┬──────┘
      │
      ▼
┌─────────────┐
│  GLISSADE   │ ──────► Mario descend le long du mât
└─────┬───────┘         (contrôle retiré au joueur)
      │ atteint le sol
      ▼
┌─────────────┐
│   MARCHE    │ ──────► Mario marche automatiquement
└─────┬───────┘         vers le château (x+)
      │ arrive au château
      ▼
┌─────────────┐
│   ENTREE    │ ──────► Fade out progressif
│  _CHATEAU   │         Z-index change
└─────┬───────┘         (Mario passe derrière)
      │ disparition complète
      ▼
┌─────────────┐
│    MENU     │ ──────► Affichage "COURSE CLEAR"
└─────────────┘         Boutons: Rejouer / Suivant / Quitter
```

---

## Diagramme de Packages

```
┌───────────────────────────────────────────────────────────────┐
│                     com.mypackage.projet.jeux                  │
├───────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐      ┌──────────────────┐               │
│  │   JeuPlateforme │      │ LanceurDesktop   │               │
│  └─────────────────┘      └──────────────────┘               │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │                      modele                             │   │
│  │  ┌──────────┐  ┌───────────────┐  ┌────────────────┐  │   │
│  │  │ entites  │  │comportements  │  │ gestionnaires  │  │   │
│  │  └──────────┘  └───────────────┘  └────────────────┘  │   │
│  │  ┌──────────┐                                          │   │
│  │  │ niveau   │                                          │   │
│  │  └──────────┘                                          │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │                    controleur                           │   │
│  │  ┌──────────────┐  ┌──────────────────┐               │   │
│  │  │ControleurJeu │  │ControleurEntrees │               │   │
│  │  └──────────────┘  └──────────────────┘               │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │                        vue                              │   │
│  │  ┌─────────────┐  ┌──────────┐  ┌─────────────────┐   │   │
│  │  │RenduNiveau  │  │ RenduHUD │  │ MenuFinNiveau   │   │   │
│  │  └─────────────┘  └──────────┘  └─────────────────┘   │   │
│  │  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐   │   │
│  │  │RenduJoueur  │  │ RenduEnnemi  │  │ RenduPowerUp│   │   │
│  │  └─────────────┘  └──────────────┘  └─────────────┘   │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │                    utilitaires                          │   │
│  │  ┌──────────────┐  ┌──────────────────────┐           │   │
│  │  │ChargeurNiveau│  │SauvegardeProgression │           │   │
│  │  └──────────────┘  └──────────────────────┘           │   │
│  │  ┌──────────────────┐  ┌────────────────────────┐     │   │
│  │  │ConfigurationJeu  │  │GestionnaireRessources  │     │   │
│  │  └──────────────────┘  └────────────────────────┘     │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                 │
└───────────────────────────────────────────────────────────────┘
```

---

## Métriques du Projet

**Nombre total de classes** : 33  
**Nombre d'interfaces** : 2 (ComportementEnnemi, RenduEntite)  
**Nombre d'énumérations** : 8  
**Profondeur maximale d'héritage** : 3 (Entite → Ennemi → EnnemiTerrestre)  
**Nombre de packages** : 7  

---

**Auteurs** : Andrea Kocovic & Moussa CISSE  
**Formation** : Licence 3 MIAGE  
**Date** : Janvier 2026
