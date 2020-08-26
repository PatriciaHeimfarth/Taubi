package com.pheimfarth.taubi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static androidx.appcompat.app.AlertDialog.*;

public class TaubenButton extends androidx.appcompat.widget.AppCompatButton implements View.OnClickListener {
    public TaubenButton(Context context) {
        super(context);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        openLocationInGoogleMaps(this.getText().toString());
    }

    private void openLocationInGoogleMaps(String address){
        String map = "http://maps.google.co.in/maps?q=" + address;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        getContext().startActivity(i);
    }


}
