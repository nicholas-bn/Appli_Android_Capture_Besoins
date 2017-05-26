package com.miage.m1.capture.capturedesbesoins.xml;

import android.graphics.Color;
import android.util.Log;

import com.miage.m1.capture.capturedesbesoins.GalerieAdapter;
import com.miage.m1.capture.capturedesbesoins.Ligne_Galerie;

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

    public ArrayList<Ligne_Galerie> getListeFichiersFormatString() {

        ArrayList<Ligne_Galerie> retourListe = new ArrayList<Ligne_Galerie>();

        for (Fichier f : listeFichiers) {
            Log.i("Projet : ", f.getNom());
            retourListe.add(new Ligne_Galerie(Color.GREEN, f.getNom(), f.getListeTagsString()));
        }

        return retourListe;
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
            if (containFichier(fichier) == null) {
                // On l'ajoute dans la liste
                listeFichiers.add(fichier);
            }else{
                containFichier(fichier).setChemin(fichier.getChemin());
            }
        }

        // On efface les fichiers qui ne sont plus présents :
        ArrayList<Fichier> nouvelleListe = new ArrayList<>();

        for (Fichier fichier : listeFichiers) {
            if (containFichier(fichiers, fichier) == true) {
                nouvelleListe.add(fichier);
            }
        }

        listeFichiers = nouvelleListe;
        // TODO Effacer les fichiers supprimés
    }

    public Fichier containFichier(Fichier fichierARechercher) {
        // Pour chaque Fichier à comparer :
        for (Fichier fichier : listeFichiers) {
            // Si le fichier n'est pas dans notre liste
            if (fichier.getNom().equals(fichierARechercher.getNom())) {
                return fichier;
            }
        }
        return null;
    }

    public boolean containFichier(ArrayList<Fichier> liste, Fichier fichierARechercher) {
        // Pour chaque Fichier à comparer :
        for (Fichier fichier : liste) {
            // Si le fichier n'est pas dans notre liste
            if (fichier.getNom().equals(fichierARechercher.getNom())) {
                return true;
            }
        }
        return false;
    }

}
