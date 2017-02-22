package capture_besoins.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import capture_besoins.plugins.texte_simple.Plugin_texte_simple;
import capture_besoins.projet.GestionProjet;

/**
 * Created by Karl on 22/02/2017.
 */

public class Activity_Texte extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On récupère le nom du projet sélectionné
        Bundle b = getIntent().getExtras();
        String nomProjet = ""; // or other values
        if (b != null)
            nomProjet = b.getString("nom");

        new Plugin_texte_simple(this, nomProjet);

    }
}