package com.miage.m1.capture.capturedesbesoins.services;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

import java.io.File;

/**
 * Created by Nicho on 09/04/2017.
 */

public class DownloadDrive implements Runnable {

    private String TAG = "GOOGLE DOWNLOAD DRIVE";

    // Lien vers l'API Google Drive
    private static GoogleApiClient mGoogleApiClient;

    public Activity activity;

    private Menu menu;

    public DownloadDrive(Activity activity, Menu menu, GoogleApiClient mGoogleApiClient) {
        this.activity = activity;
        this.menu = menu;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    @Override
    public void run() {

        // Ajout d'une info du début du download pour l'user
        Snackbar.make(activity.getCurrentFocus(), "Début du download depuis le Drive ...", Snackbar.LENGTH_LONG).show();

        // On vérifie que le drive n'est pas vide
        Metadata root = getRootFolder();

        // Si null c'est qu'il y a eu un probleme rencontré
        if (root == null) {

            // Ajout d'une info pour l'user qu'il y a eu un problème lors du DL (Drive vide ou erreur de requéte)
            Snackbar.make(activity.getCurrentFocus(), "Erreur lors du download : Vous n'avez aucun fichier de l'application sur le drive, ou une requête à Google s'est mal passé.", Snackbar.LENGTH_LONG).show();

        } else { // Autrement on continue

            // Construction de l'arbo en local
            constructTreeLocal(activity.getExternalFilesDir(null).getAbsolutePath(), root);

            // Ajout d'une info de fin du download pour l'user
            Snackbar.make(activity.getCurrentFocus(), "Fin du download", Snackbar.LENGTH_LONG).show();
        }
    }

    public void constructTreeLocal(String pathParent, Metadata metadata) {

        // On récupère le type (dossier OU fichier)
        int ressourceType = metadata.getDriveId().getResourceType();

        // Si c'est un fichier
        if (ressourceType == DriveId.RESOURCE_TYPE_FILE) {

            Log.i(TAG, "FILE Path : " + pathParent + File.separator + metadata.getTitle());
            // TODO : créé le fichier en local

        } else if (ressourceType == DriveId.RESOURCE_TYPE_FOLDER) { // si c'est un dossier

            // TODO : créé le dossier en local
            // On construit le nouveau chemin local
            String newPath = pathParent + File.separator + metadata.getTitle();
            Log.i(TAG, "FOLDER Path : " + newPath);

            // On liste les enfants du nouveau driveID
            DriveApi.MetadataBufferResult result = metadata.getDriveId().asDriveFolder().listChildren(mGoogleApiClient).await();

            // Si il y a une erreur on sort
            if (!result.getStatus().isSuccess()) {
                Log.i(TAG, "Erreur dans la requête pour récupérer le dossier : " + newPath);
                return;
            }

            // Si le dossier est vide, on sort
            MetadataBuffer metadataBuffer = result.getMetadataBuffer();
            if (metadataBuffer.getCount() == 0) {
                Log.i(TAG, "Dossier vide : " + newPath);
                return;
            }

            // On appelle récursivement au prochain au lui donnant le nouveau chemin local et le metadata contenant le DriveID  le titre nécessaire
            for (Metadata md : metadataBuffer) {
                constructTreeLocal(newPath, md);
            }
        }

    }

    // Méthode permettant de vérifier l'existence de fichier de l'application sur le drive, car si il y a
    // un dossier en dessous du root, càd que quelqu'un a déja push des fichiers d'application sur ce drive
    public Metadata getRootFolder() {
        DriveApi.MetadataBufferResult result = Drive.DriveApi.getRootFolder(mGoogleApiClient).listChildren(mGoogleApiClient).await();

        if (!result.getStatus().isSuccess()) {
            Log.i(TAG, "Erreur dans la requête pour récupérer le root");
            return null;
        }
        MetadataBuffer metadataBuffer = result.getMetadataBuffer();
        if (metadataBuffer.getCount() == 0) {
            Log.i(TAG, "Aucun root trouvé, il n'y a rien de l'appli sur le drive.");
            return null;
        }

        return metadataBuffer.get(0);
    }
}
