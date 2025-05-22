package com.socio.sociosphere.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socio.sociosphere.Adapter.SEARCH_UserAdapter;
import com.socio.sociosphere.R;
import com.socio.sociosphere.User;
import com.socio.sociosphere.databinding.FragmentSearchBinding;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;

    // List to store all users and filtered users
    ArrayList<User> list = new ArrayList<>();
    ArrayList<User> filteredList = new ArrayList<>();

    FirebaseAuth auth;
    FirebaseDatabase database;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        // Set up RecyclerView
        SEARCH_UserAdapter adapter = new SEARCH_UserAdapter(getContext(), filteredList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.usersRV.setLayoutManager(layoutManager);
        binding.usersRV.setAdapter(adapter);

        // Fetch users from the database
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> newList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) { // Ensure user is not null
                        user.setUserId(dataSnapshot.getKey());
                        if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                            newList.add(user);
                        }
                    }
                }

                // Update the list
                list.clear();
                list.addAll(newList);
                filteredList.clear();
                filteredList.addAll(list); // Initially, show all users
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error, e.g., log it or show a message to the user
            }
        });

        // Add TextWatcher to EditText for real-time filtering
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return binding.getRoot();
    }

    // Filter the users based on the query
    private void filterUsers(String query) {
        filteredList.clear();

        // Loop through all users and check if the username matches the query
        for (User user : list) {
            if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }

        // Notify adapter of changes
        SEARCH_UserAdapter adapter = (SEARCH_UserAdapter) binding.usersRV.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
