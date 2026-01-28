package com.example.sixtyplus.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Volunteering {
    public String id; // מזהה ייחודי להתנדבות
    public String studentId; // מזהה התלמיד
    public String studentName; // שם התלמיד
    public String placeId; // מזהה המקום (ID של האחראי)
    public String placeName; // שם המקום
    public long dateMillis; // תאריך בפורמט milliseconds
    public HourMinute startTime; // שעת התחלה
    public HourMinute endTime; // שעת סיום
    public String status; // סטטוס: "pending", "approved", "rejected", "completed"
    public double totalHours; // סה"כ שעות התנדבות (בפורמט עשרוני)

    public Volunteering() {
    }

    public Volunteering(String studentId, String studentName, String placeId, String placeName,
                        long dateMillis, HourMinute startTime, HourMinute endTime) {
        this.id = generateId();
        this.studentId = studentId;
        this.studentName = studentName;
        this.placeId = placeId;
        this.placeName = placeName;
        this.dateMillis = dateMillis;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "pending";
        this.totalHours = calculateTotalHours();
    }

    private String generateId() {
        return System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    private double calculateTotalHours() {
        int startMinutes = startTime.getHour() * 60 + startTime.getMinute();
        int endMinutes = endTime.getHour() * 60 + endTime.getMinute();
        int totalMinutes = endMinutes - startMinutes;
        // המרה לשעות עשרוניות (מעוגל לספרה אחת אחרי הנקודה)
        return Math.round((totalMinutes / 60.0) * 10.0) / 10.0;
    }

    public String getFormattedDuration() {
        return String.format("%.1f שעות", totalHours);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }

    public long getDateMillis() { return dateMillis; }
    public void setDateMillis(long dateMillis) { this.dateMillis = dateMillis; }

    public HourMinute getStartTime() { return startTime; }
    public void setStartTime(HourMinute startTime) { this.startTime = startTime; }

    public HourMinute getEndTime() { return endTime; }
    public void setEndTime(HourMinute endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalHours() { return totalHours; }
    public void setTotalHours(double totalHours) { this.totalHours = totalHours; }
}

