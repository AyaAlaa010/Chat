package com.example.chat.tabpager.chats;

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
import com.example.chat.databinding.ItemFriendsChatBinding;


import java.util.ArrayList;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ChatHolder> {
    ArrayList<ChatUserBojo> chatUsersListList=new ArrayList<ChatUserBojo>();
    Context context;
    ChatUsersnterface chatUserInterface;
    public ChatUserAdapter(ArrayList<ChatUserBojo> chatUsersListList, ChatUsersnterface chatUserInterface, Context context)
    {
        this.chatUsersListList = chatUsersListList;
        this.chatUserInterface=chatUserInterface;
        this.context=context;
    }
    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_friends_chat,parent
                ,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        ChatUserBojo model=  chatUsersListList.get(position);
       // Log.i("group name", "onBindViewHolder: "+groupName);

        holder.itemBinding.tvChatName.setText(model.getName());
        holder.itemBinding.tvDateTime.setText("Last Seen: "+ model.getUserStateBojo().getCurrentDate()+"\n"+model.getUserStateBojo().getCurrentTime());
        holder.itemBinding.tvState.setText(model.getUserStateBojo().getState());
        Glide.with(context)
                .load(model.getProfileImage()).placeholder(R.drawable.profile_image)
                .into(holder.itemBinding.ivChat);

        Log.i("profilechat", "onBindViewHolder: "+(model.getProfileImage()));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // groupInterface.goToChatActivity(groupName);
                chatUserInterface.onItemClick(model);

            }
        });


    }

    @Override
    public int getItemCount()
    {
        return chatUsersListList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder{

        ItemFriendsChatBinding itemBinding;

        public ChatHolder(@NonNull ItemFriendsChatBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding=itemBinding;
        }
    }}
