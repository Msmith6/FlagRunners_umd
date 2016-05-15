package com.comsci436.flagrunners;

import android.app.*;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map.Entry;

/**
 * Created by thomasyang on 5/13/16.
 */
public class StatsFragment extends Fragment {

    private static final String FIREBASE_URL = "https://radiant-fire-7313.firebaseio.com";

    private TextView username;
    private TextView flagsCaptured;
    private TextView flagsDeployed;
    private TextView distanceTraveled;
    private TextView capMostFrom;
    private TextView capMostBy;

    Firebase mFirebase;
    Firebase currFire;
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.stats, container, false);
        mFirebase = new Firebase(FIREBASE_URL);
        currFire = mFirebase.child("users").child(mFirebase.getAuth().getUid());

        username = (TextView) myView.findViewById(R.id.stat_username);
        flagsCaptured = (TextView) myView.findViewById(R.id.flags_cap_num);
        flagsDeployed = (TextView) myView.findViewById(R.id.flags_dep_num);
        distanceTraveled = (TextView) myView.findViewById(R.id.dist_trav_num);
        capMostFrom = (TextView) myView.findViewById(R.id.cap_most_num);
        capMostBy = (TextView) myView.findViewById(R.id.cap_by_num);

        // Will always update whenever a value in the Firebase database changes
        currFire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player p = dataSnapshot.getValue(Player.class);
                String mString = Double.toString(p.getDistanceTraveled());
                int i = mString.indexOf('.');
                mString = mString.substring(0, i + 3);//Displays miles to 2 decimal places

                username.setText(p.getUsername());
                flagsCaptured.setText(Long.toString(p.getFlagsCaptured()));
                flagsDeployed.setText(Long.toString(p.getFlagsDeployed()));
                distanceTraveled.setText(mString + " mi.");

                // Get the data for who has the player has captured the most flags from
                String player = "";
                int num = 0;

                for (Entry<String, Integer> entry : p.getCapturedFromMap().entrySet()) {
                    String k = entry.getKey();
                    int value = entry.getValue();

                    if (value > num) {
                        num = value;
                        player = k;
                    }
                }

                if (num == 0) {
                    // Player has captured no one's flags
                    capMostFrom.setText("Nobody");
                } else {
                    // Player has captured other player's flags
                    Firebase opp = new Firebase(FIREBASE_URL).child("users").child(player).child("username");
                    opp.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            capMostFrom.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            // do nothing
                        }
                    });
                }

                // The same as above, except for who has captured the most of the player's own flags
                player = "";
                num = 0;

                for (Entry<String, Integer> entry : p.getCapturedByMap().entrySet()) {
                    String k = entry.getKey();
                    int value = entry.getValue();

                    if (value > num) {
                        num = value;
                        player = k;
                    }
                }

                if (num == 0) {
                    capMostBy.setText("Nobody");
                } else {
                    Firebase opp = new Firebase(FIREBASE_URL).child("users").child(player).child("username");
                    opp.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            capMostBy.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            // do nothing
                        }
                    });
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return myView;
    }
}
