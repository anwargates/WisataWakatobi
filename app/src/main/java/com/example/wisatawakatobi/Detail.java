package com.example.wisatawakatobi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wisatawakatobi.adapter.RekomendasiAdapter;
import com.example.wisatawakatobi.model.User;
import com.example.wisatawakatobi.model.Wisata;
import com.example.wisatawakatobi.recommendation.NBClassifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Detail extends AppCompatActivity {
    ImageView imageWisata;
    TextView namaWisata, lokasiWisata, totalPengunjung, hargaWisata, deskripsiWisata;
    String id, imageUrl;
    MaterialCardView backButton;
    ProgressDialog progressDialog;
    RekomendasiAdapter rekomendasiAdapter;
    RecyclerView recyclerView;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    User userData = new User();
    List<Wisata> listWisata = new ArrayList<>();
    List<Wisata> listFavorit = new ArrayList<>();
    List<Wisata> listRekomen = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        recyclerView = findViewById(R.id.horizontalRV);
        backButton = findViewById(R.id.backBtnSearch);
        imageWisata = findViewById(R.id.detailImageWisata);
        namaWisata = findViewById(R.id.tvTitle);
        lokasiWisata = findViewById(R.id.tvLokasi);
        totalPengunjung = findViewById(R.id.tvPengunjung);
        hargaWisata = findViewById(R.id.tvHarga);
        deskripsiWisata = findViewById(R.id.tvDescription);

        getAllWisata();

        progressDialog = new ProgressDialog(Detail.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mohon Tunggu...");
        progressDialog.show();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        namaWisata.setText(intent.getStringExtra("nama"));
        deskripsiWisata.setText(intent.getStringExtra("deskripsi"));
        lokasiWisata.setText(intent.getStringExtra("lokasi"));
        totalPengunjung.setText(intent.getStringExtra("jumlah"));
        hargaWisata.setText(intent.getStringExtra("harga"));
        imageUrl = intent.getStringExtra("foto");


        if (imageUrl != null) {
            {
                Glide.with(getApplicationContext()).load(imageUrl).into(imageWisata);
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rekomendasiAdapter = new RekomendasiAdapter(getApplicationContext(), listWisata, listFavorit, listRekomen, userData);
        rekomendasiAdapter.setDialog(new RekomendasiAdapter.Dialog() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(getApplicationContext(), Detail.class);
                intent.putExtra("id", listRekomen.get(pos).getId());
                intent.putExtra("nama", listRekomen.get(pos).getNama());
                intent.putExtra("deskripsi", listRekomen.get(pos).getDeskripsi());
                intent.putExtra("kategori", listRekomen.get(pos).getKategori());
                intent.putExtra("jumlah", listRekomen.get(pos).getJumlah());
                intent.putExtra("harga", listRekomen.get(pos).getHarga());
                intent.putExtra("lokasi", listRekomen.get(pos).getLokasi());
                intent.putExtra("foto", listRekomen.get(pos).getFoto());
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rekomendasiAdapter);


//        Log.d("FAV LIST", listFavorit.toString());
//        Log.d("WISATA LIST", listWisata.toString());
//        Log.d("REKOMEN LIST", rekomen.toString());
    }

    private void getAllWisata() {

        fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    ArrayList<String> tempList;
                    tempList = (ArrayList<String>) doc.get("favorites");
                    userData.setUid(doc.getId());
                    userData.setFavorite(tempList);

                    fStore.collection("Wisata").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                listWisata.clear();
                                listFavorit.clear();
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    Wisata wisata = new Wisata(doc.getString("nama"), doc.getString("lokasi"), doc.getString("deskripsi"), doc.getString("jumlah"), doc.getString("foto"), doc.getString("kategori"), doc.getString("harga"));
                                    wisata.setId(doc.getId());
                                    if (userData.getFavorite().contains(doc.getId())) {
                                        listFavorit.add(wisata);
                                    } else {
                                        listWisata.add(wisata);
                                    }
                                }
//                                Toast.makeText(getApplicationContext(), "Data Berhasil diambil", Toast.LENGTH_LONG).show();
//                                listRekomen = NBClassifier.recommendItems(listWisata,listFavorit);
                                listRekomen.addAll(NBClassifier.recommendItems(listWisata, listFavorit));

//                                Log.d("FAV LIST", list.toString());
                                rekomendasiAdapter.notifyDataSetChanged();
                            } else {
//                                Toast.makeText(getApplicationContext(), "Data Gagal diambil", Toast.LENGTH_LONG).show();
                            }
//                            swipeRefreshLayout.setRefreshing(false);
                            progressDialog.dismiss();
                        }
                    });
                } else {
//                    swipeRefreshLayout.setRefreshing(false);
                    progressDialog.dismiss();
                }
            }
        });

//        Log.d("FAV LIST", listFavorit.toString());
//        Log.d("WISATA LIST", listWisata.toString());
//        rekomen = NBClassifier.recommendItems(listWisata,listFavorit);
    }
}