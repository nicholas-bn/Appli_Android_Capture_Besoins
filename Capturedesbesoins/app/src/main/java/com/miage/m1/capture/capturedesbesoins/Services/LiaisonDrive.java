package com.miage.m1.capture.capturedesbesoins.Services;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.miage.m1.capture.capturedesbesoins.R;


public class LiaisonDrive implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Ce sera le dossier racine sur le Drive de l'utilisateur
    private static final String dossierRacineDrive = "Capture des besoins - Android app";

    private static GoogleApiClient mGoogleApiClient;

    private String TAG = "GOOGLE CONNEXION DRIVE";

    public Activity activity;

    private int REQUEST_CODE_RESOLUTION = 11;

    private Menu menu;

    public LiaisonDrive(Activity activity, Menu menu) {


        this.activity = activity;
        this.menu = menu;

        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(Drive.API)
                    .addApi(AppIndex.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }


    }

    public boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    public void connect() {
        mGoogleApiClient.connect();

        Toast.makeText(activity.getApplicationContext(), "Connecté", Toast.LENGTH_LONG).show();
    }

    public void disconnect() {
        //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        mGoogleApiClient.disconnect();
        Toast.makeText(activity.getApplicationContext(), "Déconnecté", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());

        if (!result.hasResolution()) {

            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(activity, result.getErrorCode(), 0).show();
            return;
        }

        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */

        try {

            result.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);

        } catch (IntentSender.SendIntentException e) {

            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    /**
     * It invoked when Google API client connected
     *
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Toast.makeText(activity.getApplicationContext(), "Connecté", Toast.LENGTH_LONG).show();

        MenuItem item = menu.findItem(R.id.drive);

        item.setTitle("Déconnexion");

    }

    /**
     * It invoked when connection suspended
     *
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {

        Log.i(TAG, "GoogleApiClient connection suspended");
    }



    // Méthode appellé lors du clic du bouton "Envoyer sur le Drive"
    public void askedToPushToDrive() {
        createFolderIfInexistant();
    }

    // Créer un dossier en vérifiant d'abord son existance pour éviter les doublons
    public void createFolderIfInexistant() {
        Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, dossierRacineDrive)).build();
        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(metadataCallback);
    }

    // Callback de createFolderIfInexistant
    ResultCallback<MetadataBufferResult> metadataCallback = new ResultCallback<MetadataBufferResult>() {
                @Override
                public void onResult(MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.i(TAG, "Probleme rencontré lors de la récupération des metadata");
                        return;
                    }

                    // Booléen pour savoir si on a trouvé le dossier qu'on cherche
                    boolean dossierTrouver = false;

                    // On parcourt les metadata pour trouver si notre dossier est déja présent
                    for(Metadata metadata : result.getMetadataBuffer()){
                        if (metadata.getTitle().equals(dossierRacineDrive)) {
                            Log.i(TAG, "Dossier existe déja : "+metadata.getTitle());
                            dossierTrouver = true;
                            break;
                        }
                    }
                    // On release le buffer pour éviter les fuites mémoires
                    result.getMetadataBuffer().release();

                    // Si on a pas trouvé le dossier on le créé
                    if( ! dossierTrouver){
                        creerUnDossierDrive(dossierRacineDrive);
                    }

                }
            };

    // Créer un dossier qui est spécifié
    public void creerUnDossierDrive(String titre){

        // Création du dossier racine sur le drive
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(titre).build();
        Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(mGoogleApiClient, changeSet).setResultCallback(folderCreatedCallback);
    }

    // Callback de creerUnDossierDrive
    ResultCallback<DriveFolderResult> folderCreatedCallback = new ResultCallback<DriveFolderResult>() {
        @Override
        public void onResult(DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.i(TAG, "Erreur lors de la création du dossier");
                return;
            }
            Log.i(TAG,"Création du dossier : " +  result.getDriveFolder().getDriveId());

        }
    };





}
