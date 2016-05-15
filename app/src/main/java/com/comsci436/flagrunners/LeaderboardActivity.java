package com.comsci436.flagrunners;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Leaderboards");
        setContentView(R.layout.activity_leaderboard);
        final ArrayList<User> userList = new ArrayList<User>();

        Firebase ref = new Firebase("https://radiant-fire-7313.firebaseio.com/").child("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()){
                    userList.add(new User((String) user.child("username").getValue(),
                            (long) user.child("flagsCaptured").getValue(),
                            (double) user.child("distanceTraveled").getValue()));

                }
                Collections.sort(userList, new FlagsComparator());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new UserAdapter(this, R.layout.leaderboard_row, userList));


    }
}
