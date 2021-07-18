package com.vkapps.vkfirebase;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView username = findViewById(R.id.username);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null) {
            username.setText(currentUser.getEmail());
            System.out.println("user email: " + currentUser.getEmail());
            System.out.println("display name: " + currentUser.getDisplayName());
        }


        if(currentUser.isEmailVerified()) {
            // TODO: verify email
        }
    }

    public void signOutUser(View view) {
        firebaseAuth.signOut();
        Toast.makeText(this, "signed out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void moveToSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}