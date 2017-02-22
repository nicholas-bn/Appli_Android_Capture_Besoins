package capture_besoins.projet;

import android.app.Activity;
import android.util.Log;
import android.view.View;
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

    /** Activité principale */
    Activity activity;

    /** Bouton pour créer un projet */
    Button btn_creerProjet;

    /**  Liste des projets */
    ListView list_projets;

    //private static final String repName = "Capture_Besoins";


    public GestionProjet(Activity activity) {

        // On récupère l'activity
        this.activity = activity;

        // TODO Chercher dans les fichiers
        List<File> listProjets = getProjets();

        // On récupére le bouton pour créer un projet
        btn_creerProjet = (Button) activity.findViewById(R.id.btn_createProjet);

        // On y ajoute un listener
        btn_creerProjet.setOnClickListener(this);

        // On récupère le ListView des projets
        list_projets = (ListView) activity.findViewById(R.id.listProjets);

    }

    private List<File> getProjets() {
        ArrayList<File> projets = new ArrayList<File>();

        File racineApp = activity.getFilesDir();
        Log.i("MyApp","Racine de l'application : " + racineApp);

        // Dossier contenant les fichiers textes
        File dossierApp = new File(racineApp.getAbsolutePath() + File.separator);
        Log.i("MyApp","Dossier Texte : " + dossierApp);


        return projets;
    }

    @Override
    public void onClick(View v) {
        // TODO Créer nouveau projet

        // TODO 1 : AlertBox pour demander le nom

        // TODO 2 : Créer répertoire du projet

        // TODO 3 : Ouvrir projet créé
    }
}
