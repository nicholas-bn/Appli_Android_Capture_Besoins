package com.miage.m1.capture.capturedesbesoins.xml;

import java.util.ArrayList;

/**
 * Created by Karl on 01/04/2017.
 */

public class Fichier {

    // Nom du fichier
    public String nom;

    // Type du fichier (Texte, son, photo)
    public String type;

    // Liste des Tags
    public ArrayList<String> listeTags;


    public Fichier() {
        listeTags = new ArrayList<>();
    }
    // --- Accesseurs de consultation :

    public String getNom() {
        return nom;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getListeTags() {
        return listeTags;
    }

    public String getListeTagsString() {
        String tags = "";

        // Pour chaque Tags :
        for (String tag : listeTags) {
            tags += tag + "/";
        }

        return tags;
    }


    // --- Accesseurs de modification

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addTag(String tag) {
        listeTags.add(tag);
    }

    public void setTags(String tags) {
        // On parse la chaine
        String[] tabTags = tags.split("/");

        // On les ajoute dans la liste des Tags
        for (String tag : tabTags) {
            listeTags.add(tag);
        }
    }

}
