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
import com.google.android.gms.common.api.ResultCallback;

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
        DriveId root = getRootFolder();
        if (root == null) {
            // Ajout d'une info pour l'user qu'il y a eu un problème lors du DL (Drive vide ou erreur de requéte)
            Snackbar.make(activity.getCurrentFocus(), "Erreur lors du download : Vous n'avez aucun fichier de l'application sur le drive, ou une requête à Google s'est mal passé.", Snackbar.LENGTH_LONG).show();
        } else {



            // Ajout d'une info de fin du download pour l'user
            Snackbar.make(activity.getCurrentFocus(), "Fin du download", Snackbar.LENGTH_LONG).show();
        }
    }

    // Méthode permettant de vérifier l'existence de fichier de l'application sur le drive, car si il y a
    // un dossier en dessous du root, càd que quelqu'un a déja push des fichiers d'application sur ce drive
    public DriveId getRootFolder() {
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

        return metadataBuffer.get(0).getDriveId();
    }
}
