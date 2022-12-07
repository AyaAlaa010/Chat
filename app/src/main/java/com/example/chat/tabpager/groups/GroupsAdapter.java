package com.example.chat.tabpager.groups;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.databinding.ItemGroupBinding;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsHolder> {
    ArrayList<String> groupsNameList=new ArrayList<String>();

    GroupInterface groupInterface;
    public GroupsAdapter(ArrayList<String> groupsNameList, GroupInterface groupInterface)
    {
        this.groupsNameList = groupsNameList;
        this.groupInterface=groupInterface;
    }
    @NonNull
    @Override
    public GroupsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupsHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_group,parent
                ,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsHolder holder, int position) {
        String groupName=  groupsNameList.get(position);
        Log.i("group name", "onBindViewHolder: "+groupName);

        holder.itemBinding.tvGroupName.setText(groupName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupInterface.goToChatActivity(groupName);

            }
        });


    }

    @Override
    public int getItemCount()
    {
        return groupsNameList.size();
    }

    class GroupsHolder extends RecyclerView.ViewHolder{

        ItemGroupBinding itemBinding;

        public GroupsHolder(@NonNull ItemGroupBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding=itemBinding;
        }
    }}
