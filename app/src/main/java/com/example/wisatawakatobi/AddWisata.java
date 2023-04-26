package com.example.wisatawakatobi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddWisata extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 123;
    String id = null;
    EditText namaWisata, deskripsiWisata, lokasiWisata, jumlahPengunjung, hargaWisata;
    ImageView imageUploadPreview;
    Button btnUpload, btnSubmit;
    AutoCompleteTextView kategoriWisata;
    Uri filePath;
    String imageUrl = null;

    String[] categories = {"Tourist Spot", "Tempat Makan", "Penginapan", "Tempat Ibadah"};

    DocumentReference documentReference;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    ProgressDialog progressDialog;
    MaterialCardView backButton;
    MaterialAlertDialogBuilder errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wisata);

        backButton = findViewById(R.id.backBtn);
        imageUploadPreview = findViewById(R.id.imageUploadPreview);
        namaWisata = findViewById(R.id.registerEmail);
        deskripsiWisata = findViewById(R.id.deskripsiWisata);
        lokasiWisata = findViewById(R.id.registerPassword);
        kategoriWisata = findViewById(R.id.kategoriWisata);
        jumlahPengunjung = findViewById(R.id.registerFullname);
        hargaWisata = findViewById(R.id.hargaWisata);
        btnUpload = findViewById(R.id.uploadBtn);
        btnSubmit = findViewById(R.id.submitBtn);

        storageReference = FirebaseStorage.getInstance().getReference();
        fStore = FirebaseFirestore.getInstance();

        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, categories);
        //Getting the instance of AutoCompleteTextView
        kategoriWisata = (AutoCompleteTextView) findViewById(R.id.kategoriWisata);
        kategoriWisata.setThreshold(1);//will start working from first character

        errorDialog = new MaterialAlertDialogBuilder(AddWisata.this);
        progressDialog = new ProgressDialog(AddWisata.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Menyimpan...");

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            namaWisata.setText(intent.getStringExtra("nama"));
            deskripsiWisata.setText(intent.getStringExtra("deskripsi"));
            kategoriWisata.setText(intent.getStringExtra("kategori"));
            lokasiWisata.setText(intent.getStringExtra("lokasi"));
            jumlahPengunjung.setText(intent.getStringExtra("jumlah"));
            hargaWisata.setText(intent.getStringExtra("harga"));
            imageUrl = intent.getStringExtra("foto");
            if (imageUrl != null) {
                Glide.with(getApplicationContext()).load(imageUrl).into(imageUploadPreview);
            }
        }

        kategoriWisata.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnUpload.setOnClickListener(v -> showFileChooser());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                Map<String, Object> wisataInfo = new HashMap<>();
                wisataInfo.put("nama", namaWisata.getText().toString());
                wisataInfo.put("deskripsi", deskripsiWisata.getText().toString());
                wisataInfo.put("lokasi", lokasiWisata.getText().toString());
                wisataInfo.put("kategori", kategoriWisata.getText().toString());
                wisataInfo.put("jumlah", jumlahPengunjung.getText().toString());
                wisataInfo.put("harga", hargaWisata.getText().toString());
                wisataInfo.put("foto", imageUrl);
                saveData(wisataInfo);
            }
        });
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
                                    imageUrl = uri.toString();
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

    private void saveData(Map<String, Object> wisataInfo) {
        progressDialog.show();
        if (id != null) {
            fStore.collection("Wisata").document(id).set(wisataInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(AddWisata.this, "Data Berhasil Diedit", Toast.LENGTH_SHORT).show();
                    finish();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(AddWisata.this, "Data Gagal Diedit", Toast.LENGTH_SHORT).show();
                    errorDialog.setTitle("Edit Data Failed").setMessage(e.getMessage()).show();
                    Log.d("EDIT DATA FAILED", e.toString());
                }
            });
        } else {
            fStore.collection("Wisata").add(wisataInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(AddWisata.this, "Data Berhasil Diinput", Toast.LENGTH_SHORT).show();
                    finish();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(AddWisata.this, "Data Gagal Diinput", Toast.LENGTH_SHORT).show();
                    errorDialog.setTitle("Input Data Failed").setMessage(e.getMessage()).show();
                    Log.d("INPUT DATA FAILED", e.toString());
                }
            });
        }
    }
}