package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ImageSelection extends AppCompatActivity {

    ImageView imageView;
    EditText noteEditText;
    Uri imageUri;
    String imageName = UUID.randomUUID()+".jpg";
    String downloadUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);

        imageView = findViewById(R.id.imageView);
        noteEditText = findViewById(R.id.noteEditText);
    }

    public void openGallery(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1){
            imageUri =data.getData();
            imageView.setImageURI(imageUri);
        }
    }
    public void nextClicked(View view){
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();


        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images").child(imageName);
        final UploadTask uploadTask = ref.putBytes(data);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUri = task.getResult().toString();
                    Toast.makeText(ImageSelection.this,"sucessful! "+downloadUri,Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ImageSelection.this,ChoosePerson.class);
                    intent.putExtra("name",imageName);
                    intent.putExtra("url", downloadUri);
                    intent.putExtra("message",noteEditText.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(ImageSelection.this, "error on upload the document",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}