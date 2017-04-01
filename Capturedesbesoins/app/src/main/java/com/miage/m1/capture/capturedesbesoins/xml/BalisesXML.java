package com.miage.m1.capture.capturedesbesoins.xml;

/**
 * Created by Karl on 01/04/2017.
 */

public enum BalisesXML {

    PROJET("projet"),
    NOM("nom"),
    DESCRIPTION("description"),
    FICHIERS("fichiers"),
    FICHIER("fichier"),
    TYPE("type"),
    TAGS("tags");

    // Nom du xml
    public String xml;


    BalisesXML(String nom) {
        xml = nom;
    }
}
