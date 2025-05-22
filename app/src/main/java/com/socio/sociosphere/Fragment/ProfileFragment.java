package com.socio.sociosphere.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.socio.sociosphere.Adapter.PROF_FollowersAdapter;
//import com.socio.sociosphere.Model.Follow;
import com.socio.sociosphere.Model.Follow;
import com.socio.sociosphere.R;
import com.socio.sociosphere.User;
import com.socio.sociosphere.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ArrayList<Follow> list;

    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;

    // ActivityResultLauncher for selecting images
    private ActivityResultLauncher<Intent> pickCoverImageLauncher;
    private ActivityResultLauncher<Intent> pickProfileImageLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        // Initialize the ActivityResultLauncher for selecting cover images
        pickCoverImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        binding.imageView4.setImageURI(uri); // Update the cover image view

                        // Upload cover image to Firebase Storage
                        StorageReference reference = storage.getReference().child("cover_photo").child(auth.getUid());

                        reference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(getContext(), "Cover photo saved!", Toast.LENGTH_SHORT).show();
                            reference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                database.getReference().child("Users").child(auth.getUid()).child("coverPhoto").setValue(uri1.toString());
                            });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
        );

        // Initialize the ActivityResultLauncher for selecting profile images
        pickProfileImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        binding.profileImage.setImageURI(uri); // Update the profile image view

                        // Upload profile image to Firebase Storage
                        StorageReference reference = storage.getReference().child("profile_photo").child(auth.getUid());

                        reference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(getContext(), "Profile photo saved!", Toast.LENGTH_SHORT).show();
                            reference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                database.getReference().child("Users").child(auth.getUid()).child("profilePhoto").setValue(uri1.toString());
                            });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Load user data from Firebase
        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getCoverPhoto()).placeholder(R.drawable.placeholder).into(binding.imageView4);
                    Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.placeholder).into(binding.profileImage);
                    binding.uName.setText(user.getName());
                    binding.profession.setText(user.getProfession());

                    binding.followers.setText(user.getFollowerCount()+ "");
                    

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

        FirebaseDatabase.getInstance().getReference().child("posts")
                .orderByChild("postedBy") // Use 'postedBy' to filter posts
                .equalTo(FirebaseAuth.getInstance().getUid()) // Match the current user's UID
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int postCount = 0;

                        // Iterate through all posts and count them
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            postCount++;
                        }

                        // Set the count to TextView10
                        binding.textView10.setText( String.valueOf(postCount)); // Assuming binding is properly initialized
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error if necessary
                        Toast.makeText(getContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        // Set up RecyclerView for friends list
        list = new ArrayList<>();

        PROF_FollowersAdapter adapter = new PROF_FollowersAdapter(list, getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.friendRV.setLayoutManager(llm);
        binding.friendRV.setAdapter(adapter);

        database.getReference().child("Users")
                .child(auth.getUid())
                .child("followers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        list.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            Follow follow = dataSnapshot.getValue(Follow.class);
                            list.add(follow);
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        // Handle the click for changing the cover photo
        binding.changeCoverPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickCoverImageLauncher.launch(intent);
        });

        // Handle the click for changing the profile photo
        binding.verifiedAccount.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickProfileImageLauncher.launch(intent);
        });

        return binding.getRoot();
    }
}