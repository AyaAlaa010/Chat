package com.example.chat.loginrigestration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chat.settings.SettingsActivity;
import com.example.chat.ui.MainActivity;
import com.example.chat.R;
import com.example.chat.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private Toolbar mtoolbar;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingDialog;
    private DatabaseReference rootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();
        init();
        viewsAction();
    }


    private void init() {
        mtoolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("WhatsApp");
        loadingDialog = new ProgressDialog(this);

    }


    private void viewsAction() {
        binding.tvHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });

        binding.btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createNewAccount();
            }
        });


    }


    private void createNewAccount() {

        String email = binding.etRegisterEmail.getText().toString();
        String password = binding.etRegisterPassword.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(getBaseContext(), "please enter your email", Toast.LENGTH_LONG).show();
        }

        if (password.isEmpty()) {
            Toast.makeText(getBaseContext(), "please enter your password", Toast.LENGTH_LONG).show();
        }

        if (!email.isEmpty() && !password.isEmpty()) {
            loadingDialog.setTitle("Creating New Account");
            loadingDialog.setMessage("please wait, while we are creating new account for you ...");
            loadingDialog.setCanceledOnTouchOutside(true);
            loadingDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String deviceToken = FirebaseMessaging.getInstance().getToken().toString();
                        String currentUsrId = mAuth.getCurrentUser().getUid();
                        rootReference.child("User").child(currentUsrId).setValue("");
                        rootReference.child("User").child(currentUsrId).child("device_token").setValue(deviceToken);

                        // sendUserToMainActivity();
                        sendUserToSettingsActivity();
                        Toast.makeText(getBaseContext(), " The Account created successfully", Toast.LENGTH_LONG).show();
                        loadingDialog.dismiss();
                    } else {
                        String errorMessage = task.getException().toString();
                        Toast.makeText(getBaseContext(), "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                        loadingDialog.dismiss();

                    }
                }
            });
        } else {
            Toast.makeText(getBaseContext(), " Please Enter Your Data ", Toast.LENGTH_LONG).show();
        }


    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    private void sendUserToSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // to prevent the backing to RegisterActivity

        startActivity(intent);
        finish();

    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}