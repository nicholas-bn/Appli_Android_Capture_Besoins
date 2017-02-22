package capture_besoins.services;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Karl on 22/02/2017.
 */

public class Affichage {

    public static void generateToast(Activity activity, String texte) {
        // On génère le Toast
        Toast.makeText(activity.getApplicationContext(), texte, Toast.LENGTH_SHORT).show();
    }
}
