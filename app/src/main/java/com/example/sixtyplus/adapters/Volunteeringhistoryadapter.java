package com.example.sixtyplus.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixtyplus.R;
import com.example.sixtyplus.models.Volunteering;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Volunteeringhistoryadapter extends RecyclerView.Adapter<Volunteeringhistoryadapter.ViewHolder> {

    private List<Volunteering> volunteeringList;

    public Volunteeringhistoryadapter() {
        this.volunteeringList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_volunteering_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Volunteering volunteering = volunteeringList.get(position);

        // שם המקום
        holder.tvPlaceName.setText(volunteering.getPlaceName());

        // תאריך ויום
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(volunteering.getDateMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dayName = getHebrewDayName(calendar.get(Calendar.DAY_OF_WEEK));
        holder.tvHistoryDate.setText("תאריך: " + dateFormat.format(calendar.getTime()) + " (יום " + dayName + ")");

        // שעות ומשך
        String timeText = "שעות: " + volunteering.getStartTime().toString() + " - " +
                volunteering.getEndTime().toString() + "  |  משך: " +
                String.format("%.1f שעות", volunteering.getTotalHours());
        holder.tvHistoryTime.setText(timeText);

        // סטטוס
        String status = volunteering.getStatus();
        String statusText;
        int statusColor;

        switch (status) {
            case "approved":
                statusText = "סטטוס: אושרה";
                statusColor = Color.parseColor("#4CAF50"); // ירוק
                break;
            case "rejected":
                statusText = "סטטוס: לא אושרה";
                statusColor = Color.parseColor("#F44336"); // אדום
                break;
            case "pending":
            default:
                statusText = "סטטוס: מחכה לאישור";
                statusColor = Color.parseColor("#FF9800"); // כתום
                break;
        }

        holder.tvHistoryStatus.setText(statusText);
        holder.tvHistoryStatus.setTextColor(statusColor);
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
        TextView tvPlaceName;
        TextView tvHistoryDate;
        TextView tvHistoryTime;
        TextView tvHistoryStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            tvHistoryDate = itemView.findViewById(R.id.tvHistoryDate);
            tvHistoryTime = itemView.findViewById(R.id.tvHistoryTime);
            tvHistoryStatus = itemView.findViewById(R.id.tvHistoryStatus);
        }
    }
}