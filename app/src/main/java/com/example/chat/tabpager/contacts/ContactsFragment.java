package com.example.chat.tabpager.contacts;

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
import com.example.chat.bojo.ContactsBojo;
import com.example.chat.bojo.UserStateBojo;
import com.example.chat.databinding.FragmentContactsBinding;
import com.example.chat.settings.ProfileActivity;
import com.example.chat.settings.findfriends.FindFriendsAdapter;
import com.example.chat.settings.findfriends.FindFriendsInterface;
import com.example.chat.tabpager.chats.ChatUsersnterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment {

   private FragmentContactsBinding binding;
   private DatabaseReference contactsReference;
   private DatabaseReference userReference;
   private FirebaseAuth mAuth;
   private String currentUserId;
    private  List<ChatUserBojo> contactsList;
    private ContactsAdapter adapter;
    private ChatUsersnterface contactInterface;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        contactsReference= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userReference=FirebaseDatabase.getInstance().getReference().child("User");
        contactInterface=new ChatUsersnterface(){
            @Override
            public void onItemClick(ChatUserBojo model) {

                Intent intent= new Intent(requireContext(), ProfileActivity.class);
                intent.putExtra("name",model.getName());
                intent.putExtra("status",model.getStatus());
                intent.putExtra("uid",model.getId());

                startActivity(intent);




            }
        };

        contactsList=new ArrayList<>();
        adapter=new ContactsAdapter(contactsList,contactInterface,requireContext());
        binding.recContacts.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        binding.recContacts.setLayoutManager(layoutManager);
        getContactsFromFirebase();


    }

    private void getContactsFromFirebase() {



        contactsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactsList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {


                    Log.i("fire22", "onDataChange: "+postSnapshot.getKey() );

                                 String userId =postSnapshot.getKey() ;
                                 userReference.child(userId).addValueEventListener(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         Log.i("name", "onDataChange: "+snapshot.child("name").getValue().toString()+"  status :"+snapshot.child("status").getValue().toString()+" uid"+snapshot.child("uid").getValue().toString()+"image"+snapshot.child("image").getValue().toString());
//                                         if(snapshot.child("image").exists()) {

                                             contactsList.add(new ChatUserBojo(snapshot.child("uid").getValue().toString(), snapshot.child("name").getValue().toString(), snapshot.child("status").getValue().toString(),
                                                     new UserStateBojo(snapshot.child("userState").child("date").getValue().toString(), snapshot.child("userState").child("time").getValue().toString(),
                                                             snapshot.child("userState").child("state").getValue().toString()), snapshot.child("image").getValue().toString()));
//                                         }else{

//                                             contactsList.add(new ChatUserBojo(snapshot.child("uid").getValue().toString(), snapshot.child("name").getValue().toString(), snapshot.child("status").getValue().toString(),
//                                                     new UserStateBojo(snapshot.child("userState").child("date").getValue().toString(), snapshot.child("userState").child("time").getValue().toString(), snapshot.child("userState").child("state").getValue().toString())));
//                                         }

                                         Log.i("fire", "onDataChange: "+contactsList.size() );
                                         adapter.setUsersList(contactsList);


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
}