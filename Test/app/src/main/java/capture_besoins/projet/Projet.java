package capture_besoins.projet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import capture_besoins.main.R;
import capture_besoins.plugins.texte_simple.Plugin_texte_simple;
import capture_besoins.plugins.texte_word.Plugin_texte_word;

/**
 * Created by Karl on 22/02/2017.
 */

public class Projet extends AppCompatActivity implements View.OnClickListener {

    // Nom du projet courant
    private String nomProjet;

    // Bouton plugin Texte simple
    private Button btn_TexteSimple;

    // Bouton plugin Texte word
    private Button btn_TexteWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout associé à cette Activity
        setContentView(R.layout.activity_projet);

        // On récupère le nom du projet sélectionné
        Bundle b = getIntent().getExtras();
        nomProjet = "";
        if (b != null) {
            nomProjet = b.getString("nom");
        }

        // On change le label de l'Activity
        setTitle(nomProjet);

        // On récupére le bouton du plugin de Texte simple
        btn_TexteSimple = (Button) findViewById(R.id.btn_pluginTexteSimple);
        // Ajout de son listener
        btn_TexteSimple.setOnClickListener(this);

        // On récupére le bouton du plugin de Texte word
        btn_TexteWord = (Button) findViewById(R.id.btn_pluginTexteWord);
        // Ajout de son listener
        btn_TexteWord.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        // Intent pour switch entre les Activities
        Intent i = null;

        // Si on a cliqué sur le bouton "Texte simple"
        if (v.getId() == R.id.btn_pluginTexteSimple) {
            // Intent pour switch entre 2 activities
            i = new Intent(this, Plugin_texte_simple.class);
        }

        // Si on a cliqué sur le bouton "Word"
        if (v.getId() == R.id.btn_pluginTexteWord) {
            // Intent pour switch entre 2 activities
            i = new Intent(this, Plugin_texte_word.class);
        }

        // Bundle pour passer en paramètre le nom du projet
        Bundle b = new Bundle();
        b.putString("nom", nomProjet); // Nom du projet sélectionné
        i.putExtras(b);

        // On lance la deuxième activity
        startActivity(i);

    }
}
