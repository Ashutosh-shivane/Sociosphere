package com.socio.sociosphere.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.socio.sociosphere.Model.NotifiationModel;
import com.socio.sociosphere.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

    ArrayList<NotifiationModel> list;

    Context context;

    public NotificationAdapter(ArrayList<NotifiationModel> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification2sample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        NotifiationModel model = list.get(position);

        holder.profile.setImageResource(model.getProfile());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.notification.setText(Html.fromHtml(model.getNotification(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.notification.setText(Html.fromHtml(model.getNotification()));
        }

        holder.time.setText(model.getTime());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        ImageView profile;
        TextView notification, time;

        public viewHolder(@NonNull View itemView) {

            super(itemView);

            profile = itemView.findViewById(R.id.profile_image);
            notification = itemView.findViewById(R.id.notification);
            time = itemView.findViewById(R.id.time);
        }
    }
}
