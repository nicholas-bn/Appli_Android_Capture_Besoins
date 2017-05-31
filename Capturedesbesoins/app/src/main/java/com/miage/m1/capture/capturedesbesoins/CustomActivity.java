package com.miage.m1.capture.capturedesbesoins;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v7.app.AlertDialog;

import com.miage.m1.capture.capturedesbesoins.services.LiaisonDrive;

/**
 * Created by Karl on 22/03/2017.
 */

public class CustomActivity extends AppCompatActivity {
    int onStartCount = 0;

    private LiaisonDrive liaisonDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onStartCount = 1;
        if (savedInstanceState == null) {// 1st time        {
            this.overridePendingTransition(R.transition.slide_in_left,
                    R.transition.slide_out_left);

        }

        // already created so reverse animation
        else if (getLocalClassName().equals("MainActivity")) {
            onStartCount = 2;
        }

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.transition.slide_in_right,
                    R.transition.slide_out_right);

        } else if (onStartCount == 1) {
            onStartCount++;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        // Liaison avec le Drive
        liaisonDrive = new LiaisonDrive(this, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.a_propos) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("À propos");
            builder.setMessage( "\nProjet d'année de Master 1 MIAGE encadré par Monsieur CRESCENZO.\n\n" +
                                "Membres du projet :\n" +
                                " - Barnini Nicholas,\n" +
                                " - Ferrero Karl,\n" +
                                " - Valverde Thomas,\n" +
                                " - Deï Léonard.\n");

            builder.setPositiveButton("Oui.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            builder.create();
            builder.show();

            /*
            Projet d'année de Master 1 MIAGE encadré par Monsieur CRESCENZO.
            Membres du projet :
             */

            return true;
        }

        if (id == R.id.drive) {
            // On déconnecte
            if (item.getTitle().equals("Déconnexion")) {
                liaisonDrive.disconnect();
                item.setTitle("Connexion");
            } else if (item.getTitle().equals("Connexion")) {
                liaisonDrive.connect();
                //item.setTitle("Déconnexion");
            }

            return true;
        }

        if(id == R.id.pushToDrive) {
            // Si l'utilisateur n'est pas connecté
            if(!liaisonDrive.isConnected()){
                Log.i("PUSH TO DRIVE", "Tentative d'envoi sur le drive sans connexion au compte Google");
                Snackbar.make(this.getCurrentFocus(), "Vous devez vous connecter sur votre compte Google pour utiliser votre Drive.", Snackbar.LENGTH_LONG).show();
            } else {
                liaisonDrive.askedToPushToDrive();
            }
        }

        if(id == R.id.pullFromDrive) {
            // Si l'utilisateur n'est pas connecté
            if(!liaisonDrive.isConnected()){
                Log.i("PULL FROM DRIVE", "Tentative de récupérer du drive sans connexion au compte Google");
                Snackbar.make(this.findViewById(R.id.listeProjets), "Vous devez vous connecter sur votre compte Google pour utiliser votre Drive.", Snackbar.LENGTH_LONG).show();
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Attention");
                builder.setMessage("Êtes-vous sûr de vouloir récupérer du Drive (le contenu local sera écrasé) ?");

                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        liaisonDrive.askedToPullFromDrive();
                    }
                });
                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                builder.create();
                builder.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
