package com.example.sixtyplus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserStudent;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;


public class UserStudentAdapter extends RecyclerView.Adapter<UserStudentAdapter.ViewHolder> {


    public interface OnUserClickListener {
        void onUserClick(UserStudent user);
        void onLongUserClick(UserStudent user);
    }

    private final List<UserStudent> userList;
    private final OnUserClickListener onUserClickListener;
    public UserStudentAdapter(@Nullable final OnUserClickListener onUserClickListener) {
        userList = new ArrayList<>();
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserStudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserStudent user = userList.get(position);
        if (user == null) return;

        holder.tvName.setText(user.getFirstName() + " " + user.getLastName());
        holder.tvPhone.setText(user.getPhoneNumber());

        // Set initials
        String initials = "";
        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            initials += user.getFirstName().charAt(0);
        }
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            initials += user.getLastName().charAt(0);
        }
        holder.tvInitials.setText(initials.toUpperCase());

        // Show admin chip if user is admin
        if (user.isAdmin()) {
            holder.chipRole.setVisibility(View.VISIBLE);
            holder.chipRole.setText("Admin");
        } else {
            holder.chipRole.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onLongUserClick(user);
            }
            return true;
        });

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<UserStudent> users) {
        userList.clear();
        userList.addAll(users);
        notifyDataSetChanged();
    }

    public void addUser(UserStudent user) {
        userList.add(user);
        notifyItemInserted(userList.size() - 1);
    }
    public void updateUser(UserStudent user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.set(index, user);
        notifyItemChanged(index);
    }

    public void removeUser(UserStudent user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.remove(index);
        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvId, tvCity, tvPhone, tvInitials;
        Chip chipRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_userstudent_name);
            tvId = itemView.findViewById(R.id.tv_item_userstudent_id);
            tvCity = itemView.findViewById((R.id.tv_item_userstudent_city));
            tvPhone = itemView.findViewById(R.id.tv_item_userstudent_phone);
            tvInitials = itemView.findViewById(R.id.tv_userstudent_initials);
            chipRole = itemView.findViewById(R.id.chip_userstudent_role);
        }
    }
}