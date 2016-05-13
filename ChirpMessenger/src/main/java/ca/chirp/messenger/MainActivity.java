package ca.chirp.messenger;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import ca.chirp.chirpmessenger.R;

public class MainActivity extends AppCompatActivity {

    private Firebase myFirebaseRef;
    private String currentUserId;
    private AuthData authData;
    private UserDAO userDAO;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFirebaseRef = MainDAO.getInstance().getFirebase();
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);

        final TabLayout.Tab home = tabLayout.newTab();
        final TabLayout.Tab inbox = tabLayout.newTab();

        home.setText("Group");
        inbox.setText("Nearby");

        tabLayout.addTab(home, 0);
        tabLayout.addTab(inbox, 1);

        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_start_new:
                openGroupDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openNewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select type of conversation")
                .setItems(new String[]{"Group", "Individual"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            openGroupDialog();
                        }
                    }
                });
        builder.create().show();
    }

    private void showGroupDialog(String[] usernameList, final String[] ids, Activity contextActivity) {
        final ArrayList<Integer> selectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(contextActivity);
        builder.setTitle("Select the users to chat with")
                .setMultiChoiceItems(usernameList, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    selectedItems.add(which);
                                } else if (selectedItems.contains(which)) {
                                    selectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ArrayList<String> selectedIds = new ArrayList<String>();
                        for (int pos : selectedItems) {
                            selectedIds.add(ids[pos]);
                        }
                        //openGroupConversation(selectedIds);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.create().show();
    }

    private void openGroupDialog() {
        final ArrayList<String> nameOptions = new ArrayList<String>();
        final ArrayList<String> ids = new ArrayList<String>();
        final Activity context = this;

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

            }
        });

        // Order alphabetical order
        Query query = userDAO.getRef().orderByChild("email");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    String s = userSnapshot.getValue(UserModel.class).getEmail();
                    if (s.equals(currentUserId)){
                        // Don't add to list
                    }
                    else {
                        nameOptions.add(s);
                        ids.add(s);
                    }
                }
                showGroupDialog(nameOptions.toArray(new String[]{}), ids.toArray(new String[]{}), context);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());
            }
        });
    }
}
