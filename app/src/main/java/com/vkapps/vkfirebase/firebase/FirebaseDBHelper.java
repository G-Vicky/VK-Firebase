package com.vkapps.vkfirebase.firebase;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

public class FirebaseDBHelper {
    private final FirebaseDatabase firebaseDatabase;
    private final DatabaseReference rootRef;
    private final DatabaseReference userRef;
    private User user;

    public FirebaseDBHelper() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = firebaseDatabase.getReference();
        userRef = rootRef.child("users");
    }
    public boolean addNewUser(User user) {
        if(user != null) {
            System.out.println("adding: \n" + user);
            userRef.child(user.getUID()).setValue(user);
            return true;
        }
        return false;
    }

    public User getUserDetails(String UID) {
        userRef.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot != null)    user = snapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return user;
    }

}
