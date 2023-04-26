package com.example.wisatawakatobi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

public class Detail extends AppCompatActivity {
    ImageView imageWisata;
    TextView namaWisata, lokasiWisata, totalPengunjung, hargaWisata, deskripsiWisata;
    String id, imageUrl;
    MaterialCardView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        backButton = findViewById(R.id.backBtnSearch);
        imageWisata = findViewById(R.id.detailImageWisata);
        namaWisata = findViewById(R.id.tvTitle);
        lokasiWisata = findViewById(R.id.tvLokasi);
        totalPengunjung = findViewById(R.id.tvPengunjung);
        hargaWisata = findViewById(R.id.tvHarga);
        deskripsiWisata = findViewById(R.id.tvDescription);

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
    }
}