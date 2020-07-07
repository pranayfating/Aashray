package com.example.hp.maps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import mehdi.sakout.fancybuttons.Utils;
import plugins.gligerglg.locusservice.LocusService;

public class Joining_activity extends AppCompatActivity  {

    private EditText full_name,house_name,mob_number,available,charges,description,email_dealer;
    private String set_name,set_housename,set_mobnumber,set_available,set_charges,set_description;
    CheckBox family,men,women,boy,girl;
    private DatabaseReference mRootRef,mUserRef,key;
    private AppCompatButton btn_submit;
    String check = "";
    Button image1,image2,image3,gps_location;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filepath;
    public static final int REQUEST_CODE = 1;
    public int Count = 1;
    Boolean image1_clicked,image2_clicked,image3_clicked,img_upld1 = false ,img_upld2 = false ,img_upld3 = false,uploaded = false;
    String image1_url,image2_url,image3_url;
    ProgressDialog progressDialog,progressDialog2;
    StorageReference storageRef,StorageRef2;
    StorageReference mountainImagesRef;
    LocusService locusService;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining_activity);

        full_name = findViewById(R.id.input_name);
        house_name = findViewById(R.id.house_name);
        mob_number = findViewById(R.id.phone_number);
        available = findViewById(R.id.available_rooms);
        charges = findViewById(R.id.charges);
        description = findViewById(R.id.Description_input);
        btn_submit = findViewById(R.id.btn_submit);
        gps_location = findViewById(R.id.gps_location);
        email_dealer = findViewById(R.id.email_dealer);

        family = findViewById(R.id.family);
        men = findViewById(R.id.men);
        women = findViewById(R.id.women);
        boy = findViewById(R.id.boy);
        girl = findViewById(R.id.girl);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);

        progressDialog = new ProgressDialog(Joining_activity.this);
        progressDialog2 = new ProgressDialog(Joining_activity.this);

        mRootRef = FirebaseDatabase.getInstance().getReference().child("Dealers");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        family.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox chk=(CheckBox) buttonView;
                if(chk.isChecked())
                {
                    check = check + "F" + ",";
                }
            }
        });
        locusService = new LocusService(Joining_activity.this,false);
        boolean net_status = locusService.isNetProviderEnabled();
        if(!net_status) {
            locusService.openSettingsWindow("Do you want to enable GPS ?");
        }

        men.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox chk=(CheckBox) buttonView;
                if(chk.isChecked())
                {
                    check = check + "M" + ",";
                }
            }
        });
        women.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox chk=(CheckBox) buttonView;
                if(chk.isChecked())
                {
                    check = check + "W" + ",";
                }
            }
        });
        boy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox chk=(CheckBox) buttonView;
                if(chk.isChecked())
                {
                    check = check + "B" + ",";
                }
            }
        });
        girl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox chk=(CheckBox) buttonView;
                if(chk.isChecked())
                {
                    check = check + "G" + ",";
                }
            }
        });

        if(SaveSharedPreference.getUserName(Joining_activity.this).equals("Google")) {
            full_name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString());
            if(!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString().isEmpty()){
                mob_number.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString());
            }

        }
        else{
            mUserRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       full_name.setText(dataSnapshot.child("Name").getValue().toString());
                       mob_number.setText(dataSnapshot.child("Number").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        final boolean location_status = false;
        gps_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean net_status = locusService.isNetProviderEnabled();

                if (net_status) {
                    progressDialog2.setMessage("Getting Location...");
                    progressDialog2.show();

                    locusService.startRealtimeNetListening(1000);

                    locusService.setRealTimeLocationListener(new LocusService.RealtimeListenerService() {

                        @Override
                        public void OnRealLocationChanged(Location location) {
                            if (location != null) {
                                progressDialog2.dismiss();
                                locusService.stopRealTimeGPSListening();
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Toast.makeText(Joining_activity.this, "Location set", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(Joining_activity.this, "Click once again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                else{
                    Toast.makeText(Joining_activity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });



        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_name = full_name.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                Boolean ok = true;
                if (set_name.length() == 0) {
                    full_name.setError("Required");
                    ok = false;
                } else if (house_name.getText().toString().length() == 0) {
                    house_name.setError("Required");
                    ok = false;
                } else if (mob_number.getText().toString().length() != 10) {
                    mob_number.setError("Invalid Number");
                    ok = false;
                } else if (available.getText().toString().length() == 0) {
                    available.setError("Required");
                    ok = false;
                } else if (charges.getText().toString().length() == 0) {
                    charges.setError("Required");
                    ok = false;
                }
                else if(!email_dealer.getText().toString().matches(emailPattern) || email_dealer.getText().toString().length() == 0){
                    email_dealer.setError("Invalid Email Address");
                    ok = false;
                }

                if (ok) {
                    progressDialog2.setMessage("Uploading Info...");
                    progressDialog2.show();

                        if (img_upld1 && img_upld2 && img_upld3) {
                            key = mRootRef.child(set_name.substring(0, 4));
                            key.child("Name").setValue(set_name);
                            key.child("Home_Name").setValue(house_name.getText().toString());
                            key.child("Phone").setValue(mob_number.getText().toString());
                            key.child("Available").setValue(available.getText().toString());
                            key.child("Charges").setValue("₹ " + charges.getText().toString());
                            key.child("description").setValue(description.getText().toString());
                            key.child("IdealFor").setValue(check);
                            key.child("X_co").setValue(latitude);
                            key.child("Y_co").setValue(longitude);
                            key.child("Email").setValue(email_dealer.getText().toString());

                            storageRef.child("image1").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    image1_url = uri.toString();
                                    key.child("url").setValue(image1_url);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Joining_activity.this, "Upload Image1", Toast.LENGTH_SHORT).show();

                                }
                            });


                            storageRef.child("image2").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    image2_url = uri.toString();
                                    key.child("url2").setValue(image2_url);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Joining_activity.this, "Upload Image2", Toast.LENGTH_SHORT).show();

                                }
                            });

                            storageRef.child("image3").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    image3_url = uri.toString();
                                    key.child("url3").setValue(image3_url);
                                    progressDialog2.dismiss();
                                    finish();
                                    Intent intent = new Intent(Joining_activity.this,MapActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog2.dismiss();
                                    Toast.makeText(Joining_activity.this, "Upload Image3", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                        else{
                            Toast.makeText(Joining_activity.this, "Upload All Images", Toast.LENGTH_SHORT).show();

                        }


                }
            }
        });


            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    set_name = full_name.getText().toString();
                    if(set_name.length() > 3 ) {
                        image1_clicked = true;
                        image2_clicked = false;
                        image3_clicked = false;
                        chooseImage();
                    }
                    else{
                        full_name.setError("Invalid");
                        }
                }
            });

            image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image1_clicked = false;
                    image2_clicked = true;
                    image3_clicked = false;
                    chooseImage();
                }
            });

            image3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image1_clicked = false;
                    image2_clicked = false;
                    image3_clicked = true;
                    chooseImage();
                }
            });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
                InputStream inputStream;
                try {
                    set_name = full_name.getText().toString();
                    inputStream = Joining_activity.this.getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    uploadImage(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }
    }

    private void uploadImage(Bitmap bitmap)
    {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        mountainImagesRef = null;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        set_name = full_name.getText().toString();
        storageRef = storage.getReferenceFromUrl("gs://maps2-8f29c.appspot.com/Photos/" + set_name);
        if(image1_clicked){ mountainImagesRef = storageRef.child("image1"); }
        else if(image2_clicked){mountainImagesRef = storageRef.child("image2"); }
        else if(image3_clicked){mountainImagesRef = storageRef.child("image3");}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(Joining_activity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                uploaded = false;

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //
                progressDialog.dismiss();
                uploaded = true;
                if(image1_clicked){ img_upld1 = true; }
                else if(image2_clicked){img_upld2 = true; }
                else if(image3_clicked){img_upld3 = true;}
                }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        set_name = full_name.getText().toString();
        if(set_name.length() > 0){
            storageRef = storage.getReferenceFromUrl("gs://maps2-8f29c.appspot.com/Photos/" + set_name);
            storageRef.child("image1").delete();
            storageRef.child("image2").delete();
            storageRef.child("image3").delete();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

    }
}
