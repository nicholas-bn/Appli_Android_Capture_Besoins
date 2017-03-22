package com.miage.m1.capture.capturedesbesoins;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.miage.m1.capture.capturedesbesoins.Services.GestionnaireFichiers;

public class ProjetActivity extends AppCompatActivity implements View.OnClickListener {

    // Nom du ProjetActivity en cours
    private String nomProjet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout associé à l'activité
        setContentView(R.layout.activity_projet);

        // On récupère le nom du projet sélectionné
        Bundle b = getIntent().getExtras();
        nomProjet = "";
        if (b != null) {
            nomProjet = b.getString("nom");
        }

        // On change le label de l'Activity
        setTitle(nomProjet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton pour choisir un Plugin
        FloatingActionButton btn_choisirPlugin = (FloatingActionButton) findViewById(R.id.choisir_plugin);
        btn_choisirPlugin.setOnClickListener(this);

        // Bouton pour modifier les informations sur le Projet
        FloatingActionButton btn_modifierProjet = (FloatingActionButton) findViewById(R.id.modifier_projet);
        btn_modifierProjet.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Texte de description du Projet
        TextView description = (TextView) findViewById(R.id.description_projet);

    }

    @Override
    public void onClick(View view) {
        // Bouton '+' pour choisir un Plugin
        if (view.getId() == R.id.choisir_plugin) {
            Snackbar.make(view, "Choix du Plugin", Snackbar.LENGTH_LONG).show();

            Intent intent = new Intent("captureDesBesoins.plugin");

            // Bundle pour passer en paramètre le nom du projet
            Bundle b = new Bundle();
            b.putString("nom", nomProjet); // Nom du projet sélectionné
            b.putString("cheminProjet", GestionnaireFichiers.getCheminProjet(this, nomProjet));
            intent.putExtras(b);

            String title = "Choisir un Plugin";
            // Create intent to show chooser
            Intent chooser = Intent.createChooser(intent, title);

            // Verify the intent will resolve to at least one activity
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
        }

        // Bouton 'crayon' pour modifier le Projet
        if (view.getId() == R.id.modifier_projet) {
            Snackbar.make(view, "Modification du Projet", Snackbar.LENGTH_LONG).show();
        }
    }
}
