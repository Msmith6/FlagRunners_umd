package com.comsci436.flagrunners;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class RegisterActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://radiant-fire-7313.firebaseio.com";
    private Firebase mFirebase;

    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebase = new Firebase(FIREBASE_URL);

        usernameInput = (EditText) findViewById(R.id.register_user);
        emailInput = (EditText) findViewById(R.id.register_email);
        passwordInput = (EditText) findViewById(R.id.register_password);

        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptFirebaseRegister();
                }
                return false;
            }
        });

        signUpButton = (Button) findViewById(R.id.button_sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager kbm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                kbm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                attemptFirebaseRegister();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void attemptFirebaseRegister() {
        String name = this.usernameInput.getText().toString();
        final String email = this.emailInput.getText().toString();
        final String password = this.passwordInput.getText().toString();

        if (!name.isEmpty()) {
            mFirebase.createUser(email, password, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    mFirebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            String currId = authData.getUid();
                            Firebase currUser = mFirebase.child("users").child(currId);

                            Player p = new Player(usernameInput.getText().toString(), currId);

                            currUser.setValue(p);
                            finish();
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            usernameInput.setText("");
                            emailInput.setText("");
                            passwordInput.setText("");
                        }
                    });
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    usernameInput.setText("");
                    emailInput.setText("");
                    passwordInput.setText("");
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
