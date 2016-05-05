package ca.chirp.messenger;

import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class UserDAO {
    // User Data Access Object for Firebase
    private String LOG_TAG = "UserDAO";
    // Access child nodes in the data, accessing users
    private Firebase chirpRef = MainDAO.getInstance().getFirebase().child("users");

    public UserDAO() {}

    public Firebase getRef() {
        return chirpRef;
    }

    public void addUser(String uid, UserModel user) {
        chirpRef.child(uid).setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.e(LOG_TAG, "Saving user data error " + firebaseError.getMessage());
                }
            }
        });
    }

    public Firebase getUserRef(String uid) {
        return chirpRef.child(uid);
    }

}
