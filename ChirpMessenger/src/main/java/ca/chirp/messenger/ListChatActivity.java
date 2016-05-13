package ca.chirp.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class ListChatActivity extends Fragment{

    private String currentUserId;
    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayList<String> names;
    private View chatView;
    private View ListChatView;

    private Firebase myFirebaseRef;
    private UserDAO userDAO;

    private static String LOG_TAG = "LIST_CHAT";

    public ListChatActivity(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFirebaseRef = MainDAO.getInstance().getFirebase();

        ListChatView = getActivity().getLayoutInflater().inflate(R.layout.activity_list_chat, null);
        chatView = ListChatView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return ListChatView;
    }

    // TODO
    // Only show group conversations, empty if there are no group conversations
    private void setConversationsList() {
        userDAO = new UserDAO();
        AuthData authData = myFirebaseRef.getAuth();

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
                    if (!s.equals(currentUserId)){
                        names.add(s);
                    }
                }

                ListView chatListView = (ListView) chatView.findViewById(R.id.chatListView);
                namesArrayAdapter =
                        new ArrayAdapter<String>(getActivity(),
                                R.layout.chat_list_item, names);
                chatListView.setAdapter(namesArrayAdapter);

                chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    // Open conversation with group
    public void openGroupConversation(ArrayList<String> selectedIds) {
        String recipientString = "";

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

    @Override
    public void onResume() {
        setConversationsList();
        super.onResume();
    }
}
