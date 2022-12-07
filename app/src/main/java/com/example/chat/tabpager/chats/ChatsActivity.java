package com.example.chat.tabpager.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.bojo.Messages;
import com.example.chat.databinding.ActivityChatBinding;
import com.example.chat.settings.ProfileActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatsActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private String receiverName, messageSenderId, messageRecieverId;
    private Toolbar toolbar;
    private TextView tvName, tvLastSeen;
    private ImageView imgProfile;
    private FirebaseAuth auth;
    private DatabaseReference rootRef;
    private ArrayList<Messages> chatMessages = new ArrayList<>();
    private MessagesAdapter adapter;
    private String saveCurrentTime, saveCurrentDate;
    private String checker = "", myUrl = "";
    private Uri fileUri;
    private StorageTask uploadTask;
    private final int REQUEST_CODE = 500;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        getIntentNameAndID();
        intializeFirebase();
        intializeToolbar();
        intializeAdapter();
        getDateAndTime();
        getMessages();
        setActionForButtons();


    }

    private void getIntentNameAndID() {

        receiverName = getIntent().getStringExtra("name");
        messageRecieverId = getIntent().getStringExtra("uid");


    }


    private void intializeFirebase() {
        auth = FirebaseAuth.getInstance();
        messageSenderId = auth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();


    }

    private void intializeAdapter() {
        adapter = new MessagesAdapter(chatMessages,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recMessages.setLayoutManager(layoutManager);
        binding.recMessages.setAdapter(adapter);


    }


    private void intializeToolbar() {
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);
        tvName = findViewById(R.id.tv_chat_user_name);
        tvLastSeen = findViewById(R.id.tv_chat_user_lastseen);
        imgProfile = findViewById(R.id.img_chat_friend);
        tvName.setText(receiverName);
        loadingBar=new ProgressDialog(this);
        getUserLastSeen();
        getUserImage();


    }

    private void getDateAndTime() {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calender.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calender.getTime());


    }


    private void getMessages() {

        rootRef.child("Message").child(messageSenderId).child(messageRecieverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages message = snapshot.getValue(Messages.class);
                chatMessages.add(message);
                Log.i("message1", "onChildAdded: " + message.getMessage());
                adapter.notifyDataSetChanged();
                //binding.recMessages.smoothScrollToPosition( binding.recMessages.getAdapter().getItemCount());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getUserLastSeen() {

        rootRef.child("User").child(messageRecieverId).child("userState").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvLastSeen.setText("Last Seen : " + snapshot.child("date").getValue().toString() + "    " + snapshot.child("time").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

private void getUserImage(){


    rootRef.child("User").child(messageRecieverId).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Glide.with(ChatsActivity.this)
                    .load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.profile_image)
                    .into(imgProfile);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });


}

    private void sendMessage() {

        String messageText = binding.edMessage.getText().toString();
        if (messageText.isEmpty()) {

            Toast.makeText(this, "plese write your message", Toast.LENGTH_LONG).show();

        } else {
            String messageSenderRef = "Message/" + messageSenderId + "/" + messageRecieverId;

            String messageReceiverRef = "Message/" + messageRecieverId + "/" + messageSenderId;
            DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderId)
                    .child(messageRecieverId).push();

            String messagepushId = userMessageKeyRef.getKey();

            Map messageTextBodey = new HashMap();
            messageTextBodey.put("message", messageText);
            messageTextBodey.put("type", "text");
            messageTextBodey.put("from", messageSenderId);
            messageTextBodey.put("to", messageRecieverId);
            messageTextBodey.put("messageID", messagepushId);
            messageTextBodey.put("time", saveCurrentTime);
            messageTextBodey.put("date", saveCurrentDate);


            Map messageBodeyDetails = new HashMap();
            //for creating dynamic key for both the sender and reciever
            messageBodeyDetails.put(messageSenderRef + "/" + messagepushId, messageTextBodey);
            messageBodeyDetails.put(messageReceiverRef + "/" + messagepushId, messageTextBodey);

            rootRef.updateChildren(messageBodeyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_LONG).show();


                    } else {

                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();


                    }
                    binding.edMessage.setText("");
                    binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);


                }
            });


        }


    }


    private void setActionForButtons() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });
        binding.btnSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAlertDialogForSendingFile();
            }});



    }


    private void initAlertDialogForSendingFile(){

        CharSequence options[] = new CharSequence[]{
                "Images",
                "PDF Files",
                "MS Word Files"
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(ChatsActivity.this);
        builder.setTitle("Select The File");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialogOptionsAction(i);


            }
        });
        builder.show();

    }


     private void dialogOptionsAction(int i){

         if (i == 0) {
            sendImageMessage();

         }

         if (i == 1) {
             sendPDFMessage();



         }

         if (i == 2) {
             sendDocxMessage();


         }


     }
    private void sendImageMessage() {
        checker = "image";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE);
    }

    private void sendPDFMessage() {
        checker = "pdf";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), REQUEST_CODE);


    }




    private void sendDocxMessage() {
        checker = "docx";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/msword");
        startActivityForResult(Intent.createChooser(intent, "Select MS Word File"), REQUEST_CODE);


    }


private void initProgressBar(){
    loadingBar.setTitle("sending File");
    loadingBar.setMessage("please wait we are sending that file...");
    loadingBar.setCanceledOnTouchOutside(false);
    loadingBar.show();


}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            initProgressBar();
            fileUri = data.getData();
            if (!checker.equals("image")) {//pdf or  word file

                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Document Files");

                String messageSenderRef = "Message/" + messageSenderId + "/" + messageRecieverId;

                String messageReceiverRef = "Message/" + messageRecieverId + "/" + messageSenderId;
                DatabaseReference userMessageKeyRef = rootRef.child("Message").child(messageSenderId)
                        .child(messageRecieverId).push();

                final String messagepushId = userMessageKeyRef.getKey();

                StorageReference filePath=storageReference.child(messagepushId+"."+checker);//two make two files , one for pdf and the other to word
                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map messageImageBodey = new HashMap();
                                    messageImageBodey.put("message", uri.toString());
                                    messageImageBodey.put("name", fileUri.getLastPathSegment());
                                    messageImageBodey.put("type", checker);
                                    messageImageBodey.put("from", messageSenderId);
                                    messageImageBodey.put("to", messageRecieverId);
                                    messageImageBodey.put("messageID", messagepushId);
                                    messageImageBodey.put("time", saveCurrentTime);
                                    messageImageBodey.put("date", saveCurrentDate);


                                    Map messageBodeyDetails = new HashMap();
                                    //for creating dynamic key for both the sender and reciever
                                    messageBodeyDetails.put(messageSenderRef + "/" + messagepushId, messageImageBodey);
                                    messageBodeyDetails.put(messageReceiverRef + "/" + messagepushId, messageImageBodey);
                                    rootRef.updateChildren(messageBodeyDetails);
                                    loadingBar.dismiss();
                                }
                            });
                         }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        Toast.makeText(ChatsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress=(100.0)*snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                   loadingBar.setMessage((int)progress+"% Uploading.....");

                    }

                });








            } else if (checker.equals("image")) {

                //create file to store images in storage
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Files");


                String messageSenderRef = "Message/" + messageSenderId + "/" + messageRecieverId;

                String messageReceiverRef = "Message/" + messageRecieverId + "/" + messageSenderId;
                DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderId)
                        .child(messageRecieverId).push();

               final String messagepushId = userMessageKeyRef.getKey();

                StorageReference filePath=storageReference.child(messagepushId+"."+"jpg");

                uploadTask=filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                      if(!task.isSuccessful()){

                          throw task.getException();

                      }

                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){

                            Uri downloadUri=task.getResult();
                            myUrl=downloadUri.toString();

                            /////

                            Map messageImageBodey = new HashMap();
                            messageImageBodey.put("message", myUrl);
                            messageImageBodey.put("name", fileUri.getLastPathSegment());
                            messageImageBodey.put("type", checker);
                            messageImageBodey.put("from", messageSenderId);
                            messageImageBodey.put("to", messageRecieverId);
                            messageImageBodey.put("messageID", messagepushId);
                            messageImageBodey.put("time", saveCurrentTime);
                            messageImageBodey.put("date", saveCurrentDate);


                            Map messageBodeyDetails = new HashMap();
                            //for creating dynamic key for both the sender and reciever
                            messageBodeyDetails.put(messageSenderRef + "/" + messagepushId, messageImageBodey);
                            messageBodeyDetails.put(messageReceiverRef + "/" + messagepushId, messageImageBodey);

                            rootRef.updateChildren(messageBodeyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        loadingBar.dismiss();
                                        Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_LONG).show();

                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();


                                    }
                                    binding.edMessage.setText("");
                                    binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);


                                }
                            });


                            ////////


                        }

                    }
                });


            } else {
                Toast.makeText(ChatsActivity.this, "No Thing Selected, Error ", Toast.LENGTH_LONG).show();

            }
;

        }


    }
}