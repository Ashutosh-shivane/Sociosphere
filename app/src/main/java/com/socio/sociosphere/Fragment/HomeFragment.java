package com.socio.sociosphere.Fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.content.Intent;


import com.cooltechworks.views.shimmer.ShimmerAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.socio.sociosphere.Adapter.PostAdapter;
import com.socio.sociosphere.Adapter.StoryAdapter;
import com.socio.sociosphere.AiActivity;
import com.socio.sociosphere.Messagenew.messagelistactivity;
import com.socio.sociosphere.Model.Post;
import com.socio.sociosphere.Model.Story;
import com.socio.sociosphere.Model.UserStories;
import com.socio.sociosphere.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class HomeFragment extends Fragment {

    RecyclerView storyRv;
    ShimmerRecyclerView dashboardRV;

    ArrayList<Story> list;
    ArrayList<Post> postList;

    FirebaseDatabase database;
    FirebaseStorage storage;

    FirebaseAuth auth;

    RoundedImageView addStoryImage;

    ActivityResultLauncher<String> galleryLauncher;






    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        storyRv = view.findViewById(R.id.storyRV);
        dashboardRV = view.findViewById(R.id.dashboardRV); // Add this line

        list = new ArrayList<>();




        StoryAdapter adapter = new StoryAdapter(list, getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        storyRv.setLayoutManager(linearLayoutManager);
        storyRv.setNestedScrollingEnabled(false);

        storyRv.setAdapter(adapter);

        database.getReference()
                .child("stories").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            list.clear();
                            for(DataSnapshot storySnapshot : snapshot.getChildren()){
                                Story story = storySnapshot.getValue(Story.class);
                                story.setSotryBy(storySnapshot.getKey());
                                story.setStoryAt(storySnapshot.child("postedBy").getValue(Long.class));

                                ArrayList<UserStories> stories = new ArrayList<>();

                                for (DataSnapshot snapshot1: storySnapshot.child("UserStories").getChildren()){
                                    UserStories userStories = snapshot1.getValue(UserStories.class);
                                    stories.add(userStories);
                                }

                                story.setStories(stories); // added setStories method in the Story.java
                                list.add(story);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        // DashboardRV

        postList = new ArrayList<>();
        PostAdapter postAdapter = new PostAdapter(postList, getContext());
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        dashboardRV.setLayoutManager(lm);  // Use dashboardRV now that it's initialized
        dashboardRV.setNestedScrollingEnabled(false);
        dashboardRV.setAdapter(postAdapter);

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    postList.add(post);
                }
                Collections.reverse(postList);
                dashboardRV.showShimmerAdapter();
                dashboardRV.hideShimmerAdapter();
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        addStoryImage = view.findViewById(R.id.storyImg);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                addStoryImage.setImageURI(result);
                final StorageReference reference = storage.getReference().child("stories")
                        .child(auth.getUid())
                        .child(new Date().getTime()+"");

                reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Story story = new Story();
                                story.setStoryAt(new Date().getTime());

                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("postedBy")
                                        .setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                UserStories stories = new UserStories(uri.toString(), story.getStoryAt());
                                                database.getReference()
                                                        .child("stories")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .child("UserStories")
                                                        .push()
                                                        .setValue(stories);
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        });

        View aiclickaction = view.findViewById(R.id.aiclickitem);
        aiclickaction.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AiActivity.class);
            startActivity(intent);
        });

        ImageView msgImageView = view.findViewById(R.id.FragmentHome_msg);

// Set the click listener
        msgImageView.setOnClickListener(v -> {
            // Show a Toast message
//            Toast.makeText(getContext(), "Opening message Activity...", Toast.LENGTH_SHORT).show();

            // Create an intent to start AiActivity
            Intent intent = new Intent(getContext(), messagelistactivity.class); // or AiActivity.class if in Activity
            startActivity(intent);
        });



        return view;
    }


    // Method to handle statusCircle click
//    public void onAiclickCircle(View view) {
//        // Code to handle the click event
//        Toast.makeText(getContext(), "Status Circle clicked!", Toast.LENGTH_SHORT).show();
//    }

}
