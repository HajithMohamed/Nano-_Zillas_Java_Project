package org.example.mini_project_java.Models;

public class Courses {
    private String courseCode;
    private String courseTitle;
    private String lecturerId;
    private int courseCredit;
    private String courseType;
    private int creditHours;

    // Constructor
    public Courses(String courseCode, String courseTitle, String lecturerId, int courseCredit, String courseType, int creditHours) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.lecturerId = lecturerId;
        this.courseCredit = courseCredit;
        this.courseType = courseType;
        this.creditHours = creditHours;
    }

    // Getters
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public String getLecturerId() { return lecturerId; }
    public int getCourseCredit() { return courseCredit; }
    public String getCourseType() { return courseType; }
    public int getCreditHours() { return creditHours; }
}
