package com.miage.m1.capture.capturedesbesoins.services;

import android.app.Activity;
import android.util.Xml;

import com.miage.m1.capture.capturedesbesoins.xml.BalisesXML;
import com.miage.m1.capture.capturedesbesoins.xml.Fichier;
import com.miage.m1.capture.capturedesbesoins.xml.Projet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by Karl on 01/04/2017.
 */

public class GestionnaireXML {

    // Création du XML à la création d'un Projet
    public static String createXML(String nomProjet) throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);

        // Début du document XML
        xmlSerializer.startDocument("UTF-8", true);

        // <projet nom = '..'>
        xmlSerializer.startTag("", BalisesXML.PROJET.xml);
        xmlSerializer.attribute("", BalisesXML.NOM.xml, nomProjet);

        //      <description>
        xmlSerializer.startTag("", BalisesXML.DESCRIPTION.xml);
        xmlSerializer.text("Pas de description...");
        //      </description>
        xmlSerializer.endTag("", BalisesXML.DESCRIPTION.xml);

        //      <fichiers>
        xmlSerializer.startTag("", BalisesXML.FICHIERS.xml);
        //      </fichiers>
        xmlSerializer.endTag("", BalisesXML.FICHIERS.xml);

        // </projet>
        xmlSerializer.endTag("", BalisesXML.PROJET.xml);

        // Fin du document XML
        xmlSerializer.endDocument();

        return writer.toString();
    }

    // MAJ d'un XML
    public static String majXML(Projet projet) throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);

        // Début du document XML
        xmlSerializer.startDocument("UTF-8", true);

        // <projet nom = '..'>
        xmlSerializer.startTag("", BalisesXML.PROJET.xml);
        xmlSerializer.attribute("", BalisesXML.NOM.xml, projet.getNom());

        //      <description>
        xmlSerializer.startTag("", BalisesXML.DESCRIPTION.xml);
        xmlSerializer.text(projet.getDescription());
        //      </description>
        xmlSerializer.endTag("", BalisesXML.DESCRIPTION.xml);

        //      <fichiers>
        xmlSerializer.startTag("", BalisesXML.FICHIERS.xml);

        // --- Traitement des Fichiers :

        // Liste des Fichiers du Projet
        ArrayList<Fichier> listeFichiers = projet.getListeFichiers();

        // Pour chaque Fichier
        for(Fichier fichier : listeFichiers){
            //           <fichier>
            xmlSerializer.startTag("", BalisesXML.FICHIER.xml);

            //                  <nom>
            xmlSerializer.startTag("", BalisesXML.NOM.xml);
            xmlSerializer.text(fichier.getNom());

            //                  </nom>
            xmlSerializer.endTag("", BalisesXML.NOM.xml);

            //                  <type>
            xmlSerializer.startTag("", BalisesXML.TYPE.xml);
            xmlSerializer.text(fichier.getType());

            //                  </type>
            xmlSerializer.endTag("", BalisesXML.TYPE.xml);

            //                  <tags>
            xmlSerializer.startTag("", BalisesXML.TAGS.xml);
            xmlSerializer.text(fichier.getListeTagsString());

            //                  </tags>
            xmlSerializer.endTag("", BalisesXML.TAGS.xml);

            //           </fichier>
            xmlSerializer.endTag("", BalisesXML.FICHIER.xml);
        }

        //      </fichiers>
        xmlSerializer.endTag("", BalisesXML.FICHIERS.xml);

        // </projet>
        xmlSerializer.endTag("", BalisesXML.PROJET.xml);

        // Fin du document XML
        xmlSerializer.endDocument();

        return writer.toString();
    }

    // Lecture du fichier XML et insertation des données dans une classe Projet
    public static Projet getDetailsProjet(Activity activity, String nomProjet) {

        // Création d'une classe Projet qui va contenir toutes les informations du XML
        Projet projet = new Projet(nomProjet);

        String description = "";
        try {
            // Chemin du fichier XML
            String cheminXML = GestionnaireFichiers.getCheminFichierXML(activity, nomProjet);

            // Lecture du fichier
            InputStream inputStream = new FileInputStream(cheminXML);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            // Parser qui va nous permettre de lire les informations du XML
            XmlPullParser parser = factory.newPullParser();

            // On spécifie le fichier à étudier
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();

            // Tampon pour sauvegarder le texte du Tag en cours
            String texte = "";

            // Tampon pour les Fichiers
            Fichier fichier = null;

            // On parcourt tous le XML jusqu'à arriver à la fin du document
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                // Pour chaque type d'event (START_TAG, END_TAG, TEXT,...)
                switch (eventType) {

                    // Le début d'un Tag
                    case XmlPullParser.START_TAG:
                        // Si c'est <fichier>
                        if (tagName.equalsIgnoreCase(BalisesXML.FICHIER.xml)) {
                            // On crée un nouveau Fichier
                            fichier = new Fichier();
                        }
                        break;

                    // Le texte d'un Tag
                    case XmlPullParser.TEXT:
                        texte = parser.getText();
                        break;

                    // La fin d'un Tag
                    case XmlPullParser.END_TAG:
                        // Si c'est </description>
                        if (tagName.equalsIgnoreCase(BalisesXML.DESCRIPTION.xml)) {
                            // On spécifie la description du Projet
                            projet.setDescription(texte);
                        }

                        // Si c'est </nom>
                        if (tagName.equalsIgnoreCase(BalisesXML.NOM.xml)) {
                            // On spécifie le nom du Fichier
                            fichier.setNom(texte);
                        }

                        // Si c'est </type>
                        if (tagName.equalsIgnoreCase(BalisesXML.TYPE.xml)) {
                            // On spécifie le type du Fichier
                            fichier.setType(texte);
                        }

                        // Si c'est </tags>
                        if (tagName.equalsIgnoreCase(BalisesXML.TAGS.xml)) {
                            // On spécifie le nom du Fichier
                            fichier.setTags(texte);
                        }

                        // Si c'est </fichier>
                        if (tagName.equalsIgnoreCase(BalisesXML.FICHIER.xml)) {
                            // On ajoute le Fichier au Projet
                            projet.addFichier(fichier);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }


        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return projet;
    }

}