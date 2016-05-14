package com.comsci436.flagrunners;

import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class Friends extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://radiant-fire-7313.firebaseio.com";
    Firebase mFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mFirebase = new Firebase(FIREBASE_URL);

        FriendsFragment friendsFragment = new FriendsFragment();
        getFragmentManager().beginTransaction().add(android.R.id.content, friendsFragment).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_add:
                addFriendDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addFriendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText emailInput = new EditText(this);
        emailInput.setHint("Player Email");

        builder.setView(emailInput)
                .setMessage("Enter the player's email:")
                .setTitle("Add a Friend")
                .setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addFriend(emailInput.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addFriend(final String email) {
        Firebase fb = new Firebase(FIREBASE_URL).child("users");
        fb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    Player p = dataSnap.getValue(Player.class);

                    if (email.equals(p.getEmail())) {
                        final Firebase curr = mFirebase.child("users")
                                .child(mFirebase.getAuth().getUid())
                                .child("friendsList");
                        curr.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long num = dataSnapshot.getChildrenCount();
                                curr.child(Long.toString(num)).setValue(dataSnap.getKey());
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // Do nothing
            }
        });
    }
}
