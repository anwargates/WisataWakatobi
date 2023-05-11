package com.example.wisatawakatobi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wisatawakatobi.R;
import com.example.wisatawakatobi.model.User;
import com.example.wisatawakatobi.model.Wisata;
import com.example.wisatawakatobi.recommendation.NBClassifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.List;

public class RekomendasiAdapter extends RecyclerView.Adapter<RekomendasiAdapter.RekomendasiViewHolder> {
    Context context;
    List<Wisata> listWisata;
    List<Wisata> listFavorit;
    List<Wisata> listRekomen;
    User userData;
    Dialog dialog;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public interface Dialog {
        void onClick(int pos);
    }

    public RekomendasiAdapter(Context context, List<Wisata> listWisata, List<Wisata> listFavorit, List<Wisata> listRekomen, User userData) {
        this.context = context;
        this.listFavorit = listFavorit;
        this.listWisata = listWisata;
        this.listRekomen = listRekomen;
        this.userData = userData;
    }

    @NonNull
    @Override
    public RekomendasiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rekomendasi, parent, false);
            return new RekomendasiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RekomendasiViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(listRekomen.get(position).getFoto()).into(holder.cardImage);
        holder.cardTitle.setText(listRekomen.get(position).getNama());
//        ArrayList<String> favs = (ArrayList<String>) userData.getFavorite();
//        Log.d("FAVORITED", userData.getFavorite().toString());
//        getData();
    }

    @Override
    public int getItemCount() {
        return listRekomen.size();
    }

    class RekomendasiViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImage;
        TextView cardTitle;

        public RekomendasiViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.rekomenImage);
            cardTitle = itemView.findViewById(R.id.rekomenTitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null) {
                        dialog.onClick(getLayoutPosition());
                    }
                }
            });
        }
    }

//    private void getData() {
//        fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot doc = task.getResult();
//                    ArrayList<String> tempList;
//                    tempList = (ArrayList<String>) doc.get("favorites");
//                    userData.setUid(doc.getId());
//                    userData.setFavorite(tempList);
//
//                    fStore.collection("Wisata").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                listWisata.clear();
//                                listFavorit.clear();
//                                for (QueryDocumentSnapshot doc : task.getResult()) {
//                                    Wisata wisata = new Wisata(doc.getString("nama"), doc.getString("lokasi"), doc.getString("deskripsi"), doc.getString("jumlah"), doc.getString("foto"), doc.getString("kategori"), doc.getString("harga"));
//                                    wisata.setId(doc.getId());
//                                    if (userData.getFavorite().contains(doc.getId())) {
//                                        listFavorit.add(wisata);
//                                    }else{
//                                        listWisata.add(wisata);
//                                    }
//                                }
////                                Toast.makeText(getApplicationContext(), "Data Berhasil diambil", Toast.LENGTH_LONG).show();
//                                listRekomen = NBClassifier.recommendItems(listWisata,listFavorit);
//
//
////                                Log.d("FAV LIST", list.toString());
////                                wisataAdapter.notifyDataSetChanged();
//                            } else {
////                                Toast.makeText(getApplicationContext(), "Data Gagal diambil", Toast.LENGTH_LONG).show();
//                            }
////                            swipeRefreshLayout.setRefreshing(false);
////                            progressDialog.dismiss();
//                        }
//                    });
//                } else {
////                    swipeRefreshLayout.setRefreshing(false);
////                    progressDialog.dismiss();
//                }
//            }
//        });
//
//    }
}
