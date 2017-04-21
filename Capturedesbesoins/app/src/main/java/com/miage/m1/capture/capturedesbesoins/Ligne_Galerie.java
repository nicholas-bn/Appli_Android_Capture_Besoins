package com.miage.m1.capture.capturedesbesoins;

/**
 * Created by Nicho on 21/04/2017.
 */

public class Ligne_Galerie {

    private int color;
    private String pseudo;
    private String text;

    public Ligne_Galerie(int color, String pseudo, String text) {
        this.color = color;
        this.pseudo = pseudo;
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
