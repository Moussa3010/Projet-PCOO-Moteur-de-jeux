# Moteur de Jeu de Plateforme 2D - Style Super Mario Bros

**Projet académique - Licence 3 MIAGE**  
Moteur de jeu 2D extensible développé en Java avec LibGDX, permettant la création de niveaux via Tiled sans modifier le code.

---

## 🎮 Fonctionnalités

**Gameplay**
- Joueur avec 3 transformations (PETIT → GRAND → FEU)
- Ennemis terrestres avec IA (Goomba)
- 4 types de power-ups (Champignon, Fleur, Étoile, 1UP)
- Système de collisions complet
- Séquence de fin de niveau animée (4 étapes)
- HUD temps réel (score, vies, pièces)

**Technique**
- Architecture MVC stricte
- Design patterns (Strategy, State Machine, Observer, Factory)
- Chargement dynamique des niveaux depuis Tiled
- Configuration JSON
- 33 classes, 5000+ lignes de code

---

## 🚀 Installation et Lancement

### Prérequis
- Java JDK 17+
- Dépendances LibGDX (incluses dans `lib/`)

### Exécution Rapide

**Méthode automatique (recommandée)** :
```bash
chmod +x lancer.sh
./lancer.sh
```

**Méthode manuelle** :
```bash
# Compilation
./scripts/compiler_javac.sh

# Exécution
./scripts/executer_java.sh
```

Le jeu se lance dans une fenêtre 800×600 pixels.

---

## 🎮 Contrôles

- **← / →** : Déplacer Mario
- **ESPACE** : Sauter (maintenir pour sauter plus haut)
- **ÉCHAP** : Pause

---

## 📁 Structure du Projet

```
projet/
├── src/com/mypackage/projet/jeux/
│   ├── modele/              # Logique métier (entités, IA, collisions)
│   ├── vue/                 # Rendu graphique (HUD, animations)
│   ├── controleur/          # Gestion états et entrées
│   ├── utilitaires/         # Chargement Tiled, configuration
│   ├── JeuPlateforme.java   # Classe principale
│   └── LanceurDesktop.java  # Point d'entrée
│
├── assets/
│   ├── cartes/              # Niveaux Tiled (.tmx)
│   └── textures/            # Sprites PNG
│
├── config/
│   └── configuration.json   # Configuration du jeu
│
├── lib/                     # Dépendances LibGDX
└── scripts/                 # Scripts de compilation
```

---

## 🗺️ Créer un Niveau avec Tiled

### Couches Obligatoires

**Joueur** : Point de spawn (Object Layer)  
**Ennemis** : Objets avec propriété `type` = "terrestre"  
**PowerUps** : Objets avec propriété `type` = "CHAMPIGNON"/"FLEUR"/"ETOILE"/"1UP"  
**Obstacles** : Blocs avec propriété `type` = "BLOC_NORMAL"/"PLATEFORME"  
**Drapeau** : Drapeau de fin  
**Collision** : Zones solides (Tile Layer)

### Ajouter un Niveau

1. Créer le fichier `.tmx` dans `assets/cartes/`
2. Ajouter dans `config/configuration.json` :
```json
{
  "niveaux": [
    "assets/cartes/niveau1.tmx",
    "assets/cartes/niveau2.tmx"
  ]
}
```

Aucune modification du code Java nécessaire.

---

## 🏗️ Architecture

**Pattern MVC**
- **Modèle** : Entités, IA, collisions, progression
- **Vue** : Rendu multi-couches, HUD, animations
- **Contrôleur** : Machine à états, gestion des entrées

**Design Patterns**
- Strategy (comportements ennemis)
- State Machine (états jeu, séquence fin)
- Observer (événements de jeu)
- Factory (création entités depuis Tiled)

**Principes SOLID**
- Single Responsibility, Open/Closed, Liskov Substitution
- Interface Segregation, Dependency Inversion

---

## 🔧 Extensibilité

**Ajouter un ennemi** : Hériter de `Ennemi`, ajouter dans `ChargeurNiveau`  
**Ajouter un power-up** : Étendre `TypePowerUp`, implémenter l'effet  
**Ajouter un niveau** : Créer TMX, déclarer dans configuration JSON

---

## 💻 Technologies

- **Java 17** : Langage principal
- **LibGDX 1.9.10** : Framework de jeu 2D/3D
- **LWJGL 3.3.1** : Backend OpenGL
- **Tiled Map Editor** : Éditeur de niveaux
- **IntelliJ IDEA** : IDE
- **Git** : Gestion de versions

---

## 👥 Équipe

**Andrea Kocovic** - Développeur Vue & Contrôleur  
Rendu graphique, interface utilisateur, états du jeu, intégration Tiled

**Moussa CISSE** - Développeur Modèle & Logique  
Architecture entités, système de collisions, IA ennemis, logique métier

---

## 📊 Métriques

- 33 classes Java
- ~5000 lignes de code
- 6 design patterns
- 7 packages
- 32 textures PNG
- 1 niveau (extensible)

---

## 📚 Ressources

- **LibGDX** : [libgdx.com](https://libgdx.com/)
- **Tiled** : [mapeditor.org](https://www.mapeditor.org/)
- **Documentation** : Voir `ARCHITECTURE.md` et `RAPPORT_PROJET.md`

---

## 📄 Licence

Projet académique - Licence 3 MIAGE - Janvier 2026

---

**Développé avec Java & LibGDX** 🎮
