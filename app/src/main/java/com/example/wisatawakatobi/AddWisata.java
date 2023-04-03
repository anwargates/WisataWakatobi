package com.example.wisatawakatobi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class AddWisata extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 123;
    EditText namaWisata, deskripsiWisata, lokasiWisata;
    ImageView imageUploadPreview;
    Button btnUpload;
    Uri filePath;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wisata);

        imageUploadPreview = findViewById(R.id.imageUploadPreview);
        namaWisata = findViewById(R.id.namaWisata);
        deskripsiWisata = findViewById(R.id.deskripsiWisata);
        lokasiWisata = findViewById(R.id.lokasiWisata);
        btnUpload = findViewById(R.id.uploadBtn);
        storageReference = FirebaseStorage.getInstance().getReference();
        btnUpload.setOnClickListener(v -> showFileChooser());


    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //imageView.setImageBitmap(bitmap); //if you want to show the image to the user before uploading
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            Toast.makeText(AddWisata.this, "Upload Success :" + result, Toast.LENGTH_LONG).show();
                            Log.d("UPLOAD SUCCESS", result.toString());

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    Glide.with(getApplicationContext()).load(imageUrl).into(imageUploadPreview);
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle unsuccessful uploads
                            // ...
                            Toast.makeText(AddWisata.this, e.toString(), Toast.LENGTH_LONG).show();
                            Log.d("UPLOAD FAILURE", e.toString());
                        }
                    });
        }
    }
}