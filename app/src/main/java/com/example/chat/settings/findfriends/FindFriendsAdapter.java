package com.example.chat.settings.findfriends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.bojo.ContactsBojo;
import com.example.chat.databinding.FindFriendsItemBinding;
import com.example.chat.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.HomeHolder> {
    List<ContactsBojo> contactsList=new ArrayList<>();
    FindFriendsInterface contactInterface;
    Context context;

    public FindFriendsAdapter(List<ContactsBojo> contactsList, FindFriendsInterface contactInterface, Context context)
    {
        this.context=context;
        this.contactsList = contactsList;
        this.contactInterface=contactInterface;
    }

    public void setUsersList(List<ContactsBojo> contactsList) {
        this.contactsList = contactsList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.find_friends_item,parent
                ,false));}

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        ContactsBojo model=  contactsList.get(position);

        holder.itemBinding.friendName.setText(model.getName());
        holder.itemBinding.friendStatus.setText(model.getStatus());

//        int productId= product.getId();
//        holder.itemHomeBinding.btProductAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                homeInterface.onItemViewClick(product,productId);
//
//            }
//        });
        Glide.with(context)
                .load(model.getProfileImage()).placeholder(R.drawable.profile_image)
                .into(holder.itemBinding.friendProfile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactInterface.onItemClick(model);

            }
        });
        // Picasso.get().load(homeModel.getUrlToImage()).placeholder(R.mipmap.ic_launcher_round).into((holder.imgNews));





    }

    @Override
    public int getItemCount()
    {
        return contactsList.size();
    }

    class HomeHolder extends RecyclerView.ViewHolder{
        FindFriendsItemBinding itemBinding;



        public HomeHolder(@NonNull FindFriendsItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding=itemBinding;
        }
    }}
