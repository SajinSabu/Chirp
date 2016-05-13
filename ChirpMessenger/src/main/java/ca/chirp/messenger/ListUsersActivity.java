package ca.chirp.messenger;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import ca.chirp.chirpmessenger.R;

public class ListUsersActivity extends Fragment {

    private String currentUserId;
    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayList<String> names;
    private View userView;
    private View ListUserView;
    private Button logoutButton;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;

    private Firebase myFirebaseRef;
    private AuthData authData;
    private UserDAO userDAO;

    private static String LOG_TAG = "LIST_USERS";

    public ListUsersActivity(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFirebaseRef = MainDAO.getInstance().getFirebase();

        showSpinner();

        ListUserView = getActivity().getLayoutInflater().inflate(R.layout.activity_list_users, null);
        userView = ListUserView;
        logoutButton = (Button) ListUserView.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().stopService(new Intent(getActivity(), MessageService.class));
                // Logout
                myFirebaseRef.unauth();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return ListUserView;
    }

    // TODO
    // Add group chat to conversation list
    private void setConversationsList() {
        userDAO = new UserDAO();
        authData = myFirebaseRef.getAuth();

        // Get the current user id from Firebase
        Firebase userRef = userDAO.getUserRef(authData.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if there is data at the database location
                if (dataSnapshot.exists()) {
                    currentUserId = dataSnapshot.getValue(UserModel.class).getEmail();
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "Error trying to get current user data" + firebaseError.getMessage());
            }
        });

        names = new ArrayList<String>();

        // Order alphabetical order
        Query query = userDAO.getRef().orderByChild("email");

        Log.e(LOG_TAG, "Count " + currentUserId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e(LOG_TAG, "Count " + snapshot.getChildrenCount());
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    String s = userSnapshot.getValue(UserModel.class).getEmail();
                    if (s.equals(currentUserId)){
                        // Don't add to list
                    }
                    else {
                        names.add(s);
                    }
                }

                ListView usersListView = (ListView) userView.findViewById(R.id.usersListView);
                namesArrayAdapter =
                        new ArrayAdapter<String>(getActivity(),
                                R.layout.user_list_item, names);
                usersListView.setAdapter(namesArrayAdapter);

                usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                        openConversation(names, i);
                    }
                });
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());
            }
        });

    }

    //open a conversation with one person
    public void openConversation(ArrayList<String> names, int pos) {
        Firebase userRef = MainDAO.getInstance().getFirebase();
        Log.e(LOG_TAG, "User # in Array: " + names.get(pos));
        // Order alphabetical order
        Query query = userDAO.getRef().orderByChild("email");
        // Get the selected user
        query.equalTo(names.get(pos));
        final String userSelected = names.get(pos);

        // Find user in Firebase then start the chat
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    //String s = snapshot.getValue(UserModel.class).getEmail();
                    Log.e(LOG_TAG, "Name of userSnapshot " + userSelected);
                    if (userSelected.equals(userSelected)){
                        Intent intent = new Intent(getActivity(), MessagingActivity.class);
                        intent.putExtra("RECIPIENT_ID", userSelected);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getActivity(),
                                "Error finding that user",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());
            }
        });
    }

    // Open conversation with group
    public void openGroupConversation(ArrayList<String> selectedIds) {
        String recipientString = "";
    }

    //show a loading spinner while the sinch client starts
    private void showSpinner() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                progressDialog.dismiss();
                if (!success) {
                    Toast.makeText(getActivity(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("ca.chirp.messenger.ListUsersActivity"));
    }

    @Override
    public void onResume() {
        setConversationsList();
        super.onResume();
    }
}
