 package com.example.hp.maps;


 import android.app.Dialog;
 import android.app.ProgressDialog;
 import android.content.Context;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.net.ConnectivityManager;
 import android.net.NetworkInfo;
 import android.os.Handler;
 import android.preference.PreferenceManager;
 import android.support.annotation.NonNull;
 import android.support.design.widget.Snackbar;
 import android.support.v4.app.ActivityOptionsCompat;
 import android.support.v7.app.AppCompatActivity;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.View;
 import android.widget.Button;
 import android.widget.ImageView;
 import android.widget.ProgressBar;
 import android.widget.Toast;


 import com.google.android.gms.common.ConnectionResult;
 import com.google.android.gms.common.GoogleApiAvailability;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;
 import com.google.firebase.auth.UserInfo;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.ValueEventListener;

 public class MainActivity extends AppCompatActivity {

     private static final String TAG = "MainActivity";

     private static final int ERROR_DIALOG_REQUEST = 9001;

     private static int TIME_OUT = 4000;

     private ImageView mImage;

     private DatabaseReference mDatabase;
     ProgressDialog mProgressdialogue;
     private FirebaseAuth mAuth;

     private Handler mHandler = new Handler();


     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         mAuth = FirebaseAuth.getInstance();
         mImage = (ImageView)findViewById(R.id.icon3);


         mHandler.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     if(isServicesOK() && isNetworkAvailable()) {
                         FirebaseUser currentUser = mAuth.getCurrentUser();
                         if(currentUser == null){
                             finish();
                             Intent i = new Intent(MainActivity.this, SignIn.class);

                             startActivity(i);
                             overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                         }
                         else{

                                     Intent i = new Intent(MainActivity.this, MapActivity.class);
                                     startActivity(i);
                                     overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                     finish();
                         }


                             }

                     else {

                         Snackbar.make(findViewById(R.id.main_layout), "Check your connection and restart the app..", Snackbar.LENGTH_SHORT).show();

                     }
                 }
             }, TIME_OUT);


     }



     public boolean isServicesOK(){
         Log.d(TAG, "isServicesOK: checking google services version");

         int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

         if(available == ConnectionResult.SUCCESS){
             //everything is fine and the user can make map requests
             Log.d(TAG, "isServicesOK: Google Play Services is working");
             return true;
         }
         else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
             //an error occured but we can resolve it
             Log.d(TAG, "isServicesOK: an error occured but we can fix it");
             Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
             dialog.show();
         }
         else{
             Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
         }
         return false;
     }

     private boolean isNetworkAvailable() {
         ConnectivityManager connectivityManager
                 = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
         return activeNetworkInfo != null && activeNetworkInfo.isConnected();
     }



 }
