# 🏰 Guide : Configuration de la Zone de Fin de Niveau (Drapeau + Château)

## ✅ Système Implémenté

Le système détecte et valide automatiquement la zone de fin de niveau avec :
1. **Drapeau** : Ligne d'arrivée (déclenche la victoire)
2. **Château** : Destination finale (où Mario disparaît)

---

## 📋 Ce Qui a Été Implémenté

### 1. **Classe `Niveau.java`**
- ✅ `positionDrapeauX` : Position X du drapeau (ligne d'arrivée)
- ✅ `positionChateauPorteX` : Position X de la porte du château (centre)
- ✅ Getters et setters pour ces positions

### 2. **Classe `ChargeurNiveau.java`**
- ✅ `chargerCoucheDrapeau()` : Détecte et stocke la position du drapeau
- ✅ `chargerCoucheChateau()` : Détecte et stocke la position du château
- ✅ `validerZoneFinNiveau()` : Valide l'ordre Drapeau → Château

### 3. **Validation Automatique**
- ✅ Vérifie que le château est APRÈS le drapeau
- ✅ Affiche une erreur si l'ordre est incorrect
- ✅ Avertit si le château est trop proche (< 64px)

### 4. **Caméra**
- ✅ La caméra peut scroller jusqu'à la fin du niveau (incluant le château)

---

## 🎮 Comment Configurer dans Tiled

### Étape 1 : Le Drapeau (Déjà Configuré)

**Vous avez déjà un drapeau dans votre niveau**, donc cette partie est OK ! ✅

La couche `Drapeau` existe déjà avec :
- Position X : environ 3050-3133
- Hauteur : 160 pixels

### Étape 2 : Ajouter le Château

1. **Ouvrir Tiled** :
   ```bash
   open -a Tiled assets/cartes/niveau1.tmx
   ```

2. **Créer la couche "Chateau"** :
   - Clic droit sur la liste des calques
   - Nouveau → Calque d'objets
   - Nom : `Chateau` (ou `Castle`)

3. **Placer le rectangle du château** :
   - Sélectionner la couche `Chateau`
   - Outil Rectangle (R)
   - Dessiner un rectangle à la position voulue
   
   **Dimensions suggérées** :
   - Largeur : 128 pixels (taille typique d'un château)
   - Hauteur : 128 pixels (ou plus)
   
   **Position suggérée** (pour niveau1) :
   - X = 3200 (après le drapeau à X=3133)
   - Y = 320 (au niveau du sol)

4. **Sauvegarder** (Cmd+S)

5. **Recompiler et Tester** :
   ```bash
   ./scripts/compiler_javac.sh
   ./lancer.sh
   ```

---

## 📐 Ordre Logique de la Zone de Fin

```
┌──────────────────────────────────────────────────────────┐
│                                                          │
│  Obstacles → Plateforme → Drapeau → Espace → Château    │
│  finale                    ↓          de      ↓         │
│                         X=3133     marche   X=3200      │
│                      (ligne d'arrivée)    (destination) │
│                                                          │
│  Distance recommandée : 64-200 pixels                   │
└──────────────────────────────────────────────────────────┘
```

### Positions Recommandées (Niveau 1)

| Élément | Position X | Rôle |
|---------|-----------|------|
| Dernière plateforme | 3040-3200 | Sol avant le drapeau |
| **Drapeau** | 3133 | **Ligne d'arrivée** (déclenche victoire) |
| Espace de marche | 3133-3200 | Zone pour l'animation de victoire |
| **Château** | 3200 | **Destination finale** (Mario y entre) |

---

## 🧪 Vérification au Lancement

Quand vous lancez le jeu, cherchez ces lignes dans la console :

```
✅ Bonne configuration :

ChargeurNiveau: → Drapeau de fin chargé à (3133.0, 289.0)
ChargeurNiveau: 📍 Position Drapeau X = 3133.0 (ligne d'arrivée)
ChargeurNiveau: → Château de fin chargé à (3200.0, 320.0)
ChargeurNiveau: 🏰 Position Porte du Château X = 3264.0 (destination finale)
ChargeurNiveau: ✅ Zone de fin validée : Distance Drapeau→Château = 131.0 pixels
```

```
❌ Configuration incorrecte :

ChargeurNiveau: ❌ ERREUR LEVEL DESIGN : Le château (X=3000.0) 
                est placé AVANT le drapeau (X=3133.0) !
                L'ordre logique est : Obstacles → Drapeau → Château.

╔════════════════════════════════════════════════════════╗
║  ⚠️  ERREUR DE LEVEL DESIGN DÉTECTÉE !              ║
╚════════════════════════════════════════════════════════╝

💡 Solution : Déplacez le château APRÈS le drapeau dans Tiled.
```

---

## 📊 Structure XML dans Tiled

Voici à quoi devrait ressembler votre fichier `.tmx` :

```xml
<!-- Drapeau (déjà présent) -->
<objectgroup id="6" name="Drapeau">
  <object id="1000" x="3133" y="289" width="32" height="160">
    <properties>
      <property name="hauteur" type="float" value="160"/>
    </properties>
  </object>
</objectgroup>

<!-- Château (à ajouter) -->
<objectgroup id="8" name="Chateau">
  <object id="2000" x="3200" y="320" width="128" height="128"/>
</objectgroup>
```

---

## 🎯 Cas d'Utilisation

### 1. **Niveau Simple (sans château)**
- ✅ Drapeau uniquement
- Mario touche le drapeau → Victoire
- Pas d'erreur si le château est absent

### 2. **Niveau avec Animation de Fin (avec château)**
- ✅ Drapeau + Château
- Mario touche le drapeau → Victoire déclenchée
- Mario continue jusqu'au château → Animation d'entrée
- Validation automatique de l'ordre

### 3. **Niveau avec Espace de Marche**
- ✅ Distance Drapeau→Château : 64-200 pixels
- Permet à Mario de marcher après avoir touché le drapeau
- Animation de victoire fluide

---

## ⚠️ Erreurs Courantes

### Erreur 1 : Château Avant le Drapeau
```
❌ Château X=3000, Drapeau X=3133
→ ERREUR : Ordre incorrect !

✅ Drapeau X=3133, Château X=3200
→ Ordre correct
```

### Erreur 2 : Château Trop Proche
```
⚠️ Distance < 64 pixels
→ Mario n'aura pas d'espace pour marcher

✅ Distance ≥ 64 pixels
→ Espace suffisant pour l'animation
```

### Erreur 3 : Mauvais Nom de Couche
```
❌ Couche "Castle building" ou "chateau_fin"
→ Non détecté

✅ Couche "Chateau" ou "Castle"
→ Détecté automatiquement
```

---

## 💡 Conseils de Level Design

### Distance Drapeau → Château

| Distance | Effet | Recommandation |
|----------|-------|----------------|
| < 64px | Trop proche, pas d'espace | ❌ Éviter |
| 64-128px | Espace minimal | ⚠️ Acceptable |
| 128-200px | Espace confortable | ✅ **Recommandé** |
| > 200px | Trop loin, anti-climax | ⚠️ Vérifier |

### Placement du Sol

Assurez-vous qu'il y a une plateforme/sol entre le drapeau et le château :
```
Sol: X=3040, largeur=160px (couvre jusqu'à X=3200)
     ↓
Drapeau: X=3133 (sur le sol)
         ↓
Château: X=3200 (sur le sol)
```

---

## 🔧 API pour les Développeurs

### Méthodes Disponibles dans `Niveau.java`

```java
// Obtenir la position du drapeau
float drapeauX = niveau.getPositionDrapeauX();

// Obtenir la position du château
float chateauX = niveau.getPositionChateauPorteX();

// Vérifier si les deux sont définis
if (drapeauX > 0 && chateauX > 0) {
    float distance = chateauX - drapeauX;
    System.out.println("Distance: " + distance + " pixels");
}
```

### Utilisation Future (Animation de Victoire)

```java
// Quand Mario touche le drapeau
if (joueurToucheDrapeau()) {
    // 1. Déclencher la victoire
    niveau.terminerNiveau(true);
    
    // 2. Faire marcher Mario vers le château
    float destination = niveau.getPositionChateauPorteX();
    animerMarioVersDestination(destination);
    
    // 3. Animation d'entrée dans le château
    jouerAnimationEntreeChâteau();
}
```

---

## 🎉 Résumé

Le système de zone de fin de niveau est **100% fonctionnel** !

✅ Détection automatique du Drapeau et du Château  
✅ Validation de l'ordre (Drapeau → Château)  
✅ Avertissements en cas d'erreur de design  
✅ Caméra ajustée pour voir jusqu'au château  
✅ Positions stockées et accessibles  

**Ajoutez simplement une couche "Chateau" dans Tiled et le système fait le reste !** 🏰✨
