package com.example.wisatawakatobi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {
    MaterialCardView logoutBtn;
    FloatingActionButton addBtn;
    RecyclerView recyclerView;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    List<Wisata> list = new ArrayList<>();
    User userData =  new User();
    WisataAdapter wisataAdapter;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        addBtn = findViewById(R.id.fabAdd);
        logoutBtn = findViewById(R.id.logoutBtn);

        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        recyclerView = findViewById(R.id.rvAdmin);

        progressDialog = new ProgressDialog(Admin.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mohon Tunggu...");

        wisataAdapter = new WisataAdapter(getApplicationContext(), list, userData);
        wisataAdapter.setDialog(new WisataAdapter.Dialog() {
            @Override
            public void onClick(int pos) {
                final CharSequence[] dialogItem = {"Lihat Detail", "Edit", "Hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(Admin.this);
                dialog.setItems(dialogItem, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent;
                        switch (i) {
                            case 0:
                                intent = new Intent(getApplicationContext(), Detail.class);
                                intent.putExtra("id", list.get(pos).getId());
                                intent.putExtra("nama", list.get(pos).getNama());
                                intent.putExtra("deskripsi", list.get(pos).getDeskripsi());
                                intent.putExtra("kategori", list.get(pos).getKategori());
                                intent.putExtra("jumlah", list.get(pos).getJumlah());
                                intent.putExtra("harga", list.get(pos).getHarga());
                                intent.putExtra("lokasi", list.get(pos).getLokasi());
                                intent.putExtra("foto", list.get(pos).getFoto());
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(getApplicationContext(), AddWisata.class);
                                intent.putExtra("id", list.get(pos).getId());
                                intent.putExtra("nama", list.get(pos).getNama());
                                intent.putExtra("deskripsi", list.get(pos).getDeskripsi());
                                intent.putExtra("kategori", list.get(pos).getKategori());
                                intent.putExtra("jumlah", list.get(pos).getJumlah());
                                intent.putExtra("harga", list.get(pos).getHarga());
                                intent.putExtra("lokasi", list.get(pos).getLokasi());
                                intent.putExtra("foto", list.get(pos).getFoto());
                                startActivity(intent);
                                break;
                            case 2:
                                deleteData(list.get(pos).getId());
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(wisataAdapter);

        getData();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddWisata.class));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    private void getData() {
        fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    ArrayList<String> tempList = (ArrayList<String>) doc.get("favorites");
                    userData.setUid(doc.getId());
//                    Log.d("ON SEARCH ID", doc.getId());
//                    Log.d("ON SEARCH", doc.toString());
////                    Log.d("ON SEARCH", doc.get("favorites").toString());
                    userData.setFavorite(tempList);
                    fStore.collection("Wisata").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                list.clear();
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    Wisata wisata = new Wisata(doc.getString("nama"), doc.getString("lokasi"), doc.getString("deskripsi"), doc.getString("jumlah"), doc.getString("foto"), doc.getString("kategori"), doc.getString("harga"));
                                    wisata.setId(doc.getId());
                                    list.add(wisata);
                                }
                                wisataAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "Data Gagal diambil", Toast.LENGTH_LONG).show();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
                swipeRefreshLayout.setRefreshing(false);
                progressDialog.dismiss();
            }
        });
    }

    private void deleteData(String id) {
        progressDialog.show();
        fStore.collection("Wisata").document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Data gagal di hapus!", Toast.LENGTH_SHORT).show();
                            Log.d("TASK FAILED", task.toString());
                        }
                        Toast.makeText(getApplicationContext(), "Data berhasil di hapus!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        getData();
                    }
                });
    }
}