package com.example.chat.tabpager.requests;

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
import com.example.chat.databinding.RequestsItemBinding;

import java.util.ArrayList;
import java.util.List;

public class RequestsAdapter  extends RecyclerView.Adapter<RequestsAdapter.RequestsHolder> {
    List<ContactsBojo> contactsList=new ArrayList<>();
   // FindFriendsInterface contactInterface;
    Context context;
    RequestInterface contactInterface;

    public RequestsAdapter(List<ContactsBojo> contactsList, Context context, RequestInterface contactInterface)
    {
        this.context=context;
        this.contactsList = contactsList;
       // this.contactInterface=contactInterface;
        this. contactInterface=contactInterface;
    }

    public void setUsersList(List<ContactsBojo> contactsList) {
        this.contactsList = contactsList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RequestsAdapter.RequestsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestsAdapter.RequestsHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.requests_item,parent
                ,false));}

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.RequestsHolder holder, int position) {
        ContactsBojo model=  contactsList.get(position);
        holder.itemBinding.friendName.setText(model.getName());
        holder.itemBinding.friendStatus.setText(model.getStatus());
      //  Log.i("check", "onClickkk: "+ model.getId());
        Glide.with(context)
                .load(model.getProfileImage()).placeholder(R.drawable.profile_image)
                .into(holder.itemBinding.friendProfile);


        holder.itemBinding.btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactInterface.acceptRequest( model.getId());
               // Log.i("checkkk", "onClick: "+ model.getId());

            }
        });

        holder.itemBinding.btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contactInterface.cancelRequest( model.getId());
            }
        });




    }

    @Override
    public int getItemCount()
    {
        return contactsList.size();
    }

    class RequestsHolder extends RecyclerView.ViewHolder{
        RequestsItemBinding itemBinding;



        public RequestsHolder(@NonNull RequestsItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding=itemBinding;
        }
    }}
