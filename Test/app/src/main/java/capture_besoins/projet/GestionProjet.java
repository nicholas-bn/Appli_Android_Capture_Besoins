package capture_besoins.projet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import capture_besoins.main.Activity_Texte;
import capture_besoins.main.MainActivity;
import capture_besoins.main.R;
import capture_besoins.plugins.texte_simple.Plugin_texte_simple;

/**
 * Created by Karl on 21/02/2017.
 */

public class GestionProjet implements View.OnClickListener, AdapterView.OnItemClickListener {

    //Activité principale
    private Activity activity;

    //Bouton pour créer un projet
    private Button btn_creerProjet;

    // Liste des projets
    private ListView listView_projets;

    // Variable temporaire du nom/id utilisateur
    private final String varTempoNomUser = "userTest";


    public GestionProjet(Activity activity) {

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
        List<String> listNomProjets = getNomProjets();

        // Adapter qui contient les éléments de la liste
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (activity, android.R.layout.simple_list_item_1, listNomProjets);

        // On set l'adapter à la ListView
        listView_projets.setAdapter(adapter);

        // On ajoute un listener
        listView_projets.setOnItemClickListener(this);

    }

    private List<String> getNomProjets() {
        ArrayList<String> listProjets = new ArrayList<String>();

        // Racine de l'application
        File racineApp = activity.getFilesDir();

        // Dossier contenant les projets
        File dossierApp = new File(racineApp.getAbsolutePath() + File.separator + varTempoNomUser);

        // Liste des fichiers du répertoire
        File[] files = dossierApp.listFiles();

        // Parcours des fichiers :
        for (File file : files) {
            // Si c'est un répertoire
            if (file.isDirectory()) {
                listProjets.add(file.getName());
            }
        }
        return listProjets;
    }

    @Override
    public void onClick(View v) {
        // TODO Créer nouveau projet

        // TODO 1 : AlertBox pour demander le nom

        // TODO 2 : Créer répertoire du projet

        // TODO 3 : Ouvrir projet créé
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // - Clique sur un élément de la liste des Projets :


        // Récupération du nom du projet sélectionné
        String nomProjet = ((TextView) view).getText().toString();

        Toast.makeText(activity.getApplicationContext(),
                nomProjet, Toast.LENGTH_SHORT).show();

        //
        Intent i = new Intent(activity, Activity_Texte.class);
        Bundle b = new Bundle();
        b.putString("nom", nomProjet); //Your id
        i.putExtras(b); //Put your id to your next Intent
        activity.startActivity(i);

    }
}
