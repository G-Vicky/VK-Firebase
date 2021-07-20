package com.vkapps.vkfirebase.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDBHelper {
    private final DatabaseReference databaseReference;
    public FirebaseDBHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    public boolean addNewUser(User user) {
        if(user != null) {
            databaseReference.child("users").child(user.getUID()).setValue(user);
            databaseReference.child("custom").setValue(user);
            System.out.println("adding: \n" + user);
            return true;
        }
        return false;
    }

}
