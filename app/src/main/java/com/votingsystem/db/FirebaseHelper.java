package com.votingsystem.db;

import com.google.firebase.database.FirebaseDatabase;
import com.votingsystem.db.model.User;
import com.votingsystem.util.StringConstants;

public class FirebaseHelper {

    String TAG = getClass().getSimpleName();
    private static FirebaseHelper helper;

    public static FirebaseHelper getHelper() {
        if (helper == null)
            helper = new FirebaseHelper();
        return helper;
    }

    private FirebaseHelper() {

    }

    public void addUser(String id, String password, boolean isAdmin) {
        if (isAdmin) {
            FirebaseDatabase.getInstance().getReference().child(StringConstants.DB_ADMIN).child(id).setValue(new User(id, password));
        } else {
            FirebaseDatabase.getInstance().getReference().child(StringConstants.DB_USERS).child(id).setValue(new User(id, password));
        }
    }


}
