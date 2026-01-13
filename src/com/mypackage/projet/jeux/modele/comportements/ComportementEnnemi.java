package com.mypackage.projet.jeux.modele.comportements;

import com.mypackage.projet.jeux.modele.entites.Ennemi;

/**
 * Interface définissant le comportement d'un ennemi (Design Pattern Strategy)
 */
public interface ComportementEnnemi {
    
    /**
     * Exécute le comportement de l'ennemi
     * @param ennemi L'ennemi concerné
     * @param deltaTemps Temps écoulé depuis la dernière frame
     */
    void executer(Ennemi ennemi, float deltaTemps);
}
