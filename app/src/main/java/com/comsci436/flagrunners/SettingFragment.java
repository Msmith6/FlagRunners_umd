package com.comsci436.flagrunners;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class SettingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    private static final String FIREBASE_URL = "https://radiant-fire-7313.firebaseio.com";

    Firebase mFirebase;
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.setting, container, false);
        mFirebase = new Firebase(FIREBASE_URL);

        Switch sw = (Switch) myView.findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(this);

        Button logout = (Button) myView.findViewById(R.id.button3);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogout();
            }
        });

        return myView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            AudioManager amanager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            amanager.setStreamMute(AudioManager.STREAM_RING, false);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            Toast.makeText(getActivity(), "Sound Enabled",
                    Toast.LENGTH_SHORT).show();
        } else {
            AudioManager amanager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            Toast.makeText(getActivity(), "Sound Disabled",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptLogout() {
        mFirebase.unauth();
        startActivity(new Intent(super.getActivity(), LoginActivity.class));
        super.getActivity().finish();
    }
}
