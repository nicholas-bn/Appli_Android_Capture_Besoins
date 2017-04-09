package com.miage.m1.capture.capturedesbesoins.services;

import android.app.Activity;
import android.view.Menu;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Nicho on 09/04/2017.
 */

public class DownloadDrive implements Runnable {

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

    }
}
