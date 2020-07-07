package com.example.hp.maps;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hp.maps.models.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import plugins.gligerglg.locusservice.LocusService;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public double x_co;
    public double y_co;
    Marker marker;
    LinearLayout hor_Lin;
    ViewPager viewPager;
    ViewPageAdapter adapter;
    int count;
    private Marker mSelectedMarker;
    double latitude = 0;
    double longitude = 0;
    public  Map<String, Double> hm1 ;


    ArrayList<String> ar;
    ArrayList<String> usernames ;
    private HashMap<Marker, Integer> mHashMap ;
    private HashMap<String, Double> hashmap_sorting;



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");

        ar = new ArrayList<>();
        usernames = new ArrayList<>();
        mHashMap = new HashMap<Marker, Integer>();
        hashmap_sorting = new HashMap<String, Double>();

        mMap = googleMap;
        viewPager = findViewById(R.id.view_pager_map);

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

            mMap.setOnCameraIdleListener(onCameraIdleListener);

            locusService.startRealtimeNetListening(1000);
            locusService.setRealTimeLocationListener(new LocusService.RealtimeListenerService() {
                @Override
                public void OnRealLocationChanged(Location location) {
                    if(location!=null){
                        locusService.stopRealTimeGPSListening();
                         latitude= location.getLatitude();
                         longitude = location.getLongitude();
                        LatLng latlng = new LatLng(latitude,longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,18),5000,null);
                        UpdatePages(latitude,longitude);
                    }
                }
            });


            UpdatePages(latitude,longitude);


            init();

        }

    }




    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;


    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));


    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo, mPlacePicker, big, small, btn2;

    FirebaseDatabase firebaseDatabase;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private Marker mMarker,mMarker_searched;
    private Button btn;
    private RelativeLayout rl1;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;


    private TextView mtextView;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Bundle bundle;

    private String name_of_user,SignIN;
    private String Google_SignIn;
    private String Email;

    private NavigationView navigationView;
    private View headerView;

    private DatabaseReference mDatabase;
    private DatabaseReference Userss;

    private ProgressDialog mProgressDialog;
    LocusService locusService;

    SupportMapFragment mapFragment;
    private Fragment map_fragment;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    String username;


    private CircleImageView photo;
    private TextView remove;


    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_map);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        setSupportActionBar(toolbar);

        rl1 = (RelativeLayout) findViewById(R.id.relLayout_main);

        navigationView = (NavigationView) findViewById(R.id.nav_view_map);
        remove = findViewById(R.id.delete);

        getLocationPermission();

        locusService = new LocusService(MapActivity.this,false);
        boolean net_status = locusService.isNetProviderEnabled();
        if(!net_status){
            locusService.openSettingsWindow("Do you want to enable GPS ?");
        }


       mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    finish();
                    Intent intent = new Intent(MapActivity.this,SignIn.class);
                    startActivity(intent);

                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(SaveSharedPreference.getUserName(MapActivity.this).equals("Google")) {
            mDatabase.child("Dealers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (final DataSnapshot s : dataSnapshot.getChildren()) {
                        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals((String) s.child("Email").getValue())) {
                            remove.setVisibility(View.VISIBLE);
                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MapActivity.this, databaseError.toString(), Toast.LENGTH_LONG).show();

                }

            });
            }
        else{

            mDatabase.child("Dealers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (final DataSnapshot s : dataSnapshot.getChildren()) {
                        if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().equals("+91" + (String) s.child("Phone").getValue())) {
                            remove.setVisibility(View.VISIBLE);
                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MapActivity.this, databaseError.toString(), Toast.LENGTH_LONG).show();

                }

            });
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id == R.id.SignOut){
                    mAuth = FirebaseAuth.getInstance();
                    SaveSharedPreference.clearUserName(MapActivity.this);
                    mAuth.signOut();
                    Intent i = new Intent(MapActivity.this, SignIn.class);
                    startActivity(i);
                    finish();
                }
                else if(id == R.id.Terms_and_conditions_menu){
                    Intent intent = new Intent(MapActivity.this, Terms_and_conditions.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }

                else if(id == R.id.privacy_policy_menu){
                    Intent intent = new Intent(MapActivity.this, Privacy_policy.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }


                else if(id == R.id.nav_help){
                    Intent intent = new Intent(MapActivity.this, Help.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }

                else if(id == R.id.join_us){
                    Intent intent = new Intent(MapActivity.this, Joining_activity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }

                return false;
            }
        });


        navigationView.getMenu().getItem(0).setChecked(true);
        headerView = navigationView.getHeaderView(0);
        mtextView = (TextView) headerView.findViewById(R.id.nav_header_title);
        photo = (CircleImageView)headerView.findViewById(R.id.imageView);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layoutId);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        bundle = getIntent().getExtras();
        assert bundle != null;

        if(SaveSharedPreference.getUserName(MapActivity.this).equals("Google")){
            mtextView.setText("Hello," + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

            Glide.with(MapActivity.this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).into(photo);
        }
        else{
            mtextView.setText("Hello," + SaveSharedPreference.getUserName(MapActivity.this));
            }


        mProgressDialog = new ProgressDialog(this);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, Remove_activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    private String signinmethod() {
        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("google.com")) {
                return "Google";
            }
            else{
                return "OTP";
            }
        }
        return null;
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener((AdapterView.OnItemClickListener) mAutocompleteClickListener);


        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);


        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE){
                    //method for searching
                    geoLocate();
                }

                return false;


            }


        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicjed gps");

                getDeviceLocation();
            }
        });





     //   HideSoftKeyboard();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }
    }



    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocationName(searchString,1);

        }
        catch (IOException e){
            Log.e(TAG, "geoLocate: IOException:" + e.getMessage() );

        }

        if(list.size() > 0){

            Address address = list.get(0);

            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
            Log.d(TAG,"geolocate : found a location:" + address.toString());
            //moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18),5000,null);
        }

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting device location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);

        try{
            if(mLocationPermissionsGranted){

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18),4000,null);

                            //moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"My Location");
                            }
                        else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();



                        }

                    }
                });
            }
        }catch(SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }



    private void moveCamera(LatLng latLng,float zoom,PlaceInfo placeInfo){
        Log.d(TAG, "moveCamera: moving the camera to :lat:" + latLng.latitude + ", lng: "+ latLng.longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13),4000,null);



        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));

        if(placeInfo != null){
            try{
                String snippet = "Address : " +placeInfo.getAddress() + "\n" ;

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                UpdatePages(latLng.latitude,latLng.longitude);

                mMarker_searched = mMap.addMarker(options);

            }catch (NullPointerException e){
                Log.e(TAG, "moveCamera: NullPointerException" + e.getMessage() );
            }
            }
        else{
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        HideSoftKeyboard();
    }

    private Marker moveCamera(LatLng latLng,float zoom,String title){
        Log.d(TAG, "moveCamera: moving the camera to :lat:" + latLng.latitude + ", lng: "+ latLng.longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16),1000,null);
        Marker current = null;
        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
             current = mMap.addMarker(options);
        }
        //HideSoftKeyboard();
        return current;
        }
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(MapActivity.this);
        }
    }


    private void getLocationPermission()
    {

        Log.d(TAG, "getLocationPermission: getting locations permissions");
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted  = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this,permission,LOCATION_PERMISSION_REQUEST_CODE);
            }

        }else{
            ActivityCompat.requestPermissions(this,permission,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted  = false ;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i=0;i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted  = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission denied");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted  = true;
                    //initialize our map
                    initMap();
                }

            }
        }
    }

    public void HideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(
                        this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(this.getCurrentFocus()).getWindowToken(), 0);
    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully :" + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            
            try{
                mPlace = new PlaceInfo();
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setName(place.getName().toString());
                mPlace.setAttributions(place.getAttributions().toString());
                mPlace.setId(place.getId());
                mPlace.setLatLng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setWebsiteuri(place.getWebsiteUri());

                Log.d(TAG, "onResult: place:" + mPlace.toString());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointExceoption :" + e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude),10f,mPlace);
            places.release();
        }
    };

    @Override
    public void onBackPressed() {


        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            MapActivity.super.onBackPressed();
                            finish();
                        }
                    }).create().show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {

            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);

    }

    void SignOut()
    {
        SaveSharedPreference.clearUserName(MapActivity.this);
        Intent i = new Intent(MapActivity.this, SignIn.class);
        startActivity(i);
        finish();
    }

    private String getUserName() {
        mAuth = FirebaseAuth.getInstance();
        final String mobile = mAuth.getCurrentUser().getPhoneNumber();
        username = "trhy";
        return username;

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    public void onLocationChanged(Location location) {
        }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }





    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }

    public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list =
                new LinkedList<Map.Entry<String, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private void UpdatePages(final double latitude, final double longitude) {
        ar = new ArrayList<>();
        usernames = new ArrayList<>();
        mMap.clear();
        mSelectedMarker = null;
        mHashMap = new HashMap<Marker, Integer>();
        hashmap_sorting = new HashMap<String, Double>();

        mDatabase.child("Dealers").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = 0;

                for (DataSnapshot s : dataSnapshot.getChildren()) {

                    x_co = (double) s.child("X_co").getValue();
                    y_co = (double) s.child("Y_co").getValue();
                    hashmap_sorting.put(s.getKey().toString(),Math.pow(x_co-latitude,2)+Math.pow(y_co-longitude,2));

                }

                hm1 = sortByValue(hashmap_sorting);

                count = 0;
                for (final Map.Entry<String, Double> en : hm1.entrySet()) {
                    mDatabase.child("Dealers").child(en.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot != null) {
                                x_co = (double) dataSnapshot.child("X_co").getValue();
                                y_co = (double) dataSnapshot.child("Y_co").getValue();

                                LatLng location = new LatLng(x_co, y_co);
                                marker = mMap.addMarker(new MarkerOptions().position(location).title(dataSnapshot.getKey()));
                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                mHashMap.put(marker, count);
                                count++;

                                ar.add((String) dataSnapshot.child("url").getValue());
                                usernames.add(en.getKey());
                                adapter = new ViewPageAdapter(MapActivity.this, ar, usernames);
                                viewPager.setClipToPadding(false);
                                viewPager.setPadding(0, 0, 50, 0);
                                viewPager.setPageMargin(20);
                                viewPager.setAdapter(adapter);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        if (null != mSelectedMarker) {
                            mSelectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        }
                        Marker mMarker = (Marker) getKeyFromValue(mHashMap,position);
                        mSelectedMarker = mMarker;

                        mSelectedMarker =  moveCamera(mMarker.getPosition(),DEFAULT_ZOOM,"balabaj");
                        mSelectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    }

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        if(marker.equals(mMarker_searched)){marker.showInfoWindow();}
                        else {
                            int pos = mHashMap.get(marker);
                            viewPager.setCurrentItem(pos, true);
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }
                        return true;
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

}






