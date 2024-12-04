package com.example.studoc_clone.adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studoc_clone.R;
import com.example.studoc_clone.models.Pack;

import java.util.ArrayList;
import java.util.List;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.PackViewHolder> {

    private final Context context; // Added context
    private ArrayList<Pack> packList = new ArrayList<>();
    private final OnPackClickListener listener;

    public interface OnPackClickListener {
        void onPackClick(Pack pack);
    }

    public PackAdapter(Context context, ArrayList<Pack> packList, OnPackClickListener listener) {
        this.context = context; // Initialize context
        this.packList = packList;
        this.listener = listener;
    }

    @Override
    public PackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pack, parent, false);
        return new PackViewHolder(view);
    }

    public void updateData(List<Pack> newPackList) {
        this.packList.clear();
        this.packList.addAll(newPackList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(PackViewHolder holder, int position) {
        Pack pack = packList.get(position);
        holder.packName.setText(pack.getName());
        holder.packSubName.setText(pack.getSubName());
        holder.packType.setText(pack.getType().toString());

        // Set background color based on Pack type
        String type = pack.getType().toString().toLowerCase(); // Get the type as string
        switch (type) {
            case "book":
                holder.packLayout.setBackgroundResource(R.drawable.books_pack_background); // Set light purple
                holder.packName.setTextColor(Color.parseColor("#6956b2"));
                holder.packSubName.setTextColor(Color.parseColor("#998bc8"));
                holder.packType.setBackgroundResource(R.drawable.rounded_books_tag);
                break;
            case "course":
                holder.packLayout.setBackgroundResource(R.drawable.courses_pack_background); // Set light purple
                holder.packName.setTextColor(Color.parseColor("#1e9a00"));
                holder.packSubName.setTextColor(Color.parseColor("#418f22"));
                holder.packType.setBackgroundResource(R.drawable.rounded_courses_tag);

                break;

            default:
                // Set a default color or background if necessary
                break;
        }

        // Set the click listener on the item view
        holder.itemView.setOnClickListener(v -> listener.onPackClick(pack));
    }


    @Override
    public int getItemCount() {
        return packList.size();
    }

    // ViewHolder to bind the data to the views
    public static class PackViewHolder extends RecyclerView.ViewHolder {
        TextView packName;
        TextView packSubName;
        TextView packType;
        RelativeLayout packLayout;

        public PackViewHolder(View itemView) {
            super(itemView);
            packName = itemView.findViewById(R.id.textPackName);
            packSubName = itemView.findViewById(R.id.textPackSubName);
            packType = itemView.findViewById(R.id.textPackType);
            packLayout = itemView.findViewById(R.id.pack_layout);
        }
    }

    // Method to filter the list based on Pack Type
//    public void filterByType(String type) {
//        filteredPackList.clear();
//
//        if (type == null || type.isEmpty()) {
//            filteredPackList.addAll(packList);
//        } else {
//            for (Pack pack : packList) {
//                if (pack.getType().equalsIgnoreCase(type)) {
//                    filteredPackList.add(pack);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//    // Method to reset the list to its original state
//    public void resetFilter() {
//        filteredPackList.clear();
//        filteredPackList.addAll(packList);
//        notifyDataSetChanged();
//    }
}
