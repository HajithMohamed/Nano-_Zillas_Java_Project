package org.example.mini_project_java.Models;

public class Courses {
    String courseCode;
    String courseTitle;
    String lectureId;
    int courseCredit;
    String courseType;
    int courseCreditHours;

    public Courses(String courseCode, String courseTitle, String lecId, int courseCredit, String courseType, int courseCreditHours) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.courseCredit = courseCredit;
        this.courseType = courseType;
        this.courseCreditHours = courseCreditHours;
        this.lectureId = lecId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public int getCourseCredit() {
        return courseCredit;
    }

    public void setCourseCredit(String courseCredit) {
        this.courseCredit = Integer.parseInt(courseCredit);
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public int getCourseCreditHours() {
        return courseCreditHours;
    }

    public void setCourseCreditHours(String courseCreditHours) {
        this.courseCreditHours = Integer.parseInt(courseCreditHours);
    }


}
