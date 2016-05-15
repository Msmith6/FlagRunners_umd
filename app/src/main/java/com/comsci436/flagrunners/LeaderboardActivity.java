package com.comsci436.flagrunners;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class LeaderboardActivity extends AppCompatActivity {
    Context ctx = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Leaderboards");
        setContentView(R.layout.activity_leaderboard);
        final ArrayList<User> userListFlags = new ArrayList<User>();
        final ArrayList<User> userListDist = new ArrayList<User>();


        Button button1 = (Button) this.findViewById(R.id.button_dist);

        if (button1 != null) {
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userListDist.size() == 0) {
                        Firebase ref = new Firebase("https://radiant-fire-7313.firebaseio.com/").child("users");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot user : dataSnapshot.getChildren()) {
                                    userListDist.add(new User((String) user.child("username").getValue(),
                                            (long) user.child("flagsCaptured").getValue(),
                                            (double) user.child("distanceTraveled").getValue()));

                                }
                                Collections.sort(userListDist, new DistanceComparator());
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                    ListView listView = (ListView) findViewById(R.id.list);
                    listView.setAdapter(new distanceAdapter(ctx, R.layout.leaderboard_row, userListDist));
                }
            });
        }
        Button button2 = (Button) this.findViewById(R.id.button_flag);
        if (button2 != null) {
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListView listView = (ListView) findViewById(R.id.list);
                    listView.setAdapter(new UserAdapter(ctx, R.layout.leaderboard_row, userListFlags));
                }
            });
        }

        Firebase ref = new Firebase("https://radiant-fire-7313.firebaseio.com/").child("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()){
                    userListFlags.add(new User((String) user.child("username").getValue(),
                            (long) user.child("flagsCaptured").getValue(),
                            (double) user.child("distanceTraveled").getValue()));

                }
                Collections.sort(userListFlags, new FlagsComparator());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new UserAdapter(this, R.layout.leaderboard_row, userListFlags));


    }
}
