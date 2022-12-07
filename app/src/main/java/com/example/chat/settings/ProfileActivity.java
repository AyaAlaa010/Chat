package com.example.chat.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private String name;
    private String status;
    private String recieverUid, senderUid, current_state;
    private FirebaseAuth mAuth;
    private DatabaseReference chatRequestReference, contactReference ,notificationReference;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        setData();
        manageChatRequest();
    }


    private void setData() {
        name = getIntent().getStringExtra("name");
        status = getIntent().getStringExtra("status");
        recieverUid = getIntent().getStringExtra("uid");
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        senderUid = mAuth.getCurrentUser().getUid();
        chatRequestReference = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        contactReference = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        current_state = "new";

        binding.tvUserName.setText(name);
        binding.tvUserStatus.setText(status);
        rootRef.child("User").child(recieverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Glide.with(ProfileActivity.this)
                        .load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.profile_image)
                        .into(binding.imgProfile);

            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void manageChatRequest() {
        //for check the state of the button

        chatRequestReference.child(senderUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(recieverUid)) {

                    String requestType = snapshot.child(recieverUid).child("request_type").getValue().toString();

                    if (requestType.equals("sent")) {
                        current_state = "request_sent";
                        binding.btSendMessage.setText("Cancel chat request");

                    } else if (requestType.equals("received")) {

                        current_state = "request_received";
                        binding.btSendMessage.setText("Accept chat request");//btn accept request
                        binding.btDeclineMessageRequest.setVisibility(View.VISIBLE); //btn cancel request
                        binding.btDeclineMessageRequest.setEnabled(true);
                        binding.btDeclineMessageRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelChatRequest();


                            }
                        });

                    }


                } else {
                    contactReference.child(senderUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(recieverUid)) {
                                current_state = "friends";
                                binding.btSendMessage.setText("Remove this contact");


                            }
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


        Log.i("uid", "manageChatRequest: senderuid=" + senderUid + "   recieveruid= " + recieverUid);


        if (!recieverUid.equals(senderUid)) {

            binding.btSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    binding.btSendMessage.setEnabled(false);
                    if (current_state.equals("new")) { // make request
                        sendChatRequest();
                    }

                    if (current_state.equals("request_sent")) { // cancel the request
                        cancelChatRequest();


                    }

                    if (current_state.equals("request_received")) { // received the request
                        acceptChatRequest();


                    }

                    if (current_state.equals("friends")) { // received the request
                        removeSpacificContacts();


                    }



                }
            });


        } else {
            binding.btSendMessage.setVisibility(View.INVISIBLE);


        }
//
//


    }

    private void removeSpacificContacts() {

        contactReference.child(senderUid).child(recieverUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    contactReference.child(recieverUid).child(senderUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                binding.btSendMessage.setEnabled(true);
                                current_state = "new";
                                binding.btSendMessage.setText("Send Message");
                                binding.btDeclineMessageRequest.setVisibility(View.INVISIBLE);
                                binding.btDeclineMessageRequest.setEnabled(false);
                            }


                        }
                    });

                }
            }
        });


    }

    private void acceptChatRequest() {
        contactReference.child(senderUid).child(recieverUid).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
//

                    contactReference.child(recieverUid).child(senderUid).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                chatRequestReference.child(senderUid).child(recieverUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //
                                            chatRequestReference.child(recieverUid).child(senderUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        binding.btSendMessage.setEnabled(true);
                                                        current_state = "friends";
                                                        binding.btSendMessage.setText("Remove this contact");
                                                        binding.btDeclineMessageRequest.setVisibility(View.INVISIBLE);
                                                        binding.btDeclineMessageRequest.setEnabled(false);


                                                    }
                                                }
                                            });
                                            //


                                        }
                                    }
                                });


                            }


                        }
                    });


                    //


                }


            }
        });


    }


    private void sendChatRequest() {


        chatRequestReference.child(senderUid).child(recieverUid).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    chatRequestReference.child(recieverUid).child(senderUid).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                    setNotificationforRequest();

                            }


                        }
                    });


                }
            }
        });


    }

    private void cancelChatRequest() {
        chatRequestReference.child(senderUid).child(recieverUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    chatRequestReference.child(recieverUid).child(senderUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                binding.btSendMessage.setEnabled(true);
                                current_state = "new";
                                binding.btSendMessage.setText("Send Message");
                                binding.btDeclineMessageRequest.setVisibility(View.INVISIBLE);
                                binding.btDeclineMessageRequest.setEnabled(false);
                            }


                        }
                    });

                }
            }
        });


    }
    private void setNotificationforRequest() {
        HashMap<String,String> chatNotification=new HashMap<>();
        chatNotification.put("from",senderUid);
        chatNotification.put("type","request");
        notificationReference.child(recieverUid).push()
                .setValue(chatNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    binding.btSendMessage.setEnabled(true);
                    current_state = "request_sent";
                    binding.btSendMessage.setText("Cancel Chat Request");




                }
            }
        });

    }
}