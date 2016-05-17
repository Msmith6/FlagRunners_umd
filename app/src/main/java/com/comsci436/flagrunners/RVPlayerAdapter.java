package com.comsci436.flagrunners;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

/**
 * Created by thomasyang on 5/14/16.
 */
public class RVPlayerAdapter extends RecyclerView.Adapter<RVPlayerAdapter.PlayerViewHolder> {

    private static final String FIREBASE_URL ="https://radiant-fire-7313.firebaseio.com";

    List<String> players;

    RVPlayerAdapter(List<String> players) {
        this.players = players;
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        PlayerViewHolder pvh = new PlayerViewHolder(v);

        return pvh;
    }

    @Override
    public void onBindViewHolder(final PlayerViewHolder holder, int pos) {
        Firebase f = (new Firebase(FIREBASE_URL)).child("users").child(players.get(pos));

        f.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player p = dataSnapshot.getValue(Player.class);

                holder.playerName.setText(p.getUsername());
                holder.playerCap.setText("Captured: " + Long.toString(p.getFlagsCaptured()));
                holder.playerDep.setText("Deployed: " + Long.toString(p.getFlagsDeployed()));
                holder.playerFriendNum.setText("Friends: " + (p.getFriendsList().size() - 1));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // do nothing
            }
        });
    }

    @Override
    public int getItemCount() {
        if (players != null) {
            return players.size();
        }
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView playerName;
        TextView playerCap;
        TextView playerDep;
        TextView playerFriendNum;

        public PlayerViewHolder(View v) {
            super(v);

            cv = (CardView) v.findViewById(R.id.friend_cv);
            playerName = (TextView) v.findViewById(R.id.friend_name);
            playerCap = (TextView) v.findViewById(R.id.friend_cap);
            playerDep = (TextView) v.findViewById(R.id.friend_dep);
            playerFriendNum = (TextView) v.findViewById(R.id.friend_num);
        }
    }
}
