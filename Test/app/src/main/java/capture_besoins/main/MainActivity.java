package capture_besoins.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import capture_besoins.plugins.texte_simple.Plugin_texte_simple;
import capture_besoins.projet.GestionProjets;

public class MainActivity extends AppCompatActivity {

    Plugin_texte_simple pluginTexteSimple;

    GestionProjets gestionProjet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout associé à cette Activity
        setContentView(R.layout.activity_gestion_projets);

        gestionProjet = new GestionProjets(this);

    }
}
