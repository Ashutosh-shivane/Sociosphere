package com.socio.sociosphere.Messagenew;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socio.sociosphere.Adapter.SEARCH_UserAdapter;
import com.socio.sociosphere.R;
import com.socio.sociosphere.User;

import java.util.ArrayList;
import java.util.List;

public class messagelistactivity extends AppCompatActivity {

    RecyclerView recyclerView;

    String yourName;
    DatabaseReference databaseReference;
    usersadpter usersadpter1;

    private List<User> originalList=new ArrayList<>();
    EditText searchEditTextmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messagelistactivity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("chat");

        usersadpter1=new usersadpter(this);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setAdapter(usersadpter1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersadpter1.clear(); // Clear the adapter at the start of each update
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uId = dataSnapshot.getKey();
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        String userId = uId;
                        String currentUserId = FirebaseAuth.getInstance().getUid();

                        user.setUserId(userId);
//                        Log.d("UserData", "Fetched User: " + user.toString());
//
//                        Log.d("TAG", "User ID: " + userId + ", Current User ID: " + currentUserId);

                        // Check if this user is NOT the current user
                        if (userId != null && currentUserId != null && !userId.equals(currentUserId)) {
//                            Log.d("TAG", "Adding user: " + user.getName());
                            usersadpter1.add(user);
                            originalList.add(user);// Add the user to the adapter
                        }
                    }
                }

                // Get the list of users from the adapter
                List<User> users = usersadpter1.getUserslist();
//                Log.d("TAG", "Number of users added: " + users.size());

                // Notify adapter of data changes
                usersadpter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "Database Error: " + error.getMessage());
            }
        });

        searchEditTextmsg=findViewById(R.id.searchEditTextmsg);

        searchEditTextmsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });



    }

    private void filterUsers(String query) {
        usersadpter1.clear();

        // Loop through all users and check if the username matches the query
        for (User user : originalList) {
            if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                usersadpter1.add(user);
            }
        }

        // Notify adapter of changes

        usersadpter1.notifyDataSetChanged();
    }
}