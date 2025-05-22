package com.socio.sociosphere.Messagenew;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.socio.sociosphere.R;
import com.socio.sociosphere.User;

import java.util.ArrayList;
import java.util.List;

public class usersadpter extends RecyclerView.Adapter<usersadpter.MyViewHolder> {

    private Context context;
    private List<User> userslist;

    public usersadpter(Context context) {
        this.context = context;
        this.userslist = new ArrayList<>();
    }

    public void add(User user) {
        userslist.add(user);
        notifyItemInserted(userslist.size() - 1);
    }

    public void clear() {
        userslist.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public usersadpter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull usersadpter.MyViewHolder holder, int position) {
        User user = userslist.get(position);
        holder.name.setText(user.getName());
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("name", user.getName());
            intent.putExtra("uid", user.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userslist.size();
    }

    public List<User> getUserslist() {
        return userslist;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.username);
        }
    }
}
