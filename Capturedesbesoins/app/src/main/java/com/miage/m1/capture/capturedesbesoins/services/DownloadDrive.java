package com.miage.m1.capture.capturedesbesoins.services;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

/**
 * Created by Nicho on 09/04/2017.
 */

public class DownloadDrive implements Runnable {

    // Tag qui permet de faciliter la lecture de l'output
    private String TAG = "GOOGLE DOWNLOAD DRIVE";

    // Lien vers l'API Google Drive
    private static GoogleApiClient mGoogleApiClient;

    // Activité d'où c'est lancé, c'est nécessaire pour lui demander de se refresh aprés le DL depuis le Drive
    public Activity activity;

    // Menu
    private Menu menu;

    // Permet de garder le titre du root pour éviter de le créer plus tard et ainsi éviter le décalage de dossier quand on DL
    String rootDriveName = "";

    // Constructeur
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

            // Pour comparer et ignorer dans la méthode récursive
            rootDriveName = root.getTitle();

            // Sous dossier racine, la ou on commence à supprimer
            File subRoot = new File(activity.getExternalFilesDir(null).getAbsolutePath() + File.separator + GestionnaireFichiers.nomDossierMain);

            // Si il existe
            if (subRoot.exists()) {
                // On nettoie le contenu local avant de le remplacer par le contenu du Drive
                deleteLocalContent(subRoot);
            }

            // Construction de l'arbo en local
            constructTreeLocal(activity.getExternalFilesDir(null).getAbsolutePath(), root);

            // Ajout d'une info de fin du download pour l'user
            Snackbar.make(activity.getCurrentFocus(), "Fin du download", Snackbar.LENGTH_LONG).show();

            // Permet de rafraichir l'activité aprés avoir supprimer le contenu local
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }

    // Méthode récursive permettant de supprimer le contenu local
    private void deleteLocalContent(File folder) {

        // On liste les sous dossiers/fichiers
        File[] files = folder.listFiles();

        // Risque de retour null quand dossier vide
        if (files != null) {
            // On parcours
            for (File f : files) {
                // Si c'est un dossier on fait un appel récursif
                if (f.isDirectory()) {
                    deleteLocalContent(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    // Méthode permettant de parcourir le Drive et de recréer la même architecture en local
    public void constructTreeLocal(String pathParent, Metadata metadata) {

        // On récupère le type (dossier OU fichier)
        int ressourceType = metadata.getDriveId().getResourceType();

        // Si c'est un fichier
        if (ressourceType == DriveId.RESOURCE_TYPE_FILE) {

            String newFile = pathParent + File.separator + metadata.getTitle();

            // On affiche le nouveau chemin local
            Log.i(TAG, "FILE Path : " + newFile);

            // On transforme le driveID en driveFile
            DriveFile driveFile = metadata.getDriveId().asDriveFile();

            // On l'open (en write only), ce qui permet de récupérer le driveContentResult
            DriveApi.DriveContentsResult driveContentsResult = driveFile.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();

            // On récupére le driveContents car il permet d'obtenir le OutputStream
            DriveContents contents = driveContentsResult.getDriveContents();

            // On récupère l'InputStream du fichier sur le Drive
            InputStream inputStream = contents.getInputStream();

            try {

                FileOutputStream fileOutputStream = new FileOutputStream(new File(newFile));

                // On boucle sur l'inputstream pour écrire sur le fichier du Drive
                if (inputStream != null) {


                    // On met (int)file.length() pour éviter d'avoir des NULL en fin de fichier
                    byte[] data = new byte[(int) metadata.getFileSize()];
                    while (inputStream.read(data) != -1) {
                        fileOutputStream.write(data);
                    }
                    inputStream.close();
                }

                fileOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (ressourceType == DriveId.RESOURCE_TYPE_FOLDER) { // si c'est un dossier

            // Nouveau chemin
            String newPath;

            // On l'instancie pas directement, suivant si c'est le root ou non il sera différemment construit
            if (metadata.getTitle().equals(rootDriveName)) {

                // On construit le nouveau chemin local
                newPath = pathParent;
                Log.i(TAG, "FOLDER Path : " + newPath);

            } else {

                // On construit le nouveau chemin local
                newPath = pathParent + File.separator + metadata.getTitle();
                Log.i(TAG, "FOLDER Path : " + newPath);

                // On créé le répertoire
                File f = new File(newPath);
                f.mkdirs();
            }
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

            // Pour éviter les fuites mémoires
            metadataBuffer.release();
            result.release();
        }
    }

    // Méthode permettant de vérifier l'existence de fichier de l'application sur le drive, car si il y a
    // un dossier en dessous du root, càd que quelqu'un a déja push des fichiers d'application sur ce drive
    public Metadata getRootFolder() {

        // Query pour récupérer le root
        DriveApi.MetadataBufferResult result = Drive.DriveApi.getRootFolder(mGoogleApiClient).listChildren(mGoogleApiClient).await();

        // Si c'est un échec
        if (!result.getStatus().isSuccess()) {
            Log.i(TAG, "Erreur dans la requête pour récupérer le root");
            return null;
        }

        // Récupére le buffer de Metadata
        MetadataBuffer metadataBuffer = result.getMetadataBuffer();

        // Si la taille du buffer est égal à 0, c'est qu'il n'y a rien sur le drive
        if (metadataBuffer.getCount() == 0) {
            Log.i(TAG, "Aucun root trouvé, il n'y a rien de l'appli sur le drive.");
            return null;
        }

        // On utilise la méthode freeze pour obtenir une copie de l'objet et ainsi pouvoir effectuer un release sur le buffer et donc éviter les fuites mémoires
        Metadata retour = metadataBuffer.get(0).freeze();
        metadataBuffer.release();
        return retour;
    }
}
