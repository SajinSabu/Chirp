package ca.chirp.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import ca.chirp.chirpmessenger.R;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button signUpButton;
    private Button loginButton;
    private EditText usernameField;
    private EditText passwordField;

    private String username;
    private String password;

    private Intent intent;
    private Intent serviceIntent;
    private Intent signupIntent;

    private Firebase chirpFirebaseRef;

    private UserDAO userDAO;

    private static String LOG_TAG = "LOGIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serviceIntent = new Intent(getApplicationContext(), MessageService.class);
        signupIntent = new Intent(getApplicationContext(), SignupActivity.class);
        chirpFirebaseRef = MainDAO.getInstance().getFirebase();
        userDAO = new UserDAO();

        // See if there is a user already logged in.
        if (chirpFirebaseRef.getAuth() != null) {
            loginDone();
        }

        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signupButton);
        usernameField = (EditText) findViewById(R.id.loginUsername);
        passwordField = (EditText) findViewById(R.id.loginPassword);

        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == loginButton) {
            handleLogin();
        }
        else if (view == signUpButton) {
            handleSignUp();
        }
    }

    private void handleSignUp() {
        startActivity(signupIntent);
    }

    private void handleLogin() {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        loginUser(username, password);
    }

    private void loginUser(String username, String password) {
        chirpFirebaseRef.authWithPassword(username, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Log.d(LOG_TAG, "User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                loginDone();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(),
                        firebaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginDone() {
        // Set the current user details as a UserModel
        AuthData authData = chirpFirebaseRef.getAuth();
        Firebase userRef = userDAO.getUserRef(authData.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if there is data at the database location
                if (dataSnapshot.exists()) {
                    Log.i(LOG_TAG, "Data Exists " + dataSnapshot.getValue(UserModel.class).getName());
                    MainDAO.setCurrentUser(dataSnapshot.getValue(UserModel.class));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "Error trying to get current user data" + firebaseError.getMessage());
            }
        });

        startActivity(intent);
        startService(serviceIntent);
    }

}
