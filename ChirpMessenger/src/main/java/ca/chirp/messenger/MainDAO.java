package ca.chirp.messenger;

import android.content.res.Resources;
import android.util.Log;

import com.firebase.client.Firebase;
import ca.chirp.chirpmessenger.R;

public class MainDAO {
    private Firebase firebaseRef;
    private Resources resources;

    private static MainDAO instance;
    private static UserModel currentUser;

    public static void initInstance(Resources res) {
        if (instance == null) {
            instance = new MainDAO(res);
        }
    }

    public static MainDAO getInstance() {
        return instance;
    }

    public static UserModel getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(UserModel user) {
        currentUser = user;
    }

    private MainDAO(Resources res) {
        resources = res;
        // Read and write data from Firebase database
        firebaseRef = new Firebase(resources.getString(R.string.firebase_url));
        Log.d("Testing", "test");
    }

    public Firebase getFirebase() {
        return firebaseRef;
    }
}