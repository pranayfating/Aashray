package com.example.hp.maps;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer;

import java.util.ArrayList;
import java.util.Map;

public class Dealer_detail extends AppCompatActivity  {


    private Bundle bundle;

    private int position;
    private DatabaseReference mDatabase;

    ViewPager viewPager;
    ViewPageAdapter adapter;

    ArrayList<String> ar = new ArrayList<String>();
    FloatingActionButton direction_button;
    LinearLayout sliderDots,IdealFor_view ;
    private int dotscount;
    private ImageView[] dots;

    private TextView available,Charges,HomeName,descrption;
    String Phone,Ideal;
    BookFlipPageTransformer bookFlipPageTransformer ;

    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton floatingActionButton;
    AppCompatButton btn_call;
    Map map;
    private ArrayList<String> usernames;
    int pos_current = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_dealer_detail);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Dealers");

        bundle = getIntent().getExtras();
        position = bundle.getInt("position");
        usernames = bundle.getStringArrayList("usernames");

        viewPager = findViewById(R.id.view_pager_new);

        sliderDots = findViewById(R.id.dots_view);
        dots = new ImageView[3];

        IdealFor_view = findViewById(R.id.IdealFor_view);
        available = (TextView) findViewById(R.id.available);
        Charges = (TextView) findViewById(R.id.Charges);
        descrption = (TextView) findViewById(R.id.Description);

        btn_call = findViewById(R.id.btn_call);
        direction_button = findViewById(R.id.direction_button);
        bookFlipPageTransformer = new BookFlipPageTransformer();

        for (String name : usernames) {
            if (pos_current == position) {
                mDatabase.child(name).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot s) {
                        ar.add((String) s.child("url").getValue());
                        ar.add((String) s.child("url2").getValue());
                        ar.add((String) s.child("url3").getValue());

                        available.setText((String) s.child("Available").getValue());
                        Charges.setText((String) s.child("Charges").getValue());
                        Ideal = (String) s.child("IdealFor").getValue();
                        Phone = (String) s.child("Phone").getValue();
                        descrption.setText((String) s.child("description").getValue());

                        adapter = new ViewPageAdapter(Dealer_detail.this, ar, null);
                        viewPager.setClipToPadding(false);
                        viewPager.setPageMargin(20);
                        viewPager.setPaddingRelative(0, 0, 0, 0);
                        viewPager.setAdapter(adapter);

                        direction_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Double.toString((Double) s.child("X_co").getValue()) + "," + Double.toString((Double) s.child("Y_co").getValue()));
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);

                            }
                        });


                        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
                        collapsingToolbarLayout.setTitle((String) s.child("Home_Name").getValue());

                        for (int i = 0; i < Ideal.length(); i = i + 2) {
                            IdealFor_view.setLeft(10);
                            if (Ideal.charAt(i) == 'M') {
                                ImageView image = new ImageView(Dealer_detail.this);
                                image.setBackgroundResource(R.drawable.man);
                                IdealFor_view.addView(image);
                            }
                            if (Ideal.charAt(i) == 'W') {
                                ImageView image = new ImageView(Dealer_detail.this);
                                image.setBackgroundResource(R.drawable.women);
                                IdealFor_view.addView(image);
                            }
                            if (Ideal.charAt(i) == 'B') {
                                ImageView image = new ImageView(Dealer_detail.this);
                                image.setBackgroundResource(R.drawable.boy);
                                IdealFor_view.addView(image);
                            }
                            if (Ideal.charAt(i) == 'G') {
                                ImageView image = new ImageView(Dealer_detail.this);
                                image.setBackgroundResource(R.drawable.girl);
                                IdealFor_view.addView(image);
                            }
                            if (Ideal.charAt(i) == 'F') {
                                ImageView image = new ImageView(Dealer_detail.this);
                                image.setBackgroundResource(R.drawable.family);
                                IdealFor_view.addView(image);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;

            } else {
                pos_current++;
            }
        }

            bookFlipPageTransformer.setEnableScale(true);
            bookFlipPageTransformer.setScaleAmountPercent(5f);
            viewPager.setPageTransformer(true, bookFlipPageTransformer);

            dotscount = ar.size();
            for (int i = 0; i < 3; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);
                sliderDots.addView(dots[i], params);
            }


            dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int position) {

                    for (int i = 0; i < 3; i++) {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                    }
                    dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));


                }


                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + Phone));
                    startActivity(callIntent);
                }
            });




        }
    @Override
    public void finish () {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
