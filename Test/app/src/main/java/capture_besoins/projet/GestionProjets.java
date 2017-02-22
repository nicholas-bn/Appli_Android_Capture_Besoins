package capture_besoins.projet;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import capture_besoins.plugins.texte_simple.Activity_Texte;
import capture_besoins.main.R;

/**
 * Created by Karl on 21/02/2017.
 */

public class GestionProjets implements View.OnClickListener, AdapterView.OnItemClickListener {

    // Activité principale
    private Activity activity;

    // Bouton pour créer un projet
    private Button btn_creerProjet;

    // Liste des projets
    private ListView listView_projets;

    // Variable temporaire du nom/id utilisateur
    private final String varTempoNomUser = "userTest";


    public GestionProjets(Activity activity) {

        // On récupère l'activity
        this.activity = activity;

        // On récupére le bouton pour créer un projet
        btn_creerProjet = (Button) activity.findViewById(R.id.btn_createProjet);

        // On y ajoute un listener
        btn_creerProjet.setOnClickListener(this);

        // On récupère le ListView des projets
        listView_projets = (ListView) activity.findViewById(R.id.listProjets);

        // On remplit la liste des projets
        remplirListeProjets();


    }

    private void remplirListeProjets() {
        // On récupère la liste des Projets déja créés
        List<String> listNomProjets = getNomProjetsExistants();

        // Si la liste est vide
        if (listNomProjets == null) {
            return;
        }

        // Adapter qui contient les éléments de la liste
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (activity, android.R.layout.simple_list_item_1, listNomProjets);

        // On set l'adapter à la ListView
        listView_projets.setAdapter(adapter);

        // On met à jour le texte
        adapter.notifyDataSetChanged();

        // On ajoute un listener
        listView_projets.setOnItemClickListener(this);

    }

    private List<String> getNomProjetsExistants() {
        ArrayList<String> listProjets = new ArrayList<String>();

        // Racine de l'application
        File racineApp = activity.getFilesDir();

        // Dossier contenant les projets
        File dossierApp = new File(racineApp.getAbsolutePath() + File.separator + varTempoNomUser);

        // Liste des fichiers du répertoire
        File[] files = dossierApp.listFiles();

        // Cas où aucun dossier n'est présent
        if (files == null) {
            return null;
        }

        // Parcours des fichiers :
        for (File file : files) {
            // Si c'est un répertoire
            if (file.isDirectory()) {
                listProjets.add(file.getName());
            }
        }
        return listProjets;
    }

    private void creerProjet(String nomProjet) {
        // Racine de l'application
        File racineApp = activity.getFilesDir();

        // Dossier contenant les projets
        File projet = new File(racineApp.getAbsolutePath() + File.separator + varTempoNomUser + File.separator + nomProjet);

        // S'il n'existe pas on le créé
        if (!projet.exists()) {
            projet.mkdirs();
            // Toast pour indiquer que le projet a été créé
            Toast.makeText(activity.getApplicationContext(),
                    "Création du projet : " + nomProjet + " !", Toast.LENGTH_SHORT).show();
        } else {
            // Toast pour indiquer que le projet existe déja
            Toast.makeText(activity.getApplicationContext(),
                    "Le projet " + nomProjet + " existe déja !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        // Dialogue pour choisir le nom du nouveau projet
        AlertDialog.Builder building = new AlertDialog.Builder(v.getContext());

        // AutoCompleteTextView où l'on peut écrire le nom du fiprojet
        final AutoCompleteTextView myAutoCompleteChoixNomFichier = new AutoCompleteTextView(building.getContext());
        myAutoCompleteChoixNomFichier.setHint("Nom du projet");
        building.setView(myAutoCompleteChoixNomFichier);

        // Titre
        building.setTitle("Choisir le nom du projet :");

        // Définir le comportement du bouton "OK"
        building.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // On crée le projet
                creerProjet(myAutoCompleteChoixNomFichier.getText().toString());

                // On met à jour la liste
                remplirListeProjets();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // - Clique sur un élément de la liste des Projets :


        // Récupération du nom du projet sélectionné
        String nomProjet = ((TextView) view).getText().toString();

        // Toast pour indiquer qu'on ouvre ce projet
        Toast.makeText(activity.getApplicationContext(),
                "Ouverture de : " + nomProjet, Toast.LENGTH_SHORT).show();

        // Intent pour switch entre 2 activities
        Intent i = new Intent(activity, Projet.class);

        // Bundle pour passer en paramètre le nom du projet
        Bundle b = new Bundle();
        b.putString("nom", nomProjet); // Nom du projet sélectionné
        i.putExtras(b);

        // On lance la deuxième activity
        activity.startActivity(i);

    }
}
