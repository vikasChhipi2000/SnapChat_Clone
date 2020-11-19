package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email;
    EditText password;
    String tag = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.paswordEditText);
        Log.d(tag, "on create called");

        if(mAuth.getCurrentUser() != null){
            // already login
            logIn();
        }
    }

    public void nextClicked(View view){
        // first try to login if that not pssible then signin the user
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(tag, "signInWithEmail:success");
                            logIn();
                        } else {
                            // If sign in fails
                            Log.d(tag, "signInWithEmail:failed");
                            signup();
                        }
                    }
                });
    }

    public void signup(){
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(tag, "createUserWithEmailAndPassword:success");
                            FirebaseDatabase
                                    .getInstance()
                                    .getReference()
                                    .child("users")
                                    .child(task.getResult().getUser().getUid()).child("email").setValue(email.getText().toString());
                            logIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(tag, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void logIn(){
        // go to next activity
        Log.d(tag,"login called");
        Intent intent = new Intent(this,ChatBox.class);
        startActivity(intent);
    }
}