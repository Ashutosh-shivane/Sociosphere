// Generated by view binder compiler. Do not edit!
package com.socio.sociosphere.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.socio.sociosphere.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityCommentBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView comment;

  @NonNull
  public final EditText commentET;

  @NonNull
  public final ImageView commentPostBtn;

  @NonNull
  public final RecyclerView commentRV;

  @NonNull
  public final TextView description;

  @NonNull
  public final TextView like;

  @NonNull
  public final ConstraintLayout main;

  @NonNull
  public final TextView name;

  @NonNull
  public final ImageView postImage;

  @NonNull
  public final CircleImageView profileImage;

  @NonNull
  public final ScrollView scrollView2;

  @NonNull
  public final TextView share;

  @NonNull
  public final Toolbar toolbar2;

  @NonNull
  public final View view11;

  @NonNull
  public final View view12;

  private ActivityCommentBinding(@NonNull ConstraintLayout rootView, @NonNull TextView comment,
      @NonNull EditText commentET, @NonNull ImageView commentPostBtn,
      @NonNull RecyclerView commentRV, @NonNull TextView description, @NonNull TextView like,
      @NonNull ConstraintLayout main, @NonNull TextView name, @NonNull ImageView postImage,
      @NonNull CircleImageView profileImage, @NonNull ScrollView scrollView2,
      @NonNull TextView share, @NonNull Toolbar toolbar2, @NonNull View view11,
      @NonNull View view12) {
    this.rootView = rootView;
    this.comment = comment;
    this.commentET = commentET;
    this.commentPostBtn = commentPostBtn;
    this.commentRV = commentRV;
    this.description = description;
    this.like = like;
    this.main = main;
    this.name = name;
    this.postImage = postImage;
    this.profileImage = profileImage;
    this.scrollView2 = scrollView2;
    this.share = share;
    this.toolbar2 = toolbar2;
    this.view11 = view11;
    this.view12 = view12;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCommentBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCommentBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_comment, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCommentBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.comment;
      TextView comment = ViewBindings.findChildViewById(rootView, id);
      if (comment == null) {
        break missingId;
      }

      id = R.id.commentET;
      EditText commentET = ViewBindings.findChildViewById(rootView, id);
      if (commentET == null) {
        break missingId;
      }

      id = R.id.commentPostBtn;
      ImageView commentPostBtn = ViewBindings.findChildViewById(rootView, id);
      if (commentPostBtn == null) {
        break missingId;
      }

      id = R.id.commentRV;
      RecyclerView commentRV = ViewBindings.findChildViewById(rootView, id);
      if (commentRV == null) {
        break missingId;
      }

      id = R.id.description;
      TextView description = ViewBindings.findChildViewById(rootView, id);
      if (description == null) {
        break missingId;
      }

      id = R.id.like;
      TextView like = ViewBindings.findChildViewById(rootView, id);
      if (like == null) {
        break missingId;
      }

      ConstraintLayout main = (ConstraintLayout) rootView;

      id = R.id.name;
      TextView name = ViewBindings.findChildViewById(rootView, id);
      if (name == null) {
        break missingId;
      }

      id = R.id.postImage;
      ImageView postImage = ViewBindings.findChildViewById(rootView, id);
      if (postImage == null) {
        break missingId;
      }

      id = R.id.profile_image;
      CircleImageView profileImage = ViewBindings.findChildViewById(rootView, id);
      if (profileImage == null) {
        break missingId;
      }

      id = R.id.scrollView2;
      ScrollView scrollView2 = ViewBindings.findChildViewById(rootView, id);
      if (scrollView2 == null) {
        break missingId;
      }

      id = R.id.share;
      TextView share = ViewBindings.findChildViewById(rootView, id);
      if (share == null) {
        break missingId;
      }

      id = R.id.toolbar2;
      Toolbar toolbar2 = ViewBindings.findChildViewById(rootView, id);
      if (toolbar2 == null) {
        break missingId;
      }

      id = R.id.view11;
      View view11 = ViewBindings.findChildViewById(rootView, id);
      if (view11 == null) {
        break missingId;
      }

      id = R.id.view12;
      View view12 = ViewBindings.findChildViewById(rootView, id);
      if (view12 == null) {
        break missingId;
      }

      return new ActivityCommentBinding((ConstraintLayout) rootView, comment, commentET,
          commentPostBtn, commentRV, description, like, main, name, postImage, profileImage,
          scrollView2, share, toolbar2, view11, view12);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
