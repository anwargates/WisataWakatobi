package com.example.wisatawakatobi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Search extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    List<Wisata> list = new ArrayList<>();
    List<Wisata> listBackup = new ArrayList<>();
    User userData = new User();
    WisataAdapter wisataAdapter;
    ProgressDialog progressDialog;
    MaterialCardView backBtn,sortBtn;
    TextInputEditText searchBar;
    SwipeRefreshLayout swipeRefreshLayout;
    String kategori;
    boolean flag = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        kategori = intent.getStringExtra("kategori");

        searchBar = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.rvSearch);
        backBtn = findViewById(R.id.backBtnSearch);
        swipeRefreshLayout = findViewById(R.id.swipeContainerSearch);
        sortBtn = findViewById(R.id.sortButtonSearch);

        getData();

        progressDialog = new ProgressDialog(Search.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mohon Tunggu...");
        progressDialog.show();
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

        searchBar.addTextChangedListener(new TextWatcher() {
            private Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("runnable", "action running");
                    JaroWinklerSimilarity jws = new JaroWinklerSimilarity();
                    double threshold = 0.8;
                    List<Wisata> matchedWisata = new ArrayList<>();
                    for (Wisata wisata : list) {
                        String[] dataWords = wisata.getNama().split("\\s+");
                        for (String word : dataWords) {
                            double similarity = jws.apply(searchBar.getText().toString().toLowerCase(), word.toLowerCase());
                            if (similarity > threshold) {
                                Log.d("similar", wisata.getNama());
                                matchedWisata.add(wisata);
                                break;
                            }
                        }
                    }
                    Log.d("list state before", list.toString());
                    list.clear();
                    list.addAll(matchedWisata);
                    wisataAdapter.notifyDataSetChanged();
                    Log.d("list state after", list.toString());
                }
            };

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.d("afterTextChanged", "text changed");
                handler.removeCallbacks(runnable);
                if (count == 0) {
                    list.clear();
                    list.addAll(listBackup);
                    wisataAdapter.notifyDataSetChanged();
                } else {
                    handler.postDelayed(runnable, 1000);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                list = new ArrayList<>(listBackup);
            }
        });

        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comparator<Wisata> descendingNameComparator = new Comparator<Wisata>() {
                    @Override
                    public int compare(Wisata o1, Wisata o2) {
                        return o2.getNama().compareTo(o1.getNama());
                    }
                };
                if (!flag){
                    Collections.sort(list, descendingNameComparator);
                    flag = true;
                }else{
                    Collections.sort(list);
                    flag = false;
                }
                wisataAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getData() {
        fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    ArrayList<String> tempList = (ArrayList<String>) doc.get("favorites");
                    userData.setUid(doc.getId());
//                    Log.d("ON SEARCH ID", doc.getId());
//                    Log.d("ON SEARCH", doc.toString());
//                    Log.d("ON SEARCH", doc.get("favorites").toString());
                    userData.setFavorite(tempList);
                    fStore.collection("Wisata").whereEqualTo("kategori", kategori).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                list.clear();
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    Wisata wisata = new Wisata(doc.getString("nama"), doc.getString("lokasi"), doc.getString("deskripsi"), doc.getString("jumlah"), doc.getString("foto"), doc.getString("kategori"), doc.getString("harga"));
                                    wisata.setId(doc.getId());
                                    list.add(wisata);
                                }
                                listBackup.addAll(list);
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