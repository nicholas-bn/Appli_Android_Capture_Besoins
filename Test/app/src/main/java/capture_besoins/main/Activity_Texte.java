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

        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        if (b != null)
            value = b.getString("nom");

        System.out.println("+++++++++ ++++ " + value);
        new Plugin_texte_simple(this, value);

        // MET EN COMMENTAIRE CELUI QUE TU NUTILISES PAS SINON CA PLANTE

    }
}