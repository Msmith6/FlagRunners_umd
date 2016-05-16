package com.comsci436.flagrunners;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TCF extends AppCompatActivity {
    private GoogleApiClient client;
    private Firebase mFirebase;
    ListView listView;
    HashMap<String,String> map = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> feedList = new ArrayList<HashMap<String, String>>();
    HashMap<Integer, HashSet<String>> userList = new HashMap<Integer, HashSet<String>>();
    ArrayList<Group> copy = new ArrayList<Group>();
    int selected_position  = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("TCF");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcf);

        mFirebase = new Firebase("https://radiant-fire-7313.firebaseio.com/");
        listView = (ListView) findViewById(R.id.listView);

        mFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot postSnapshot : snapshot.child("Groups").getChildren()){
                    for(DataSnapshot post : postSnapshot.getChildren()) {
                        Group group = post.getValue(Group.class);

                        if(group.getOpen_spot() > 0) {
                            map = new HashMap<String, String>();
                            map.put("groupName", group.getGroup_name());
                            map.put("host", group.getCurrent_username());
                            map.put("open", String.valueOf(group.getOpen_spot()));
                            if (group.getPassword() != "") {
                                map.put("password", "Yes");
                            } else {
                                map.put("password", "No");
                            }
                            userList.put(i, group.getUserList());
                            feedList.add(map);
                            i++;

                            // making copy
                            Group groupCopy = new Group();
                            groupCopy.setCurrent_username(group.getCurrent_username());
                            groupCopy.setGroup_name(group.getGroup_name());
                            groupCopy.setOpen_spot(group.getOpen_spot());
                            groupCopy.setPassword(group.getPassword());
                            groupCopy.setTeam_size(group.getTeam_size());
                            groupCopy.setUserList(group.getUserList());

                            copy.add(groupCopy);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {            }
        });

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, feedList, R.layout.view_item,
                new String[]{"groupName", "host", "open", "password"},
                new int[]{R.id.textViewGroupName, R.id.textViewHost, R.id.textViewOpen, R.id.textViewPassword})
        {};
        listView.setAdapter(simpleAdapter);

        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_position = position;
                if(selected_position >= 0){
                    Intent joinGroup = new Intent(TCF.this, JoinGroup.class);
                    joinGroup.putExtra("passed", copy.get(selected_position));
                    startActivity(joinGroup);
                }
            }
        });
    }


    public void cancel(View view){
        finish();
    }

    public void createGroup(View view) {
        Intent intent = new Intent(this, CreateGroup.class);
        startActivity(intent);
    }

}