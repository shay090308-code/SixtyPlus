package com.example.sixtyplus.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.UserInCharge;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;


public class UserInChargeFindPlaces extends RecyclerView.Adapter<UserInChargeFindPlaces.ViewHolder> {


    public interface OnUserClickListener {
        void onUserClick(UserInCharge user);
        void onLongUserClick(UserInCharge user);
    }

    private final List<UserInCharge> userList;
    private final OnUserClickListener onUserClickListener;
    public UserInChargeFindPlaces(@Nullable final OnUserClickListener onUserClickListener) {
        userList = new ArrayList<>();
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserInChargeFindPlaces.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserInCharge user = userList.get(position);
        if (user == null) return;

        Log.d("UserStudentAdapter", "User PlaceName: " + user.getPlaceName());
        Log.d("UserStudentAdapter", "User City: " + user.getCity());
        Log.d("UserStudentAdapter", "User Phone: " + user.getPhoneNumber());

        holder.tvName.setText(user.getPlaceName());
        holder.tvPhone.setText(user.getPhoneNumber());

        if (user.getFirstName() != null && !user.getFirstName().isEmpty()
        && user.getLastName() != null && !user.getLastName().isEmpty()) {
            holder.tvId.setText(user.getFirstName() + " " + user.getLastName());
        } else {
            holder.tvId.setText("לא צוין");
        }

        if (user.getCity() != null && !user.getCity().isEmpty()) {
            holder.tvCity.setText(user.getCity());
        } else {
            holder.tvCity.setText("לא צוין");
        }

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