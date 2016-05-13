package com.comsci436.flagrunners;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        View.OnClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Firebase mFirebase;

    private String currentUserId;

    public static boolean TCF_ENABLED = false;
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_DEPLOY = "Deploy";
    private static final String TAG_TCF = "TCF";
    private static final String FIREBASE_URL ="https://radiant-fire-7313.firebaseio.com";

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int SUBACTION_BTN_SIZE = 150;
    private final static double DEPLOYMENT_RADIUS = 402.336; //Quarter Mile, in meters
    private final static double DEGREES_LAT_TO_METERS = 111045.0;
    private final static double DEGREES_LONGITUDE_TO_METERS_AT_POLES = 111321.543;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setBackgroundDrawable(R.drawable.button_action_blue);
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(SUBACTION_BTN_SIZE, SUBACTION_BTN_SIZE);
        itemBuilder.setLayoutParams(params);
        ImageView icon1 = new ImageView(this);
        icon1.setImageResource(R.drawable.ic_flag);
        SubActionButton button1 = itemBuilder.setContentView(icon1).build();
        button1.setTag(TAG_DEPLOY);
        button1.setOnClickListener(this);
        ImageView icon2 = new ImageView(this);
        icon2.setImageResource(R.drawable.ic_tcf);
        SubActionButton button2 = itemBuilder.setContentView(icon2).build();
        button2.setOnClickListener(this);
        button2.setTag(TAG_TCF);

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .attachTo(fab)
                .build();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20 * 1000) //20 seconds
                .setFastestInterval(1 * 1000); //1 second
    }
    @Override
    protected void onStart() {
        super.onStart();
        mFirebase = new Firebase(FIREBASE_URL);
        currentUserId = mFirebase.getAuth().getUid();

        final Firebase userRef = mFirebase.child("users");
    }

    @Override
    protected void onResume() {
        mGoogleApiClient.connect();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        android.app.Fragment settingFragment = getFragmentManager().findFragmentByTag("setting_frag");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        }
        else if (settingFragment != null && settingFragment.isVisible()){
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.show();

            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().remove(settingFragment).commit();
           // fragmentManager.beginTransaction().replace(R.id.map, new SettingFragment()).addToBackStack("setting_frag").commit();\

        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {


            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.hide();

            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.map, new SettingFragment(), "setting_frag").commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        int status = ContextCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (status == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "New Location");
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(DEPLOYMENT_RADIUS)
                .strokeColor(Color.GREEN)
                .fillColor(Color.BLUE));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    /*
    Deploys a flag within a quarter mile radius of the center
    */
    private void deployFlag(Location center) {
        Random random = new Random();
        double mLat = center.getLatitude();
        double mLng = center.getLongitude();
        double d1 = random.nextDouble(), d2 = random.nextDouble(); //Multipliers
        double rand_angle = 2 * Math.PI * d1; //Our random angle, measured in radians
        double latMultiplier = DEPLOYMENT_RADIUS / DEGREES_LAT_TO_METERS;
        double lngMultiplier = Math.cos(mLat) * DEGREES_LONGITUDE_TO_METERS_AT_POLES;

        lngMultiplier = DEPLOYMENT_RADIUS / lngMultiplier;
        lngMultiplier *= (d2/2.0); //Retrieving a random value in the interval (0,lng_multiplier]
        latMultiplier *= d2; //Retrieving a random value in the interval (0,lat_multiplier]

        double newLat = mLat + latMultiplier * Math.sin(rand_angle);
        double newLng = mLng + latMultiplier * Math.cos(rand_angle);

        Firebase mFirebaseFlags = mFirebase.child("flags");
        Map<String, Double> flags = new HashMap<String,Double>();
        flags.put("latitude", newLat);
        flags.put("longitude", newLng);
        mFirebaseFlags.push().setValue(flags);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(newLat, newLng))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_neutral)));

    }

    @Override
    public void onClick(View v) {
        switch ((String) v.getTag()) {
            case "Deploy":
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    deployFlag(location);
                }
                break;
            case "TCF":
                Toast toast = Toast.makeText(this, "TCF Button Clicked", Toast.LENGTH_SHORT);
                toast.show();

        }
    }
}
