package com.miage.m1.capture.capturedesbesoins;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miage.m1.capture.capturedesbesoins.Services.GestionnaireFichiers;

import java.util.List;

public class MainActivity extends CustomActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    // Bouton pour créer un projet
    private FloatingActionButton btn_CreerProjet;

    // Liste des projets
    private ListView list_Projets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout lié à l'activité
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.transition.slide_in_right,
                R.transition.slide_out_right);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Récupération du bouton '+' pour créer un projet + ajout du listener
        btn_CreerProjet = (FloatingActionButton) findViewById(R.id.creerProjet);
        btn_CreerProjet.setOnClickListener(this);

        // Récupération de la ListeView contenant les projets
        list_Projets = (ListView) findViewById(R.id.listeProjets);

    }

    @Override
    public void onResume() {
        super.onResume();

        // On remplit la liste des Projets
        remplirListeProjets();
    }

    private void remplirListeProjets() {
        // On récupère la liste des Projets existants
        List<String> listNomProjets = GestionnaireFichiers.getNomProjetsExistants(this);

        // Si la liste est vide
        if (listNomProjets == null) {
            // On ne fait rien
            return;
        }

        // Adapter qui contient les éléments de la liste
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, listNomProjets);

        // On set l'adapter à la ListView
        list_Projets.setAdapter(adapter);

        // On met à jour le texte
        adapter.notifyDataSetChanged();

        // On ajoute un listener
        list_Projets.setOnItemClickListener(this);
    }

    private void creerNouveauProjet(View view) {

        Snackbar.make(view, "Création d'un projet", Snackbar.LENGTH_LONG).show();

        // Dialogue pour choisir le nom du nouveau projet
        AlertDialog.Builder building = new AlertDialog.Builder(this);

        // Fenetre pour écrire le nom du projet
        final AutoCompleteTextView choixNomProjet = new AutoCompleteTextView(building.getContext());
        choixNomProjet.setHint("Nom du projet");
        building.setView(choixNomProjet);

        // Titre
        building.setTitle("Choisir le nom du projet :");

        // Définir le comportement du bouton "OK"
        building.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Nom du projet choisi
                String nomProjet = choixNomProjet.getText().toString();

                // On crée le ProjetActivity (nouveau dossier)
                boolean retourCréationProjet = GestionnaireFichiers.creerDossier(MainActivity.this, nomProjet);

                // Si le ProjetActivity a été créé :
                if (retourCréationProjet == true) {
                    Toast.makeText(getApplicationContext(), "ProjetActivity " + nomProjet + " créé !", Toast.LENGTH_SHORT).show();

                    // On met à jour la liste des Projets
                    remplirListeProjets();
                } else {
                    Toast.makeText(getApplicationContext(), "ProjetActivity " + nomProjet + " existe déja !", Toast.LENGTH_SHORT).show();
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
        // On lance une fenètre pour demander à l'utilisateur de choisir un nom pour le projet
        creerNouveauProjet(view);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // - Clique sur un élément de la liste des Projets :

        // Récupération du nom du projet sélectionné
        String nomProjet = ((TextView) view).getText().toString();

        // Toast pour indiquer qu'on ouvre ce projet
        Toast.makeText(getApplicationContext(), "Ouverture de : " + nomProjet, Toast.LENGTH_SHORT).show();

        // Intent pour switch entre 2 activities
        Intent intent = new Intent(this, ProjetActivity.class);

        // Bundle pour passer en paramètre le nom du projet
        Bundle b = new Bundle();
        b.putString("nom", nomProjet); // Nom du projet sélectionné
        intent.putExtras(b);

        // On lance la deuxième activity
        startActivity(intent);
    }
}
