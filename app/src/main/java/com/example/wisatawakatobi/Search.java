package com.example.wisatawakatobi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.example.wisatawakatobi.adapter.WisataAdapter;
import com.example.wisatawakatobi.model.Wisata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    List<Wisata> list = new ArrayList<>();
    WisataAdapter wisataAdapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.rvSearch);

        progressDialog = new ProgressDialog(Search.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mengambil Data...");
        wisataAdapter = new WisataAdapter(getApplicationContext(),list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(wisataAdapter);

        progressDialog.show();
        fStore.collection("Wisata").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc: task.getResult()){
                        Wisata wisata = new Wisata(doc.getString("nama"), doc.getString("lokasi"), doc.getString("deskripsi"), doc.getString("rating"));
                        list.add(wisata);
                    }
                    wisataAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getApplicationContext(), "Data Gagal diambil", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}