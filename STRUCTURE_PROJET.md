# 🗂️ Structure du Projet - Jeu de Plateforme Mario

## 📁 Arborescence Complète

```
projet/
│
├── 📂 src/com/mypackage/projet/jeux/
│   │
│   ├── 📂 modele/ ............................ LOGIQUE MÉTIER
│   │   │
│   │   ├── 📂 entites/ ....................... Entités du jeu
│   │   │   ├── Entite.java .................. Classe abstraite de base
│   │   │   │
│   │   │   ├── 🎮 Joueur.java ............... Personnage jouable (Mario)
│   │   │   │   • États : IMMOBILE, MARCHE, SAUTE, TOMBE, MORT
│   │   │   │   • Tailles : PETIT, GRAND, FEU
│   │   │   │   • Vies, score, invincibilité
│   │   │   │   • Alpha et visibilité (entrée château)
│   │   │   │
│   │   │   ├── 👾 Ennemi.java ............... Classe abstraite ennemis
│   │   │   │   ├── EnnemiTerrestre.java ..... Goomba (marche au sol)
│   │   │   │   └── EnnemiVolant.java ........ Koopa (vole)
│   │   │   │   • Comportement IA (Strategy)
│   │   │   │   • Limites de déplacement
│   │   │   │
│   │   │   ├── 💊 PowerUp.java .............. Classe abstraite power-ups
│   │   │   │   ├── Champignon.java .......... PETIT → GRAND
│   │   │   │   ├── FleurDeFeu.java .......... GRAND → FEU
│   │   │   │   ├── Champignon1UP.java ....... +1 vie
│   │   │   │   └── Etoile.java .............. Invincibilité
│   │   │   │
│   │   │   ├── 🔥 Projectile.java ........... Classe abstraite projectiles
│   │   │   │   └── BouleDeFeu.java .......... Lancée par Mario Feu
│   │   │   │
│   │   │   ├── 🏰 ObjetInteractif.java ...... Classe abstraite objets
│   │   │   │   ├── Drapeau.java ............. Fin de niveau
│   │   │   │   ├── Tuyau.java ............... Téléportation
│   │   │   │   └── BlocMystere.java ......... Contient power-ups
│   │   │   │
│   │   │   └── Obstacle.java ................ Murs, plateformes
│   │   │
│   │   ├── 📂 gestionnaires/ ................. Gestionnaires de logique
│   │   │   ├── GestionnaireCollisions.java .. Détecte et gère collisions
│   │   │   │   • Joueur vs Ennemis
│   │   │   │   • Joueur vs PowerUps
│   │   │   │   • Projectiles vs Ennemis
│   │   │   │   • Joueur vs Drapeau
│   │   │   │   • Joueur vs Tuyaux
│   │   │   │
│   │   │   ├── GestionnaireNiveaux.java ..... Charge et gère niveaux
│   │   │   │   • Liste des niveaux
│   │   │   │   • Niveau actuel
│   │   │   │   • Progression
│   │   │   │
│   │   │   └── GestionnaireTransformations.java
│   │   │       • Gère transformations Mario
│   │   │       • Champignon, Fleur, Dégâts
│   │   │
│   │   ├── 📂 niveau/ ........................ Structure des niveaux
│   │   │   ├── Niveau.java .................. Conteneur du niveau
│   │   │   │   • Liste ennemis, power-ups, projectiles
│   │   │   │   • Carte Tiled
│   │   │   │   • Drapeau, tuyaux
│   │   │   │
│   │   │   └── ProgressionNiveau.java ....... Score, pièces, progression
│   │   │
│   │   └── 📂 comportements/ ................. IA des ennemis (Strategy)
│   │       ├── ComportementEnnemi.java ...... Interface Strategy
│   │       ├── ComportementPatrouille.java .. Allers-retours
│   │       └── ComportementPoursuiteJoueur.java
│   │
│   ├── 📂 vue/ ............................... AFFICHAGE
│   │   │
│   │   ├── 📂 rendu/ ......................... Classes de rendu
│   │   │   ├── RenduEntite.java ............. Interface de rendu
│   │   │   │
│   │   │   ├── RenduJoueur.java ............. Affiche Mario
│   │   │   │   • Animations selon état/taille
│   │   │   │   • Alpha blending (transparence)
│   │   │   │   • Effet clignotement invincible
│   │   │   │
│   │   │   ├── RenduEnnemi.java ............. Affiche ennemis
│   │   │   ├── RenduPowerUp.java ............ Affiche power-ups
│   │   │   ├── RenduProjectile.java ......... Affiche projectiles
│   │   │   │
│   │   │   ├── RenduNiveau.java ............. Coordonne tout le rendu
│   │   │   │   • Gestion Z-index (ordre couches)
│   │   │   │   • Fond → Entités → Joueur → Château
│   │   │   │   • Carte Tiled
│   │   │   │
│   │   │   ├── RenduHUD.java ................ Interface utilisateur
│   │   │   │   • Score, vies, monde
│   │   │   │   • (Timer supprimé)
│   │   │   │
│   │   │   └── RenduMenu.java ............... Menus
│   │   │       • Menu principal
│   │   │       • Menu pause
│   │   │       • Menu fin de niveau (COURSE CLEAR)
│   │   │       • Menu game over
│   │   │
│   │   └── 📂 animations/ .................... Gestion animations
│   │       └── GestionnaireAnimations.java .. Charge et gère animations
│   │
│   ├── 📂 controleur/ ........................ COORDINATION
│   │   │
│   │   ├── ControleurJeu.java ................ CONTRÔLEUR PRINCIPAL
│   │   │   • Machine à états (State Machine)
│   │   │     - MENU_PRINCIPAL
│   │   │     - EN_JEU
│   │   │     - PAUSE
│   │   │     - SEQUENCE_FIN_NIVEAU ← NOUVEAU !
│   │   │     - RECAPITULATIF_NIVEAU
│   │   │     - GAME_OVER
│   │   │     - VICTOIRE
│   │   │   • Séquence de fin en 4 étapes :
│   │   │     1. GLISSADE_DRAPEAU
│   │   │     2. MARCHE_VERS_CHATEAU
│   │   │     3. ENTREE_CHATEAU (fade out)
│   │   │     4. ATTENTE_MENU
│   │   │   • Coordonne Modèle et Vue
│   │   │   • Boucle de jeu
│   │   │
│   │   ├── ControleurEntrees.java ............ Capture clavier/souris
│   │   │   • ← / → : Déplacement
│   │   │   • ESPACE : Saut
│   │   │   • F : Boule de feu
│   │   │   • ESC : Pause
│   │   │
│   │   └── ControleurMenu.java ............... Navigation menus
│   │
│   ├── 📂 utilitaires/ ....................... HELPERS
│   │   ├── ChargeurNiveau.java ............... Charge niveaux Tiled (.tmx)
│   │   │   • Parse couches d'objets
│   │   │   • Crée entités (joueur, ennemis, power-ups)
│   │   │
│   │   ├── ConfigurationJeu.java ............. Lit config.json
│   │   │   • Résolution, titre
│   │   │   • Liste des niveaux
│   │   │
│   │   ├── GestionnaireRessources.java ....... Charge textures, sons, fonts
│   │   │   • Singleton
│   │   │   • Cache des ressources
│   │   │
│   │   ├── GestionnaireSons.java ............. Joue sons et musiques
│   │   │   • Effets sonores
│   │   │   • Musique de fond
│   │   │
│   │   └── GestionnaireCamera.java ........... Suit le joueur
│   │       • Interpolation douce (lerp)
│   │       • Limites du niveau
│   │
│   └── JeuPlateforme.java .................... CLASSE PRINCIPALE (LibGDX)
│       • Point d'entrée render()
│       • Boucle de jeu (60 FPS)
│       • Initialisation
│
├── 📂 assets/ ................................ RESSOURCES DU JEU
│   │
│   ├── 📂 textures/ .......................... Images
│   │   ├── 📂 joueur/
│   │   │   ├── mario_petit.png
│   │   │   ├── mario_grand.png
│   │   │   ├── mario_feu.png
│   │   │   └── mario_spritesheet.png
│   │   │
│   │   ├── 📂 ennemis/
│   │   │   ├── goomba.png
│   │   │   └── koopa.png
│   │   │
│   │   ├── 📂 powerups/
│   │   │   ├── mushroom.png
│   │   │   ├── fire_flower.png
│   │   │   ├── mushroom_1up.png
│   │   │   └── star.png
│   │   │
│   │   ├── 📂 decor/
│   │   │   ├── castle.png .................. Château (fin de niveau)
│   │   │   ├── flag.png .................... Drapeau
│   │   │   ├── pipe.png .................... Tuyaux
│   │   │   └── blocks.png .................. Blocs
│   │   │
│   │   └── 📂 ui/
│   │       ├── font.fnt .................... Police HUD
│   │       └── buttons.png ................. Boutons menus
│   │
│   ├── 📂 maps/ .............................. Niveaux Tiled
│   │   ├── niveau1.tmx ..................... Niveau 1
│   │   ├── niveau2.tmx ..................... Niveau 2
│   │   │
│   │   └── 📂 tilesets/
│   │       ├── terrain.tsx ................. Tileset terrain
│   │       └── decor.tsx ................... Tileset décor
│   │
│   ├── 📂 sons/ .............................. Effets sonores
│   │   ├── saut.wav
│   │   ├── powerup.wav
│   │   ├── boule_de_feu.wav
│   │   ├── ennemi_mort.wav
│   │   ├── degats.wav
│   │   ├── game_over.wav
│   │   └── fin_niveau.wav
│   │
│   └── 📂 musiques/ .......................... Musiques de fond
│       ├── overworld.ogg ................... Musique niveau
│       └── game_over.ogg ................... Musique game over
│
├── 📂 saves/ ................................. Sauvegardes
│   └── .gitkeep .............................. Force Git à tracker le dossier
│
├── 📂 .gradle/ ............................... Build Gradle (ignoré)
├── 📂 .idea/ ................................. Config IntelliJ (ignoré)
├── 📂 build/ ................................. Fichiers compilés (ignoré)
│
├── 📄 build.gradle ........................... Configuration Gradle
├── 📄 gradle.properties ...................... Propriétés Gradle
├── 📄 settings.gradle ........................ Paramètres projet
│
├── 📄 .gitignore ............................. Fichiers ignorés par Git
│
├── 📄 ARCHITECTURE.md ........................ 📚 CETTE DOCUMENTATION
├── 📄 STRUCTURE_PROJET.md .................... 📚 CE FICHIER
├── 📄 GUIDE_POWERUP_CHAMPIGNON.md ............ Guide power-ups
├── 📄 GUIDE_ZONE_FIN_NIVEAU.md ............... Guide fin de niveau
├── 📄 TEST_POWERUP_TILED.md .................. Tests Tiled
└── 📄 ARBORESCENCE.txt ....................... Arborescence simple
```

---

## 🎯 Organisation par Pattern

### 🔷 MODÈLE (Model) - Logique Métier

```
📦 modele/
   ├── Entités (données + comportements)
   ├── Gestionnaires (orchestration logique)
   ├── Niveaux (structure des niveaux)
   └── Comportements (IA Strategy)
```

**Responsabilité** : Contient TOUTE la logique métier du jeu, **indépendante de l'affichage**.

### 🔷 VUE (View) - Affichage

```
📦 vue/
   ├── Rendu (affichage des entités)
   └── Animations (gestion animations)
```

**Responsabilité** : Affiche les données du Modèle, **sans logique métier**.

### 🔷 CONTRÔLEUR (Controller) - Coordination

```
📦 controleur/
   ├── ControleurJeu (chef d'orchestre)
   ├── ControleurEntrees (clavier/souris)
   └── ControleurMenu (navigation menus)
```

**Responsabilité** : **Coordonne** Modèle et Vue, gère les états du jeu.

### 🔷 UTILITAIRES (Utilities) - Helpers

```
📦 utilitaires/
   ├── Chargeurs (niveaux, ressources)
   ├── Gestionnaires (sons, caméra)
   └── Configuration
```

**Responsabilité** : Fonctions transversales et outils.

---

## 🔗 Dépendances entre Composants

```
┌─────────────────────────────────────────────────────────────────┐
│                    JeuPlateforme.java                           │
│                   (Classe principale)                           │
└────────────────────────────┬────────────────────────────────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
    ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
    │ Contrôleur  │  │   Modèle    │  │     Vue     │
    └─────┬───────┘  └──────┬──────┘  └──────┬──────┘
          │                 │                 │
          │    Commandes    │                 │
          └────────────────>│                 │
          │                 │                 │
          │    Données      │                 │
          │<────────────────┘                 │
          │                                   │
          │    Affichage                      │
          └──────────────────────────────────>│
                                              │
                            Écran ◄───────────┘
```

**Flux**:
1. **ControleurJeu** reçoit les entrées utilisateur
2. **ControleurJeu** met à jour le **Modèle** (entités, collisions)
3. **ControleurJeu** demande à la **Vue** d'afficher le **Modèle**
4. La **Vue** dessine le jeu à l'écran

---

## 📊 Statistiques du Projet

### Répartition du Code

| Composant | Nombre de Classes | % du Total |
|-----------|-------------------|------------|
| **Modèle** | ~23 classes | 58% |
| **Vue** | ~7 classes | 18% |
| **Contrôleur** | ~3 classes | 7% |
| **Utilitaires** | ~7 classes | 17% |
| **TOTAL** | **~40 classes** | **100%** |

### Lignes de Code (approximatif)

```
Modèle ...................... ~2500 lignes
Vue ......................... ~1000 lignes
Contrôleur .................. ~1000 lignes
Utilitaires ................. ~500 lignes
─────────────────────────────────────────
TOTAL ....................... ~5000 lignes
```

---

## 🎨 Couches de Rendu (Z-Index)

```
┌─────────────────────────────────────────┐  ▲
│          DRAPEAU (toujours visible)     │  │ Plus
├─────────────────────────────────────────┤  │ proche
│      CHÂTEAU & PREMIER PLAN (layer 3)   │  │ du
├─────────────────────────────────────────┤  │ joueur
│            JOUEUR (Mario)               │  │
├─────────────────────────────────────────┤  │
│  ENNEMIS, POWER-UPS, PROJECTILES        │  │
├─────────────────────────────────────────┤  │
│       DÉCOR ARRIÈRE (layer 1)           │  │
├─────────────────────────────────────────┤  │
│           FOND (layer 0)                │  ▼ Plus
└─────────────────────────────────────────┘    loin
```

**Effet** : Mario passe **DERRIÈRE** le château lors de l'entrée.

---

## 🔄 Cycle de Vie d'une Frame (60 FPS)

```
┌─────────────────────────────────────────────────────────┐
│ 1. ENTRÉES (ControleurEntrees)                          │
│    └─> Capture clavier : ←, →, ESPACE, F               │
│        └─> Traduit en actions : marcher, sauter, tirer │
└───────────────────────────┬─────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────┐
│ 2. LOGIQUE MÉTIER (Modèle)                              │
│    ├─> Joueur.mettreAJour(deltaTemps)                   │
│    │   ├─ Applique gravité                              │
│    │   ├─ Met à jour position                           │
│    │   └─ Gère invincibilité                            │
│    │                                                     │
│    ├─> Ennemis.mettreAJour(deltaTemps)                  │
│    │   ├─ Exécute comportement IA                       │
│    │   └─ Vérifie limites                               │
│    │                                                     │
│    ├─> PowerUps.mettreAJour(deltaTemps)                 │
│    │   └─ Déplacement horizontal                        │
│    │                                                     │
│    ├─> Projectiles.mettreAJour(deltaTemps)              │
│    │   └─ Déplacement + durée de vie                    │
│    │                                                     │
│    └─> Drapeau.mettreAJour(deltaTemps)                  │
│        └─ Descente si touché                            │
└───────────────────────────┬─────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────┐
│ 3. COLLISIONS (GestionnaireCollisions)                  │
│    ├─> Joueur vs Ennemis                                │
│    ├─> Joueur vs PowerUps                               │
│    ├─> Projectiles vs Ennemis                           │
│    ├─> Joueur vs Drapeau                                │
│    └─> Joueur vs Tuyaux                                 │
└───────────────────────────┬─────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────┐
│ 4. VÉRIFICATIONS (ControleurJeu)                        │
│    ├─> Fin de niveau ? (drapeau touché)                 │
│    └─> Game over ? (vies == 0)                          │
└───────────────────────────┬─────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────┐
│ 5. RENDU (Vue)                                           │
│    ├─> RenduNiveau.dessiner()                           │
│    │   ├─ Fond (layer 0)                                │
│    │   ├─ Décor arrière (layer 1)                       │
│    │   ├─ Ennemis, power-ups, projectiles               │
│    │   ├─ Joueur (avec alpha blending)                  │
│    │   ├─ Premier plan / Château (layer 2-3)            │
│    │   └─ Drapeau                                        │
│    │                                                     │
│    └─> RenduHUD.dessiner()                              │
│        └─ Score, vies, monde                            │
└───────────────────────────┬─────────────────────────────┘
                            ▼
                    AFFICHAGE À L'ÉCRAN
                  (Attend ~16ms pour 60 FPS)
```

---

## 🚀 Points d'Extension

### 🆕 Ajouter un Nouvel Ennemi

**Fichiers à modifier**:
1. `modele/entites/` → Créer `NouvelEnnemi.java` extends `Ennemi`
2. `vue/rendu/` → Créer `RenduNouvelEnnemi.java` implements `RenduEntite`
3. `utilitaires/ChargeurNiveau.java` → Ajouter case dans `creerEnnemiDepuisObjet()`
4. `assets/textures/ennemis/` → Ajouter sprite

### 🆕 Ajouter un Nouveau Power-Up

**Fichiers à modifier**:
1. `modele/entites/` → Créer `NouveauPowerUp.java` extends `PowerUp`
2. `vue/rendu/` → Utiliser `RenduPowerUp.java` existant
3. `utilitaires/FabriqueEntites.java` → Ajouter case
4. `assets/textures/powerups/` → Ajouter sprite

### 🆕 Ajouter un Nouveau Niveau

**Fichiers à modifier**:
1. `assets/maps/` → Créer `niveauX.tmx` dans Tiled
2. `config.json` → Ajouter chemin du niveau

**Aucun code Java à modifier !** 🎉

---

## 🏆 Fonctionnalités Implémentées

### ✅ Joueur
- [x] Déplacement fluide (← / →)
- [x] Saut (ESPACE)
- [x] 3 tailles : PETIT, GRAND, FEU
- [x] 3 vies de base
- [x] Invincibilité temporaire
- [x] Transparence progressive (alpha)
- [x] Boules de feu (touche F)

### ✅ Ennemis
- [x] Goomba (terrestre)
- [x] Koopa (volant)
- [x] IA avec comportements (Strategy)
- [x] Limites de déplacement (ne dépassent pas le château)

### ✅ Power-Ups
- [x] Champignon (agrandit Mario)
- [x] Fleur de Feu (donne pouvoir de feu)
- [x] Champignon 1UP (+1 vie)
- [x] Étoile (invincibilité)

### ✅ Objets Interactifs
- [x] Drapeau (déclenche fin de niveau)
- [x] Château (entrée avec effet de profondeur)
- [x] Tuyaux (téléportation)
- [x] Blocs mystères

### ✅ Système de Fin de Niveau
- [x] Séquence en 4 étapes (State Machine)
  1. Glissade le long du mât
  2. Marche vers le château
  3. Entrée dans le château (fade out)
  4. Affichage du menu "COURSE CLEAR"
- [x] Reset complet lors du "Rejouer"

### ✅ Interface
- [x] HUD (score, vies, monde)
- [x] Menu principal
- [x] Menu pause
- [x] Menu fin de niveau
- [x] Menu game over

### ✅ Technique
- [x] Architecture MVC propre
- [x] State Machine (états du jeu)
- [x] Pattern Strategy (comportements IA)
- [x] Z-index pour le rendu
- [x] Alpha blending
- [x] Caméra qui suit le joueur
- [x] Chargement depuis Tiled
- [x] Animations fluides (60 FPS)

---

## 📚 Documentation Disponible

| Fichier | Description |
|---------|-------------|
| **ARCHITECTURE.md** | Documentation complète de l'architecture (CE FICHIER) |
| **STRUCTURE_PROJET.md** | Vue d'ensemble de la structure (ce fichier) |
| **GUIDE_POWERUP_CHAMPIGNON.md** | Guide d'implémentation des power-ups |
| **GUIDE_ZONE_FIN_NIVEAU.md** | Guide de la séquence de fin de niveau |
| **TEST_POWERUP_TILED.md** | Tests et configuration Tiled |
| **ARBORESCENCE.txt** | Arborescence simple du projet |

---

## 🎓 Concepts Pédagogiques Démontrés

### Programmation Orientée Objet (POO)

✅ **Héritage** : `Entite` → `Joueur`, `Ennemi`, `PowerUp`  
✅ **Polymorphisme** : Méthode `mettreAJour()` redéfinie  
✅ **Encapsulation** : Attributs privés + getters/setters  
✅ **Abstraction** : Classes abstraites et interfaces  
✅ **Composition** : `Niveau` contient des `Entite`  

### Patterns de Conception (Design Patterns)

✅ **MVC** : Séparation Modèle-Vue-Contrôleur  
✅ **Strategy** : Comportements interchangeables  
✅ **State Machine** : Gestion des états  
✅ **Observer** : Événements de collision  
✅ **Singleton** : Gestionnaires uniques  
✅ **Factory** : Création d'entités  

### Principes SOLID

✅ **S** - Single Responsibility : Une classe = une responsabilité  
✅ **O** - Open/Closed : Ouvert extension, fermé modification  
✅ **L** - Liskov Substitution : Sous-classes interchangeables  
✅ **I** - Interface Segregation : Interfaces ciblées  
✅ **D** - Dependency Inversion : Dépendance aux abstractions  

---

## 🔧 Outils et Dépendances

### Build System

- **Gradle 7.4** - Gestionnaire de build
- **Java 17** - Langage

### Bibliothèques

- **LibGDX 1.12.0** - Framework de jeu 2D
- **LWJGL 3** - Backend OpenGL
- **FreeType** - Rendu de fonts

### Outils de Développement

- **IntelliJ IDEA** - IDE
- **Tiled Map Editor** - Éditeur de niveaux
- **Git** - Contrôle de version
- **GIMP / Aseprite** - Édition sprites

---

## 📍 Conclusion

Cette structure démontre une **organisation professionnelle** d'un projet de jeu, avec :

✅ Séparation claire des responsabilités (MVC)  
✅ Code maintenable et extensible  
✅ Respect des principes SOLID  
✅ Documentation complète  
✅ Organisation logique des fichiers  

Le projet peut facilement **évoluer** pour ajouter de nouvelles fonctionnalités sans réécriture majeure ! 🚀

---

**Projet réalisé pour** : LICENCE 3 MIAGE - SESSION 5  
**Date** : Janvier 2026  
**Langage** : Java 17  
**Framework** : LibGDX 1.12.0
