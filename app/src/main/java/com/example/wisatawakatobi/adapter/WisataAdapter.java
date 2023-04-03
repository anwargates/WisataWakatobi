package com.example.wisatawakatobi.adapter;

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
import com.example.wisatawakatobi.model.Wisata;

import java.util.List;

public class WisataAdapter extends RecyclerView.Adapter<WisataAdapter.WisataViewHolder> {
    private Context context;
    private List<Wisata> list;
    private Dialog dialog;

    public interface Dialog{
        void onClick(int pos);
    }

    public Dialog getDialog() {
        return dialog;
    }

    public WisataAdapter(Context context, List<Wisata> list){
        this.context = context;
        this.list = list;
    }



    @NonNull
    @Override
    public WisataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (parent.getContext().toString()=="com.example.wisatawakatobi.Search@b47e21f"){
            Log.d("PARENT CONTEXT", parent.getContext().toString());
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wisata, parent, false);
            return new WisataViewHolder(itemView);
        }else{
            Log.d("PARENT CONTEXT", parent.getContext().toString());
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wisata_admin, parent, false);
            return new WisataViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull WisataViewHolder holder, int position) {
//        Glide.with(holder)

//        holder.cardImage.
        holder.cardTitle.setText(list.get(position).getNama());
        holder.cardDescription.setText(list.get(position).getDeskripsi());
        holder.cardDetails.setText(list.get(position).getLokasi());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class WisataViewHolder extends RecyclerView.ViewHolder{
        ImageView cardImage;
        TextView cardTitle, cardDescription, cardDetails;

        public WisataViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.cardImage);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardDescription = itemView.findViewById(R.id.cardDescription);
            cardDetails = itemView.findViewById(R.id.cardDetails);

        }
    }
}
