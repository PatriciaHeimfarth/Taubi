package com.pheimfarth.taubi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;


public class TaubenButton extends androidx.appcompat.widget.AppCompatButton implements View.OnClickListener {

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
        builder.setTitle("Enter Text");


        final EditText input = new EditText(getContext());

        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // input.getText().toString();
                openLocationInGoogleMaps(getText().toString());

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
