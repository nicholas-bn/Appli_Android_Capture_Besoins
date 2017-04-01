package com.miage.m1.capture.capturedesbesoins.Services;

/**
 * Created by Karl on 01/04/2017.
 */

public enum BalisesXML {

    PROJET("projet"),
    NOM("nom"),
    DESCRIPTION("description");

    String tag;


    BalisesXML(String nom) {
        tag = nom;
    }
}
