package capture_besoins.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import capture_besoins.plugins.texte_simple.Plugin_texte_simple;

public class MainActivity extends AppCompatActivity {

    Plugin_texte_simple pluginTexteSimple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pluginTexteSimple = new Plugin_texte_simple(this);
    }
}
