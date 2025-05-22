package com.socio.sociosphere.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socio.sociosphere.Model.Follow_Orig;
import com.socio.sociosphere.R;
import com.socio.sociosphere.User;
import com.socio.sociosphere.databinding.UserSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class SEARCH_UserAdapter extends RecyclerView.Adapter<SEARCH_UserAdapter.ViewHolder> {

    Context context;
    ArrayList<User> list;

    public SEARCH_UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position); // Get the User object for this position

        // Check if user is not null before binding data
        if (user != null) {
            Picasso.get()
                    .load(user.getProfilePhoto()) // Load profile photo
                    .placeholder(R.drawable.profile)
                    .into(holder.binding.profileImage);
            holder.binding.name.setText(user.getName()); // Set user name
            holder.binding.profession.setText(user.getProfession()); // Set user profession

            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(user.getUserId())
                    .child("followers")
                    .child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // User is already followed, so show "Unfollow" button
                                holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn));
                                holder.binding.followBtn.setText("Unfollow");
                                holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.grey));
                                holder.binding.followBtn.setEnabled(true); // Enable the button for unfollow action

                                // Handle unfollow button click
                                holder.binding.followBtn.setOnClickListener(v -> {
                                    unfollowUser(user, holder);
                                });
                            } else {
                                // User is not followed yet, so show "Follow" button
                                holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_btn_bg));
                                holder.binding.followBtn.setText("Follow");
                                holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.white));
                                holder.binding.followBtn.setEnabled(true); // Enable the button for follow action

                                // Handle follow button click
                                holder.binding.followBtn.setOnClickListener(v -> {
                                    followUser(user, holder);
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error if any
                        }
                    });
        }
    }

    private void followUser(User user, ViewHolder holder) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId != null) {
            Follow_Orig follow = new Follow_Orig();
            follow.setFollowedBy(currentUserId);
            follow.setFollowedAt(new Date().getTime());

            // Add follow data to the target user's followers
            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(user.getUserId())
                    .child("followers")
                    .child(currentUserId)
                    .setValue(follow)
                    .addOnSuccessListener(aVoid -> {
                        // Update the follow button state immediately without refreshing entire list
                        FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(user.getUserId())
                                .child("followerCount")
                                .setValue(user.getFollowerCount() + 1)
                                .addOnSuccessListener(aVoid1 -> {
                                    holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn));
                                    holder.binding.followBtn.setText("Unfollow");
                                    holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.grey));
                                    Toast.makeText(context, "You followed " + user.getName(), Toast.LENGTH_SHORT).show();
                                });
                    });
        }
    }

    private void unfollowUser(User user, ViewHolder holder) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId != null) {
            // Remove follow data from the target user's followers
            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(user.getUserId())
                    .child("followers")
                    .child(currentUserId)
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Update the follow button state immediately without refreshing entire list
                        FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(user.getUserId())
                                .child("followerCount")
                                .setValue(user.getFollowerCount() - 1)
                                .addOnSuccessListener(aVoid1 -> {
                                    holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_btn_bg));
                                    holder.binding.followBtn.setText("Follow");
                                    holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.white));
                                    Toast.makeText(context, "You unfollowed " + user.getName(), Toast.LENGTH_SHORT).show();
                                });
                    });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        UserSampleBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = UserSampleBinding.bind(itemView); // Bind the layout
        }
    }
}
