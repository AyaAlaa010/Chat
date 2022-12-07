package com.example.chat.settings.findfriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.bojo.ContactsBojo;
import com.example.chat.databinding.ActivityFindFriendsBinding;
import com.example.chat.settings.ProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindFriendsActivity extends AppCompatActivity {
    private ActivityFindFriendsBinding binding;
   private Toolbar  toolbar;
    private DatabaseReference rootRef;
    private List<ContactsBojo> contactsList;
    private FindFriendsAdapter adapter;
    private FindFriendsInterface contactInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil. setContentView(this,R.layout.activity_find_friends);

        contactInterface=new FindFriendsInterface(){
            @Override
            public void onItemClick(ContactsBojo model) {

                Intent intent= new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("name",model.getName());
                intent.putExtra("status",model.getStatus());
                intent.putExtra("uid",model.getId());

                startActivity(intent);




            }
        };

        init();


    }

    private void init(){

        toolbar=(Toolbar) findViewById(R.id.toolbar_find);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
        rootRef = FirebaseDatabase.getInstance().getReference();
        contactsList=new ArrayList<>();
        adapter=new FindFriendsAdapter(contactsList,contactInterface,this);
        binding.recFindFriends.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recFindFriends.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();






        rootRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                Object retrievedName = snapshot.getValue();
//                String retrievedStatus = snapshot.child("status").getValue().toString();
//                String retrievedImage = snapshot.child("image").getValue().toString();
//                Log.i("fire", "onDataChange: "+snapshot.child("status"));
                contactsList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
//                   if(postSnapshot.hasChild("image")) {
//                       Log.i("bool", "onDataChange: "+"true");

                        contactsList.add(new ContactsBojo(postSnapshot.child("name").getValue().toString(), postSnapshot.child("status").getValue().toString(), postSnapshot.child("uid").getValue().toString(), postSnapshot.child("image").getValue().toString()));
//                  }
//
//                    else{
//
//                       Log.i("bool", "onDataChange: "+"false");
//
//                     //   contactsList.add(new ContactsBojo(postSnapshot.child("name").getValue().toString(), postSnapshot.child("status").getValue().toString(), postSnapshot.child("uid").getValue().toString()));
//
//                       Log.i("bool", "onDataChange: "+ "name="+postSnapshot.child("status").getValue().toString());
//
//                   }
                }

                    adapter.setUsersList(contactsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



}