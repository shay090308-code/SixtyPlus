package com.example.sixtyplus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.Volunteering;
import com.example.sixtyplus.models.Weekday;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Volunteeringrequestadapter extends RecyclerView.Adapter<Volunteeringrequestadapter.ViewHolder> {

    private List<Volunteering> volunteeringList;
    private OnVolunteeringActionListener listener;

    public interface OnVolunteeringActionListener {
        void onApprove(Volunteering volunteering);
        void onReject(Volunteering volunteering);
    }

    public Volunteeringrequestadapter(OnVolunteeringActionListener listener) {
        this.volunteeringList = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_volunteering_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Volunteering volunteering = volunteeringList.get(position);

        // שם המתנדב
        holder.tvVolunteerName.setText(volunteering.getStudentName());

        // תאריך ויום
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(volunteering.getDateMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dayName = getHebrewDayName(calendar.get(Calendar.DAY_OF_WEEK));
        holder.tvVolunteerDate.setText("תאריך: " + dateFormat.format(calendar.getTime()) + " (יום " + dayName + ")");

        // שעות
        String timeText = "שעות: " + volunteering.getStartTime().toString() + " - " + volunteering.getEndTime().toString();
        holder.tvVolunteerTime.setText(timeText);

        // משך
        holder.tvVolunteerDuration.setText(String.format("משך: %.1f שעות", volunteering.getTotalHours()));

        // כפתורים
        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApprove(volunteering);
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReject(volunteering);
            }
        });
    }

    @Override
    public int getItemCount() {
        return volunteeringList.size();
    }

    public void setVolunteeringList(List<Volunteering> list) {
        this.volunteeringList = list;
        notifyDataSetChanged();
    }

    private String getHebrewDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY: return "ראשון";
            case Calendar.MONDAY: return "שני";
            case Calendar.TUESDAY: return "שלישי";
            case Calendar.WEDNESDAY: return "רביעי";
            case Calendar.THURSDAY: return "חמישי";
            case Calendar.FRIDAY: return "שישי";
            case Calendar.SATURDAY: return "שבת";
            default: return "";
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVolunteerName;
        TextView tvVolunteerDate;
        TextView tvVolunteerTime;
        TextView tvVolunteerDuration;
        Button btnApprove;
        Button btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVolunteerName = itemView.findViewById(R.id.tvVolunteerName);
            tvVolunteerDate = itemView.findViewById(R.id.tvVolunteerDate);
            tvVolunteerTime = itemView.findViewById(R.id.tvVolunteerTime);
            tvVolunteerDuration = itemView.findViewById(R.id.tvVolunteerDuration);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}