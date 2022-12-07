package com.example.chat.tabpager.groups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.example.chat.R;
import com.example.chat.databinding.ActivityGroupChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class GroupChatActivity extends AppCompatActivity {
 private ActivityGroupChatBinding binding;
 private  Toolbar toolbar;
 private FirebaseAuth mAuth;
 private DatabaseReference usersReference,groupNameReference,groupMessageKeyReference;
 private String currentGroupName,currentUserId,currentUserName,currentDate,currentTime;


    @Override
    protected void onStart() {
        super.onStart();




        groupNameReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){

                    displayMessage(snapshot);


                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){

                    displayMessage(snapshot);


                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    displayMessage(snapshot);


                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){

                    displayMessage(snapshot);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_group_chat);
        currentGroupName= getIntent().getStringExtra("groupName");
        initToolbar();
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        usersReference= FirebaseDatabase.getInstance().getReference().child("User");
        groupNameReference= FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        getUserInfo();
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfoToDataBase();
                binding.edMessage.setText("");
             //   binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });




    }
    private void displayMessage(DataSnapshot snapshot) {
        Iterator iterator=snapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatDate=(String)((DataSnapshot)iterator.next()).getValue();

            String chatMessage=(String)((DataSnapshot)iterator.next()).getValue();
            String chatName=(String)((DataSnapshot)iterator.next()).getValue();

            String chatTime=(String)((DataSnapshot)iterator.next()).getValue();
            binding.groupChatDisplay.append(chatName+" :"+"\n"+chatMessage+"\n"+chatTime +"   "+chatDate+"\n\n\n\n");
         //   binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }




    }


    private void saveMessageInfoToDataBase(){

        String message=binding.edMessage.getText().toString();
        String messageKey=groupNameReference.push().getKey();
        if(message.isEmpty()){
            Toast.makeText(getBaseContext(), "please write message first ...", Toast.LENGTH_LONG).show();

        }
        else{
            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("MMM dd, yyyy ");
            currentDate=currentDateFormat.format(calForDate.getTime());



            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormat.format(calForTime.getTime());

            HashMap<String,Object>  groupMessageKey= new HashMap<>();
            groupNameReference.updateChildren(groupMessageKey);

            groupMessageKeyReference=groupNameReference.child(messageKey);
            HashMap<String, Object> messageInfoMap= new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);
            groupMessageKeyReference.updateChildren(messageInfoMap);



        }


    }

    private void getUserInfo() {
        usersReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    currentUserName=snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }

    private void initToolbar() {
         toolbar= (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);



    }
}