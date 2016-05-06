package com.comsci436.flagrunners;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Michael on 5/5/2016.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
