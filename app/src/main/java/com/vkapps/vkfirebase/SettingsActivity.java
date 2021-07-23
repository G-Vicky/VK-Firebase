package com.vkapps.vkfirebase;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.vkapps.vkfirebase.firebase.FirebaseDBHelper;
import com.vkapps.vkfirebase.firebase.User;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneVerificationCallback;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseUser currentUser;
    FirebaseDBHelper dbHelper;
    User user;
    EditText usernameInput;
    EditText otpInput;
    EditText emailAddressInput;
    EditText phoneNoInput;
    LinearLayout otpLayout;
    Button verifyMailBtn;
    Button sendOTPBtn;
    Button resendOTPBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        usernameInput = findViewById(R.id.username);
        emailAddressInput = findViewById(R.id.emailAddress);
        phoneNoInput = findViewById(R.id.phoneNo);
        otpLayout = findViewById(R.id.otpLayout);
        verifyMailBtn = findViewById(R.id.verifyMailBtn);
        otpInput = findViewById(R.id.otpInput);
        sendOTPBtn = findViewById(R.id.sendOTPBtn);
        resendOTPBtn = findViewById(R.id.resendOTPBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        dbHelper = new FirebaseDBHelper();
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        otpLayout.setVisibility(View.GONE);
        resendOTPBtn.setVisibility(View.GONE);

        fillUserDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser.isEmailVerified()) {
            verifyMailBtn.setVisibility(View.GONE);
        }
        phoneVerificationCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                System.out.println("credentials: auto verified! "+ credential); //auto verification can be implemented here
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                System.out.println("OTP Error: " + e.getMessage());
                Toast.makeText(SettingsActivity.this, "unable to send otp", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(SettingsActivity.this, "otp has been sent", Toast.LENGTH_SHORT).show();
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void fillUserDetails() {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                System.out.println(user);
                if(user != null) {
                    usernameInput.setText(user.getUsername());
                    usernameInput.setEnabled(false);
                    emailAddressInput.setText(user.getEmailAddress());
                    emailAddressInput.setEnabled(false);
                    if(user.getPhoneNumber().length() > 3) {
                        phoneNoInput.setText(user.getPhoneNumber());
                        phoneNoInput.setEnabled(false);
                        sendOTPBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void sendVerifyMail(View view) {
        if(currentUser.isEmailVerified()) {
            Toast.makeText(this, "email already verified", Toast.LENGTH_SHORT).show();
        } else {
            currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    verifyMailBtn.setVisibility(View.GONE);
                    Toast.makeText(SettingsActivity.this, "verification mail has been sent", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(SettingsActivity.this, "unable to verify mail", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void sendOTP(View view) {
        otpLayout.setVisibility(View.VISIBLE);
        sendOTPBtn.setVisibility(View.GONE);
        resendOTPBtn.setVisibility(View.VISIBLE);
        if(!isValidPhoneNumber()) {
            return;
        }
        String phoneNumber = "+91" + getPhoneNumber();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(phoneVerificationCallback)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(phoneVerificationCallback)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyOTP(View view) {
        if(!isValidOTP()) {
            Toast.makeText(this, "enter valid otp", Toast.LENGTH_SHORT).show();
            return;
        }
        String otpCode = getOTPText();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "phone number verified", Toast.LENGTH_SHORT).show();
                    dbHelper.updatePhoneNumber(user.getUID(), getPhoneNumber());
                    phoneNoInput.setEnabled(false);
                    sendOTPBtn.setVisibility(View.GONE);
                    resendOTPBtn.setVisibility(View.GONE);
                    otpLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(SettingsActivity.this, "invalid otp", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getPhoneNumber() {
        return phoneNoInput.getText().toString().trim();
    }

    public String getOTPText() {
        return otpInput.getText().toString().trim();
    }

    public boolean isValidOTP() {
        String code = getOTPText();
        return !code.equals("") && code.length() == 6;
    }

    public boolean isValidPhoneNumber() {
        String phoneNumber = getPhoneNumber();
        return (!TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 10);
    }

    public void resendOTP(View view) {
        if(!isValidPhoneNumber()) {
            Toast.makeText(this, "enter valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        resendVerificationCode(getPhoneNumber(), mResendToken);
    }
}