package com.miage.m1.capture.capturedesbesoins.services;

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
import com.google.android.gms.drive.Drive;

import com.google.android.gms.drive.DriveId;
import com.miage.m1.capture.capturedesbesoins.R;

import java.io.File;
import java.util.ArrayList;


public class LiaisonDrive implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Ce sera le dossier racine sur le Drive de l'utilisateur
    private static final String dossierRacineDrive = "Capture des besoins - Android app";
    private DriveId dossierRacineDriveID;

    // On conserve le dernier DriveId et son adresse local utilisé pour définir si le suivant est un enfant ou non
    private File lastFileLocalUsed;
    private DriveId lastDriveIDUsed;

    File parentProjectFolders;

    private static GoogleApiClient mGoogleApiClient;

    private ArrayList<File> fileList = new ArrayList<File>();

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

        Log.i(TAG, "GoogleApiClient a réussi à se connecter.");

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

        UploadDrive ud = new UploadDrive(activity, menu, mGoogleApiClient);
        Thread t = new Thread(ud);
        t.start();
    }

    // Méthode appellé lors du clic du bouton "Récupérer du Drive"
    public void askedToPullFromDrive() {

        DownloadDrive dd = new DownloadDrive(activity, menu, mGoogleApiClient);
        Thread t = new Thread(dd);
        t.start();
    }

}
