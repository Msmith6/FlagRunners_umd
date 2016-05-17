package com.comsci436.flagrunners;

import android.content.Context;
import android.graphics.Color;
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
    Firebase ref;
    ArrayList<User> userListFlags;
    ArrayList<User> userListDist;
    boolean initialStart = true;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Leaderboards");
        setContentView(R.layout.activity_leaderboard);
        userListFlags = new ArrayList<User>();
        userListDist = new ArrayList<User>();
        listView = (ListView) findViewById(R.id.list);

        final Button button1 = (Button) this.findViewById(R.id.button_dist);
        button1.setBackgroundColor(Color.DKGRAY);
        final Button button2 = (Button) this.findViewById(R.id.button_flag);
        button2.setBackgroundColor(Color.LTGRAY);

        ref = new Firebase("https://radiant-fire-7313.firebaseio.com").child("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()){
                    userListFlags.add(new User((String) user.child("username").getValue(),
                            (long) user.child("flagsCaptured").getValue(),
                            (double) user.child("distanceTraveled").getValue()));

                    String mString = String.valueOf((double) user.child("distanceTraveled").getValue());
                    int i = mString.indexOf('.');
                    mString = mString.substring(0, i + 3);//Displays miles to 2 decimal places
                    double distanceTraveled = Double.valueOf(mString);

                    userListDist.add(new User((String) user.child("username").getValue(),
                            (long) user.child("flagsCaptured").getValue(),
                            distanceTraveled));

                }
                Collections.sort(userListFlags, new FlagsComparator());
                Collections.sort(userListDist, new DistanceComparator());
                if (initialStart) {
                    listView.setAdapter(new UserAdapter(LeaderboardActivity.this, R.layout.leaderboard_row, userListFlags));
                    initialStart = false;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(Color.LTGRAY);
                button2.setBackgroundColor(Color.DKGRAY);
                ListView listView = (ListView) findViewById(R.id.list);
                listView.setAdapter(new distanceAdapter(ctx, R.layout.leaderboard_row, userListDist));
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(Color.LTGRAY);
                button1.setBackgroundColor(Color.DKGRAY);
                ListView listView = (ListView) findViewById(R.id.list);
                listView.setAdapter(new UserAdapter(ctx, R.layout.leaderboard_row, userListFlags));
            }
        });

    }
}
