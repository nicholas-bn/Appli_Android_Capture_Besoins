package com.miage.m1.capture.capturedesbesoins;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nicho on 21/04/2017.
 */

public class GalerieAdapter extends ArrayAdapter<Ligne_Galerie> {

    public GalerieAdapter(Context context, List<Ligne_Galerie> listeGalerie) {
        super(context, 0, listeGalerie);

        Log.i("TAILLE    ", ""+getCount()  );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_galerie,parent, false);
        }

        GalerieViewHolder viewHolder = (GalerieViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new GalerieViewHolder();
            viewHolder.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);

        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        Ligne_Galerie ligne_galerie = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.pseudo.setText(ligne_galerie.getPseudo());
        viewHolder.text.setText(ligne_galerie.getText());
        viewHolder.avatar.setImageDrawable(new ColorDrawable(ligne_galerie.getColor()));

        Log.i("GalerieAdapter", position+ " " + ligne_galerie.getPseudo() );

        return convertView;
    }

    private class GalerieViewHolder{
        public TextView pseudo;
        public TextView text;
        public ImageView avatar;
    }
}
