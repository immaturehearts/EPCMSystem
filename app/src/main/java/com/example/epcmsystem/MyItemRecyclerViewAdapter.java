package com.example.epcmsystem;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.epcmsystem.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.epcmsystem.databinding.FragmentTabItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<RiskArea> mAreaList;
    private Context hContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
//        CardView cardView;
        LinearLayout linearLayout;
        TextView province_tv, city_tv, district_tv;

        public ViewHolder(View view) {
            super(view);
//            cardView = (CardView) view;
            linearLayout = (LinearLayout) view.findViewById(R.id.item_ll);
            province_tv = (TextView) view.findViewById(R.id.item_province);
            city_tv = (TextView) view.findViewById(R.id.item_city);
            district_tv = (TextView) view.findViewById(R.id.item_district);
        }
    }

    public MyItemRecyclerViewAdapter(List<RiskArea> areaList) {
        mAreaList = areaList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(hContext == null){
            hContext = parent.getContext();
        }
        View view = LayoutInflater.from(hContext).inflate(R.layout.fragment_tab_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RiskArea area = mAreaList.get(position);
        if ((position + 1) % 2 == 0) {
            int color = Color.argb(100,187,134,252);
            holder.linearLayout.setBackgroundColor(color);
        }
        holder.province_tv.setText(area.getProvince());
        holder.city_tv.setText(area.getCity());
        holder.district_tv.setText(area.getDistrict());
    }

    @Override
    public int getItemCount() {
        if(mAreaList == null) return 0;
        return mAreaList.size();
    }

}