package com.miage.m1.capture.capturedesbesoins.Services;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
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

    /* Checks if external storage is available for read and write TODO */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read TODO */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            // TODO Log.e(LOG_TAG, "Directory not created");
        }
        return file;
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
        // Racine de l'application
        File racineApp = activity.getExternalFilesDir(null);

        // Chemin du Dossier à créer
        String cheminDossier = getCheminProjet(activity, nomProjet);

        // fichier à créer
        File fichier = new File(cheminDossier + File.separator + nomProjet + ".xml");

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
}
