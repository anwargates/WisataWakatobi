package com.example.wisatawakatobi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wisatawakatobi.R;
import com.example.wisatawakatobi.model.User;
import com.example.wisatawakatobi.model.Wisata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.List;

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.WisataViewHolder> {
    Context context;
    List<Wisata> list;
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

    public WisataAdapter(Context context, List<Wisata> list, User userData) {
        this.context = context;
        this.list = list;
        this.userData = userData;
    }

    @NonNull
    @Override
    public WisataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (parent.getContext().toString().contains("Admin")) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wisata_admin, parent, false);
            return new WisataViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wisata, parent, false);
            return new WisataViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull WisataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(list.get(position).getFoto()).into(holder.cardImage);
        holder.cardTitle.setText(list.get(position).getNama());
        holder.cardDescription.setText(list.get(position).getDeskripsi());
        holder.cardDetails.setText(list.get(position).getJumlah());
        ArrayList<String> favs = (ArrayList<String>) userData.getFavorite();
//        Log.d("FAVORITED", userData.getFavorite().toString());
        getData();
        if (holder.favBtn!=null){
            if (favs != null && favs.contains(list.get(position).getId())) {
//            Log.d("FAVORITED", favs.toString());
                holder.favBtn.setChecked(true);
            } else {
                holder.favBtn.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class WisataViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImage;
        TextView cardTitle, cardDescription, cardDetails;
        SparkButton favBtn;

        public WisataViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.rekomenImage);
            cardTitle = itemView.findViewById(R.id.rekomenTitle);
            cardDescription = itemView.findViewById(R.id.cardDescription);
            cardDetails = itemView.findViewById(R.id.cardDetails);
            favBtn = itemView.findViewById(R.id.favBtn);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null) {
                        dialog.onClick(getLayoutPosition());
                    }
                }
            });
            if (favBtn!=null){
                favBtn.setEventListener(new SparkEventListener() {
                    @Override
                    public void onEvent(ImageView button, boolean buttonState) {
                        String id = list.get(getLayoutPosition()).getId();
                        String UserId = fAuth.getCurrentUser().getUid();
                        String currentItemId = list.get(getLayoutPosition()).getId();
//                    Log.d("FAV BTN ID", list.get(getLayoutPosition()).getId());
                        if (userData.getFavorite().contains(currentItemId)) {
                            fStore.collection("Users").document(UserId).update("favorites", FieldValue.arrayRemove(currentItemId)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    getData();
                                    if (task.isSuccessful()) {
                                        Log.d("DELETE FAV SUCCESS", "DELETE FAV SUCCESS");
                                        Snackbar.make(itemView, "Favorite Dihapus", Snackbar.LENGTH_LONG).show();
                                        favBtn.setChecked(false);
                                    } else {
                                        Log.d("DELETE FAV GAGAL", "DELETE FAV GAGAL");
                                        favBtn.setChecked(true);
                                    }
                                }
                            });
                        } else {
                            fStore.collection("Users").document(UserId).update("favorites", FieldValue.arrayUnion(id)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    getData();
                                    if (task.isSuccessful()) {
                                        Log.d("ADD FAV SUCCESS", "ADD FAV SUCCESS");
                                        Snackbar.make(itemView, "Favorite Ditambah", Snackbar.LENGTH_LONG).show();
                                        favBtn.setChecked(true);
                                    } else {
                                        Log.d("ADD FAV GAGAL", "ADD FAV GAGAL");
                                        favBtn.setChecked(false);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onEventAnimationEnd(ImageView button, boolean buttonState) {

                    }

                    @Override
                    public void onEventAnimationStart(ImageView button, boolean buttonState) {

                    }
                });
            }

        }
    }
    private void getData() {
        fStore.collection("Users").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    ArrayList<String> list = (ArrayList<String>) doc.get("favorites");
                    userData.setUid(doc.getId());
                    userData.setFavorite(list);
                }
            }
        });
    }
}
