// Generated by view binder compiler. Do not edit!
package com.socio.sociosphere.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.socio.sociosphere.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DemoPostBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final View view;

  @NonNull
  public final View view13;

  @NonNull
  public final View view14;

  @NonNull
  public final View view15;

  @NonNull
  public final View view16;

  private DemoPostBinding(@NonNull RelativeLayout rootView, @NonNull View view,
      @NonNull View view13, @NonNull View view14, @NonNull View view15, @NonNull View view16) {
    this.rootView = rootView;
    this.view = view;
    this.view13 = view13;
    this.view14 = view14;
    this.view15 = view15;
    this.view16 = view16;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DemoPostBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DemoPostBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.demo_post, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DemoPostBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.view;
      View view = ViewBindings.findChildViewById(rootView, id);
      if (view == null) {
        break missingId;
      }

      id = R.id.view13;
      View view13 = ViewBindings.findChildViewById(rootView, id);
      if (view13 == null) {
        break missingId;
      }

      id = R.id.view14;
      View view14 = ViewBindings.findChildViewById(rootView, id);
      if (view14 == null) {
        break missingId;
      }

      id = R.id.view15;
      View view15 = ViewBindings.findChildViewById(rootView, id);
      if (view15 == null) {
        break missingId;
      }

      id = R.id.view16;
      View view16 = ViewBindings.findChildViewById(rootView, id);
      if (view16 == null) {
        break missingId;
      }

      return new DemoPostBinding((RelativeLayout) rootView, view, view13, view14, view15, view16);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
