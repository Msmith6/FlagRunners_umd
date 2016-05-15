package com.comsci436.flagrunners;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class JoinGroup extends AppCompatActivity {
    Firebase mFirebase;
    ListView listView;
    HashMap<String, String> map = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> feedList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Join Group");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        mFirebase = new Firebase("https://radiant-fire-7313.firebaseio.com/");
        listView = (ListView) findViewById(R.id.listViewTwo);

      /*  HashSet<String> passed = (HashSet<String>)getIntent().getSerializableExtra("passedValue");

        for(String temp : passed){
            map.put("name", temp);
        }
        feedList.add(map);
*/
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, feedList, R.layout.view_group,
                new String[]{"name"},
                new int[]{R.id.textViewName}) {};
        listView.setAdapter(simpleAdapter);

        listView.setTextFilterEnabled(true);
    }

    public void cancel(View view){
        finish();
    }
    public void joinGroup(View view){

    }
}
