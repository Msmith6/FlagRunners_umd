package com.comsci436.flagrunners;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Taemin on 2016-05-14.
 */
public class distanceAdapter extends ArrayAdapter<User> {


    public distanceAdapter (Context ctx, int resourceId, List<User> objects) {
        super( ctx, resourceId, objects );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.leaderboard_row_distance, null);
        }

        User user1 = this.getItem(position);


        TextView rank = (TextView) v.findViewById(R.id.rank);
        rank.setText(String.valueOf(position+1));

        TextView userName = (TextView) v.findViewById(R.id.userName);
        userName.setText(user1.getName());

        TextView distanceTraveled = (TextView) v.findViewById(R.id.distanceTraveled);
        distanceTraveled.setText(String.valueOf(user1.getDistance()));



        return v;
    }
}
