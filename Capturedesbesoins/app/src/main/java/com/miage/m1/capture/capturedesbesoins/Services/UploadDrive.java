package com.miage.m1.capture.capturedesbesoins.services;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Nicho on 26/03/2017.
 */

public class UploadDrive implements Runnable {

    private String TAG = "GOOGLE UPLOAD DRIVE";

    private ArrayList<File> fileList = new ArrayList<File>();

    // Ce sera le dossier racine sur le Drive de l'utilisateur
    private static final String dossierRacineDrive = "Capture des besoins - Android app";

    // On conserve le dernier DriveId et son adresse local utilisé pour définir si le suivant est un enfant ou non
    private File lastFileLocalUsed = null;
    private DriveId lastDriveIDUsed = null;

    File parentProjectFolders;

    private static GoogleApiClient mGoogleApiClient;

    public Activity activity;

    private Menu menu;

    public UploadDrive(Activity activity, Menu menu, GoogleApiClient mGoogleApiClient) {
        this.activity = activity;
        this.menu = menu;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    // Méthode appellé lors du clic du bouton "Envoyer sur le Drive"
    @Override
    public void run() {

        // Ajout d'une info du début de l'upload pour l'user
        Snackbar.make(activity.getCurrentFocus(), "Début de l'upload sur le Drive ...", Snackbar.LENGTH_LONG).show();

        // On récupére le point de départ du pro
        parentProjectFolders = new File(activity.getExternalFilesDir(null).getAbsolutePath() + File.separator + GestionnaireFichiers.nomDossierMain);

        // On récupére toute l'archi fichier à partir de " parentProjectFolders "
        getAllFoldersAndFilesLocalApp(parentProjectFolders);

        System.out.println("List de fichier sous : " + parentProjectFolders);
        for (File f : fileList) {
            System.out.println("    " + f.getAbsolutePath());
        }

        // On créé le dossier root sur le drive si nécessaire
        createRootFolderIfInexistant(dossierRacineDrive);


        // Si on a pas trouvé le dossier on le créé
        if (!doesFolderExists(parentProjectFolders.getName())) {
            createFolder(parentProjectFolders, lastDriveIDUsed);
        }

        System.out.println("lastDriveIDUsed : " + lastDriveIDUsed + " // lastFileLocalUsed : " + lastFileLocalUsed);

        // On va maintenant upload les dossier et les fichiers
        uploadSubFoldersAndSubFiles();


        // Ajout d'une info de fin d'upload
        Snackbar.make(activity.getCurrentFocus(), "Fin de l'upload sur le Drive ...", Snackbar.LENGTH_LONG).show();
    }

    // Méthode pour envoyer tous les fichiers et dossiers
    public void uploadSubFoldersAndSubFiles() {

        // On parcours l'arbo local
        for (File file : fileList) {

            Log.i(TAG, "File : " + file.getName());
            // SI c'est un repertoire
            if (file.isDirectory()) {

                // si le dossier existe déja on récupére le DriveID de son parent
                if (!doesFolderExists(file.getName())) {
                    DriveId driveid = getFolderParentDriveID(file);
                    createFolder(file, driveid);
                }

            } else { // Si c'est un fichier

                // Si il existe pas on l'upload
                if (!doesFolderExists(file.getName())) {
                    Log.i(TAG, file.getName() + " est pas sur le DRIVE");

                    // On récupére le DriveID du folder dans lequel on le range
                    DriveId driveid = getFolderParentDriveID(file);

                    DriveApi.DriveContentsResult result = Drive.DriveApi.newDriveContents(mGoogleApiClient).await();

                    if (!result.getStatus().isSuccess()) {
                        Log.i(TAG, "Erreur en essayant de créer le contenu du nouveau fichier.");
                        return;
                    }

                    final DriveContents driveContents = result.getDriveContents();

                    OutputStream outputStream = driveContents.getOutputStream();
                    try {
                        InputStream inputStream = new FileInputStream(file);

                        if (inputStream != null) {
                            byte[] data = new byte[(int)file.length()];
                            while (inputStream.read(data) != -1) {
                                outputStream.write(data);
                            }
                            inputStream.close();
                        }

                        outputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }

                    String mimeType = "";
                    String nameFile = file.getName();
                    if (nameFile.endsWith(".jpg")) {
                        mimeType = "image/jpeg";
                    } else if (nameFile.endsWith(".jpeg")) {
                        mimeType = "image/jpeg";
                    } else if (nameFile.endsWith(".png")) {
                        mimeType = "image/png";
                    } else {
                        mimeType = "text/*";
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(file.getName())
                            .setMimeType(mimeType)
                            .setStarred(true).build();

                    DriveFolder parentRepertory = driveid.asDriveFolder();
                    DriveFolder.DriveFileResult fileResult = parentRepertory.createFile(mGoogleApiClient, changeSet, driveContents).await();

                } else { // Autrement on vérifie si c'est le bon

                }
            }
        }



    }

    public DriveId getFolderParentDriveID(File f) {
        DriveId retour = null;
        Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, f.getParentFile().getName())).build();
        DriveApi.MetadataBufferResult mdResultSet = Drive.DriveApi.query(mGoogleApiClient, query).await();
        for (Metadata metadata : mdResultSet.getMetadataBuffer()) {
            if (metadata.getTitle().equals(f.getParentFile().getName())) {
                Log.i(TAG, "Dossier parent trouvé : " + metadata.getTitle());
                retour = metadata.getDriveId();
            }
        }
        if (retour == null)
            Log.i(TAG, "Dossier parent PAS trouvé : " + f.getParentFile().getName());

        // On release le buffer pour éviter les fuites mémoires
        mdResultSet.getMetadataBuffer().release();

        return retour;
    }

    public void createFolder(File title, DriveId driveid) {
        // On créé le dossier user
        DriveFolder folder = driveid.asDriveFolder();
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(title.getName()).build();
        DriveFolder.DriveFolderResult dfr = folder.createFolder(mGoogleApiClient, changeSet).await();
        if (!dfr.getStatus().isSuccess()) {
            Log.i(TAG, "Probleme lors de la création du dossier " + title.getName());
            return;
        }
        lastDriveIDUsed = dfr.getDriveFolder().getDriveId();
        lastFileLocalUsed = title;
        Log.i(TAG, "Dossier " + title.getName() + " créé.");
    }

    public boolean doesFolderExists(String titre) {
        Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, titre)).build();
        DriveApi.MetadataBufferResult mdResultSet = Drive.DriveApi.query(mGoogleApiClient, query).await();

        // Booléen pour savoir si on a trouvé le dossier qu'on cherche
        boolean dossierTrouver = false;

        // On parcourt les metadata pour trouver si notre dossier est déja présent
        for (Metadata metadata : mdResultSet.getMetadataBuffer()) {
            if (metadata.getTitle().equals(titre)) {
                Log.i(TAG, "Dossier existe déja : " + metadata.getTitle());
                dossierTrouver = true;
                lastDriveIDUsed = metadata.getDriveId();
                lastFileLocalUsed = parentProjectFolders;
                break;
            }
        }
        // On release le buffer pour éviter les fuites mémoires
        mdResultSet.getMetadataBuffer().release();

        return dossierTrouver;
    }

    // Méthode récursive pour obtenir la liste de fichier
    public void getAllFoldersAndFilesLocalApp(File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                fileList.add(file);
                getAllFoldersAndFilesLocalApp(file);
            } else {
                fileList.add(file);
            }
        }
    }

    // Créer un dossier en vérifiant d'abord son existance pour éviter les doublons
    public void createRootFolderIfInexistant(String paramDossierRacineDrive) {

        Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, paramDossierRacineDrive)).build();
        DriveApi.MetadataBufferResult mdResultSet = Drive.DriveApi.query(mGoogleApiClient, query).await();

        // Booléen pour savoir si on a trouvé le dossier qu'on cherche
        boolean dossierTrouver = false;

        // On parcourt les metadata pour trouver si notre dossier est déja présent
        for (Metadata metadata : mdResultSet.getMetadataBuffer()) {
            if (metadata.getTitle().equals(dossierRacineDrive)) {
                Log.i(TAG, "Dossier existe déja : " + metadata.getTitle());
                dossierTrouver = true;
                lastDriveIDUsed = metadata.getDriveId();
                break;
            }
        }
        // On release le buffer pour éviter les fuites mémoires
        mdResultSet.getMetadataBuffer().release();

        // Si on a pas trouvé le dossier on le créé
        if (!dossierTrouver) {
            creerUnDossierDrive(dossierRacineDrive, null);
        }
    }

    // Créer un dossier qui est spécifié
    public void creerUnDossierDrive(String titre, DriveId driveID) {

        // Création du dossier racine sur le drive
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(titre).build();
        Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(mGoogleApiClient, changeSet).setResultCallback(folderCreatedCallback);
    }

    // Callback de creerUnDossierDrive
    ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.i(TAG, "Erreur lors de la création du dossier");
                return;
            }
            Log.i(TAG, "Création du dossier : " + (lastDriveIDUsed = result.getDriveFolder().getDriveId()));

        }
    };

}
