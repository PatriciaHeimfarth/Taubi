package com.pheimfarth.taubi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.common.collect.Iterables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.text.InputType;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null && currentUser.isEmailVerified()){
            user = new User();

            final TableLayout tl = (TableLayout) findViewById(R.id.taubenTable);
            DatabaseReference myDbRef = database.getReference("Tauben");
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            getLocation();

            myDbRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    List<Taube> taubenSorted = sortTauben(dataSnapshot);

                    tl.removeAllViews();
                    for (Taube taube : taubenSorted) {
                        TableRow tr = new TableRow(getBaseContext());
                        tr.setBackgroundColor(Color.BLACK);
                        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                        Context c = MainActivity.this;
                        TaubenButton b = new TaubenButton(c);
                        b.setTaube(taube);
                        b.setText(b.getTaube().distanceBetweenTaubenAddressAndCurrentLocation(user.getLatitude(), user.getLongitude()));
                        tr.addView(b);
                        b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));

                        if(b.getTaube().getHelper()){
                            b.setText("Wird übernommen!");
                        }

                        tl.addView(tr);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("TAG", "Failed to read value.", error.toException());
                }
            });
        }
        else{

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addTaube(View view) {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Taube hinzufügen");


        final EditText input = new MaterialAutoCompleteTextView(this);
        input.setHint("Beschreibe die Taube und ihren Standort möglichst genau");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);
        builder.setPositiveButton("Taube hinzufügen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // input.getText().toString();
                if (ActivityCompat.checkSelfPermission(getBaseContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationProviderClient.getLastLocation().
                        addOnCompleteListener(new OnCompleteListener<Location>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<Location> task) {
                                                      Location location = task.getResult();

                                                      if (location != null) {
                                                          try {
                                                              Geocoder geocoder = new Geocoder(MainActivity.this,
                                                                      Locale.getDefault());
                                                              List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                                              Log.i("Location", String.valueOf(addresses.get(0).getLatitude() + "  " + addresses.get(0).getLongitude()));
                                                              Taube taube = new Taube(String.valueOf(new Date().getTime()), String.valueOf(addresses.get(0).getLatitude()), String.valueOf(addresses.get(0).getLongitude()), false, input.getText().toString());

                                                              DatabaseReference myRef = database.getReference("Tauben/" + taube.getId());
                                                              HashMap map = new HashMap();
                                                              map.put("Description", taube.getDescription());
                                                              map.put("Latitude", taube.getLatitude());
                                                              map.put("Longitude", taube.getLongitude());
                                                              map.put("Helper", taube.getHelper());

                                                              myRef.updateChildren(map);

                                                          } catch (IOException e) {
                                                              e.printStackTrace();
                                                          }
                                                      }
                                                  }
                                              }
                        );
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

    private List<Taube> sortTauben(DataSnapshot tauben){

        ArrayList<Taube> taubenList = new ArrayList();
        for (DataSnapshot taube : tauben.getChildren()) {

            taubenList.add(new Taube(taube.getKey(), Iterables.get(taube.getChildren(), 2).getValue().toString(),
                    Iterables.get(taube.getChildren(), 3).getValue().toString(),
                    Boolean.valueOf(Iterables.get(taube.getChildren(), 1).getValue().toString()), Iterables.get(taube.getChildren(), 0).getValue().toString()));
        }

        Collections.sort(taubenList, new Comparator<Taube>() {
            @Override
            public int compare(Taube o1, Taube o2) {
                return o1.distanceBetweenTaubenAddressAndCurrentLocation(user.getLatitude(), user.getLongitude()).
                        compareTo(o2.distanceBetweenTaubenAddressAndCurrentLocation(user.getLatitude(), user.getLongitude()));
            }
        });

        return taubenList;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, 44);
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();

                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this,
                                Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Log.i("Location", String.valueOf(addresses.get(0).getLatitude() + "  " + addresses.get(0).getLongitude()));
                        user.setLatitude(addresses.get(0).getLatitude());
                        user.setLongitude(addresses.get(0).getLongitude());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        );
    }
}