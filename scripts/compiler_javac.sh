#!/bin/bash

# Script de compilation avec javac (sans Gradle)

echo "=========================================="
echo "  Compilation avec javac"
echo "=========================================="
echo ""

# Vérifier si les dépendances sont présentes
if [ ! -d "lib" ] || [ -z "$(ls -A lib)" ]; then
    echo "❌ Les dépendances ne sont pas présentes dans le répertoire 'lib/'"
    echo ""
    echo "Voulez-vous les télécharger maintenant ? (o/n)"
    read -r reponse
    if [ "$reponse" = "o" ] || [ "$reponse" = "O" ]; then
        ./telecharger_dependances.sh
    else
        echo "Veuillez d'abord exécuter : ./telecharger_dependances.sh"
        exit 1
    fi
fi

# Créer le répertoire de sortie
echo "Création du répertoire de sortie..."
mkdir -p bin

# Construire le classpath avec tous les JARs
echo "Construction du classpath..."
CLASSPATH="bin"
for jar in lib/*.jar; do
    CLASSPATH="${CLASSPATH}:${jar}"
done

echo "Classpath configuré avec $(ls lib/*.jar | wc -l) bibliothèques"
echo ""

# Trouver tous les fichiers Java
echo "Recherche des fichiers source Java..."
SOURCES=$(find src/com -name "*.java")
NB_FILES=$(echo "$SOURCES" | wc -l)
echo "Trouvé ${NB_FILES} fichiers source"
echo ""

# Compiler
echo "Compilation en cours..."
javac -encoding UTF-8 -cp "${CLASSPATH}" -d bin $SOURCES

# Vérifier si la compilation a réussi
if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "  ✅ Compilation réussie !"
    echo "=========================================="
    echo ""
    echo "Les fichiers .class sont dans le répertoire 'bin/'"
    echo ""
    echo "Pour exécuter le jeu, utilisez :"
    echo "  ./executer_java.sh"
    echo ""
else
    echo ""
    echo "=========================================="
    echo "  ❌ Erreur lors de la compilation"
    echo "=========================================="
    exit 1
fi


