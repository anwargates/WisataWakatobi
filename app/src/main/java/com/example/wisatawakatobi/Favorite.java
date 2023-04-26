package com.example.wisatawakatobi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wisatawakatobi.adapter.WisataAdapter;
import com.example.wisatawakatobi.model.User;
import com.example.wisatawakatobi.model.Wisata;
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

public class Favorite extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    List<Wisata> list = new ArrayList<>();
    User userData = new User();
    WisataAdapter wisataAdapter;
    ProgressDialog progressDialog;
    MaterialCardView backBtn;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.rvFavorite);
        backBtn = findViewById(R.id.backBtnFavorite);
        swipeRefreshLayout = findViewById(R.id.swipeContainerFavorite);

        progressDialog = new ProgressDialog(Favorite.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mohon Tunggu...");
        wisataAdapter = new WisataAdapter(getApplicationContext(), list, userData);
        wisataAdapter.setDialog(new WisataAdapter.Dialog() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(getApplicationContext(), Detail.class);
                intent.putExtra("id", list.get(pos).getId());
                intent.putExtra("nama", list.get(pos).getNama());
                intent.putExtra("deskripsi", list.get(pos).getDeskripsi());
                intent.putExtra("kategori", list.get(pos).getKategori());
                intent.putExtra("jumlah", list.get(pos).getJumlah());
                intent.putExtra("harga", list.get(pos).getHarga());
                intent.putExtra("lokasi", list.get(pos).getLokasi());
                intent.putExtra("foto", list.get(pos).getFoto());
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(wisataAdapter);

        progressDialog.show();
        getData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getData() {
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
                                list.clear();
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    if (userData.getFavorite().contains(doc.getId())) {
                                        Wisata wisata = new Wisata(doc.getString("nama"), doc.getString("lokasi"), doc.getString("deskripsi"), doc.getString("jumlah"), doc.getString("foto"), doc.getString("kategori"), doc.getString("harga"));
                                        wisata.setId(doc.getId());
                                        list.add(wisata);
                                    }
                                }
                                Log.d("FAV LIST", list.toString());
                                wisataAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "Data Gagal diambil", Toast.LENGTH_LONG).show();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    progressDialog.dismiss();
                }
            }
        });

    }
}