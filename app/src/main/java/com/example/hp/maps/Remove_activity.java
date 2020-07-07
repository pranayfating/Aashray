package com.example.hp.maps;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Remove_activity extends AppCompatActivity {
    LinearLayout webView;
    DatabaseReference mDatabase;
    AppCompatButton remove_button;
    RelativeLayout relativeLayout;
    TextView deleted,not_deleted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Dealers");
        remove_button = findViewById(R.id.btn_delete);
        relativeLayout = findViewById(R.id.delete);
        webView = findViewById(R.id.webview);
        not_deleted = findViewById(R.id.text_otp);


        if(SaveSharedPreference.getUserName(Remove_activity.this).equals("Google"))
        {

            relativeLayout.setVisibility(View.GONE); }
        else{
            webView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
            remove_button.setVisibility(View.VISIBLE);
            not_deleted.setVisibility(View.VISIBLE);
            remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(final DataSnapshot s:dataSnapshot.getChildren()){
                                Toast.makeText(Remove_activity.this, "675ghkn", Toast.LENGTH_SHORT).show();

                                if(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().equals("+91" + (String)s.child("Phone").getValue())){
                                    Toast.makeText(Remove_activity.this, "675ghkn", Toast.LENGTH_SHORT).show();

                                    new AlertDialog.Builder(Remove_activity.this)
                                            .setMessage("Are you sure you want to remove ?")
                                            .setNegativeButton(android.R.string.no, null)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface arg0, int arg1) {

                                                    mDatabase.child(s.getKey().toString()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            Toast.makeText(Remove_activity.this, "deleted", Toast.LENGTH_SHORT).show();
                                                            Intent i = new Intent(Remove_activity.this, MapActivity.class);
                                                            startActivity(i);
                                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                        }

                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(Remove_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                                    Toast.makeText(Remove_activity.this, "deleted", Toast.LENGTH_SHORT).show();
                                                }
                                            }).create().show();


                                }

                                else{
                                    Toast.makeText(Remove_activity.this, "+91" + (String)s.child("Phone").getValue(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Remove_activity.this,databaseError.toString(),Toast.LENGTH_LONG).show();

                        }

                    });
                }
            });

        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
