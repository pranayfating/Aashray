package com.example.hp.maps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import mehdi.sakout.fancybuttons.FancyButton;

public class Pre_Otp_verification extends AppCompatActivity {

    EditText mob_num_otp,name,email;
    private AppCompatButton btn_sendotp;
    Bundle bundle;
    String pre_act;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre__otp_verification);
        mob_num_otp = findViewById(R.id.mob_no);
        name = findViewById(R.id.input_name);
        email = findViewById(R.id.input_email);
        btn_sendotp = findViewById(R.id.btn_sendotp);

        bundle = getIntent().getExtras();
        pre_act = bundle.getString("pre_act");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            SaveSharedPreference.clearUserName(Pre_Otp_verification.this);
            mAuth.signOut();
        }

        btn_sendotp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean status = true;
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String Phonenumber =  "+91" + mob_num_otp.getText().toString();
                String nameofuser = name.getText().toString();
                String EmailAddress = email.getText().toString();

                if(nameofuser.isEmpty()){
                    name.setError("Enter Name");
                    status = false;
                }

                if(!EmailAddress.matches(emailPattern) || EmailAddress.length() == 0){
                    email.setError("Invalid Email Address");
                    status = false;
                }
                if(Phonenumber.length() != 13){
                    mob_num_otp.setError("Invalid Mobile Number");
                    status = false;
                }

                if(status) {
                    Intent intent = new Intent(Pre_Otp_verification.this, OTP_Verification.class);
                    intent.putExtra("mob", Phonenumber);
                    intent.putExtra("Name", nameofuser);
                    intent.putExtra("Email", EmailAddress);
                    intent.putExtra("pre_act",pre_act);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Pre_Otp_verification.this, SignIn.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
