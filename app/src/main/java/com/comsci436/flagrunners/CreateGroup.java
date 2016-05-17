package com.comsci436.flagrunners;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;



public class CreateGroup extends AppCompatActivity {
    private Firebase mFirebase;
    private int isErrorOne = 0;
    private int isErrorTwo = 0;
    String username = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Create Group");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mFirebase = new Firebase("https://radiant-fire-7313.firebaseio.com/");

        mFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot n = dataSnapshot.child("users").child(mFirebase.getAuth().getUid()).child("username");
                username = n.getValue().toString();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void cancelCreateGroup(View view){
        finish();
    }
    public void createGroupvalue(View view) {

        EditText size = (EditText) findViewById(R.id.value_teamSize);
        EditText name = (EditText) findViewById(R.id.value_groupName);
        EditText password = (EditText) findViewById(R.id.value_password);
        Integer teamSize = 0;

        if (size.getText().toString().equals(null) || size.getText().toString().equals("")) {
            size.setError("Team Size is required!");
        } else {
            teamSize = Integer.parseInt(size.getText().toString());
        }
        if (teamSize > 5) {
            size.setError("Team Size should be lower than 6!");
            isErrorOne = -1;
        } else if (teamSize < 1) {
            size.setError("Team Size should be grater than 0");
            isErrorOne = -1;
        } else{
            isErrorOne = 0;
        }

        if (name.getText().toString().trim().equals("")) {
            name.setError("Group Name is required!");
            isErrorTwo = -1;
        }else {
            isErrorTwo = 0;
        }

        if(isErrorOne == 0 && isErrorTwo == 0) {


            String groupName = name.getText().toString();
            String groupPassword = password.getText().toString();

            Firebase groupList = mFirebase.child("Groups");
            HashMap<String, Group> group = new HashMap<String, Group>();
            Group current = new Group();
            current.setCurrent_username(username);
            current.setGroup_name(groupName);
            current.setOpen_spot(teamSize - 1);
            current.setPassword(groupPassword);
            current.setTeam_size(teamSize);
            current.addUserList(current.getCurrent_username());

            group.put("createdGroup", current);
            groupList.push().setValue(group);

            Toast.makeText(CreateGroup.this, "New Group Has Been Created", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(CreateGroup.this, TCF.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}