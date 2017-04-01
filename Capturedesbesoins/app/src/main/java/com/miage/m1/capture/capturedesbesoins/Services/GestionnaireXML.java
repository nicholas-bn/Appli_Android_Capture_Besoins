package com.miage.m1.capture.capturedesbesoins.Services;

import android.app.Activity;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Created by Karl on 01/04/2017.
 */

public class GestionnaireXML {

    public static String createXML(String nomProjet) throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        xmlSerializer.setOutput(writer);

        // Début du document XML
        xmlSerializer.startDocument("UTF-8", true);

        // open tag: <projet>
        xmlSerializer.startTag("", BalisesXML.PROJET.tag);
        xmlSerializer.attribute("", BalisesXML.NOM.tag, nomProjet);

        // open tag: <description>
        xmlSerializer.startTag("", BalisesXML.DESCRIPTION.tag);
        xmlSerializer.text("Pas de description...");
        // close tag: </description>
        xmlSerializer.endTag("", BalisesXML.DESCRIPTION.tag);

        // close tag: </projet>
        xmlSerializer.endTag("", BalisesXML.PROJET.tag);

        // Fin du document XML
        xmlSerializer.endDocument();

        return writer.toString();

    }

    public static String getDescriptionDuProjet(Activity activity, String nomProjet) {

        String description = "g";

        try {
            // Racine de l'application
            File racineApp = activity.getExternalFilesDir(null);

            // Chemin du Dossier à créer
            String cheminDossier = GestionnaireFichiers.getCheminFichierXML(activity, nomProjet);

            // Ouverture du fichier
            InputStream inputStream = new FileInputStream(cheminDossier);

            XmlPullParserFactory factory = null;
            XmlPullParser parser = null;

            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();

            String texte = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase(BalisesXML.DESCRIPTION.tag)) {
                            // create a new instance of employee
                            Log.e("DESCRIPTION", tagname);
                        }
                        break;

                    case XmlPullParser.TEXT:
                        Log.e("DESCRIPTION TEXTE", parser.getText());
                        texte = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase(BalisesXML.DESCRIPTION.tag)) {
                            // add employee object to list
                            Log.e("DESCRIPTION FIN", tagname);
                            description = texte;
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return description;
    }

}