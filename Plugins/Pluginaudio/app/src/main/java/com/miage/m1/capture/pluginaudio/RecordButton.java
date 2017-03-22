package com.miage.m1.capture.pluginaudio;

import android.view.View;
import android.widget.Button;

/**
 * Created by Karl on 21/03/2017.
 */

public class RecordButton extends Button implements View.OnClickListener {

    // Plugin pour le son
    private MainActivity plugin_son;

    // Boolean qui indique si on est entrain d'enregistrer
    boolean isRecording = true;


    public RecordButton(MainActivity plugin_son) {
        super(plugin_son);
        this.plugin_son = plugin_son;
        setText("Start recording");
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // On dit au plugin son de gérer l'enregistrement
        plugin_son.onRecord(isRecording);

        // Changement du texte du bouton
        if (isRecording) {
            setText("Stop recording");
        } else {
            setText("Start recording");
        }
        isRecording = !isRecording;
    }
}
