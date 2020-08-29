package com.pheimfarth.taubi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;


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

        openLocationInGoogleMaps(this.getText().toString());
    }

    private void openLocationInGoogleMaps(String address){
        String map = "http://maps.google.co.in/maps?q=" + taube.getLatitude() + "," + taube.getLongitude();
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        getContext().startActivity(i);
    }


}
