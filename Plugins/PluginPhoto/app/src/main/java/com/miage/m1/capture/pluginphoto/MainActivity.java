package com.miage.m1.capture.pluginphoto;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.net.URI;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String AUTHORITY=
            BuildConfig.APPLICATION_ID+".provider";
    private String nomProjet;
    private String cheminProjet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout associé à l'activité
        setContentView(R.layout.activity_main);

        // On récupère le nom du projet sélectionné
        Bundle b = getIntent().getExtras();
        if (b != null) {
            nomProjet = b.getString("nom");
            cheminProjet = b.getString("cheminProjet");
        }

        // On change le label de l'Activity
        setTitle(nomProjet + " - Photo");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton take_photo = (FloatingActionButton) findViewById(R.id.take_photo);
        take_photo.setOnClickListener(this);
    }

    private void lancerAppareilPhoto() {

        File dossierPhoto = new File(cheminProjet, nomProjet+"-Photo");
        // Si il existe pas on le créé
        if (!dossierPhoto.exists())
            Log.e("AAAAAAAAA  ccc ", "Dossier Photo : " + dossierPhoto.mkdirs());
        //dossierTexte.mkdirs();


        Uri uri =  FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider",new File(dossierPhoto,"test.jpg"));;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Snackbar.make(view, "Prise de photo", Snackbar.LENGTH_LONG).show();

        lancerAppareilPhoto();

    }


}
