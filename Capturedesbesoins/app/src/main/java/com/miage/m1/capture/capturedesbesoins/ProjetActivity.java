package com.miage.m1.capture.capturedesbesoins;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miage.m1.capture.capturedesbesoins.services.GestionnaireFichiers;
import com.miage.m1.capture.capturedesbesoins.services.GestionnaireXML;
import com.miage.m1.capture.capturedesbesoins.xml.Fichier;
import com.miage.m1.capture.capturedesbesoins.xml.Projet;

import java.io.IOException;
import java.util.ArrayList;

public class ProjetActivity extends CustomActivity implements View.OnClickListener {

    // Nom du ProjetActivity en cours
    private String nomProjet;

    // Informations sur le Projet en cours
    private Projet projet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout associé à l'activité
        setContentView(R.layout.activity_projet);

        //overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_left);

        // On récupère le nom du projet sélectionné
        Bundle b = getIntent().getExtras();
        nomProjet = "";
        if (b != null) {
            nomProjet = b.getString("nom");
        }

        // On change le label de l'Activity
        setTitle(nomProjet);

        projet = GestionnaireXML.getDetailsProjet(this, nomProjet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bouton pour choisir un Plugin
        FloatingActionButton btn_choisirPlugin = (FloatingActionButton) findViewById(R.id.choisir_plugin);
        btn_choisirPlugin.setOnClickListener(this);

        // Bouton pour modifier les informations sur le Projet
        FloatingActionButton btn_modifierProjet = (FloatingActionButton) findViewById(R.id.modifier_projet);
        btn_modifierProjet.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    public void onResume() {
        super.onResume();
        // Texte de description du Projet
        TextView description = (TextView) findViewById(R.id.description_projet);
        description.setText(projet.getDescription());

        // On récupère la liste des Fichiers présents sur le téléphone
        ArrayList<Fichier> listeFichiers = GestionnaireFichiers.getListeFichiersDuProjet(this, nomProjet);

        // On ajoute dans Projet ceux qui n'y sont pas (qui viennent d'être crées)
        projet.compareAndAddFichiers(listeFichiers);

        // On met à jour le XML
        GestionnaireFichiers.majXML(this, projet);
    }

    private void ouvrirModification(final View view) {

        // Dialogue pour choisir le nom du nouveau projet
        AlertDialog.Builder building = new AlertDialog.Builder(this);

        // Fenetre pour écrire le nom du projet
        final EditText choixNomProjet = new EditText(building.getContext());
        choixNomProjet.setText(projet.getDescription());
        building.setView(choixNomProjet);

        // Titre
        building.setTitle("Choisir la description du Projet :");

        // Définir le comportement du bouton "OK"
        building.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Nom du projet choisi
                String newDescription = choixNomProjet.getText().toString();

                // Si ce n'est pas le même
                if (newDescription.equals(projet.getDescription()) == false) {
                    projet.setDescription(newDescription);

                    Snackbar.make(view, "Description modifiée", Snackbar.LENGTH_LONG).show();

                    // On met à jour la fenetre
                    onResume();
                }

            }
        });

        // Définir le comportement du bouton "Annuler"
        building.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Si il appuie sur Annuler on fait rien
                return;
            }
        });

        // On le créé et on l'affiche
        building.create();
        building.show();
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

            // Fenètre pour modifier
            ouvrirModification(view);
        }
    }
}
