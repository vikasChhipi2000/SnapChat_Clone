package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class ChatBox extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ListView snapListView;
    ArrayList<String> emails = new ArrayList<>();
    ArrayList<DataSnapshot> snaps = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        snapListView = findViewById(R.id.snapListView);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,emails);
        snapListView.setAdapter(arrayAdapter);

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                emails.add(snapshot.child("from").getValue().toString());
                snaps.add(snapshot);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                for(int i =0;i<snaps.size();i++){
//                    if(snaps.get(i).getKey() == snapshot.getKey()){
//                        snaps.remove(i);
//                        emails.remove(i);
//                        arrayAdapter.notifyDataSetChanged();
//                        break;
//                    }
//                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        snapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataSnapshot snap = snaps.get(position);
                Intent intent = new Intent(ChatBox.this,SnapView.class);
                intent.putExtra("message",snap.child("message").getValue().toString());
                intent.putExtra("imageName",snap.child("imageName").getValue().toString());
                intent.putExtra("imageUrl",snap.child("imageUrl").getValue().toString());
                intent.putExtra("snapKey",snap.getKey());
                startActivity(intent);
                snaps.remove(position);
                emails.remove(position);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.simplemenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.createSnapOption){
            Log.d("ChatBox"," create snap called");
            Intent intent = new Intent(this,ImageSelection.class);
            startActivity(intent);
        }else {
            Log.d("ChatBox"," signing out.");
            mAuth.signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mAuth.signOut();
        finish();
        super.onBackPressed();
    }
}