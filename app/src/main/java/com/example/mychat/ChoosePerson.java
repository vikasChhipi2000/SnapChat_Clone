package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChoosePerson extends AppCompatActivity {
    ListView choosePersonListView;
    ArrayList<String> emails = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_person);
        Log.d("chooseperson"," choose person on create called");

        choosePersonListView = findViewById(R.id.choosePersonListView);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,emails);
        choosePersonListView.setAdapter(arrayAdapter);

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String email = snapshot.child("email").getValue().toString();
                    emails.add(email);
                    ids.add(snapshot.getKey());
                    Log.d("chooseperson"," email add called");
                    arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        choosePersonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> map = new HashMap<>();
                map.put("from" , FirebaseAuth.getInstance().getCurrentUser().getEmail());
                map.put("imageUrl",getIntent().getStringExtra("url"));
                map.put("imageName",getIntent().getStringExtra("name"));
                map.put("message",getIntent().getStringExtra("message"));
                FirebaseDatabase.getInstance().getReference().child("users").child(ids.get(position)).child("snaps").push().setValue(map);
            }
        });
    }
}