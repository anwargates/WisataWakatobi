package com.example.wisatawakatobi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
//    Button logoutBtn;
    MaterialCardView cardSetting, cardTouristSpot,cardMeal ,cardWorship , cardResidence,  cardFavorite;
    TextView tvHomeName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardSetting = findViewById(R.id.cardSetting);
        cardTouristSpot = findViewById(R.id.cardTouristSpot);
        cardMeal = findViewById(R.id.cardMeal);
        cardResidence = findViewById(R.id.cardResidence);
        cardWorship = findViewById(R.id.cardWorship);
        cardFavorite = findViewById(R.id.cardFavorite);
        tvHomeName = findViewById(R.id.tvHomeName);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser fUser= fAuth.getCurrentUser();

        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(fUser.getUid());
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tvHomeName.setText(documentSnapshot.getString("FullName"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
            }
        });

        cardSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });

        cardTouristSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Search.class).putExtra("kategori", "Tourist Spot"));
            }
        });

        cardMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Search.class).putExtra("kategori", "Tempat Makan"));
            }
        });

        cardWorship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Search.class).putExtra("kategori", "Tempat Ibadah"));
            }
        });

        cardResidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Search.class).putExtra("kategori", "Penginapan"));
            }
        });

        cardFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Favorite.class));
            }
        });

    }
}