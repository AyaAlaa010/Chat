package com.example.chat.loginrigestration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chat.ui.MainActivity;
import com.example.chat.R;
import com.example.chat.databinding.ActivityPhoneLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private ActivityPhoneLoginBinding binding;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private  String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_phone_login);
       mAuth=FirebaseAuth.getInstance();
        loading=new ProgressDialog(this);
       initActionField();
    }

    private void initActionField() {
        binding.btSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber=binding.etPhone.getText().toString();


                if(phoneNumber.isEmpty()){
                    Toast.makeText(getBaseContext(), "phone number is required", Toast.LENGTH_LONG).show();


                }
                else{
                    loading.setTitle("phone verification");
                    loading.setMessage("please wait, while we are authenticating your phone...");
                   loading.setCanceledOnTouchOutside(false);
                    loading.show();
                     sendSMSCode();

//                    PhoneAuthProvider.getInstance().
//                            verifyPhoneNumber(phoneNumber,60,TimeUnit.SECONDS,
//                                    PhoneLoginActivity.this,callbacks);


            }}});


        binding.btVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.layoutEtPhone.setVisibility(View.INVISIBLE);
                binding.btSendCode.setVisibility(View.INVISIBLE);
                String verificationcode=binding.etVerifyCode.getText().toString();
                if(verificationcode.isEmpty()){
                    Toast.makeText(getBaseContext(), "please write verification code", Toast.LENGTH_LONG).show();


                }
                else{
                    loading.setTitle(" verification code");
                    loading.setMessage("please wait, while we are verifying verification code...");
                    loading.setCanceledOnTouchOutside(false);
                    loading.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);

                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        ///

                callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) { //  this method work when phone number in the same device

                                signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        loading.dismiss();


                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getBaseContext(), "Invalid request", Toast.LENGTH_LONG).show();

                            // Invalid request
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            Toast.makeText(getBaseContext(), " The SMS quota for the project has been exceeded", Toast.LENGTH_LONG).show();

                        }


                       // Toast.makeText(getBaseContext(), "invalid phone number,please enter correct phone number with your country code .... ", Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i("errorrr", "onVerificationFailed: "+e.getMessage());

                        binding.layoutEtPhone.setVisibility(View.VISIBLE);
                        binding.btSendCode.setVisibility(View.VISIBLE);
                        binding.etLayoutVerifyCode.setVisibility(View.INVISIBLE);
                        binding.btVerifyCode.setVisibility(View.INVISIBLE);

                    }


                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) { //this method work when phone number  not in the same device
                        loading.dismiss();


                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                        mResendToken = token;
                        Toast.makeText(getBaseContext(), "code has been sent", Toast.LENGTH_LONG).show();
                        binding.layoutEtPhone.setVisibility(View.INVISIBLE);
                        binding.btSendCode.setVisibility(View.INVISIBLE);
                        binding.etLayoutVerifyCode.setVisibility(View.VISIBLE);
                        binding.btVerifyCode.setVisibility(View.VISIBLE);
                    }



                };






    }

    private void sendSMSCode() {


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(binding.etPhone.getText().toString())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(PhoneLoginActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loading.dismiss();
                            FirebaseUser user=task.getResult().getUser();
                            Toast.makeText(getBaseContext(), "congratulations you are logged successfully", Toast.LENGTH_LONG).show();
                            SendUserToMainActivity();
                            // Sign in success, update UI with the signed-in user's information
                          //  Log.d(TAG, "signInWithCredential:success");

                         //   FirebaseUser user = task.getResult().getUser();
                            // Update UI


                        } else {
                          //  loading.dismiss();

                            String message=task.getException().toString();
                            Toast.makeText(getBaseContext(), "Error: "+message, Toast.LENGTH_LONG).show();

                            // Sign in failed, display a message and update the UI
                           // Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                            }
                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}