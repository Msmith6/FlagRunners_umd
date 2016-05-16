package com.comsci436.flagrunners;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class JoinGroup extends AppCompatActivity {
    ListView listView;
    ArrayList<HashMap<String, String>> feedList = new ArrayList<HashMap<String, String>>();
    Firebase mFirebase = new Firebase("https://radiant-fire-7313.firebaseio.com/");
    String username = "";
    Group passed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Join Group");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        listView = (ListView) findViewById(R.id.listViewTwo);

        passed = (Group)getIntent().getSerializableExtra("passed");

        for(String temp : passed.getUserList()){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", temp);
            feedList.add(map);
        }


        SimpleAdapter simpleAdapter = new SimpleAdapter(this, feedList, R.layout.view_group,
                new String[]{"name"},
                new int[]{R.id.textViewName}) {};
        listView.setAdapter(simpleAdapter);
    }

    public void cancel(View view){
        finish();
    }
    public void joinGroup(View view){
        if (passed.getPassword().equals("")) {
            mFirebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    DataSnapshot n = snapshot.child("users").child(mFirebase.getAuth().getUid()).child("username");
                    username = n.getValue().toString();

                    for (DataSnapshot postSnapshot : snapshot.child("Groups").getChildren()) {
                        for (DataSnapshot post : postSnapshot.getChildren()) {
                            Group group = post.getValue(Group.class);
                            String curr = group.getCurrent_username();

                            if (curr.equals(passed.getCurrent_username())) {
                                HashSet<String> userList = passed.getUserList();
                                userList.add(username);

                                int open = passed.getOpen_spot() - 1;

                                post.getRef().child("userList").setValue(userList);
                                post.getRef().child("open_spot").setValue(open);
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

            Toast.makeText(JoinGroup.this, "You Join a Group", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(JoinGroup.this, TCF.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{
            //If the room has password
        }
    }
}
