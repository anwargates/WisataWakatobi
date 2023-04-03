package com.example.wisatawakatobi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.wisatawakatobi.adapter.WisataAdapter;
import com.example.wisatawakatobi.model.Wisata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wisatawakatobi.databinding.ActivityAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {
//    Button logoutBtn;
//    Button profileBtn;
    FloatingActionButton addBtn;

    RecyclerView recyclerView;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    List<Wisata> list = new ArrayList<>();
    WisataAdapter wisataAdapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        addBtn = findViewById(R.id.fabAdd);

        recyclerView = findViewById(R.id.rvAdmin);

        progressDialog = new ProgressDialog(Admin.this);
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

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddWisata.class));
            }
        });




//        logoutBtn = findViewById(R.id.logoutBtnAdmin);
//        profileBtn = findViewById(R.id.profileBtnAdmin);
//
//        profileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), Profile.class));
//            }
//        });
//
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getApplicationContext(), Login.class));
//                finish();
//            }
//        });

    }
}