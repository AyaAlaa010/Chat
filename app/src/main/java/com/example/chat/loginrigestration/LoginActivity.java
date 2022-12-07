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

import com.example.chat.ui.MainActivity;
import com.example.chat.R;
import com.example.chat.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;
    private Toolbar mtoolbar;
    private ProgressDialog loadingDialog;
    private FirebaseAuth mAuth;
private DatabaseReference userReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        userReference= FirebaseDatabase.getInstance().getReference().child("User");
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
        activityLoginBinding.tvNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });
        activityLoginBinding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowUserToLogin();

            }
        });


        activityLoginBinding.btLoginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(intent);
            }
        });



    }

    private void sendUserToRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // to prevent the backing to RegisterActivity
        startActivity(intent);
        finish();

    }

    private void allowUserToLogin() {

        String email = activityLoginBinding.etLoginEmail.getText().toString();
        String password = activityLoginBinding.etLoginPassword.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(getBaseContext(), "please enter your email", Toast.LENGTH_LONG).show();
        }

        if (password.isEmpty()) {
            Toast.makeText(getBaseContext(), "please enter your password", Toast.LENGTH_LONG).show();
        }

        if (!email.isEmpty() && !password.isEmpty()) {
            loadingDialog.setTitle("Sign In");
            loadingDialog.setMessage("please wait ...");
            loadingDialog.setCanceledOnTouchOutside(true);
            loadingDialog.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String currentUserID=mAuth.getCurrentUser().getUid();
                        String deviceToken= FirebaseMessaging.getInstance().getToken().toString();
                        userReference.child(currentUserID).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){


                                    sendUserToMainActivity();
                                    Toast.makeText(getBaseContext(), " Logged In successful", Toast.LENGTH_LONG).show();
                                    loadingDialog.dismiss();


                                }
                            }
                        });

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


}