package com.example.otpverifysignin;

import static android.widget.Toast.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    EditText phn ,OTP;
    Button btngenerateotp,verifyotp;
    FirebaseAuth mAuth;
    String verificationID;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phn=findViewById(R.id.phone);
        OTP=findViewById(R.id.otp);
        btngenerateotp=findViewById(R.id.btngenotp);
        verifyotp=findViewById(R.id.verifyotp);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.bar);
        btngenerateotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(phn.getText().toString()))
                {
                    Toast.makeText(Login.this, "Enter the valid number",LENGTH_SHORT).show();
                }
                else
                {
                        String number= phn.getText().toString();
                        progressBar.setVisibility(View.VISIBLE);
                        sendverification(number);
                }

            }
        });
        verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(TextUtils.isEmpty(OTP.getText().toString()))
                {
                    Toast.makeText(Login.this, "Wrong otp entered",LENGTH_SHORT).show();
                }
                else

                     verifycode(OTP.getText().toString());
            }
        });

    }

    private void sendverification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential)
        {
            final String code= credential.getSmsCode();
            if(code!=null)
            {
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e)
        {
            Toast.makeText(Login.this, "verification failed", LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s,
                @NonNull PhoneAuthProvider.ForceResendingToken token)
        {
           super.onCodeSent(s,token);
           verificationID=s;
           Toast.makeText(Login.this,"code sent", LENGTH_SHORT).show();
           verifyotp.setEnabled(true);
           progressBar.setVisibility(View.INVISIBLE);
        }
    };

    private void verifycode(String Code)
    {
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationID,Code);
        siginbycredentials(credential);
    }

    private void siginbycredentials(PhoneAuthCredential credential)
    {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Login.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this,details.class));
                        }
                    }
                });
    }


}

