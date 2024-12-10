package com.example.studoc_clone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studoc_clone.R;
import com.example.studoc_clone.models.Group;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private final Context context;
    private final List<Group> groupList;
    private final OnItemClickListener onItemClickListener;

    // Interface for handling clicks
    public interface OnItemClickListener {
        void onItemClick(Group group);
    }

    public GroupAdapter(Context context, List<Group> groupList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.groupList = groupList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chats_activity, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);

        holder.groupName.setText(group.getName());
        holder.numOfPeople.setText(String.valueOf(group.getMembers().size()));
        holder.universityName.setText(group.getDescription());

        holder.groupImage.setImageResource(R.drawable.chats_group_logo);


        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(group));
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {

        private final ImageView groupImage;
        private final TextView groupName;
        private final TextView numOfPeople;
        private final TextView universityName;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.groupImage);
            groupName = itemView.findViewById(R.id.groupName);
            numOfPeople = itemView.findViewById(R.id.num_of_people);
            universityName = itemView.findViewById(R.id.description);
        }
    }
}
