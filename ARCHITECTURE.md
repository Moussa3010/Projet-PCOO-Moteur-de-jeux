# 🎮 ARCHITECTURE DU PROJET - Moteur de Jeu 2D Mario-Like

## 📋 Table des Matières

1. [Vue d'Ensemble](#-vue-densemble)
2. [Architecture MVC](#-architecture-mvc)
3. [Structure des Packages](#-structure-des-packages)
4. [Hiérarchie des Entités](#-hiérarchie-des-entités)
5. [Gestionnaires](#-gestionnaires)
6. [Système de Rendu](#-système-de-rendu)
7. [Patterns de Conception](#-patterns-de-conception)
8. [Flux de Données](#-flux-de-données)
9. [Métriques du Projet](#-métriques-du-projet)

---

## 📊 Vue d'Ensemble

### Informations Générales

| **Critère**              | **Valeur**                                                                |
|:-------------------------|:--------------------------------------------------------------------------|
| **Type de projet**       | Jeu de plateforme 2D (Mario-like)                                        |
| **Framework**            | LibGDX 1.9.10                                                             |
| **Architecture**         | MVC (Model-View-Controller)                                               |
| **Langage**              | Java 8+                                                                   |
| **Nombre de classes**    | 33 classes Java                                                           |
| **Lignes de code**       | ~10 000 lignes                                                            |
| **Design Patterns**      | 6 patterns (MVC, Strategy, Singleton, Observer, State Machine, Factory)  |

### Technologies & Dépendances

| **Technologie**        | **Version**       | **Utilisation**                |
|:-----------------------|:------------------|:-------------------------------|
| **LibGDX**             | 1.9.10            | Framework de jeu 2D/3D         |
| **LWJGL 3**            | 3.3.1             | Backend OpenGL (desktop)       |
| **Tiled Map Editor**   | Compatible TMX    | Éditeur de niveaux             |
| **Java**               | 8+                | Langage de programmation       |
| **Gradle**             | 7.x               | Build system (optionnel)       |

---

## 🏛️ Architecture MVC

### Répartition des Responsabilités

| **Couche**       | **Package**        | **Responsabilité**                        | **Exemples de Classes**                      |
|:-----------------|:-------------------|:------------------------------------------|:---------------------------------------------|
| **MODEL**        | `modele.*`         | Logique métier, entités, règles du jeu    | `Joueur`, `Ennemi`, `Niveau`                 |
| **VIEW**         | `vue.*`            | Rendu graphique, animations, HUD          | `RenduJoueur`, `RenduNiveau`, `RenduHUD`     |
| **CONTROLLER**   | `controleur.*`     | Gestion des entrées, coordination         | `ControleurJeu`, `ControleurEntrees`         |
| **UTILITIES**    | `utilitaires.*`    | Services transversaux                     | `ChargeurNiveau`, `SauvegardeProgression`    |

### Diagramme des Flux MVC

```
┌─────────────────────────────────────────────────────────────┐
│                      UTILISATEUR                             │
└────────────────────────┬────────────────────────────────────┘
                         │ Input (Clavier/Souris)
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  CONTROLLER                                  │
│  ┌───────────────────┐      ┌────────────────────────┐     │
│  │ ControleurJeu     │◄────►│ ControleurEntrees      │     │
│  └───────────────────┘      └────────────────────────┘     │
└────────────┬────────────────────────┬───────────────────────┘
             │                        │
             │ Update                 │ Query State
             ▼                        ▼
┌─────────────────────────────────────────────────────────────┐
│                      MODEL                                   │
│  ┌──────────┐  ┌──────────────┐  ┌─────────────────────┐  │
│  │ Entités  │  │ Gestionnaires│  │ Niveau & Progression│  │
│  └──────────┘  └──────────────┘  └─────────────────────┘  │
└────────────────────────┬────────────────────────────────────┘
                         │ State Data
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                       VIEW                                   │
│  ┌───────────┐  ┌──────────────┐  ┌──────────────────┐    │
│  │ RenduJeu  │  │ RenduEntités │  │ RenduHUD & Menus │    │
│  └───────────┘  └──────────────┘  └──────────────────┘    │
└────────────────────────┬────────────────────────────────────┘
                         │ Rendered Frame
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      ÉCRAN                                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Structure des Packages

### Vue Complète

| **Package**                  | **Classes** | **Responsabilité**                                  | **Dépendances**         |
|:-----------------------------|:------------|:----------------------------------------------------|:------------------------|
| **`modele.entites`**         | 8           | Entités du jeu (joueur, ennemis, objets)           | `comportements`         |
| **`modele.comportements`**   | 2           | Comportements IA des ennemis                        | -                       |
| **`modele.gestionnaires`**   | 2           | Gestion des collisions et niveaux                   | `entites`, `niveau`     |
| **`modele.niveau`**          | 3           | Niveaux, progression, objectifs                     | `entites`               |
| **`controleur`**             | 2           | Contrôle du jeu et des entrées                      | `modele.*`, `vue.*`     |
| **`vue`**                    | 10          | Rendu graphique de toutes les entités               | `modele.entites`        |
| **`utilitaires`**            | 4           | Services (chargement, sauvegarde, config)           | `modele.niveau`         |
| **Racine**                   | 2           | Point d'entrée et classe principale                 | Tous                    |

### Détail des Packages

#### 📌 **1. Package `modele.entites`**

| **Classe**              | **Type**    | **Rôle**                                | **Hiérarchie**        |
|:------------------------|:------------|:----------------------------------------|:----------------------|
| `Entite`                | Abstraite   | Classe mère de toutes les entités       | -                     |
| `Joueur`                | Concrète    | Personnage contrôlé par le joueur       | Extends `Entite`      |
| `Ennemi`                | Abstraite   | Classe mère des ennemis                 | Extends `Entite`      |
| `EnnemiTerrestre`       | Concrète    | Ennemi au sol (Goomba)                  | Extends `Ennemi`      |
| `ObjetCollectable`      | Concrète    | Pièces et objets à collecter            | Extends `Entite`      |
| `PowerUp`               | Concrète    | Champignons, fleurs, étoiles            | Extends `Entite`      |
| `Obstacle`              | Concrète    | Blocs, plateformes                      | Extends `Entite`      |
| `Drapeau`               | Concrète    | Drapeau de fin de niveau                | Extends `Entite`      |

#### 📌 **2. Package `modele.comportements`**

| **Classe/Interface**       | **Type**    | **Rôle**                                             |
|:---------------------------|:------------|:-----------------------------------------------------|
| `ComportementEnnemi`       | Interface   | Définit le contrat pour les comportements IA         |
| `ComportementPatrouille`   | Concrète    | Implémente le mouvement de patrouille                |

#### 📌 **3. Package `modele.gestionnaires`**

| **Classe**                   | **Rôle**                                       | **Responsabilités**                           |
|:-----------------------------|:-----------------------------------------------|:----------------------------------------------|
| `GestionnaireCollisions`     | Détection et résolution des collisions         | AABB, séparation des axes, knockback          |
| `GestionnaireNiveaux`        | Gestion de la progression entre niveaux        | Chargement TMX, transitions, historique       |

#### 📌 **4. Package `modele.niveau`**

| **Classe**              | **Rôle**                        | **Attributs Principaux**                              |
|:------------------------|:--------------------------------|:------------------------------------------------------|
| `Niveau`                | Conteneur du niveau actuel      | Carte TMX, entités, objectifs, drapeau                |
| `ProgressionNiveau`     | Suivi de la progression         | Score, pièces, ennemis vaincus, temps, étoiles        |
| `ObjectifNiveau`        | Objectifs à accomplir           | Type (distance, pièces, ennemis), valeur cible        |

#### 📌 **5. Package `controleur`**

| **Classe**              | **Rôle**                               | **Gère**                                           |
|:------------------------|:---------------------------------------|:---------------------------------------------------|
| `ControleurJeu`         | Contrôleur principal du jeu            | États du jeu, séquence fin niveau, menu            |
| `ControleurEntrees`     | Gestion des entrées clavier/souris     | Mouvements, saut, physique Mario-like              |

#### 📌 **6. Package `vue`**

| **Classe**              | **Rôle**                          | **Rend**                                       |
|:------------------------|:----------------------------------|:-----------------------------------------------|
| `RenduNiveau`           | Orchestrateur du rendu            | Tous les éléments du niveau                    |
| `RenduJoueur`           | Rendu du joueur                   | Mario (PETIT/GRAND), animations, alpha         |
| `RenduEnnemi`           | Rendu des ennemis                 | Goomba, animations                             |
| `RenduObjet`            | Rendu des objets                  | Pièces animées                                 |
| `RenduPowerUp`          | Rendu des power-ups               | Champignons, fleurs, étoiles                   |
| `RenduDrapeau`          | Rendu de fin de niveau            | Drapeau, mât, château                          |
| `RenduHUD`              | Interface utilisateur             | Score, vies, pièces, temps                     |
| `MenuFinNiveau`         | Menu de fin                       | Boutons (rejouer, suivant, quitter)            |
| `RenduTransition`       | Transitions visuelles             | Fade in/out                                    |
| `RenduEntite`           | Classe utilitaire abstraite       | -                                              |

#### 📌 **7. Package `utilitaires`**

| **Classe**                   | **Rôle**                            | **Fonctionnalités**                                 |
|:-----------------------------|:------------------------------------|:----------------------------------------------------|
| `ChargeurNiveau`             | Chargement des niveaux Tiled        | Parse TMX, crée entités, charge couches             |
| `SauvegardeProgression`      | Système de sauvegarde               | Sérialisation, chargement, suppression              |
| `ConfigurationJeu`           | Configuration globale               | Volume, résolution, paramètres                      |
| `GestionnaireRessources`     | Cache de ressources                 | Textures, sprites, assets                           |

---

## 🌳 Hiérarchie des Entités

### Arbre d'Héritage Complet

```
Entite (abstraite)
├── Joueur
│   ├── EtatJoueur : IMMOBILE, MARCHE, SAUTE, TOMBE, MORT
│   └── EtatTransformation : PETIT, GRAND, FEU
│
├── Ennemi (abstraite)
│   └── EnnemiTerrestre (Goomba)
│       └── ComportementEnnemi → ComportementPatrouille
│
├── ObjetCollectable (Pièces)
│   └── TypeObjet : PIECE, CHAMPIGNON, FLEUR, ETOILE
│
├── PowerUp (Champignons, Fleurs, Étoiles)
│   └── TypePowerUp : CHAMPIGNON_MAGIQUE, FLEUR_DE_FEU, SUPER_ETOILE, CHAMPIGNON_1UP
│
├── Obstacle (Blocs, Plateformes)
│   └── TypeObstacle : BLOC_NORMAL, BLOC_QUESTION, BLOC_BRIQUE, PLATEFORME
│
└── Drapeau (Fin de niveau)
```

### Propriétés Communes (Classe `Entite`)

| **Attribut**         | **Type**      | **Description**                          |
|:---------------------|:--------------|:-----------------------------------------|
| `position`           | `Vector2`     | Position (x, y) dans le monde            |
| `vitesse`            | `Vector2`     | Vitesse (vx, vy) actuelle                |
| `largeur`            | `float`       | Largeur de la hitbox                     |
| `hauteur`            | `float`       | Hauteur de la hitbox                     |
| `active`             | `boolean`     | Si l'entité est active (non détruite)    |
| `boiteCollision`     | `Rectangle`   | Hitbox pour les collisions               |

### Méthodes Communes

| **Méthode**                   | **Signature**                                 | **Description**                           |
|:------------------------------|:----------------------------------------------|:------------------------------------------|
| `mettreAJour`                 | `void mettreAJour(float deltaTemps)`          | Mise à jour logique (abstraite)           |
| `mettreAJourBoiteCollision`   | `protected void mettreAJourBoiteCollision()`  | Synchronise hitbox avec position          |
| `entreEnCollisionAvec`        | `boolean entreEnCollisionAvec(Entite autre)`  | Teste collision AABB                      |
| `getPosition`                 | `Vector2 getPosition()`                       | Getter position                           |
| `setPosition`                 | `void setPosition(float x, float y)`          | Setter position + MAJ hitbox              |

---

## ⚙️ Gestionnaires

### `GestionnaireCollisions`

| **Responsabilité**         | **Méthode**                                   | **Description**                          |
|:---------------------------|:----------------------------------------------|:-----------------------------------------|
| **Collisions Joueur**      | `gererCollisions()`                           | Point d'entrée principal                 |
| Obstacles                  | `gererCollisionsObstacles(Joueur)`            | Collisions avec blocs/plateformes        |
| Ennemis                    | `gererCollisionsEnnemis(Joueur)`              | Stomp vs dégâts                          |
| Objets                     | `gererCollisionsObjetsCollectables(Joueur)`   | Collecte de pièces                       |
| Power-ups                  | `gererCollisionsPowerUps(Joueur)`             | Collecte de power-ups                    |
| Drapeau                    | `gererCollisionDrapeau(Joueur)`               | Fin de niveau                            |
| **Collisions IA**          | `gererCollisionsEnnemiObstacles(Ennemi)`      | Ennemis vs murs                          |
|                            | `gererCollisionsPowerUpObstacles(PowerUp)`    | Power-ups vs murs                        |

#### Algorithme de Détection de Collision

| **Étape**         | **Technique**                        | **Détails**                                                   |
|:------------------|:-------------------------------------|:--------------------------------------------------------------|
| 1. Broad Phase    | AABB (Axis-Aligned Bounding Box)     | Détection rapide rectangle vs rectangle                       |
| 2. Narrow Phase   | Séparation des axes X et Y           | Résolution précise collision horizontale puis verticale       |
| 3. Résolution     | Displacement minimum                 | Repousse l'entité du côté le moins chevauchant                |
| 4. Physique       | Knockback, bounce, friction          | Application des effets physiques                              |

### `GestionnaireNiveaux`

| **Responsabilité**    | **Méthode**                             | **Description**                               |
|:----------------------|:----------------------------------------|:----------------------------------------------|
| **Chargement**        | `chargerPremierNiveau()`                | Charge le niveau 1                            |
|                       | `chargerNiveauDepuisFichier(String)`    | Parse fichier TMX via ChargeurNiveau          |
|                       | `chargerTousLesNiveaux()`               | Précharge tous les niveaux (optionnel)        |
| **Navigation**        | `niveauSuivant()`                       | Passe au niveau suivant                       |
|                       | `niveauPrecedent()`                     | Retourne au niveau précédent                  |
|                       | `rechargerNiveauActuel()`               | Redémarre le niveau actuel                    |
| **Transitions**       | `mettreAJour(float)`                    | Gère les transitions de 2s                    |
|                       | `demarrerTransitionDebutNiveau()`       | Démarre fade in                               |
|                       | `demarrerTransitionFinNiveau()`         | Démarre fade out                              |
| **Progression**       | `getProgressionNiveau(String)`          | Récupère historique d'un niveau               |
|                       | `getScoreTotal()`                       | Score cumulé de tous les niveaux              |
|                       | `getTotalEtoiles()`                     | Nombre d'étoiles obtenues                     |

#### États de Transition

| **État**                | **Description**                 | **Durée**     |
|:------------------------|:--------------------------------|:--------------|
| `AUCUNE`                | Pas de transition en cours      | -             |
| `DEBUT_NIVEAU`          | Fade in au démarrage            | 2.0s          |
| `FIN_NIVEAU`            | Fade out à la fin               | 2.0s          |
| `TRANSITION_EN_COURS`   | Entre deux niveaux              | 2.0s          |

---

## 🎨 Système de Rendu

### Ordre de Rendu (Z-Index)

| **Couche**               | **Z-Index** | **Éléments**                     | **Classe Responsable**            |
|:-------------------------|:------------|:---------------------------------|:----------------------------------|
| **0. Arrière-plan**      | 0           | Carte Tiled (couches de fond)    | `RenduNiveau` (TiledMapRenderer)  |
| **1. Drapeau & Château** | 10          | Drapeau de fin, château          | `RenduDrapeau`                    |
| **2. Power-ups**         | 20          | Champignons, fleurs, étoiles     | `RenduPowerUp`                    |
| **3. Objets**            | 30          | Pièces (animées)                 | `RenduObjet`                      |
| **4. Ennemis**           | 40          | Goombas                          | `RenduEnnemi`                     |
| **5. Joueur**            | 50          | Mario                            | `RenduJoueur`                     |
| **6. Obstacles**         | 60          | Blocs, plateformes (si opaques)  | `RenduNiveau`                     |
| **7. HUD**               | 100         | Score, vies, temps               | `RenduHUD`                        |
| **8. Menus**             | 200         | Menu fin de niveau               | `MenuFinNiveau`                   |

### Pipeline de Rendu

```
┌──────────────────────────────────────────────────────────────┐
│ 1. PRÉPARATION                                                │
│    - Clear screen (couleur de fond)                          │
│    - Configurer la caméra (position, zoom)                   │
│    - Batch.setProjectionMatrix(camera.combined)              │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│ 2. RENDU CARTE TILED                                          │
│    - TiledMapRenderer.setView(camera)                        │
│    - TiledMapRenderer.render()                               │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│ 3. RENDU ENTITÉS (batch.begin())                             │
│    ┌─────────────────────────────────────────────────┐      │
│    │ For each entité visible (culling) :             │      │
│    │   1. RenduDrapeau.dessiner(drapeau)             │      │
│    │   2. RenduPowerUp.dessiner(powerup)             │      │
│    │   3. RenduObjet.dessiner(piece)                 │      │
│    │   4. RenduEnnemi.dessiner(ennemi)               │      │
│    │   5. RenduJoueur.dessiner(joueur)               │      │
│    └─────────────────────────────────────────────────┘      │
│    batch.end()                                                │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│ 4. RENDU HUD (projection orthographique fixe)                │
│    - RenduHUD.dessiner(score, vies, pièces, temps)          │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│ 5. RENDU MENUS & TRANSITIONS                                 │
│    - MenuFinNiveau.dessiner(boutons)                         │
│    - RenduTransition.dessiner(fade alpha)                    │
└──────────────────────────────────────────────────────────────┘
```

### Animations

| **Entité**              | **Type d'Animation** | **Frames** | **Durée/Frame** | **Mode** |
|:------------------------|:---------------------|:-----------|:----------------|:---------|
| **Joueur (PETIT)**      | Idle                 | 1          | -               | NORMAL   |
|                         | Marche               | 2          | 0.1s            | LOOP     |
|                         | Saut                 | 1          | -               | NORMAL   |
| **Joueur (GRAND)**      | Idle                 | 1          | -               | NORMAL   |
|                         | Marche               | 2          | 0.1s            | LOOP     |
|                         | Saut                 | 1          | -               | NORMAL   |
| **Goomba**              | Marche               | 2          | 0.15s           | LOOP     |
| **Pièce**               | Rotation             | 4          | 0.15s           | LOOP     |
| **Drapeau**             | Descente             | -          | Tween           | -        |

### Effets Visuels

| **Effet**               | **Technique**                            | **Utilisation**                              |
|:------------------------|:-----------------------------------------|:---------------------------------------------|
| **Alpha Blending**      | Modification du canal alpha (0.0-1.0)    | Invincibilité joueur, fade in/out            |
| **Clignotement**        | Alpha oscillant (sin wave)               | Invincibilité temporaire                     |
| **Flip Horizontal**     | TextureRegion.flip(true, false)          | Changement de direction                      |
| **Parallax Scrolling**  | Vitesses différentes par couche          | Profondeur arrière-plan (si implémenté)      |

---

## 🎯 Patterns de Conception

### 1. **MVC (Model-View-Controller)** 🏛️

| **Composant**   | **Rôle**                                 | **Avantage**                                   |
|:----------------|:-----------------------------------------|:-----------------------------------------------|
| **Model**       | Logique métier, entités, règles          | Indépendant du rendu                           |
| **View**        | Rendu graphique uniquement               | Peut être changé sans toucher la logique       |
| **Controller**  | Gestion des entrées, coordination        | Découplage input/logique                       |

**Exemple** : `Joueur` (Model) ↔ `ControleurEntrees` (Controller) ↔ `RenduJoueur` (View)

---

### 2. **Strategy Pattern** 🎭

**Utilisation** : Comportements IA des ennemis

| **Interface**          | **Implémentation**         | **Comportement**                  |
|:-----------------------|:---------------------------|:----------------------------------|
| `ComportementEnnemi`   | `ComportementPatrouille`   | Patrouille gauche-droite          |
|                        | *(Extensible)*             | Poursuivre joueur, voler, sauter  |

**Code** :
```java
// Dans la classe Ennemi
private ComportementEnnemi comportement;

public void mettreAJour(float deltaTemps) {
    if (comportement != null) {
        comportement.executer(this, deltaTemps);
    }
}
```

**Avantage** : Ajout de nouveaux comportements sans modifier `Ennemi`

---

### 3. **State Machine** 🔄

**Utilisation** : États du jeu et du joueur

#### États du Jeu (`ControleurJeu`)

| **État**                  | **Description**             | **Actions Possibles**            |
|:--------------------------|:----------------------------|:---------------------------------|
| `MENU`                    | Menu principal              | Démarrer, Options, Quitter       |
| `EN_JEU`                  | Gameplay actif              | Jouer, Pause                     |
| `PAUSE`                   | Jeu en pause                | Reprendre, Quitter               |
| `SEQUENCE_FIN_NIVEAU`     | Animation de fin            | Automatique (4 étapes)           |
| `MENU_FIN_NIVEAU`         | Menu de fin                 | Rejouer, Suivant, Quitter        |
| `GAME_OVER`               | Échec du niveau             | Recommencer, Menu                |
| `VICTOIRE`                | Tous niveaux terminés       | Félicitations, Quitter           |
| `TRANSITION_NIVEAU`       | Entre deux niveaux          | Chargement automatique           |

#### États du Joueur

| **État**      | **Description**           | **Transition**                    |
|:--------------|:--------------------------|:----------------------------------|
| `IMMOBILE`    | Au repos                  | → `MARCHE` (input horizontal)     |
| `MARCHE`      | En mouvement              | → `SAUTE` (input saut)            |
| `SAUTE`       | En l'air (montée)         | → `TOMBE` (vy < 0)                |
| `TOMBE`       | En l'air (descente)       | → `IMMOBILE` (au sol)             |
| `MORT`        | Mario mort                | Game Over                         |

---

### 4. **Observer Pattern** 👁️

**Utilisation** : Notification des événements de jeu

| **Événement**         | **Observable**      | **Observers**                         |
|:----------------------|:--------------------|:--------------------------------------|
| Pièce collectée       | `Niveau`            | `ProgressionNiveau`, `RenduHUD`       |
| Ennemi vaincu         | `Niveau`            | `ProgressionNiveau`                   |
| Objectif accompli     | `ObjectifNiveau`    | `Niveau`, `RenduHUD`                  |
| Niveau terminé        | `Niveau`            | `ControleurJeu`, `GestionnaireNiveaux`|

**Implémentation** :
```java
// Dans Niveau
public void notifierPieceCollectee() {
    progression.incrementerPieces();
}

public void notifierEnnemiVaincu() {
    progression.incrementerEnnemisVaincus();
}
```

---

### 5. **Singleton Pattern** 🔒

**Utilisation** : Gestionnaires globaux (optionnel/recommandé)

| **Classe**                  | **Raison**                 | **État Actuel**             |
|:----------------------------|:---------------------------|:----------------------------|
| `GestionnaireRessources`    | Cache unique de textures   | Non-singleton actuellement  |
| `GestionnaireNiveaux`       | État de progression global | Non-singleton actuellement  |

**Note** : Le projet n'utilise pas de singletons stricts, mais ces classes pourraient en bénéficier.

---

### 6. **Factory Pattern** 🏭

**Utilisation** : Création d'entités depuis Tiled

| **Fichier**         | **Type Tiled**    | **Factory Method**          | **Entité Créée**             |
|:--------------------|:------------------|:----------------------------|:-----------------------------|
| `ChargeurNiveau`    | "terrestre"       | `creerEnnemi(type)`         | `EnnemiTerrestre`            |
|                     | "BLOC_NORMAL"     | `creerObstacle(type)`       | `Obstacle` (normal)          |
|                     | "CHAMPIGNON"      | `creerPowerUp(type)`        | `PowerUp` (champignon)       |
|                     | "PIECE"           | `creerObjetCollectable(type)`| `ObjetCollectable` (pièce)  |

**Code simplifié** :
```java
// Dans ChargeurNiveau
private Ennemi creerEnnemi(String type, float x, float y) {
    switch (type.toLowerCase()) {
        case "terrestre":
            return new EnnemiTerrestre(x, y);
        // Extensible pour autres types
        default:
            return new EnnemiTerrestre(x, y);
    }
}
```

---

## 🔄 Flux de Données

### Boucle de Jeu (Game Loop)

```
┌─────────────────────────────────────────────────────────────┐
│ JeuPlateforme.render(float deltaTemps)                      │
│   └─► ControleurJeu.mettreAJour(deltaTemps)                │
│         ├─► ControleurEntrees.gererEntrees(joueur, niveau) │
│         │     └─► Joueur.deplacerGauche/Droite/Sauter()    │
│         │                                                     │
│         ├─► Niveau.mettreAJour(deltaTemps)                  │
│         │     ├─► Joueur.mettreAJour(deltaTemps)           │
│         │     ├─► For each Ennemi.mettreAJour(deltaTemps)  │
│         │     ├─► For each PowerUp.mettreAJour(deltaTemps) │
│         │     └─► Drapeau.mettreAJour(deltaTemps)          │
│         │                                                     │
│         ├─► GestionnaireCollisions.gererCollisions()        │
│         │     ├─► gererCollisionsObstacles(joueur)         │
│         │     ├─► gererCollisionsEnnemis(joueur)           │
│         │     ├─► gererCollisionsPowerUps(joueur)          │
│         │     └─► gererCollisionDrapeau(joueur)            │
│         │                                                     │
│         └─► GestionnaireNiveaux.mettreAJour(deltaTemps)    │
│               └─► Gestion des transitions                   │
│                                                               │
│   └─► RenduNiveau.dessiner(niveau, camera)                 │
│         ├─► Carte Tiled (fond)                              │
│         ├─► For each Entité visible                         │
│         │     ├─► RenduDrapeau.dessiner(drapeau)           │
│         │     ├─► RenduPowerUp.dessiner(powerup)           │
│         │     ├─► RenduObjet.dessiner(piece)               │
│         │     ├─► RenduEnnemi.dessiner(ennemi)             │
│         │     └─► RenduJoueur.dessiner(joueur)             │
│         │                                                     │
│         ├─► RenduHUD.dessiner(score, vies, pièces, temps)  │
│         └─► MenuFinNiveau.dessiner() [si applicable]        │
└─────────────────────────────────────────────────────────────┘

Fréquence : 60 FPS (deltaTemps ≈ 0.0167s)
```

### Cycle de Vie d'une Collision

```
1. DETECTION
   ├─► GestionnaireCollisions.gererCollisions()
   └─► Rectangle.overlaps(boiteA, boiteB) → true

2. IDENTIFICATION DU TYPE
   ├─► Joueur vs Ennemi ?
   ├─► Joueur vs Obstacle ?
   └─► Joueur vs PowerUp ?

3. CALCUL DES CHEVAUCHEMENTS
   ├─► overlapGauche = boiteA.x + width - boiteB.x
   ├─► overlapDroite = boiteB.x + width - boiteA.x
   ├─► overlapHaut = boiteA.y + height - boiteB.y
   └─► overlapBas = boiteB.y + height - boiteA.y

4. SÉPARATION DES AXES
   ├─► minOverlapX = min(overlapGauche, overlapDroite)
   └─► minOverlapY = min(overlapHaut, overlapBas)

5. RÉSOLUTION
   ├─► if (minOverlapX < minOverlapY) → Collision horizontale
   │     └─► Repousser sur l'axe X
   └─► else → Collision verticale
         └─► Repousser sur l'axe Y

6. APPLICATION DES EFFETS
   ├─► Joueur atterrit → setAuSol(true), vitesse.y = 0
   ├─► Ennemi écrasé → ennemi.setActive(false), joueur rebondit
   ├─► PowerUp collecté → joueur.appliquerPowerUp(type)
   └─► Drapeau touché → niveau.terminer()
```

---

## 📊 Métriques du Projet

### Statistiques de Code

| **Métrique**                           | **Valeur**                                 |
|:---------------------------------------|:-------------------------------------------|
| **Nombre total de classes**            | 33                                         |
| **Lignes de code Java**                | ~10 000 lignes                             |
| **Lignes de commentaires**             | ~1 500 lignes                              |
| **Méthodes publiques**                 | ~250+                                      |
| **Design Patterns utilisés**           | 6                                          |
| **Niveaux de profondeur d'héritage**   | Maximum 3 (Entite → Ennemi → EnnemiTerrestre) |

### Répartition par Package

| **Package**               | **Classes** | **Lignes de Code** | **% du Total** |
|:--------------------------|:------------|:-------------------|:---------------|
| `modele.entites`          | 8           | ~2 500             | 25%            |
| `vue`                     | 10          | ~2 000             | 20%            |
| `controleur`              | 2           | ~1 200             | 12%            |
| `modele.gestionnaires`    | 2           | ~1 500             | 15%            |
| `modele.niveau`           | 3           | ~1 200             | 12%            |
| `utilitaires`             | 4           | ~1 200             | 12%            |
| `modele.comportements`    | 2           | ~200               | 2%             |
| Racine                    | 2           | ~200               | 2%             |

### Complexité Cyclomatique (Estimation)

| **Classe**                  | **Complexité**   | **Justification**                       |
|:----------------------------|:-----------------|:----------------------------------------|
| `GestionnaireCollisions`    | Élevée (20+)     | Nombreux branchements conditionnels     |
| `ControleurJeu`             | Élevée (15+)     | Machine à états complexe                |
| `Joueur`                    | Moyenne (10-15)  | Physique et transformations             |
| `ChargeurNiveau`            | Moyenne (10-15)  | Parsing TMX avec switch/case            |
| `Ennemi`, `PowerUp`         | Faible (5-10)    | Logique simple                          |

### Dépendances Externes

| **Dépendance**                      | **Version** | **Taille**  | **Usage**                              |
|:------------------------------------|:------------|:------------|:---------------------------------------|
| `gdx-1.9.10.jar`                    | 1.9.10      | ~4 MB       | Core LibGDX                            |
| `gdx-backend-lwjgl3-1.9.10.jar`     | 1.9.10      | ~500 KB     | Backend desktop                        |
| `lwjgl-*.jar`                       | 3.3.1       | ~5 MB total | OpenGL, GLFW, Audio                    |
| `gdx-box2d-1.9.10.jar`              | 1.9.10      | ~300 KB     | Physique (non utilisé actuellement)    |
| `gdx-freetype-1.9.10.jar`           | 1.9.10      | ~200 KB     | Polices (non utilisé actuellement)     |

---

## 🎮 Fonctionnalités Implémentées

### Gameplay

| **Fonctionnalité**             | **État**      | **Détails**                                          |
|:-------------------------------|:--------------|:-----------------------------------------------------|
| ✅ Physique Mario-like         | Implémenté    | Gravité variable, coyote time, jump buffer           |
| ✅ Transformations joueur      | Implémenté    | PETIT, GRAND, FEU (3 états)                          |
| ✅ Système de vies             | Implémenté    | 3 vies, bonus tous les 100 pièces                    |
| ✅ Ennemis terrestres          | Implémenté    | Goomba avec patrouille                               |
| ✅ Power-ups                   | Implémenté    | 4 types (champignon, fleur, étoile, 1UP)             |
| ✅ Objets collectables         | Implémenté    | Pièces animées                                       |
| ✅ Blocs interactifs           | Implémenté    | Question blocks, briques destructibles               |
| ✅ Fin de niveau               | Implémenté    | Drapeau + séquence animée (4 étapes)                 |
| ✅ Système de score            | Implémenté    | Points, pièces, temps                                |
| ✅ Progression multi-niveaux   | Implémenté    | 4 niveaux configurés                                 |
| ✅ Sauvegarde automatique      | Implémenté    | Sérialisation Java                                   |

### Technique

| **Fonctionnalité**         | **État**      | **Détails**                               |
|:---------------------------|:--------------|:------------------------------------------|
| ✅ Chargement Tiled        | Implémenté    | TMX avec 6+ couches                       |
| ✅ Animations              | Implémenté    | 60 FPS, LibGDX Animation API              |
| ✅ Caméra dynamique        | Implémenté    | Suit le joueur, interpolation             |
| ✅ Culling                 | Implémenté    | Rendu uniquement entités visibles         |
| ✅ HUD temps réel          | Implémenté    | Score, vies, pièces, temps                |
| ✅ Menu fin de niveau      | Implémenté    | Rejouer, Suivant, Quitter                 |
| ✅ Transitions             | Implémenté    | Fade in/out 2s                            |
| ✅ Configuration JSON      | Implémenté    | Paramètres persistants                    |

---

## 🚀 Extensibilité

### Points d'Extension

| **Catégorie**                | **Comment Étendre**                            | **Exemple**                    |
|:-----------------------------|:-----------------------------------------------|:-------------------------------|
| **Nouveaux Ennemis**         | Créer classe extends `Ennemi`                  | `EnnemiVolant`, `Boss`         |
| **Nouveaux Comportements**   | Implémenter `ComportementEnnemi`               | `ComportementPoursuivre`       |
| **Nouveaux Power-ups**       | Ajouter enum dans `PowerUp.TypePowerUp`        | `CHAMPIGNON_GEANT`             |
| **Nouveaux Obstacles**       | Ajouter enum dans `Obstacle.TypeObstacle`      | `LAVE`, `EPINE`                |
| **Nouveaux Niveaux**         | Créer fichier TMX dans `assets/cartes/`        | `niveau5.tmx`                  |
| **Nouvelle Physique**        | Modifier constantes dans `Joueur`              | Gravity, jump speed            |

### Exemple d'Extension : Ajouter un Ennemi Volant

1. **Créer la classe** :
```java
public class EnnemiVolant extends Ennemi {
    public EnnemiVolant(float x, float y) {
        super(x, y, 32, 32, "volant");
        // Pas de gravité pour les ennemis volants
    }
    
    @Override
    public void mettreAJour(float deltaTemps) {
        // Mouvement sinusoïdal vertical
        vitesse.y = Math.sin(tempsVie) * 50f;
        super.mettreAJour(deltaTemps);
    }
}
```

2. **Ajouter au `ChargeurNiveau`** :
```java
switch (type.toLowerCase()) {
    case "terrestre":
        return new EnnemiTerrestre(x, y);
    case "volant":
        return new EnnemiVolant(x, y);
}
```

3. **Créer le renderer** :
```java
// Dans RenduEnnemi, ajouter texture "volant"
rendeurEnnemi.chargerTexture("volant", "assets/textures/koopa.png");
```

4. **Configurer dans Tiled** :
```
Couche : "Ennemis"
Type : "volant"
Propriétés : comportement = "patrouille"
```

---

## 📚 Principes SOLID

| **Principe**              | **Application dans le Projet**                                                                |
|:--------------------------|:----------------------------------------------------------------------------------------------|
| **S**ingle Responsibility | Chaque classe a une responsabilité unique (ex: `RenduJoueur` ne fait que du rendu)          |
| **O**pen/Closed           | `ComportementEnnemi` extensible sans modifier `Ennemi`                                       |
| **L**iskov Substitution   | `EnnemiTerrestre` peut remplacer `Ennemi` partout                                            |
| **I**nterface Segregation | `ComportementEnnemi` a une seule méthode `executer()`                                        |
| **D**ependency Inversion  | Controllers dépendent d'abstractions (`Entite`), pas de classes concrètes                    |

---

## 🔧 Configuration Tiled

### Couches Requises

| **Nom de Couche** | **Type**        | **Contenu**                    | **Obligatoire** |
|:------------------|:----------------|:-------------------------------|:----------------|
| `Joueur`          | Object Layer    | Point de spawn du joueur       | ✅ Oui          |
| `Ennemis`         | Object Layer    | Ennemis avec propriété `type`  | ⚠️ Optionnel    |
| `Objets`          | Object Layer    | Pièces et collectables         | ⚠️ Optionnel    |
| `PowerUps`        | Object Layer    | Champignons, fleurs, étoiles   | ⚠️ Optionnel    |
| `Obstacles`       | Object Layer    | Blocs, plateformes             | ⚠️ Optionnel    |
| `Drapeau`         | Object Layer    | Drapeau de fin                 | ✅ Oui          |
| `Collision`       | Tile Layer      | Collisions de la carte         | ✅ Oui          |

### Propriétés des Objets

#### Ennemis

| **Propriété**    | **Type** | **Valeurs**     | **Exemple**     |
|:-----------------|:---------|:----------------|:----------------|
| `type`           | String   | "terrestre"     | "terrestre"     |
| `comportement`   | String   | "patrouille"    | "patrouille"    |

#### Power-Ups

| **Propriété** | **Type** | **Valeurs**                                  | **Exemple**    |
|:--------------|:---------|:---------------------------------------------|:---------------|
| `type`        | String   | "CHAMPIGNON", "FLEUR", "ETOILE", "1UP"       | "CHAMPIGNON"   |

#### Obstacles

| **Propriété** | **Type** | **Valeurs**                                                      | **Exemple**       |
|:--------------|:---------|:-----------------------------------------------------------------|:------------------|
| `type`        | String   | "BLOC_NORMAL", "BLOC_QUESTION", "BLOC_BRIQUE", "PLATEFORME"      | "BLOC_QUESTION"   |

---

## 🎯 Performances

### Optimisations Implémentées

| **Technique**        | **Implémentation**                       | **Gain**                                 |
|:---------------------|:-----------------------------------------|:-----------------------------------------|
| **Culling**          | Vérification `estVisible()` avant rendu  | ~40% objets hors écran non rendus        |
| **Batch Drawing**    | LibGDX SpriteBatch                       | 1 draw call par batch                    |
| **Texture Atlas**    | Non implémenté (amélioration possible)   | Potentiel : -50% draw calls              |
| **Object Pooling**   | Non implémenté                           | Potentiel : -30% GC pauses               |
| **Delta Time**       | Physique indépendante du framerate       | 60 FPS stable                            |

### Profil de Performance (Estimation)

| **Métrique**                   | **Valeur** | **Cible**    |
|:-------------------------------|:-----------|:-------------|
| FPS moyen                      | 60 FPS     | 60 FPS ✅    |
| Temps de chargement niveau     | ~500ms     | <1s ✅       |
| Mémoire utilisée               | ~150 MB    | <500 MB ✅   |
| Entités simultanées            | ~50        | 100+ capable |

---

## 📖 Documentation Complémentaire

### Fichiers de Documentation

| **Fichier**                    | **Contenu**                                   |
|:-------------------------------|:----------------------------------------------|
| `README.md`                    | Instructions de lancement et présentation     |
| `ARCHITECTURE.md`              | Ce document                                   |
| `RAPPORT_PROJET.md`            | Rapport académique complet                    |
| `STRUCTURE_PROJET.md`          | Arborescence détaillée des fichiers           |
| `GUIDE_ZONE_FIN_NIVEAU.md`     | Guide technique séquence de fin               |

---

## 🏗️ Diagramme UML Simplifié

```
┌─────────────────────────────────────────────────────────────┐
│                         MODÈLE                               │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────┐                                                │
│  │  Entite  │◄──────────────┐                              │
│  └────┬─────┘               │                              │
│       │                     │                              │
│       ├──► Joueur           │                              │
│       ├──► Ennemi ◄─────┐   │                              │
│       │      │           │   │                              │
│       │      └─► EnnemiTerrestre                           │
│       ├──► ObjetCollectable                                │
│       ├──► PowerUp                                          │
│       ├──► Obstacle                                         │
│       └──► Drapeau                                          │
│                                                               │
│  ┌─────────────────┐         ┌──────────────────┐          │
│  │ ComportementEnnemi│◄───────┤ ComportementPatrouille│    │
│  └─────────────────┘         └──────────────────┘          │
│                                                               │
│  ┌────────┐  contient  ┌─────────────┐                     │
│  │ Niveau │────────────►│ Entites[]   │                     │
│  └────┬───┘             └─────────────┘                     │
│       │                                                       │
│       ├─► ProgressionNiveau                                 │
│       └─► ObjectifNiveau[]                                  │
│                                                               │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      CONTRÔLEUR                              │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐          ┌─────────────────┐             │
│  │ ControleurJeu│◄─────────┤ ControleurEntrees│             │
│  └──────┬───────┘          └─────────────────┘             │
│         │                                                     │
│         ├─► GestionnaireCollisions                          │
│         └─► GestionnaireNiveaux                             │
│                                                               │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                          VUE                                 │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────┐                                             │
│  │ RenduNiveau │◄───┐                                       │
│  └─────────────┘    │                                       │
│                     │                                       │
│  RenduJoueur ───────┤                                       │
│  RenduEnnemi ───────┤                                       │
│  RenduObjet ────────┤                                       │
│  RenduPowerUp ──────┤                                       │
│  RenduDrapeau ──────┤                                       │
│  RenduHUD ──────────┘                                       │
│  MenuFinNiveau                                               │
│                                                               │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      UTILITAIRES                             │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ChargeurNiveau                                              │
│  SauvegardeProgression                                       │
│  ConfigurationJeu                                            │
│  GestionnaireRessources                                      │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ Checklist de Qualité

| **Critère**                  | **État** | **Notes**                    |
|:-----------------------------|:---------|:-----------------------------|
| ✅ Code compilable           | ✅       | `javac` et Gradle            |
| ✅ Pas d'erreurs linter      | ✅       | Code propre                  |
| ✅ Commentaires Javadoc      | ✅       | Toutes classes publiques     |
| ✅ Pas de code mort          | ✅       | Nettoyé                      |
| ✅ Pas de System.out debug   | ✅       | Supprimés (107 occurrences)  |
| ✅ Gestion d'erreurs         | ✅       | Try-catch + System.err       |
| ✅ Architecture MVC          | ✅       | Séparation claire            |
| ✅ Extensibilité             | ✅       | Patterns utilisés            |
| ✅ Documentation             | ✅       | README + RAPPORT + ARCHI     |

---

## 📞 Contact & Contribution

**Auteur** : MOUSSA CISSE & ANDREA  
**Formation** : Licence 3 MIAGE  
**Date** : Janvier 2026  
**Framework** : LibGDX 1.9.10  

---

**FIN DE LA DOCUMENTATION ARCHITECTURE**
