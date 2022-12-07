package com.example.chat.tabpager.requests;

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
import android.widget.Toast;

import com.example.chat.R;
import com.example.chat.bojo.ContactsBojo;
import com.example.chat.databinding.FragmentRequestsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class RequestsFragment extends Fragment {
    private FragmentRequestsBinding binding;
    private DatabaseReference requestsReference;
    private DatabaseReference typeReference;
    private DatabaseReference userReference;
    private DatabaseReference contactRefernce;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private List<ContactsBojo> contactsList;
    private RequestsAdapter adapter;
    private RequestInterface contactInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_requests, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        requestsReference = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        userReference = FirebaseDatabase.getInstance().getReference().child("User");
        contactRefernce = FirebaseDatabase.getInstance().getReference().child("Contacts");
        contactsList = new ArrayList<>();
        contactInterface = new RequestInterface() {
            @Override
            public void acceptRequest(String userId) {
                impAcceptUserRequest(userId);

            }

            @Override
            public void cancelRequest(String userId) {

                impCancelRequest(userId);


            }
        };

        setAdapter();
        getContactsFromFirebase();
    }


    private void setAdapter() {

        adapter = new RequestsAdapter(contactsList, requireContext(), contactInterface);
        binding.recRequests.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        binding.recRequests.setLayoutManager(layoutManager);


    }

    private void impAcceptUserRequest(String userId) {


        contactRefernce.child(currentUserId).child(userId).child("Contact").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactRefernce.child(userId).child(currentUserId).child("Contact").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                requestsReference.child(currentUserId).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

//

                                                            requestsReference.child(userId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        adapter.notifyDataSetChanged();
                                                                        Toast.makeText(getContext(), " New Contact Added", Toast.LENGTH_LONG).show();


                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void impCancelRequest(String userId) {


        requestsReference.child(currentUserId).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    requestsReference.child(userId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), " Request is removed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }


    private void getContactsFromFirebase() {


        requestsReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactsList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                    Log.i("fire22", "onDataChange: " + postSnapshot.getKey());

                    String userId = postSnapshot.getKey();
                    userReference.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            requestsReference.child(currentUserId).child(userId).child("request_type").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {

                                        if (snapshot.getValue().equals("received")) {

                                         //   if(postSnapshot.child("image").exists()) {

                                                //Log.i("name", "onDataChange: " + dataSnapshot.child("name").getValue().toString() + "  status :" + dataSnapshot.child("status").getValue().toString() + " uid" + dataSnapshot.child("uid").getValue().toString());
                                                contactsList.add(new ContactsBojo(dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("status").getValue().toString(), dataSnapshot.child("uid").getValue().toString(), dataSnapshot.child("image").getValue().toString()));

//                                            }
//
//                                            else{
//
//                                                contactsList.add(new ContactsBojo(dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("status").getValue().toString(), dataSnapshot.child("uid").getValue().toString()));
//
//
//                                            }

                                            Log.i("fire", "onDataChange: " + contactsList.size());
                                            adapter.setUsersList(contactsList);
                                        }
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


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

