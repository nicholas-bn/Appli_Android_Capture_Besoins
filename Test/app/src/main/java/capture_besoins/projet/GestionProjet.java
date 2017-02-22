package capture_besoins.projet;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import capture_besoins.main.R;

/**
 * Created by Karl on 21/02/2017.
 */

public class GestionProjet implements View.OnClickListener {

    //Activité principale
    private Activity activity;

    //Bouton pour créer un projet
    private Button btn_creerProjet;

    // Liste des projets
    private ListView list_projets;

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
        list_projets = (ListView) activity.findViewById(R.id.listProjets);

        // On remplit la liste des projets
        remplirListeProjets();


    }

    private void remplirListeProjets() {
        // On récupère la liste des Projets déja créés
        List<File> listProjets = getProjets();

        List<String> listNomProjets = new ArrayList<>();

        for (File projet : listProjets) {
            listNomProjets.add(projet.getName());
        }

        //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
        ArrayAdapter<String> adapter;

        adapter = new ArrayAdapter<String>
                (activity, android.R.layout.simple_list_item_1, listNomProjets);

        list_projets.setAdapter(adapter);

    }

    private List<File> getProjets() {
        ArrayList<File> listProjets = new ArrayList<File>();

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
                listProjets.add(file);
            }

            System.out.println("---------------------" + file.getName());

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
}
