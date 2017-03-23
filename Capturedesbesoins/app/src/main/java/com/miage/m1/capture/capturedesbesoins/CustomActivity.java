package com.miage.m1.capture.capturedesbesoins;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miage.m1.capture.capturedesbesoins.Services.LiaisonDrive;

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

        if (liaisonDrive.isConnected()) {
            MenuItem item = menu.findItem(R.id.drive);

            item.setTitle("Déconnexion");
        }
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

        return super.onOptionsItemSelected(item);
    }
}
