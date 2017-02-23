package capture_besoins.plugins.texte_word;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import capture_besoins.main.R;

/**
 * Created by Karl on 23/02/2017.
 */

public class Plugin_texte_word extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout associé à cette Activity
        setContentView(R.layout.activity_texte_word);

        // On récupère le nom du projet sélectionné
        Bundle b = getIntent().getExtras();
        String nomProjet = ""; // or other values
        if (b != null) {
            nomProjet = b.getString("nom");
        }

        File racineApp = getFilesDir();
        System.out.println("Racine de l'application : " + racineApp.toString());

        // Dossier contenant les fichiers textes
        File dossierTexte = new File(racineApp.getAbsolutePath() + File.separator + "userTest" + File.separator + nomProjet + File.separator + "Text");

        // Ouverture du docx



    }

    public void openDocument(String name) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        File file = new File(name);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(Uri.fromFile(file), "text/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype);
        }
        // custom message for the intent
        startActivity(Intent.createChooser(intent, "Choose an Application:"));
    }
}
