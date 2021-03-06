package com.example.epcmsystem;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryCardAdapter extends RecyclerView.Adapter<HistoryCardAdapter.ViewHolder> {
    private Context hContext;
    private int userType;

    private List<HistoryCard> hCardList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView cardDate, cardDegree, cardHealth, cardPosition, cardUID;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            cardDate = (TextView) view.findViewById(R.id.card_date);
            cardDegree = (TextView) view.findViewById(R.id.degree_tv);
            cardHealth = (TextView) view.findViewById(R.id.health_tv);
            cardPosition = (TextView) view.findViewById(R.id.location_tv);
            cardUID = (TextView) view.findViewById(R.id.card_uid);
            imageView = (ImageView) view.findViewById(R.id.history_iv);
        }
    }

    public HistoryCardAdapter(List<HistoryCard> cardList, int type){
        hCardList = cardList;
        userType = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(hContext == null){
            hContext = parent.getContext();
        }
        View view = LayoutInflater.from(hContext).inflate(R.layout.history_item, parent,false);
        HistoryCardAdapter.ViewHolder holder = new HistoryCardAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryCardAdapter.ViewHolder holder, int position) {
        if(userType == 1){
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(hContext)
                            .setTitle("??????")
                            .setMessage("??????????????????????????????")
                            .setPositiveButton("??????", (dialog, which) -> {
                                int pos = holder.getBindingAdapterPosition();
                                hCardList.remove(pos);
                                notifyItemRemoved(pos);
                            })
                            .setNegativeButton("??????", ((dialog, which) -> { }))
                            .create();
                    alertDialog.show();
                    return false;
                }
            });
        }

        HistoryCard card = hCardList.get(position);
        if (userType == 1) {
            holder.cardUID.setVisibility(View.VISIBLE);
            holder.cardUID.setText("id:" + card.getId() + " uid:" + card.getUid());
        }
        holder.cardDate.setText(card.getDate());
        holder.cardDegree.setText(card.getDegree() + "???");
        String[] items = hContext.getResources().getStringArray(R.array.health_condition_values);
        int health_index = card.getHealth();
        holder.cardHealth.setText(items[health_index]);
        if( health_index == 0 || health_index == 4 ){
            holder.cardHealth.setTextColor(Color.rgb(93,160,97));
        } else if ( health_index == 3 ){
            holder.cardHealth.setTextColor(Color.rgb(210,44,52));
        } else{
            holder.cardHealth.setTextColor(Color.rgb(231,168,58));
        }
        holder.cardPosition.setText(card.getLocation());
        Glide.with(hContext).load(card.getImage()).thumbnail(0.1f).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(hCardList==null) return 0;
        return hCardList.size();
    }
}
