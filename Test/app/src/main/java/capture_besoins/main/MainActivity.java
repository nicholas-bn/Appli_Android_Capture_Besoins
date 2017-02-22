package capture_besoins.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import capture_besoins.plugins.texte_simple.Plugin_texte_simple;
import capture_besoins.projet.GestionProjet;

public class MainActivity extends AppCompatActivity {

    Plugin_texte_simple pluginTexteSimple;

    GestionProjet gestionProjet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_projet);
        gestionProjet = new GestionProjet(this);

    }
}
