package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SnapView extends AppCompatActivity {
    ImageView snapImageView;
    TextView messageTextView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_view);

        snapImageView = findViewById(R.id.snapImageView);
        messageTextView = findViewById(R.id.messageTextView);
        messageTextView.setText(getIntent().getStringExtra("message"));

        DownloadImage task = new DownloadImage();
        Bitmap image;
        try{
            image = task.execute(getIntent().getStringExtra("imageUrl")).get();
            snapImageView.setImageBitmap(image);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(mAuth.getCurrentUser().getUid()).child("snaps")
                .child(getIntent().getStringExtra("snapKey")).removeValue();
//        FirebaseStorage.getInstance().getReference().child("images").child(getIntent().getStringExtra("imageName")).delete();
        super.onBackPressed();
    }

    class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                return BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}