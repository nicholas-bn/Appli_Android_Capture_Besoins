package com.miage.m1.capture.capturedesbesoins.xml;

import java.util.ArrayList;

/**
 * Created by Karl on 01/04/2017.
 */

public class Projet {

    // Nom du projet
    private String nom;

    // Description du projet
    private String description;

    // Liste des fichiers associés à ce projet
    private ArrayList<Fichier> listeFichiers;


    public Projet(String nomProjet) {
        nom = nomProjet;

        listeFichiers = new ArrayList();
    }

    // --- Accesseurs de consultation :

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description + "";
    }

    public ArrayList<Fichier> getListeFichiers() {
        return listeFichiers;
    }


    // --- Accesseurs de modification

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addFichier(Fichier fichier) {
        listeFichiers.add(fichier);
    }

    public void compareAndAddFichiers(ArrayList<Fichier> fichiers) {
        // Pour chaque Fichier à comparer :
        for (Fichier fichier : fichiers) {
            // Si le fichier n'est pas dans notre liste
            if (containFichier(fichier) == false) {
                // On l'ajoute dans la liste
                listeFichiers.add(fichier);
            }
        }
    }

    public boolean containFichier(Fichier fichierARechercher){
        // Pour chaque Fichier à comparer :
        for (Fichier fichier : listeFichiers) {
            // Si le fichier n'est pas dans notre liste
            if (fichier.getNom().equals(fichierARechercher.getNom())) {
                return true;
            }
        }
        return false;
    }

}