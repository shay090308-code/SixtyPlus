package com.example.sixtyplus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserInCharge;

import java.util.ArrayList;
import java.util.List;

public class UserInChargeAdapter extends RecyclerView.Adapter<UserInChargeAdapter.ViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(UserInCharge user);
        void onLongUserClick(UserInCharge user);
    }

    private final List<UserInCharge> userList;
    private final OnUserClickListener onUserClickListener;

    public UserInChargeAdapter(@Nullable final OnUserClickListener onUserClickListener) {
        userList = new ArrayList<>();
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_in_charge_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserInCharge user = userList.get(position);
        if (user == null) return;

        // שם מקום
        holder.tvPlaceName.setText(user.getPlaceName() != null ? user.getPlaceName() : "לא צוין");

        // שם פרטי + משפחה
        holder.tvName.setText(user.getFirstName() + " " + user.getLastName());

        // עיר
        holder.tvCity.setText(user.getCity() != null ? user.getCity() : "לא צוין");

        // טלפון
        holder.tvPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "לא צוין");

        // אינישלס
        String initials = "";
        if (user.getFirstName() != null && !user.getFirstName().isEmpty())
            initials += user.getFirstName().charAt(0);
        if (user.getLastName() != null && !user.getLastName().isEmpty())
            initials += user.getLastName().charAt(0);
        holder.tvInitials.setText(initials.toUpperCase());

        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) onUserClickListener.onUserClick(user);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onUserClickListener != null) onUserClickListener.onLongUserClick(user);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<UserInCharge> users) {
        userList.clear();
        userList.addAll(users);
        notifyDataSetChanged();
    }

    public void addUser(UserInCharge user) {
        userList.add(user);
        notifyItemInserted(userList.size() - 1);
    }

    public void updateUser(UserInCharge user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.set(index, user);
        notifyItemChanged(index);
    }

    public void removeUser(UserInCharge user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.remove(index);
        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaceName, tvName, tvCity, tvPhone, tvInitials;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaceName = itemView.findViewById(R.id.tv_place_name);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCity = itemView.findViewById(R.id.tv_city);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvInitials = itemView.findViewById(R.id.tv_initials);
        }
    }
}