package com.pheimfarth.taubi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "Login Activity";
    private FirebaseAuth mAuth;
    private EditText fieldEmail;
    private EditText fieldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        fieldEmail = findViewById(R.id.fieldEmail);
        fieldPassword = findViewById(R.id.fieldPassword);

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void goToSignIn(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    private void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void logIn(View view){
        if (!fieldEmail.getText().toString().trim().isEmpty() && !fieldPassword.getText().toString().trim().isEmpty()){
            mAuth.signInWithEmailAndPassword(fieldEmail.getText().toString(), fieldPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                goToMain();
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Login hat nicht funktioniert.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}