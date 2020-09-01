package com.pheimfarth.taubi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class TaubenButton extends com.google.android.material.button.MaterialButton implements View.OnClickListener {

    FirebaseDatabase database;

    public Taube getTaube() {
        return taube;
    }

    public void setTaube(Taube taube) {
        this.taube = taube;
    }

    private Taube taube;

    public TaubenButton(Context context) {
        super(context);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (taube.getHelper()){
            builder.setTitle("Wird übernommen");
        }
        else{
            builder.setTitle("Taube übernehmen");
            builder.setPositiveButton("Übernehme ich", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Tauben/" + taube.getId());

                    Map<String, Object> helperUpdate = new HashMap<>();
                    helperUpdate.put("Helper", "true");

                    myRef.updateChildren(helperUpdate);
                }
            });

        }


        builder.setNeutralButton("Auf Google Maps anzeigen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                openLocationInGoogleMaps(getText().toString());

            }
        });

        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void openLocationInGoogleMaps(String address){
        String map = "http://maps.google.co.in/maps?q=" + taube.getLatitude() + "," + taube.getLongitude();
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        getContext().startActivity(i);
    }


}
