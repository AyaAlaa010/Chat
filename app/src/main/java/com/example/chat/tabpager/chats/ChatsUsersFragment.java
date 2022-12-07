package com.example.chat.tabpager.chats;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat.R;
import com.example.chat.bojo.ChatUserBojo;
import com.example.chat.bojo.UserStateBojo;
import com.example.chat.databinding.FragmentChatsBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsUsersFragment extends Fragment {
    private FragmentChatsBinding binding;
    private FirebaseAuth auth;
    private String currentUserId;
    private DatabaseReference chatReference;
    private ArrayList<ChatUserBojo> contactsList;
    private DatabaseReference userReference;
    private ChatUsersnterface chatUserInterface;
    private ChatUserAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatUserInterface=new ChatUsersnterface(){
            @Override
            public void onItemClick(ChatUserBojo model) {

                Intent intent= new Intent(getContext(), ChatsActivity.class);
                intent.putExtra("name",model.getName());
                intent.putExtra("uid",model.getId());
                startActivity(intent);




            }
        };

        initData();

        getUserFriends();


    }

    private void getUserFriends() {
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactsList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                    Log.i("fire22", "onDataChange: " + postSnapshot.getKey());

                    String userId = postSnapshot.getKey();
                    userReference.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.i("prof", "onDataChange: "+ dataSnapshot.child("image").getValue().toString());
//                            if(dataSnapshot.child("image").exists()) {

                                contactsList.add(new ChatUserBojo(dataSnapshot.child("uid").getValue().toString(), dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("status").getValue().toString(),
                                        new UserStateBojo(dataSnapshot.child("userState").child("date").getValue().toString(), dataSnapshot.child("userState").child("time").getValue().toString(), dataSnapshot.child("userState").child("state").getValue().toString()), dataSnapshot.child("image").getValue().toString()));
//                            } else{
//                                contactsList.add(new ChatUserBojo(dataSnapshot.child("uid").getValue().toString(),dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("status").getValue().toString(),
//                                        new UserStateBojo(dataSnapshot.child("userState").child("date").getValue().toString(),dataSnapshot.child("userState").child("time").getValue().toString(),dataSnapshot.child("userState").child("state").getValue().toString())));
//                            }

                                adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void initData() {
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("User");
        chatReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        contactsList= new ArrayList<>();
        adapter=new ChatUserAdapter(contactsList,chatUserInterface,getContext());
        binding.recChats.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        binding.recChats.setLayoutManager(layoutManager);

    }





}