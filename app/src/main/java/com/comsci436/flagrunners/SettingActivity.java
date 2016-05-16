package com.comsci436.flagrunners;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

public class SettingActivity extends AppCompatActivity implements  CompoundButton.OnCheckedChangeListener {
    private Switch sw ;
    private EditText newUsername ;
    private Button button ;
    private AuthData currAuth;
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            amanager.setStreamMute(AudioManager.STREAM_RING, false);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            Toast.makeText(this, "Sound Enabled",
                    Toast.LENGTH_SHORT).show();
        } else {
            AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            Toast.makeText(this, "Sound Disabled",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.setting);
        sw = (Switch) this.findViewById(R.id.switch1);
        newUsername = (EditText) findViewById(R.id.editText_setting);
        button = (Button) findViewById(R.id.button_setting);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase ref = new Firebase("https://radiant-fire-7313.firebaseio.com/").child("users");
                currAuth = ref.getAuth();
                Firebase currUsername = ref.child("users").child(currAuth.getUid()).child("username");
                currUsername.setValue(newUsername.getText().toString());
                Toast msg = Toast.makeText(getBaseContext(),
                        "Username changed to " + newUsername.getText().toString(), Toast.LENGTH_LONG);
                msg.show();
            }
        });

        sw.setOnCheckedChangeListener(this);
    }

}
