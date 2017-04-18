package com.miage.m1.capture.capturedesbesoins.services;

import android.app.Activity;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.metadata.SearchableCollectionMetadataField;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by Nicho on 26/03/2017.
 */

public class UploadDrive implements Runnable {

    private String TAG = "GOOGLE UPLOAD DRIVE";

    private ArrayList<File> fileList = new ArrayList<File>();

    // Ce sera le dossier racine sur le Drive de l'utilisateur
    private static final String dossierRacineDrive = "Capture des besoins - Android app 1";

    // On conserve le dernier DriveId et son adresse local utilisé pour définir si le suivant est un enfant ou non
    private File lastFileLocalUsed = null;
    private DriveId lastDriveIDUsed = null;

    // Dossier parent des projets
    File parentProjectFolders;

    // Lien vers l'API Google Drive
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
    public void uploadSubFoldersAndSubFiles(){

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

                String nomDossierParent = file.getParentFile().getName();
                Log.i(TAG, "Parent de "+file.getName()+" est "+nomDossierParent);

                // Si il existe PAS on l'upload
                if ( ! doesFolderExistsWithParentGiven(file.getName(), nomDossierParent)) {
                    Log.i(TAG, file.getName() + " est pas sur le DRIVE");

                    // On récupére le DriveID du folder dans lequel on le range
                    DriveId driveid = getFolderParentDriveID(file);

                    // Requête synchrone pour créer du contenu sur le drive,  on créé le fichier
                    DriveApi.DriveContentsResult result = Drive.DriveApi.newDriveContents(mGoogleApiClient).await();

                    // Vérification si c'est un succés
                    if (!result.getStatus().isSuccess()) {
                        Log.i(TAG, "Erreur en essayant de créer le contenu du nouveau fichier.");
                        return;
                    }

                    // On récupère le DriveContent sur lequel on peut utiliser les input/output Stream
                    final DriveContents driveContents = result.getDriveContents();

                    // On récupére l'outputstream du fichier sur le drive
                    OutputStream outputStream = driveContents.getOutputStream();
                    try {
                        // On récupère l'inputstream de notre fichier local
                        InputStream inputStream = new FileInputStream(file);

                        // On boucle sur l'inputstream pour écrire sur le fichier du Drive
                        if (inputStream != null) {

                            // On met (int)file.length() pour éviter d'avoir des NULL en fin de fichier
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

                    // On va définir le MimeType du fichier qu'on va envoyé sur le drive suivant son extension donné par les plugins
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

                    // On instancie les métadonnées du fichier
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(file.getName())
                            .setMimeType(mimeType)
                            .setStarred(true).build();

                    // On créé le fichier dans le dossier parent
                    DriveFolder parentRepertory = driveid.asDriveFolder();
                    DriveFolder.DriveFileResult fileResult = parentRepertory.createFile(mGoogleApiClient, changeSet, driveContents).await();

                } else { // Autrement on écrit par dessus

                    // On transforme le driveID en driveFile
                    DriveFile driveFile = lastDriveIDUsed.asDriveFile();

                    // On l'open (en write only), ce qui permet de récupérer le driveContentResult
                    DriveApi.DriveContentsResult driveContentsResult = driveFile.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();

                    // On récupére le driveContents car il permet d'obtenir le OutputStream
                    DriveContents contents = driveContentsResult.getDriveContents();

                    // On récupére l'outputstream du fichier sur le drive
                    OutputStream outputStream = contents.getOutputStream();
                    try {
                        // On récupère l'inputstream de notre fichier local
                        InputStream inputStream = new FileInputStream(file);

                        // On boucle sur l'inputstream pour écrire sur le fichier du Drive
                        if (inputStream != null) {

                            // On met (int)file.length() pour éviter d'avoir des NULL en fin de fichier
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

                    // Aprés l'écriture sur l'OutputStream du DriveContents on commit les modifications et on s'assure du bon résultat
                    contents.commit(mGoogleApiClient, null).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {

                            if(status.isSuccess())
                                Log.i(TAG, "Ecriture avec succés ! ");
                            else
                                Log.i(TAG, "ECHEC de l'eériture  ! ");

                        }
                    });
                }
            }
        }



    }

    public boolean doesFolderExistsWithParentGiven(String titre, String nameFolderParent) {

        // Récup le driveID du parent
        Query queryParent = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, nameFolderParent)).build();
        DriveApi.MetadataBufferResult mdResultSetParent = Drive.DriveApi.query(mGoogleApiClient, queryParent).await();

        DriveId driveIdParent = null;

        // On parcourt les metadata pour trouver notre dossier parent
        for (Metadata metadata : mdResultSetParent.getMetadataBuffer()) {
            if (metadata.getTitle().equals(nameFolderParent)) {
                driveIdParent = metadata.getDriveId();
            }
        }

        // Pour éviter les fuites de mémoires internes
        mdResultSetParent.release();

        // Récupe le driveID du fichier connaissant le driveID du parent
        Query query = new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, titre),
                                                                Filters.in(SearchableField.PARENTS, driveIdParent))
                                                    ).build();
        DriveApi.MetadataBufferResult mdResultSet = Drive.DriveApi.query(mGoogleApiClient, query).await();

        // Booléen pour savoir si on a trouvé le dossier qu'on cherche
        boolean dossierTrouver = false;

        // On parcourt les metadata pour trouver si notre dossier est déja présent
        for (Metadata metadata : mdResultSet.getMetadataBuffer()) {
            if (metadata.getTitle().equals(titre)) {
                Log.i(TAG, "Dossier/fichier existe déja : " + metadata.getTitle());
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

    // Query pour retrouver le driveID du dossier parent au fichier donné en parametre
    public DriveId getFolderParentDriveID(File f) {
        DriveId retour = null;

        // On construit la requéte en donnant le nom du dossier parent
        Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, f.getParentFile().getName())).build();

        // On récupére le buffer de meta donnés renvoyés par la requete synchrone a l'appel de  await()
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

        // On construit la requéte en donnant le nom du dossier qu'on veut créer
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(title.getName()).build();

        // On récupére le buffer de meta donnés renvoyés par la requete synchrone a l'appel de  await()
        DriveFolder.DriveFolderResult dfr = folder.createFolder(mGoogleApiClient, changeSet).await();
        if (!dfr.getStatus().isSuccess()) {
            Log.i(TAG, "Probleme lors de la création du dossier " + title.getName());
            return;
        }

        // On récupére le DriveID
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
                Log.i(TAG, "Dossier/fichier existe déja : " + metadata.getTitle());
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
                Log.i(TAG, "Racine existe déja : " + metadata.getTitle());
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
