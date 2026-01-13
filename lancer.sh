#!/bin/bash


# Couleurs pour l'affichage
VERT='\033[0;32m'
ROUGE='\033[0;31m'
JAUNE='\033[1;33m'
BLEU='\033[0;34m'
NC='\033[0m' # No Color

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║        🎮  JEU DE PLATEFORME 2D - MOTEUR LibGDX          ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Vérifier si le jeu est déjà compilé
if [ ! -d "bin/com" ]; then
    echo -e "${JAUNE}⚠️  Le jeu n'est pas encore compilé${NC}"
    echo -e "${BLEU}📦 Compilation en cours...${NC}"
    echo ""
    
    # Compiler avec javac
    ./scripts/compiler_javac.sh
    
    if [ $? -ne 0 ]; then
        echo ""
        echo -e "${ROUGE} Erreur lors de la compilation${NC}"
        echo "Veuillez vérifier les erreurs ci-dessus"
        exit 1
    fi
    
    echo ""
fi

# Lancer le jeu
echo -e "${VERT}🚀 Lancement du jeu...${NC}"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "   Contrôles du jeu :"
echo "     ← → : Déplacement gauche/droite"
echo "     ESPACE : Sauter"
echo "     ÉCHAP : Pause"
echo "     ENTRÉE : Passer au niveau suivant (après victoire)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Construction du classpath
CLASSPATH="bin"
for jar in lib/*.jar; do
    CLASSPATH="$CLASSPATH:$jar"
done

# Lancer le jeu avec Java
# Sur macOS, l'option -XstartOnFirstThread est OBLIGATOIRE pour LibGDX/LWJGL
java -XstartOnFirstThread -cp "$CLASSPATH" com.mypackage.projet.jeux.LanceurDesktop

# Vérifier le code de sortie
if [ $? -ne 0 ]; then
    echo ""
    echo -e "${ROUGE}❌ Le jeu s'est terminé avec une erreur${NC}"
    echo ""
    echo "💡 Conseils de dépannage :"
    echo "   1. Vérifiez que Java est installé : java -version"
    echo "   2. Vérifiez les logs d'erreur ci-dessus"
    echo "   3. Essayez de recompiler : ./scripts/compiler_javac.sh"
    echo ""
    exit 1
fi

echo ""
echo -e "${VERT}✅ Merci d'avoir joué !${NC}"
echo ""

