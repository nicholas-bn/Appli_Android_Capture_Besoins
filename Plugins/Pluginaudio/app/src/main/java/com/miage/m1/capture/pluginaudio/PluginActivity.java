package com.miage.m1.capture.pluginaudio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Karl on 20/04/2017.
 */

public class PluginActivity extends AppCompatActivity {

    // Nom du projet
    public String nomProjet;

    // Chemin du projet (dossier)
    public String cheminProjet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // On récupère les informations :
        Bundle b = getIntent().getExtras();
        if (b != null) {
            // Récupération du nom du projet
            nomProjet = b.getString("nomProjet");

            // Récupération du chemin du projet
            cheminProjet = b.getString("cheminProjet");
        }

    }

    public String getDateHeure() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String dateHeure = date + "-" + month + "-" + year;
        dateHeure += "_" + hour + "-" + minute + "-" + second;

        return dateHeure;
    }

}
