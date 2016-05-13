package com.comsci436.flagrunners;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class LoginActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://torrid-torch-4377.firebaseio.com";
    private Firebase mFirebase;

    private EditText emailInput;
    private EditText passwordInput;
    private Button signInButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mFirebase = new Firebase(FIREBASE_URL);

        emailInput = (EditText) findViewById(R.id.editText_email);
        passwordInput = (EditText) findViewById(R.id.editText_password);

        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptFirebaseLogin();
                }
                return false;
            }
        });

        signInButton = (Button) findViewById(R.id.button_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager kbm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                kbm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                attemptFirebaseLogin();
            }
        });

        registerButton = (Button) findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager kbm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                kbm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void attemptFirebaseLogin() {
        String email = this.emailInput.getText().toString();
        String password = this.passwordInput.getText().toString();

        mFirebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                emailInput.setText("");
                passwordInput.setText("");
            }
        });
    }
}
