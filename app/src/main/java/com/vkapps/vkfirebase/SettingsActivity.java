package com.vkapps.vkfirebase;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    EditText usernameInput;
    EditText emailAddressInput;
    EditText phoneNoInput;
    LinearLayout otpLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        usernameInput = findViewById(R.id.username);
        emailAddressInput = findViewById(R.id.emailAddress);
        phoneNoInput = findViewById(R.id.phoneNo);
        otpLayout = findViewById(R.id.otpLayout);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        otpLayout.setVisibility(View.GONE);
    }

    public void sendVerifyMail(View view) {
    }

    public void sendOTP(View view) {
    }

    public void verifyOTP(View view) {
    }
}