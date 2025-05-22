package com.socio.sociosphere;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socio.sociosphere.Fragment.AddPostFragment;
import com.socio.sociosphere.Fragment.HomeFragment;
import com.socio.sociosphere.Fragment.NotificationsFragment;
import com.socio.sociosphere.Fragment.ProfileFragment;
import com.socio.sociosphere.Fragment.SearchFragment;
import com.socio.sociosphere.Messagenew.CallingScr;
import com.socio.sociosphere.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String loggedInUsername="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        MainActivity.this.setTitle("My Profile");

        // Set the default fragment
        binding.toolbar.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HomeFragment());
        transaction.commit();

        // Set listener for bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            // Select fragment based on the menu item
//            switch (item.getItemId()) {
//                case R.id.Hometest:
//                    fragment = new HomeFragment();
//                    break;
//
//                case R.id.searchtest:
//                    fragment = new NotificationsFragment();
//                    break;
//
//                case R.id.posttest:
//                    fragment = new AddPostFragment();
//                    break;
//
//                case R.id.likestest:
//                    fragment = new SearchFragment();
//                    break;
//
//                case R.id.profiletest:
//                    fragment = new ProfileFragment();
//                    break;
//
//                default:
//                    Toast.makeText(MainActivity.this, "Unexpected item selected", Toast.LENGTH_SHORT).show();
//                    return false;
//            }

            int itemId = item.getItemId();
            if (itemId == R.id.Hometest) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.searchtest) {
//                fragment = new NotificationsFragment();
                fragment = new SearchFragment();
            } else if (itemId == R.id.posttest) {
                fragment = new AddPostFragment();
            } else if (itemId == R.id.likestest) {
                fragment = new NotificationsFragment();
//                fragment = new SearchFragment();
            } else if (itemId == R.id.profiletest) {
                fragment = new ProfileFragment();
            } else {
                Toast.makeText(MainActivity.this, "Unexpected item selected", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Adjust toolbar visibility
            binding.toolbar.setVisibility(item.getItemId() == R.id.profiletest ? View.VISIBLE : View.GONE);

            return loadFragment(fragment);
        });
        fetchLoggedInUserName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void fetchLoggedInUserName() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            // Fetch the username from Firebase Database using the logged-in user's UID
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
//                        Toast.makeText(MainActivity.this, "chekc toast", Toast.LENGTH_SHORT).show();

                        // Assuming "name" is the field for the username
                        loggedInUsername = dataSnapshot.child("name").getValue(String.class);
                        // Start listening for incoming calls after fetching the username
                        listenForIncomingCalls();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                    Toast.makeText(MainActivity.this, "Error fetching username", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to listen for incoming calls
    private void listenForIncomingCalls() {
//        Toast.makeText(MainActivity.this, "inside listning activity", Toast.LENGTH_SHORT).show();

        DatabaseReference callsRef = FirebaseDatabase.getInstance().getReference("calls");

        callsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String channelName = dataSnapshot.child("channelName").getValue(String.class);
                String userName = dataSnapshot.child("userName").getValue(String.class);
                String callerUid = dataSnapshot.child("callerUid").getValue(String.class);

//                Toast.makeText(MainActivity.this, "user and channer found."+channelName+" "+userName, Toast.LENGTH_SHORT).show();

                // Only trigger the notification if this phone is not the one making the call
                if (userName != null && channelName != null) {
                    // If the userName is not equal to the current phone's user, show the incoming call notification
                    if (!userName.equals(loggedInUsername)) {

//                        Toast.makeText(MainActivity.this, "Incoming call from: " + userName + "\nChannel: " + channelName, Toast.LENGTH_LONG).show();

                        // This phone is receiving a call, trigger the notification
                        showIncomingCallNotification(userName, channelName);
                    }else{
//                        Toast.makeText(MainActivity.this, "Call is from the logged-in user, ignoring.", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    // Method to display a notification or alert for incoming calls
    private void showIncomingCallNotification(String userName, String channelName) {
        // Create the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Set the title and message for the dialog
        builder.setTitle("Incoming Call");
        builder.setMessage("Call from: " + userName );

        // Set the "Accept" button
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle Accept action
                Intent intent = new Intent(MainActivity.this, CallingScr.class);
                intent.putExtra("USER_NAME", userName); // Send the caller's username
                intent.putExtra("CHANNEL_NAME", channelName); // Send the channel name
                startActivity(intent);
            }
        });

        // Set the "Reject" button
        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle Reject action
                deleteAllCalls();
                // You can add rejection logic here (e.g., notify the caller or do nothing)
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteAllCalls() {
        // Get a reference to the "calls" directory in the Firebase Realtime Database
        DatabaseReference callsRef = FirebaseDatabase.getInstance().getReference("calls");

        // Remove all calls from the "calls" directory
        callsRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Show success message

                })
                .addOnFailureListener(e -> {
                    // Show error message if there is an issue

                });
    }

}
