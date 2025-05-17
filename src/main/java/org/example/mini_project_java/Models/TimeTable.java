

package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimeTable {
    private int id;
    private String timeSlot;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String courseCode;
    private String courseTitle;
    private String lecturerId;
    private int credits;
    private String type;
    private int creditHours;

    // Constructor for creating a new time table entry
    public TimeTable(String timeSlot, String monday, String tuesday, String wednesday,
                     String thursday, String friday, String courseCode, String courseTitle,
                     String lecturerId, int credits, String type, int creditHours) {
        this.timeSlot = timeSlot;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.lecturerId = lecturerId;
        this.credits = credits;
        this.type = type;
        this.creditHours = creditHours;
    }

    // Constructor with ID for database retrieval
    public TimeTable(int id, String timeSlot, String monday, String tuesday, String wednesday,
                     String thursday, String friday, String courseCode, String courseTitle,
                     String lecturerId, int credits, String type, int creditHours) {
        this.id = id;
        this.timeSlot = timeSlot;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.lecturerId = lecturerId;
        this.credits = credits;
        this.type = type;
        this.creditHours = creditHours;
    }

    // Empty constructor
    public TimeTable() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
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

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    // Create or Edit Time Table Entry
    public void createOrEditTimeTable(String timeSlot, String day, String courseCode, String courseTitle,
                                      String lecturerId, int credits, String type, int creditHours,
                                      boolean isEdit, int entryId) {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            if (!isEdit) {
                String insertSQL = "INSERT INTO TIMETABLE (time_slot, " + day.toLowerCase() + ", course_code, course_title, " +
                        "lecturer_id, credits, type, credit_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connectDB.prepareStatement(insertSQL)) {
                    ps.setString(1, timeSlot);
                    ps.setString(2, courseCode + "\n" + courseTitle + "\n" + lecturerId);
                    ps.setString(3, courseCode);
                    ps.setString(4, courseTitle);
                    ps.setString(5, lecturerId);
                    ps.setInt(6, credits);
                    ps.setString(7, type);
                    ps.setInt(8, creditHours);
                    ps.executeUpdate();
                }
            } else {
                String updateSQL = "UPDATE TIMETABLE SET " + day.toLowerCase() + " = ?, course_code = ?, course_title = ?, " +
                        "lecturer_id = ?, credits = ?, type = ?, credit_hours = ? WHERE id = ?";
                try (PreparedStatement ps = connectDB.prepareStatement(updateSQL)) {
                    ps.setString(1, courseCode + "\n" + courseTitle + "\n" + lecturerId);
                    ps.setString(2, courseCode);
                    ps.setString(3, courseTitle);
                    ps.setString(4, lecturerId);
                    ps.setInt(5, credits);
                    ps.setString(6, type);
                    ps.setInt(7, creditHours);
                    ps.setInt(8, entryId);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add Course to Specific Day and Time Slot
    public boolean addCourse(String day, String timeSlot, String courseCode, String courseTitle,
                             String lecturerId, int credits, String type, int creditHours) {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            // First check if there's already an entry for this time slot
            String checkSQL = "SELECT id FROM TIMETABLE WHERE time_slot = ?";
            try (PreparedStatement checkPs = connectDB.prepareStatement(checkSQL)) {
                checkPs.setString(1, timeSlot);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    // Time slot exists, update the specific day
                    int entryId = rs.getInt("id");
                    String updateSQL = "UPDATE TIMETABLE SET " + day.toLowerCase() + " = ? WHERE id = ?";
                    try (PreparedStatement updatePs = connectDB.prepareStatement(updateSQL)) {
                        updatePs.setString(1, courseCode + "\n" + courseTitle + "\n" + lecturerId);
                        updatePs.setInt(2, entryId);
                        updatePs.executeUpdate();
                    }
                } else {
                    // Time slot doesn't exist, create a new entry
                    String insertSQL = "INSERT INTO TIMETABLE (time_slot, " + day.toLowerCase() + ", course_code, course_title, " +
                            "lecturer_id, credits, type, credit_hours) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertPs = connectDB.prepareStatement(insertSQL)) {
                        insertPs.setString(1, timeSlot);
                        insertPs.setString(2, courseCode + "\n" + courseTitle + "\n" + lecturerId);
                        insertPs.setString(3, courseCode);
                        insertPs.setString(4, courseTitle);
                        insertPs.setString(5, lecturerId);
                        insertPs.setInt(6, credits);
                        insertPs.setString(7, type);
                        insertPs.setInt(8, creditHours);
                        insertPs.executeUpdate();
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a course from a specific day and time slot
    public boolean deleteCourse(String day, String timeSlot) {
        try (Connection connectDB = DatabaseConnection.getConnection()) {
            String updateSQL = "UPDATE TIMETABLE SET " + day.toLowerCase() + " = '' WHERE time_slot = ?";
            try (PreparedStatement ps = connectDB.prepareStatement(updateSQL)) {
                ps.setString(1, timeSlot);
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete an entire time table entry
    public boolean deleteTimeTableEntry(int id) {
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement deleteStmt = connectDB.prepareStatement("DELETE FROM TIMETABLE WHERE id = ?")) {
            deleteStmt.setInt(1, id);
            int rowsAffected = deleteStmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all time table entries
    public static List<TimeTable> getAllTimeTableEntries() {
        List<TimeTable> timeTableList = new ArrayList<>();
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement ps = connectDB.prepareStatement("SELECT * FROM TIMETABLE ORDER BY id");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TimeTable timeTable = new TimeTable(
                        rs.getInt("id"),
                        rs.getString("time_slot"),
                        rs.getString("monday"),
                        rs.getString("tuesday"),
                        rs.getString("wednesday"),
                        rs.getString("thursday"),
                        rs.getString("friday"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getString("lecturer_id"),
                        rs.getInt("credits"),
                        rs.getString("type"),
                        rs.getInt("credit_hours")
                );
                timeTableList.add(timeTable);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeTableList;
    }

    // Get time table entry by time slot
    public static TimeTable getTimeTableEntryByTimeSlot(String timeSlot) {
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement ps = connectDB.prepareStatement("SELECT * FROM TIMETABLE WHERE time_slot = ?")) {
            ps.setString(1, timeSlot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TimeTable(
                            rs.getInt("id"),
                            rs.getString("time_slot"),
                            rs.getString("monday"),
                            rs.getString("tuesday"),
                            rs.getString("wednesday"),
                            rs.getString("thursday"),
                            rs.getString("friday"),
                            rs.getString("course_code"),
                            rs.getString("course_title"),
                            rs.getString("lecturer_id"),
                            rs.getInt("credits"),
                            rs.getString("type"),
                            rs.getInt("credit_hours")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get courses by lecturer ID
    public static List<TimeTable> getCoursesByLecturerId(String lecturerId) {
        List<TimeTable> lecturerCourses = new ArrayList<>();
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement ps = connectDB.prepareStatement("SELECT * FROM TIMETABLE WHERE lecturer_id = ?")) {
            ps.setString(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TimeTable timeTable = new TimeTable(
                            rs.getInt("id"),
                            rs.getString("time_slot"),
                            rs.getString("monday"),
                            rs.getString("tuesday"),
                            rs.getString("wednesday"),
                            rs.getString("thursday"),
                            rs.getString("friday"),
                            rs.getString("course_code"),
                            rs.getString("course_title"),
                            rs.getString("lecturer_id"),
                            rs.getInt("credits"),
                            rs.getString("type"),
                            rs.getInt("credit_hours")
                    );
                    lecturerCourses.add(timeTable);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lecturerCourses;
    }

    // Initialize time slots in the database (for first-time setup)
    public static void initializeTimeSlots() {
        String[] timeSlots = {
                "8:00 - 10:00",
                "10:00 - 12:00",
                "12:00 - 1:00", // Lunch break
                "1:00 - 3:00",
                "3:00 - 5:00"
        };

        try (Connection connectDB = DatabaseConnection.getConnection()) {
            // First check if time slots are already initialized
            String checkSQL = "SELECT COUNT(*) FROM TIMETABLE";
            try (PreparedStatement checkPs = connectDB.prepareStatement(checkSQL);
                 ResultSet rs = checkPs.executeQuery()) {

                if (rs.next() && rs.getInt(1) == 0) {
                    // No time slots exist, initialize them
                    String insertSQL = "INSERT INTO TIMETABLE (time_slot, monday, tuesday, wednesday, thursday, friday) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertPs = connectDB.prepareStatement(insertSQL)) {
                        for (String timeSlot : timeSlots) {
                            insertPs.setString(1, timeSlot);

                            // Set lunch break for all days
                            if (timeSlot.equals("12:00 - 1:00")) {
                                insertPs.setString(2, "LUNCH BREAK");
                                insertPs.setString(3, "LUNCH BREAK");
                                insertPs.setString(4, "LUNCH BREAK");
                                insertPs.setString(5, "LUNCH BREAK");
                                insertPs.setString(6, "LUNCH BREAK");
                            } else {
                                insertPs.setString(2, "");
                                insertPs.setString(3, "");
                                insertPs.setString(4, "");
                                insertPs.setString(5, "");
                                insertPs.setString(6, "");
                            }

                            insertPs.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check if a time slot is available on a specific day
    public static boolean isTimeSlotAvailable(String day, String timeSlot) {
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement ps = connectDB.prepareStatement("SELECT " + day.toLowerCase() + " FROM TIMETABLE WHERE time_slot = ?")) {
            ps.setString(1, timeSlot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String courseInfo = rs.getString(1);
                    return courseInfo == null || courseInfo.isEmpty() || courseInfo.equals("LUNCH BREAK");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get a specific course info for a day and time slot
    public static String getCourseInfo(String day, String timeSlot) {
        try (Connection connectDB = DatabaseConnection.getConnection();
             PreparedStatement ps = connectDB.prepareStatement("SELECT " + day.toLowerCase() + " FROM TIMETABLE WHERE time_slot = ?")) {
            ps.setString(1, timeSlot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}

