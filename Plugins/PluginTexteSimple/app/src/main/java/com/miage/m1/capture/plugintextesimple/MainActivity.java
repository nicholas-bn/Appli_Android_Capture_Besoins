package com.miage.m1.capture.plugintextesimple;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Nom du Projet en cours
    private String nomProjet;

    // Chemin du Projet
    private String cheminProjet;

    // Dossier des fichiers textes
    private File dossierTexte;

    // Si c'est un fichier load on a pas besoin de
    // fournir un nom de fichier à la sauvegarde
    private boolean hasBeenLoaded = false;

    // En liaison avec "hasBeenLoaded" pour enregistrer le nom du fichier load
    private String nameUsed = "";

    // Bouton '+' pour créer un nouveau document Texte
    private FloatingActionButton btn_newTexte;

    // Bouton pour sauvegarder un Texte
    private Button btn_save;

    // Bouton pour charger un Texte
    private Button btn_load;

    // Zone de texte
    private EditText zoneDeTexte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout associé à l'activité
        setContentView(R.layout.activity_main);

        // On récupère le nom du projet sélectionné
        Bundle b = getIntent().getExtras();
        if (b != null) {
            nomProjet = b.getString("nom");
            cheminProjet = b.getString("cheminProjet");
        }



        // On change le label de l'Activity
        setTitle(nomProjet + " - Texte simple");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Récupération du bouton pour créer un nouveau document Texte
        FloatingActionButton btn_nexTexte = (FloatingActionButton) findViewById(R.id.newTexte);
        btn_nexTexte.setOnClickListener(this);

        // On récupére le button de sauvegarde + listener
        btn_save = (Button) findViewById(R.id.button_save);
        btn_save.setOnClickListener(this);

        // On récupére le bouton de chargement et d'edit de fichier texte
        btn_load = (Button) findViewById(R.id.button_load);
        btn_load.setOnClickListener(this);

        // On récupére la zone de Texte
        zoneDeTexte = (EditText) findViewById(R.id.texte);

        // Dossier contenant les fichiers textes
        dossierTexte = new File(cheminProjet + File.separator + "Text");
        Log.e("AAAAAAAAA ", "Dossier Texte : " + dossierTexte);

        // Si il existe pas on le créé
        if (!dossierTexte.exists())
            Log.e("AAAAAAAAA  ccc ", "Dossier Texte : " +  dossierTexte.mkdirs());
            //dossierTexte.mkdirs();
    }

    private void clean_and_new(View v) {

        // Si texte vide
        if (zoneDeTexte.getText().toString().equals("")) {
            hasBeenLoaded = false;
            nameUsed = "";
            // Si il y a encore du texte on demande confirmation
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

            builder.setTitle("Attention");
            builder.setMessage("Êtes-vous sûr de vouloir créer un nouveau Texte ?");

            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    hasBeenLoaded = false;
                    nameUsed = "";
                    zoneDeTexte.setText("");
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

    // Lorsqu'on appuit sur le bouton LOAD
    private void ouverture_fichier_texte(View v) {

        // Dialogue pour choisir le nom du fichier à ouvrir
        final AlertDialog.Builder building = new AlertDialog.Builder(v.getContext());

        // On récupére tous les fichiers texte déja créés précédemment
        File[] listeFichierTexte = dossierTexte.listFiles();

        // On vérifie si il y a des fichiers textes ou non
        if (listeFichierTexte.length == 0) {

            building.setMessage("Aucun fichier texte à ouvrir dans ce projet.");

        } else if (listeFichierTexte.length > 0) {

            // Titre
            building.setTitle("Choisissez un fichier : ");

            // On créé la liste des noms de fichiers
            ArrayList<String> listNomFichier = new ArrayList<String>();

            // On y ajoute les noms de fichiers présent
            for (File f : listeFichierTexte) {
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
                    ListView lw = ((AlertDialog) dialog).getListView();

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

    private void charger_fichier_texte(String nomFichier) {

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
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // On rempli le texte
        System.out.println(sb.toString());
        zoneDeTexte.setText(sb.toString());

        // On retient que le fichier a été chargé et son nom
        hasBeenLoaded = true;
        nameUsed = nomFichier;

    }

    // Lorsqu'on appuit sur le bouton SAVE
    private void choix_nom_du_Fichier(View v) {

        if (hasBeenLoaded) {
            sauvegarde_Du_Fichier(nameUsed);
        } else {
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

        // On créé un file pour le fichier à créé
        File fichierTexte = new File(dossierTexte + File.separator + nomFichier);

        // On écrit le fichier
        try {
            PrintWriter ecritureDuFichierTexte = new PrintWriter(fichierTexte);
            ecritureDuFichierTexte.println(zoneDeTexte.getText().toString());
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
        Toast.makeText(getApplicationContext(), "Fichier '" + nomFichier + "' sauvegardé !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        // Clique sur le bouton '+' pour créer un nouveau document Texte
        if (view.getId() == R.id.newTexte) {
            Snackbar.make(view, "Nouveau document !", Snackbar.LENGTH_LONG).show();
            clean_and_new(view);
        }

        // Clique sur le bouton pour sauvegarde un Texte
        if (view.getId() == R.id.button_save) {
            Snackbar.make(view, "Sauvegarde du document", Snackbar.LENGTH_LONG).show();

            choix_nom_du_Fichier(view);
        }

        // Clique sur le bouton pour charger un Texte
        if (view.getId() == R.id.button_load) {
            Snackbar.make(view, "Chargement du document", Snackbar.LENGTH_LONG).show();
            Log.e("AAAAAAAA ", dossierTexte.getAbsolutePath());
            ouverture_fichier_texte(view);
        }

    }
}
