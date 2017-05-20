package com.miage.m1.capture.capturedesbesoins.services;

import android.app.Activity;

import com.miage.m1.capture.capturedesbesoins.xml.Fichier;
import com.miage.m1.capture.capturedesbesoins.xml.Projet;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karl on 20/03/2017.
 */

public class GestionnaireFichiers {

    // Nom du dossier de sauvegarde
    public static String nomDossierMain = "userTest";

    public static boolean creerDossier(Activity activity, String nomDossier) {
        // Racine de l'application
        File racineApp = activity.getExternalFilesDir(null);

        // Chemin du Dossier à créer
        String cheminDossier = racineApp.getAbsolutePath() + File.separator + nomDossierMain;

        // Dossier à créer
        File dossier = new File(cheminDossier + File.separator + nomDossier);

        // Si le dossier n'existe pas
        if (!dossier.exists()) {
            // On crée le dossier
            dossier.mkdirs();
            return true;
        } else {
            return false;
        }
    }

    public static List<String> getNomProjetsExistants(Activity activity) {
        // Liste des Projets à remplir..
        ArrayList<String> listProjets = new ArrayList<String>();

        // Racine de l'application
        File racineApp = activity.getExternalFilesDir(null);

        // Dossier contenant les Projets
        File dossierApp = new File(racineApp.getAbsolutePath() + File.separator + nomDossierMain);

        // Liste des fichiers du répertoire
        File[] files = dossierApp.listFiles();

        // Cas où aucun dossier n'est présent
        if (files == null) {
            return null;
        }

        // Parcours des fichiers :
        for (File file : files) {
            // Si c'est un répertoire (=ProjetActivity)
            if (file.isDirectory()) {
                // On l'ajoute à la liste des projets existants
                listProjets.add(file.getName());
            }
        }
        // On retourne la liste des Projets existants
        return listProjets;
    }


    public static String getCheminProjet(Activity activity, String nomProjet) {
        // Racine de l'application
        File racineApp = activity.getExternalFilesDir(null);

        // Dossier contenant le Projet
        File dossierProjet = new File(racineApp.getAbsolutePath() + File.separator + nomDossierMain + File.separator + nomProjet);

        return dossierProjet.getAbsolutePath();
    }


    public static String getCheminFichierXML(Activity activity, String nomProjet) {
        // Racine de l'application
        File racineApp = activity.getExternalFilesDir(null);

        // Dossier contenant le Projet
        File dossierProjet = new File(getCheminProjet(activity, nomProjet) + File.separator + nomProjet + ".xml");

        return dossierProjet.getAbsolutePath();
    }

    public static void createXMLFile(Activity activity, String nomProjet) {
        // Chemin du Dossier à créer
        String cheminXML = getCheminFichierXML(activity, nomProjet);

        // fichier à créer
        File fichier = new File(cheminXML);

        // On écrit le fichier
        try {
            PrintWriter ecritureDuFichierTexte = new PrintWriter(fichier);
            ecritureDuFichierTexte.println(GestionnaireXML.createXML(nomProjet));
            ecritureDuFichierTexte.close();
        } catch (java.io.IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : \"" + fichier + "\"");
            e.printStackTrace();
        }
    }

    public static void deleteFile(Activity activity, String nomProjet, String typeFichier, String nomFichier) {
        try {
            // Chemin du Dossier à créer
            String cheminProjet = getCheminProjet(activity, nomProjet);

            File file = new File(cheminProjet+File.separator+typeFichier+File.separator+nomFichier);

            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
            } else {
                System.out.println("Delete operation is failed.");
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public static ArrayList<Fichier> getListeFichiersDuProjet(Activity activity, String nomProjet) {
        // Liste des Fichiers
        ArrayList<Fichier> listeFichiers = new ArrayList<>();

        // Chemin du Dossier à créer
        String cheminDossier = getCheminProjet(activity, nomProjet);

        // Dossier du Projet
        File dossier = new File(cheminDossier);

        // On récupère les types de documents (TEXT, IMAGE, SON)
        File[] types = dossier.listFiles();

        // Pour chacun de ces types :
        for (File type : types) {
            // Si c'est un dossier
            if (type.isDirectory()) {
                // Tableau des documents
                File[] documents = type.listFiles();

                // Pour chacun de ces documents :
                for (File document : documents) {
                    // On crée un objet Fichier
                    Fichier fichier = new Fichier();

                    // On ajoute son nom
                    fichier.setNom(document.getName());

                    // On ajoute son type
                    fichier.setType(type.getName());

                    // On l'ajoute à liste des Fichiers
                    listeFichiers.add(fichier);
                }
            }
        }

        return listeFichiers;
    }

    public static void majXML(Activity activity, Projet projet) {
        // Chemin du Dossier à créer
        String cheminXML = getCheminFichierXML(activity, projet.getNom());

        // fichier à créer
        File fichier = new File(cheminXML);

        // On écrit le fichier
        try {
            PrintWriter ecritureDuFichierTexte = new PrintWriter(fichier);
            ecritureDuFichierTexte.println(GestionnaireXML.majXML(projet));
            ecritureDuFichierTexte.close();
        } catch (java.io.IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : \"" + fichier + "\"");
            e.printStackTrace();
        }
    }
}
