package com.example.chat.tabpager.contacts;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.bojo.ChatUserBojo;
import com.example.chat.bojo.ContactsBojo;
import com.example.chat.databinding.FindFriendsItemBinding;
import com.example.chat.settings.findfriends.FindFriendsAdapter;
import com.example.chat.settings.findfriends.FindFriendsInterface;
import com.example.chat.tabpager.chats.ChatUsersnterface;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsHolder> {
    List<ChatUserBojo> contactsList=new ArrayList<>();
    ChatUsersnterface contactInterface;
    Context context;

    public ContactsAdapter(List<ChatUserBojo> contactsList, ChatUsersnterface contactInterface, Context context)
    {
        this.context=context;
        this.contactsList = contactsList;
        this.contactInterface=contactInterface;
    }

    public void setUsersList(List<ChatUserBojo> contactsList) {
        this.contactsList = contactsList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ContactsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactsAdapter.ContactsHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.find_friends_item,parent
                ,false));}

    @Override
    public void onBindViewHolder(@NonNull ContactsHolder holder, int position) {
        ChatUserBojo model=  contactsList.get(position);

        holder.itemBinding.friendName.setText(model.getName());
        holder.itemBinding.friendStatus.setText(model.getStatus());
        Log.i("Stringgg", "onBindViewHolder: "+model.getProfileImage());


        Glide.with(context)
                .load(model.getProfileImage())
                .into(holder.itemBinding.friendProfile);




        if(model.getUserStateBojo().getState().equals("online"))
        holder.itemBinding.friendStateOffOn.setVisibility(View.VISIBLE);
        else{

            holder.itemBinding.friendStateOffOn.setVisibility(View.INVISIBLE);


        }

//        int productId= product.getId();
//        holder.itemHomeBinding.btProductAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                homeInterface.onItemViewClick(product,productId);
//
//            }
//        });


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

    class ContactsHolder extends RecyclerView.ViewHolder{
        FindFriendsItemBinding itemBinding;



        public ContactsHolder(@NonNull FindFriendsItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding=itemBinding;
        }
    }}
