package capture_besoins.plugins.son;

import android.view.View;
import android.widget.Button;

/**
 * Created by Karl on 02/03/2017.
 */

public class PlayButton extends Button implements View.OnClickListener{

    // Plugin pour le son
    private Plugin_son plugin_son;

    // Boolean qui indique si le son est joué
    private boolean isPlaying = true;


    public PlayButton(Plugin_son plugin_son) {
        super(plugin_son);
        this.plugin_son = plugin_son;
        setText("Play");
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // On indique au plugin de gérer la lecture du son
        plugin_son.onPlay(isPlaying);

        // Changement du texte du bouton
        if (isPlaying) {
            setText("Stop");
        } else {
            setText("Start playing");
        }
        isPlaying = !isPlaying;
    }
}
