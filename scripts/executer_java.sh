#!/bin/bash

# Script d'exécution avec java (sans Gradle)

echo "=========================================="
echo "  Exécution avec java"
echo "=========================================="
echo ""

# Vérifier si le projet est compilé
if [ ! -d "bin" ] || [ -z "$(ls -A bin)" ]; then
    echo "❌ Le projet n'est pas compilé"
    echo ""
    echo "Veuillez d'abord compiler avec : ./compiler_javac.sh"
    exit 1
fi

# Vérifier si les dépendances sont présentes
if [ ! -d "lib" ] || [ -z "$(ls -A lib)" ]; then
    echo "❌ Les dépendances ne sont pas présentes"
    echo ""
    echo "Veuillez d'abord exécuter : ./telecharger_dependances.sh"
    exit 1
fi

# Construire le classpath
CLASSPATH="bin"
for jar in lib/*.jar; do
    CLASSPATH="${CLASSPATH}:${jar}"
done

# Créer les répertoires nécessaires s'ils n'existent pas
mkdir -p assets/textures
mkdir -p niveaux
mkdir -p config

# Créer des textures de test si elles n'existent pas
if [ ! -f "assets/textures/joueur.png" ]; then
    echo "⚠️  Les textures n'existent pas. Création de textures de test..."
    
    # Vérifier si ImageMagick est installé
    if command -v convert &> /dev/null; then
        convert -size 32x48 xc:blue assets/textures/joueur.png
        convert -size 32x32 xc:red assets/textures/ennemi_terrestre.png
        convert -size 32x32 xc:green assets/textures/ennemi_volant.png
        convert -size 24x24 xc:yellow assets/textures/piece.png
        convert -size 24x24 xc:orange assets/textures/champignon.png
        echo "✅ Textures de test créées"
    else
        echo "⚠️  ImageMagick n'est pas installé. Les textures ne seront pas créées."
        echo "    Le jeu risque de planter au démarrage."
        echo ""
        echo "    Pour installer ImageMagick sur Mac : brew install imagemagick"
        echo "    Pour installer ImageMagick sur Linux : sudo apt-get install imagemagick"
        echo ""
    fi
fi

echo ""
echo "Démarrage du jeu..."
echo ""

# Exécuter le jeu
java -cp "${CLASSPATH}" \
     -XstartOnFirstThread \
     com.mypackage.projet.jeux.LanceurDesktop

# Note: -XstartOnFirstThread est nécessaire sur macOS pour LWJGL

# Vérifier le code de sortie
if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "  Jeu terminé normalement"
    echo "=========================================="
else
    echo ""
    echo "=========================================="
    echo "  ❌ Erreur lors de l'exécution"
    echo "=========================================="
    echo ""
    echo "Si vous avez l'erreur 'XstartOnFirstThread':"
    echo "  - Sur macOS, c'est normal, LWJGL nécessite cette option"
    echo "  - Sur Linux/Windows, retirez l'option -XstartOnFirstThread"
    exit 1
fi


