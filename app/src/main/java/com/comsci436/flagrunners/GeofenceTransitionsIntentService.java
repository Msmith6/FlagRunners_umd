package com.comsci436.flagrunners;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by Michael on 5/15/2016.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    public static final String GEOFENCE_TRIGGERED_TAG =
            "com.comsci436.flagrunners.GeofenceTransitionsIS";
    protected static final String TAG = "GeofenceTransitionsIS";

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Log.i(MainActivity.TAG, "IN GEOFENCE TRIGGERED SERVICE");
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // There is only one geofence in this list
            Geofence mGeofence = (Geofence) triggeringGeofences.get(0);
            String requestId = mGeofence.getRequestId();
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.GeofenceTriggeredReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(GEOFENCE_TRIGGERED_TAG, requestId);
            sendBroadcast(broadcastIntent);
        }

    }

}
