package com.vkapps.vkfirebase;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText usernameInput;
    EditText emailAddressInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();           // get current user info
        if(currentUser != null) {                                           //move to homepage if user already signed in
            Toast.makeText(this, "user already logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        usernameInput = findViewById(R.id.username);
        emailAddressInput = findViewById(R.id.emailAddress);
        passwordInput = findViewById(R.id.password);

    }

    public void signUpWithEmail(View view) {
        if(!isValidInput()) {
            Toast.makeText(this, "invalid input", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(getEmailAddress(), getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Toast.makeText(SignUpActivity.this, "signed up successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "unable to signup!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private String getEmailAddress() {
        return emailAddressInput.getText().toString().trim();
    }

    private String getPassword() {
        return passwordInput.getText().toString().trim();
    }

    private String getUsername() {
        return usernameInput.getText().toString().trim();
    }

    public boolean isValidInput() {
        if(TextUtils.isEmpty(getEmailAddress()) || TextUtils.isEmpty(getPassword()) || TextUtils.isEmpty(getUsername())) return false;
        return getPassword().length() >= 6 || getUsername().length() >= 3;
    }


    public void moveToSignIn(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}