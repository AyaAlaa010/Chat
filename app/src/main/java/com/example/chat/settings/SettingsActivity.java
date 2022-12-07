package com.example.chat.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.ui.MainActivity;
import com.example.chat.R;
import com.example.chat.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private static final int GALLARY_PICK = 1;
    private StorageReference userProfilImageReference; // for store images
    private ProgressDialog loadingBar;
    private Toolbar  toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfilImageReference = FirebaseStorage.getInstance().getReference().child("profile Images");
        loadingBar = new ProgressDialog(this);
        initToolBar();
        initActions();
        retriveUserInfo();


    }

    private void initToolBar() {

        toolbar=(Toolbar) findViewById(R.id.toolbar_find);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }

    private void retriveUserInfo() {
        rootRef.child("User").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



           if (snapshot.exists() && snapshot.hasChild("name")&& snapshot.hasChild("image")) {

               String retrievedImage = snapshot.child("image").getValue().toString();
               String retrievedName = snapshot.child("name").getValue().toString();
               String retrievedStatus = snapshot.child("status").getValue().toString();

                    binding.setUserName.setText(retrievedName);
                    binding.setUserStatus.setText(retrievedStatus);
                    Glide.with(SettingsActivity.this)
                            .load(retrievedImage).placeholder(R.drawable.profile_image)
                            .into(binding.profileImage);

                } else if (snapshot.exists() && snapshot.hasChild("name")) {

               String retrievedName = snapshot.child("name").getValue().toString();
               String retrievedStatus = snapshot.child("status").getValue().toString();

                    binding.setUserName.setText(retrievedName);
                    binding.setUserStatus.setText(retrievedStatus);

                } else {

                    Toast.makeText(getBaseContext(), "please set or Update your profile info ...", Toast.LENGTH_LONG).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


    }

  private static int flage=0;
    private void initActions() {
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateSettings();

            }

        });

        //set Image

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flage=1;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), GALLARY_PICK);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLARY_PICK && resultCode == RESULT_OK && data != null) {

            Uri resultUri = data.getData();


            saveProfileImageToStorage(resultUri);



        }

    }

    private void saveProfileImageToStorage(Uri resultUri) {

        StorageReference filePath = userProfilImageReference.child(currentUserId + ".jpg");

        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getBaseContext(), " Profile Image Uploaded Successfully...", Toast.LENGTH_LONG).show();

                    //

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'

                            Glide.with(getBaseContext()).load(uri.toString())
                                    .into(binding.profileImage);
                            Log.i("resulturi", "onComplete: "+resultUri);
                            Log.i("resulturistr", "onComplete: "+resultUri.toString());

                            if(!uri.equals(null)){
                            saveProfileImageToRealDatabase(uri.toString());}

                            else{

                                saveProfileImageToRealDatabase("https://www.dreamstime.com/stock-photos-butterfly-flower-image23417093");}

                            Log.i("doooo", "onSuccess: "+uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            Log.i("errormessage", "onSuccess: "+exception.getMessage().toString());

                        }
                    });


                    //





                } else {
                    // loadingBar.dismiss();

                    String message = task.getException().toString();
                    Toast.makeText(getBaseContext(), " Error: " + message, Toast.LENGTH_LONG).show();
                    Log.i("image", "onComplete: " + " Error: " + message);

                }

            }
        });


    }

    private void saveProfileImageToRealDatabase(String resultUri) {

        rootRef.child("User").child(currentUserId).child("image").setValue(resultUri).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getBaseContext(), " image save in database successfuly", Toast.LENGTH_LONG).show();
                    //    loadingBar.dismiss();
                } else {

                    String message = task.getException().toString();
                    Toast.makeText(getBaseContext(), " Error: " + message, Toast.LENGTH_LONG).show();

                }


            }
        });

    }


    private void updateSettings() {
        String name = binding.setUserName.getText().toString();
        String status = binding.setUserStatus.getText().toString();
        if (name.isEmpty()) {

            Toast.makeText(getBaseContext(), "please entre your name", Toast.LENGTH_LONG).show();

        } else if (status.isEmpty()) {
            Toast.makeText(getBaseContext(), "please entre your status", Toast.LENGTH_LONG).show();

        } else if (name.isEmpty() && status.isEmpty()) {
            Toast.makeText(getBaseContext(), "please entre your data", Toast.LENGTH_LONG).show();

        } else {

            getImageAndUpdateProfile(   name,  status);





        }
    }


    private void getImageAndUpdateProfile(  String name, String status){

        rootRef.child("User").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(flage==0){


                        Toast.makeText(SettingsActivity.this, "enter the image", Toast.LENGTH_SHORT).show();
                    }
                    else{

                    String profileImage = snapshot.child("image").getValue().toString(); //getImage
                    HashMap<String, Object> profileMap = new HashMap<>();//setMap
                    profileMap.put("image", profileImage);
                    profileMap.put("name", name);
                    profileMap.put("status", status);
                    profileMap.put("uid", currentUserId);
                    setupdatedProfileMapToDB(profileMap);}

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



    private void setupdatedProfileMapToDB(HashMap<String, Object> profileMap) {

                rootRef.child("User").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() { //update
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //sendUserToMainActivity();
                            Toast.makeText(getBaseContext(), "profile updated successfully", Toast.LENGTH_LONG).show();

                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(getBaseContext(), "Error:" + message, Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }


    private void sendUserToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
     //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // to prevent the backing to RegisterActivity
        startActivity(intent);

    }

}