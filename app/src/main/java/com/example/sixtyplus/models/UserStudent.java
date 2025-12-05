package com.example.sixtyplus.models;

public class UserStudent extends UserGeneral { //Those who are students.
    public String schoolName; //Name of the school the student studies in.
    public String gradeLevel; //Class the student is in.

    public UserStudent() {
    }

    public UserStudent(String className, String idNumber, String firstName, String lastName, String phoneNumber,
                       String city, String password, String schoolName, String gradeLevel) {
        super(className, idNumber, firstName, lastName, phoneNumber, city, password);
        this.schoolName = schoolName;
        this.gradeLevel = gradeLevel;
    }

    public UserStudent(String uid, String password, String fName, String lName, String phone, boolean b) {
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }
}
