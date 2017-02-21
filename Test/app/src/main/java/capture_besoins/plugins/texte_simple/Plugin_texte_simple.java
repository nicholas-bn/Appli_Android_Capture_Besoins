package capture_besoins.plugins.texte_simple;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import capture_besoins.main.R;



public class Plugin_texte_simple {
    // Activité principale
    Activity activity;

    // Boutton pour sauvegarder le fichier
    Button save_Button;

    // Boutton pour sauvegarder le fichier
    Button load_Button;

    // Texte du fichier
    AutoCompleteTextView contenuTexte;

    // Variable temporaire du nom/id utilisateur
    final String varTempoNomUser = "userTest";

    // Nom temporaire du projet où on ajout un fichier texte
    final String varTempoNomProjet = "projetTest";

    // Dossier des fichiers textes
    private File dossierTexte;

    // On récupére le dossier où l'on se situe
    private File racineApp;



    public Plugin_texte_simple(Activity activity) {

        // On récupére le dossier où l'on se situe
        racineApp = activity.getFilesDir();
        System.out.println("Racine de l'application : " + racineApp.toString());

        // Dossier contenant les fichiers textes
        dossierTexte = new File(racineApp.getAbsolutePath() + File.separator + varTempoNomUser + File.separator + varTempoNomProjet + File.separator + "Text");
        System.out.println("Dossier Texte : " + dossierTexte);

        // On récupère l'activity
        this.activity = activity;

        // On récupére le button de sauvegarde
        save_Button = (Button) activity.findViewById(R.id.button_save);

        // On y ajoute un listener
        save_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choix_nom_du_Fichier(v);
            }
        });

        // On récupére le bouton de chargement et d'edit de fichier texte
        load_Button = (Button) activity.findViewById(R.id.button_load);

        // On y ajoute un listener
        load_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouverture_fichier_texte(v);
            }
        });

        // On récupére et affiche le contenu Texte
        contenuTexte = (AutoCompleteTextView) activity.findViewById(R.id.autoCompleteTextView_contenuFichier);
        System.out.println("Contenu Texte : " + contenuTexte.getText().toString());
    }



    // Lorsqu'on appuit sur le bouton LOAD
    private void ouverture_fichier_texte(View v) {

        // Dialogue pour choisir le nom du fichier à ouvrir
        final AlertDialog.Builder building = new AlertDialog.Builder(v.getContext());

        // On récupére tous les fichiers texte déja créés précédemment
        File[] listeFichierTexte = dossierTexte.listFiles();

        // On vérifie si il y a des fichiers textes ou non
        if(listeFichierTexte.length == 0 ){

            building.setMessage("Aucun fichier texte à ouvrir dans ce projet.");

        } else if (listeFichierTexte.length > 0) {

            // Titre
            building.setTitle("Choisissez un fichier : ");

            // On créé la liste des noms de fichiers
            ArrayList<String> listNomFichier = new ArrayList<String>();

            // On y ajoute les noms de fichiers présent
            for(File f : listeFichierTexte){
                listNomFichier.add(f.getName());
            }

            // On ne se préoccupe pas de déclencher un event quand l'user coche une case
            building.setSingleChoiceItems(listNomFichier.toArray(new CharSequence[listNomFichier.size()]), 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            // Définir le comportement du bouton "OK"
            building.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // On récupére les vues de l'alertdialog
                    ListView lw = ((AlertDialog)dialog).getListView();

                    // On récupére la case coché
                    String checkedItem = (String) lw.getAdapter().getItem(lw.getCheckedItemPosition());
                    charger_fichier_texte(checkedItem);
                }
            });

            // Définir le comportement du bouton "Annuler"
            building.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Si il appuie sur Annuler on fait rien
                    return;
                }
            });

        }

        // On le créé et on l'affiche
        building.create();
        building.show();
    }

    private void charger_fichier_texte(String nomFichier){

        // On construit l'adresse du fichier à charger
        File fichierACharger = new File(dossierTexte.getAbsolutePath() + File.separator + nomFichier);
        System.out.println("fichierACharger : " + fichierACharger);

        // On créé un StringBuilder pour gagner de la perf
        StringBuilder sb = new StringBuilder();

        try {
            // On bufferise le contenu du fichier
            String line = null;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fichierACharger));

            // On lit ligne par ligne
            while ((line = bufferedReader.readLine()) != null)
            {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(sb.toString());
        contenuTexte.setText(sb.toString());

    }

    // Lorsqu'on appuit sur le bouton SAVE
    private void choix_nom_du_Fichier(View v) {

        // Dialogue pour choisir le nom du fichier à sauvegarder
        AlertDialog.Builder building = new AlertDialog.Builder(v.getContext());

        // AutoCompleteTextView où l'on peut écrire le nom du fichier
        final AutoCompleteTextView myAutoCompleteChoixNomFichier = new AutoCompleteTextView(building.getContext());
        myAutoCompleteChoixNomFichier.setHint("Nom du fichier");
        building.setView(myAutoCompleteChoixNomFichier);

        // Titre
        building.setTitle("Choisir le nom du fichier :");

        // Définir le comportement du bouton "OK"
        building.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            // On envoie le nom du fichier fourni sous le format String
            sauvegarde_Du_Fichier(myAutoCompleteChoixNomFichier.getText().toString());
            }
        });

        // Définir le comportement du bouton "Annuler"
        building.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            // Si il appuie sur Annuler on fait rien
            return;
            }
        });

        // On le créé et on l'affiche
        building.create();
        building.show();
    }



    // Sauvegarde du fichier
    private void sauvegarde_Du_Fichier(String nomFichier) {

        // On vérifie si on nous a donné une extension, sinon on la rajoute
        Pattern p = Pattern.compile("^.*\\.[^\\\\]+$");
        Matcher m = p.matcher(nomFichier);
        if (!m.matches()) {
            System.err.println("Nom du fichier texte (" + nomFichier + ") donné sans extenxion, on rajoute \".txt\".");
            nomFichier += ".txt";
        }

        // On affiche le nom du fichier choisi
        System.out.println("Nom du Fichier : " + nomFichier);

        // Si il existe pas on le créé
        if (!dossierTexte.exists())
            dossierTexte.mkdirs();

        // On créé un file pour le fichier à créé
        File fichierTexte = new File(dossierTexte + File.separator + nomFichier);

        // On écrit le fichier
        try {
            PrintWriter ecritureDuFichierTexte = new PrintWriter(fichierTexte);
            ecritureDuFichierTexte.println(contenuTexte.getText().toString());
            ecritureDuFichierTexte.close();
        } catch (FileNotFoundException e) {
            System.err.println("Erreur lors de l'écriture du fichier : \"" + fichierTexte + "\"");
            e.printStackTrace();
        }










        // TODO Récupérer l'id google user et le nom du projet pour faire data/.../files/<user>/<projet>/...
        // TODO Vérifier au lancement de l'application si il y a une carte SD dispo et définir un booléen

        // On vérifie si le stockage externe est disponible ou non (carte SD)
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

        } else { // Si on dispose uniquement du stockage interne

        }

/*        // On informe l'utilisateur si la sauvegarde a été un échec ou non
        AlertDialog.Builder retourSauvegardeSuccesOuEchec = new AlertDialog.Builder(contenuTexte.getContext());
        retourSauvegardeSuccesOuEchec.setMessage("Fichier enregistré avec succés !");

        // On le créé et on l'affiche
        retourSauvegardeSuccesOuEchec.create();
        retourSauvegardeSuccesOuEchec.show();*/

    }
}
