package com.example.epcmsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private Context hContext;
    private int index;

    private List<Integer> hCardList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView cardText;
        ImageView cardImage;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            cardText = (TextView) view.findViewById(R.id.card_tv);
            cardImage = (ImageView) view.findViewById(R.id.card_iv);
        }
    }

    public CardAdapter(List<Integer> cardList, int type){
        hCardList = cardList;
        index = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(hContext == null){
            hContext = parent.getContext();
        }
        View view = LayoutInflater.from(hContext).inflate(R.layout.comic_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int card = hCardList.get(position);
//        holder.cardText.setText(card.getDate());
        Glide.with(hContext).load(card).thumbnail(0.1f).into(holder.cardImage);
//        holder.oldView.setImageURI(card.getOld_image_url());
//        holder.newView.setImageURI(card.getNew_image_url());
    }

    @Override
    public int getItemCount() {
        if(hCardList==null) return 0;
        return hCardList.size();
    }
}
