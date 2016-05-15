package com.comsci436.flagrunners;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.AuthData;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.Collection;
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

    private static boolean initialStart = true;
    private GeofenceTriggeredReceiver receiver;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private AuthData currAuth;
    private Firebase mFirebase;
    private Firebase mFirebaseFlags;
    private Location lastLocation;
    private PendingIntent mGeofencePendingIntent;
    private Map<String, Marker> mMarkers = new HashMap<>();
    private Map<String, Flag> mFlags = new HashMap<>();
    private Map<Marker, Geofence> markerToGeofence = new HashMap<>();
    private Button mButton;
    private double distanceTraveled;
    public static boolean TCF_ENABLED = false;

    private String flagKey;
    private String flagDeployer;
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_DEPLOY = "Deploy";
    private static final String TAG_TCF = "TCF";
    private static final String FIREBASE_URL ="https://radiant-fire-7313.firebaseio.com";

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int SUBACTION_BTN_SIZE = 150; //Modifies TCF and Deploy button size
    private final static int GEOFENCE_RADIUS_METERS = 100;
    private final static double MAX_DEPLOYMENT_RADIUS = 402.336; //Quarter mile, in meters
    private final static double MIN_DEPLOYMENT_RADIUS = 150.0;
    private final static double DEGREES_LAT_TO_METERS = 111045.0;
    private final static double DEGREES_LONGITUDE_TO_METERS_AT_POLES = 111321.543;



    public class GeofenceTriggeredReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "com.comsci436.intent.action.MESSAGE_PROCESSED";

        // This onReceive method will make the capture button visible to the user and sets the
        // geofenceId field so that when the player presses the capture button, the correct
        // geofence will be removed
        @Override
        public void onReceive(Context context, Intent intent) {
            flagKey =
                    intent.getStringExtra(GeofenceTransitionsIntentService.GEOFENCE_TRIGGERED_TAG);
            mButton.setVisibility(View.VISIBLE);
        }
    }

    public static class Flag {
        private double latitude;
        private double longitude;
        private String flagType; // Either TYPE_TCF or TYPE_NEUTRAL
        private String gameId;
        private String deployer; // Name of person who deployed flag, Null if mode is "TCF"
        final static String TYPE_NEUTRAL = "neutral";
        final static String TYPE_TCF = "tcf";

        public Flag() {
            super();
        }

        public Flag(double latitude, double longitude, String flagType, String gameId, String deployer) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.flagType = flagType;
            this.gameId = gameId;
            this.deployer = deployer;
        }

        public String getFlagType() {
            return flagType;
        }

        public String getGameId() {
            return gameId;
        }

        public String getDeployer() {
            return deployer;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mButton = (Button) findViewById(R.id.capture_button);
        mButton.setVisibility(View.INVISIBLE);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureFlag(v);
            }
        });


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

        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .attachTo(fab)
                .build();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (actionMenu.isOpen()) {
                    actionMenu.close(true);
                }
            }
        };
        drawer.addDrawerListener(toggle);
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
        mFirebaseFlags = mFirebase.child("testFlags");
        currAuth = mFirebase.getAuth();
        IntentFilter filter = new IntentFilter(GeofenceTriggeredReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new GeofenceTriggeredReceiver();
        registerReceiver(receiver, filter);
        mGoogleApiClient.connect();
    }

   @Override
    protected void onResume() {
        super.onResume();
        initialStart = true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        android.app.Fragment settingFragment = getFragmentManager().findFragmentByTag("setting_frag");
        android.app.Fragment statsFragment = getFragmentManager().findFragmentByTag("stats_frag");

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (settingFragment != null && settingFragment.isVisible()){
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.show();

            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().remove(settingFragment).commit();
           // fragmentManager.beginTransaction().replace(R.id.map, new SettingFragment()).addToBackStack("setting_frag").commit();\

        } else if (statsFragment != null && statsFragment.isVisible()) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.show();

            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().remove(statsFragment).commit();
        } else {
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

        if (id == R.id.nav_flist) {
            // Start the new activity with friends
            startActivity(new Intent(MainActivity.this, Friends.class));

        } else if (id == R.id.nav_stats) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.hide();

            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.map, new StatsFragment(), "stats_frag")
                    .commit();
        } else if (id == R.id.nav_leader) {
            // Show leaderboard
        } else if (id == R.id.nav_manage) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.hide();

            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.map, new SettingFragment(), "setting_frag")
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Prevents Flags from being clickable
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mFirebaseFlags.addChildEventListener(new ChildEventListener() {
            private GoogleMap cMap = mMap; //Obtaining our map ref

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Flag mFlag = dataSnapshot.getValue(Flag.class);
                LatLng mLatLng = new LatLng(mFlag.getLatitude(), mFlag.getLongitude());

                if (mFlag.getFlagType().equals(Flag.TYPE_NEUTRAL)) {
                    Marker mMarker = cMap.addMarker(new MarkerOptions()
                            .position(mLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_neutral)));
                    mMarkers.put(dataSnapshot.getKey(), mMarker);
                    mFlags.put(dataSnapshot.getKey(), mFlag);

                } else  {
                    Log.i(TAG, "MUST BE A TCF FLAG");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Do nothing, children in /flags are only added or removed, never changed
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String mKey = dataSnapshot.getKey();
                Marker markerToRemove = mMarkers.remove(mKey);
                mFlags.remove(mKey);

                if (markerToRemove != null) {
                    markerToRemove.remove();

                    if (markerToGeofence.get(markerToRemove) != null) {
                        removeGeofence(markerToRemove);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //Do nothing, children in /flags are only added or removed, never moved
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //Do nothing
            }
        });
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

        if (location.getAccuracy() >= 30.0) { // Discard low accuracy locations
            return;
        }
        if (initialStart) {
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            initialStart = false;
        }

        // This block focuses on dynamically adding geofences to markers within a quarter
        // mile of the user and removing geofences of markers further than a quarter mile away

        Log.i(TAG, "Beginning dynamic geofencing");
        Collection<Marker> mCollection = mMarkers.values();
        Object[] mArr = mCollection.toArray();
        for (Object obj : mArr) {
            Marker marker = (Marker) obj;
            Location targetLocation = new Location("");
            LatLng mLatLng = marker.getPosition();
            targetLocation.setLatitude(mLatLng.latitude);
            targetLocation.setLongitude(mLatLng.longitude);
            Geofence mGeofence = markerToGeofence.get(marker);

            if (location.distanceTo(targetLocation) <= MAX_DEPLOYMENT_RADIUS) {
                //Build a geofence if one did not yet exist at the location
                if (mGeofence == null) {
                    addGeofence(marker);
                }

            } else {
                //Remove a geofence if one did exist at the location
                if (mGeofence != null) {
                    removeGeofence(marker);
                }
            }
        }

        if (lastLocation == null) {
            lastLocation = location;
        } else {
            Log.i(TAG, "Calculating new distance");
            distanceTraveled = location.distanceTo(lastLocation) / 1609.34; // Converting to miles

            Firebase currUser = mFirebase.child("users").child(currAuth.getUid()).child("distanceTraveled");
            currUser.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    if (mutableData.getValue() == null) {
                        mutableData.setValue(0.0);
                    } else {
                        mutableData.setValue((Double) mutableData.getValue() + distanceTraveled);
                    }

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (firebaseError != null) {
                        System.out.println("Firebase counter increment failed: " + firebaseError.getMessage());
                    } else {
                        System.out.println("Firebase counter increment succeeded.");
                    }
                }
            });
        }

    }

    /*
    Deploys a flag within a quarter mile radius of the center
    */
    private void deployFlag(Location center) {
        Random random = new Random();
        double mLat = center.getLatitude();
        double mLng = center.getLongitude();

        //Intention: Ensures no flag is dropped within 150 (MIN_DEPLOYMENT_RADIUS) meters of the user
        //What actually happens is that no flag is dropped within ~130 meters of the user
        //This is due to longitude changing depending on the latitude, so accounting for that
        //Shrunk the max radius of longitude and made it that much closer to the user's location
        //This is still acceptable behavior as the geofence of any flag will have radius 100 meters.
        //Just wanted to avoid have the user deploy and capture the same flag in quick succession.
        double minMultiplier = MIN_DEPLOYMENT_RADIUS/MAX_DEPLOYMENT_RADIUS;

        //Multipliers
        double d1 = random.nextDouble();
        double d2 = minMultiplier + minMultiplier * random.nextDouble();

        double rand_angle = 2 * Math.PI * d1; //Our random angle, measured in radians
        double latMultiplier = MAX_DEPLOYMENT_RADIUS / DEGREES_LAT_TO_METERS;
        double lngMultiplier = Math.cos(mLat) * DEGREES_LONGITUDE_TO_METERS_AT_POLES;

        lngMultiplier = MAX_DEPLOYMENT_RADIUS / lngMultiplier;
        lngMultiplier *= (d2/2.0);
        latMultiplier *= d2;

        double newLat = mLat + latMultiplier * Math.sin(rand_angle);
        double newLng = mLng + lngMultiplier * Math.cos(rand_angle);
        String newChild = newLat + ":" + newLng;
        newChild = newChild.replace(".","");

        /*
        Map<String, Double> flags = new HashMap<String,Double>();
        flags.put("latitude", newLat);
        flags.put("longitude", newLng);
        */
        String id = currAuth.getUid();
        Flag newFlag = new Flag(newLat, newLng, Flag.TYPE_NEUTRAL, null, id);

        //The childEventListener of mFirebaseFlags will handle adding any new flags to the
        //user's map
        mFirebaseFlags.child(newChild).setValue(newFlag);
        Toast toast = Toast.makeText(this, "Flag Deployed", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 170);
        toast.show();

        Firebase currUser = mFirebase.child("users").child(currAuth.getUid()).child("flagsDeployed");
        currUser.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue((Long) mutableData.getValue() + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                if (firebaseError != null) {
                    System.out.println("Firebase counter increment failed.");
                } else {
                    System.out.println("Firebase counter increment succeeded.");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch ((String) v.getTag()) {
            case TAG_DEPLOY:
                //TODO: Set a 1 hour cooldown for deploying a flag
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    deployFlag(location);
                }
                break;
            case TAG_TCF:
                //TODO: implement TCF button press
                Toast toast = Toast.makeText(this, "TCF Button Clicked", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 170);
                toast.show();
                if (TCF_ENABLED) {
                    //Go to Game Overview Activitys
                } else {
                    //Go to TCF Lobby
                    //TCF_ENABLED is switched to true once a game starts
                    Intent intent = new Intent(this, TCF.class);
                    startActivity(intent);

                }

        }
    }

    @NonNull
    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    private void removeGeofence(Marker marker) {
        LatLng mLatLng = marker.getPosition();
        String requestId = mLatLng.latitude + ":" + mLatLng.longitude;
        ArrayList<String> mList = new ArrayList<String>();

        mList.add(requestId);
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                mList
        );

        markerToGeofence.put(marker, null);
        Log.i(TAG, "Removed old geofence");
    }

    private void addGeofence(Marker marker) {
        LatLng mLatLng = marker.getPosition();
        String requestId = mLatLng.latitude + ":" + mLatLng.longitude;
        requestId = requestId.replace(".","");
        Geofence newGeofence = new Geofence.Builder()
                .setRequestId(requestId)
                .setCircularRegion(
                        mLatLng.latitude,
                        mLatLng.longitude,
                        GEOFENCE_RADIUS_METERS
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(5000) //5 seconds
                .build();
        markerToGeofence.put(marker, newGeofence);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(newGeofence),
                    getGeofencePendingIntent()
            );
        }
        Log.i(TAG, "Added new geofence");
    }

    private void captureFlag(View v) {
        Toast toast = Toast.makeText(this, "Flag captured!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 170);
        toast.show();

        Firebase mChild = mFirebaseFlags.child(flagKey); //Entry for Flag
        Flag mFlag = mFlags.get(flagKey);
        mChild.setValue(null); // Delete marker entry; Geofence and Marker will be removed in onChildRemoved()

        // TODO: Update flag capture count, capturedFrom map, and the flag's original user's capturedBy map

        // Hide "Capture" button
        v.setVisibility(View.INVISIBLE);

        // Incrementing Flags Captured
        Firebase currUser = mFirebase.child("users").child(currAuth.getUid()).child("flagsCaptured");
        currUser.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue((Long) mutableData.getValue() + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                if (firebaseError != null) {
                    System.out.println("Firebase counter increment failed.");
                } else {
                    System.out.println("Firebase counter increment succeeded.");
                }
            }
        });

        //Adding to user's capturedFromMap
        flagDeployer = mFlag.getDeployer(); //The flag deployer's key

        if (!flagDeployer.equals(currAuth.getUid())) { //Only update if it's not our own flag

            //Updating Capturer's stats
            Firebase currUserCaptures = mFirebase.child("users").child(currAuth.getUid()).child("capturedFromMap");
            currUserCaptures.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    HashMap<String, Integer> capturedFromMap =
                            (HashMap<String, Integer>) mutableData.getValue();

                    Integer captures = capturedFromMap.get(flagDeployer);
                    if (captures == null) {
                        capturedFromMap.put(flagDeployer, 1);
                    } else {
                        capturedFromMap.put(flagDeployer, captures + 1);
                    }
                    mutableData.setValue(capturedFromMap);


                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (firebaseError != null) {
                        System.out.println("Firebase map change failed. " + firebaseError.getMessage());
                    } else {
                        System.out.println("Firebase map change succeeded.");
                    }
                }
            });

            //Updating Deployer's stats
            Firebase deployerCaptured = mFirebase.child("users").child(flagDeployer).child("capturedByMap");
            deployerCaptured.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    HashMap<String, Integer> capturedByMap =
                            (HashMap<String, Integer>) mutableData.getValue();

                    Integer captures = capturedByMap.get(currAuth.getUid());
                    if (captures == null) {
                        capturedByMap.put(currAuth.getUid(), 1);
                    } else {
                        capturedByMap.put(currAuth.getUid(), captures + 1);
                    }
                    mutableData.setValue(capturedByMap);


                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (firebaseError != null) {
                        System.out.println("Firebase map change failed.");
                    } else {
                        System.out.println("Firebase map change succeeded.");
                    }
                }
            });
        }
    }

}
