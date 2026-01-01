package com.example.sixtyplus.models;

public class DayAndHours {
    public String day;
    public String startTime;
    public String endTime;
    public String remark;

    public DayAndHours() {
    }
    public DayAndHours(String day, String startTime, String endTime, String remark) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.remark = remark;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
