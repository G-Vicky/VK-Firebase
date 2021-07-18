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
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText emailAddressInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        emailAddressInput = findViewById(R.id.emailAddress);
    }

    public void sendPwdResetMail(View view) {
        if(!isValidInput()) {
            Toast.makeText(this, "invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.sendPasswordResetEmail(getEmailAddress()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ForgetPasswordActivity.this, "check mail to reset password", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgetPasswordActivity.this, MainActivity.class));
                    finish();
                } else {
                    System.out.println(task.getException());
                    System.out.println(task.getException().getMessage());
                    Toast.makeText(ForgetPasswordActivity.this, "couldn't able to send mail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getEmailAddress() {
        return emailAddressInput.getText().toString();
    }

    public boolean isValidInput() {
        return !TextUtils.isEmpty(getEmailAddress());
    }

    public void moveToSignIn(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}