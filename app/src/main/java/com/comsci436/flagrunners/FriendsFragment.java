package com.comsci436.flagrunners;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

/**
 * Created by thomasyang on 5/14/16.
 */
public class FriendsFragment extends Fragment {

    private static final String FIREBASE_URL = "https://radiant-fire-7313.firebaseio.com";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private List<String> players;

    Firebase mFirebase;
    View myView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.friends, container, false);
        mFirebase = new Firebase(FIREBASE_URL);

        mRecyclerView = (RecyclerView) myView.findViewById(R.id.friend_rv);
        mRecyclerView.setHasFixedSize(true);

        mLinearLayoutManager = new LinearLayoutManager(super.getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Firebase flist = mFirebase.child("users").child(mFirebase.getAuth().getUid()).child("friendsList");

        flist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                players = (List<String>) dataSnapshot.getValue();
                players.remove(0);
                mAdapter = new RVPlayerAdapter(players);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // do nothing
            }
        });

        return myView;
    }
}
