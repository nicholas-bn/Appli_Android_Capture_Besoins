package capture_besoins.projet;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import capture_besoins.main.R;

/**
 * Created by Karl on 21/02/2017.
 */

public class GestionProjet {

    // Bouton pour créer un projet
    Button btn_creerProjet;

    public GestionProjet(AppCompatActivity activity) {

        // TODO Chercher dans les fichiers

        // On récupére le bouton pour créer un projet
        btn_creerProjet = (Button) activity.findViewById(R.id.btn_createProjet);

        // On y ajoute un listener
        btn_creerProjet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
