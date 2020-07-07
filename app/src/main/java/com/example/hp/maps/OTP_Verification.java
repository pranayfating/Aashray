package com.example.hp.maps;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.core.executor.TaskExecutor;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;

public class OTP_Verification extends AppCompatActivity {

    private String verificationId;
    private FancyButton signup_otp,resend_otp;
    private FirebaseAuth mAuth;
    String phonenumber;
    String name_of_user;
    String pre_act;
    ProgressBar progressBar;
    TextInputEditText editText;
    private DatabaseReference mRootRef,Key_email,Key_name,Key_password,Key_mob;
    private String Email;
    private String password;
    private TextView text_otp;
    private Bundle bundle;
    ProgressDialog progressDialog ;
     DatabaseReference mDatabase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification);

        bundle = getIntent().getExtras();
        phonenumber = bundle.getString("mob");
        name_of_user = bundle.getString("Name");
        Email = bundle.getString("Email");
        pre_act = bundle.getString("pre_act");
        sendVerificationCode(phonenumber);

        signup_otp = (FancyButton)findViewById(R.id.otp_signup);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbar);
        mRootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        editText = findViewById(R.id.otp);
        text_otp = findViewById(R.id.text_otp);
        resend_otp = findViewById(R.id.resend_otp);
        text_otp.setText("Enter verification code sent to your mobile number +91" + "******" + Character.toString(phonenumber.charAt(9)) +Character.toString(phonenumber.charAt(10)) + Character.toString(phonenumber.charAt(11)) + Character.toString(phonenumber.charAt(12)));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Dealers");

        progressDialog = new ProgressDialog(this);
        signup_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = editText.getText().toString().trim();

                if(code.length() < 6){
                    editText.setError("Enter valid code ...");
                    editText.requestFocus();
                    return;
                }

                verifyCode(code);
            }
        });

        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(phonenumber);
            }
        });




    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
    
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithCredential(credential);
        }

    private void signInWithCredential(PhoneAuthCredential credential) {

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                    Key_password = mRootRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    Key_mob = Key_password.child("Number");
                                    Key_name = Key_password.child("Name");
                                    Key_email = Key_password.child("Email");
                                    Key_mob.setValue(phonenumber);
                                    Key_name.setValue(name_of_user);
                                    Key_email.setValue(Email);
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(OTP_Verification.this, MapActivity.class);
                                    SaveSharedPreference.setUserName(OTP_Verification.this, name_of_user);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    finish();
                            }else{

                                Toast.makeText(OTP_Verification.this,"OTP is incorrect",Toast.LENGTH_LONG).show();

                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(OTP_Verification.this,"OTP is incorrect",Toast.LENGTH_LONG).show();

                }
            });

    }



    private void sendVerificationCode(String number){
        //progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

        Toast.makeText(this, "Code sent successfully", Toast.LENGTH_SHORT).show();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String   s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null){
                editText.setText(code);
                progressDialog.setMessage("Authenticating....");
                progressDialog.show();
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OTP_Verification.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        }

}
