package com.example.chat.tabpager.groups;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat.R;
import com.example.chat.databinding.FragmentGroupsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupsFragment extends Fragment {

    private FragmentGroupsBinding binding;
    private GroupsAdapter adapter;
    private ArrayList<String> groupList = new ArrayList<String>();
    private DatabaseReference groupRef;
    private GroupInterface groupInterface;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_groups, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");


        groupInterface = new GroupInterface() {
            @Override
            public void goToChatActivity(String groupName) {
                Intent intent = new Intent(getContext(), GroupChatActivity.class);
                intent.putExtra("groupName", groupName);
                startActivity(intent);
            }

        };
        intializeField();

        retriveGroups();


    }


    private void intializeField() {
        adapter = new GroupsAdapter(groupList, groupInterface);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        binding.recGroups.setAdapter(adapter);
        binding.recGroups.setLayoutManager(layoutManager);


    }


    private void retriveGroups() {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Set<String> set = new HashSet<>();
                Iterator iterator = snapshot.getChildren().iterator();

                while (iterator.hasNext()) {

                    set.add(((DataSnapshot) iterator.next()).getKey());


                }
                groupList.clear();
                groupList.addAll(set);
                groupList.size();
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}