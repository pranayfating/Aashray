package com.example.hp.maps;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {


    private DatabaseReference mDatabase;

    private Button mSignin_btn;
    private ProgressDialog mProgressDialog;
    public Keeplogin keeplogin = new Keeplogin();
    private Button g_signin;
    private FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 2;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String personName;
    int setPtype;
    TextView t_and_c;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        setPtype = 1;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mSignin_btn = (Button)findViewById(R.id.btn_signin);
        g_signin = (Button)findViewById(R.id.btn_Gsignin);
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        t_and_c = findViewById(R.id.t_and_c);

        g_signin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mProgressDialog.setMessage("Fetching Account....");
                                            mProgressDialog.show();
                                            signIn();
                                        }
                                    }
        );




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    mProgressDialog.dismiss();
                    finish();
                    Intent intent = new Intent(SignIn.this,MapActivity.class);
                    SaveSharedPreference.setUserName(SignIn.this,"Google");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                }
            }
        };

        mSignin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this,Pre_Otp_verification.class);
                intent.putExtra("pre_act","signin");
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                }
        });



        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(SignIn.this);
        if (acct != null) {
             personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
        }


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SpannableStringBuilder builder = new SpannableStringBuilder();

        String t_and_c_string = "By continuing,you agree to our ";
        SpannableString redSpannable= new SpannableString(t_and_c_string);
        redSpannable.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, t_and_c_string.length(), 0);
        builder.append(redSpannable);

        String white = getString(R.string.terms_conditions);
        SpannableString whiteSpannable= new SpannableString(white);
        whiteSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, white.length(), 0);
        builder.append(whiteSpannable);

        t_and_c.setText(builder, TextView.BufferType.SPANNABLE);

        t_and_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, Terms_and_conditions.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        HideSoftKeyboard();
    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mProgressDialog.dismiss();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mProgressDialog.setMessage("Authenticating....");
        mProgressDialog.show();

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("MapActivity", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.signInLayout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void HideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(
                        this.INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();

        if(focusedView != null){
            inputMethodManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(), 0);
        }


    }


}
