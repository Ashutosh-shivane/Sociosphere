package com.socio.sociosphere.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socio.sociosphere.CommentActivity;
import com.socio.sociosphere.Model.Post;
import com.socio.sociosphere.R;
import com.socio.sociosphere.User;
import com.socio.sociosphere.databinding.DashboardRvSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder>{

    ArrayList<Post> list;
    Context context2;

    public PostAdapter(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context2 = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context2).inflate(R.layout.dashboard_rv_sample, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Post model = list.get(position);
        Picasso.get()
                .load(model.getPostImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.postImg);

        holder.binding.like.setText(String.valueOf(model.getPostLike()+""));
        holder.binding.comment.setText(String.valueOf(model.getCommentCount()+""));


        String des = model.getPostDescription();

        if(des.equals("")){
            holder.binding.postDescriptionRV.setVisibility(View.GONE);
        }else{
            holder.binding.postDescriptionRV.setText(model.getPostDescription());
            holder.binding.postDescriptionRV.setVisibility(View.VISIBLE);
        }



        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfilePhoto())
                                .placeholder(R.drawable.profile)
                                .into(holder.binding.profileImage);

                        holder.binding.userName.setText(user.getName());
                        holder.binding.bio.setText(user.getProfession());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        FirebaseDatabase.getInstance().getReference()
                        .child("posts")
                        .child(model.getPostId())
                        .child("likes")
                                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
                        }
                        else{
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like4, 0, 0, 0);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




        holder.binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postId = model.getPostId();
                String userId = FirebaseAuth.getInstance().getUid();
                DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes").child(userId);
                DatabaseReference postLikeRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("postLike");

                // Check if the user has already liked the post
                likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // User has already liked the post, so we will "unlike" it
                            likesRef.removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        // Update local UI smoothly
                                        model.setPostLike(model.getPostLike() - 1);  // Decrease like count
                                        holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like4, 0, 0, 0);  // Update like icon
                                        holder.binding.like.setText(String.valueOf(model.getPostLike()));  // Update like count text

                                        // Update Firebase with new like count
                                        postLikeRef.setValue(model.getPostLike());
                                    });
                        } else {
                            // User has not liked the post yet, so we will "like" it
                            likesRef.setValue(true)
                                    .addOnSuccessListener(aVoid -> {
                                        // Update local UI smoothly
                                        model.setPostLike(model.getPostLike() + 1);  // Increase like count
                                        holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);  // Update like icon
                                        holder.binding.like.setText(String.valueOf(model.getPostLike()));  // Update like count text

                                        // Update Firebase with new like count
                                        postLikeRef.setValue(model.getPostLike());
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error if necessary
                    }
                });
            }
        });


        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context2, CommentActivity.class);
                intent.putExtra("postId", model.getPostId());
                intent.putExtra("postedBy", model.getPostedBy());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context2.startActivity(intent);
            }
        });

        holder.binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain"); // Specify the MIME type
                intent.putExtra(Intent.EXTRA_TEXT, "Download apk for view posts in following url :https://drive.google.com/file/d/1JLr6z7my0mXXqM0mVnYL5OZRAp49nBQ7/view?usp=drivesdk"); // Add the text to share

                // Start the chooser dialog
                context2.startActivity(Intent.createChooser(intent, "Share via"));
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{

        DashboardRvSampleBinding binding;


        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DashboardRvSampleBinding.bind(itemView);



        }
    }
}