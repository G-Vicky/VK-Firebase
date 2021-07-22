package com.vkapps.vkfirebase;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.FirebaseDatabase;
import com.vkapps.vkfirebase.firebase.FirebaseDBHelper;
import com.vkapps.vkfirebase.firebase.User;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseDBHelper database;
    private GoogleSignInClient mGoogleSignInClient;
    EditText emailAddressInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        database = new FirebaseDBHelper();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        emailAddressInput = findViewById(R.id.emailAddress);
        passwordInput = findViewById(R.id.password);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();           // get current user info
        if(currentUser != null) {                                           //move to homepage if user already signed in
            Toast.makeText(this, "user already logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    public void signInWithGoogle(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signINWithGoogleResult.launch(signInIntent);
    }

    ActivityResultLauncher<Intent> signINWithGoogleResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            Toast.makeText(MainActivity.this, "unable to sign in with google", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user != null) {
                                User newUser = new User(user.getDisplayName(), user.getEmail(), user.getUid());
                                database.addNewUser(newUser);
                            }
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "unable to sign in with google", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signInWithEmail(View view) {
        if(!isValidInput()) {
            Toast.makeText(this, "invalid input", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(getEmailAddress(), getPassword()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "invalid username/password", Toast.LENGTH_SHORT).show();
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

    public boolean isValidInput() {
        if(TextUtils.isEmpty(getEmailAddress()) || TextUtils.isEmpty(getPassword())) return false;
        return getPassword().length() >= 6;
    }

    public void moveToFgtPwd(View view) {
        startActivity(new Intent(this, ForgetPasswordActivity.class));
    }

    public void moveToSignUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}