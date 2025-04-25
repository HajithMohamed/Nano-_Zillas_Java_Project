package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DbConnection;
import org.w3c.dom.ls.LSOutput;

import java.sql.* ;
import java.util.Scanner;

public class UndergraduateDemo {
    DbConnection db = new DbConnection();
    Connection con=db.fetchConnection();
    String stid;
    Scanner sc = new Scanner(System.in);
    String ppath;
    int contact;

    public void updateProfile(Connection con) {
        // Scanner for input
        System.out.println("Enter your student id");
        stid = sc.next();  // Assuming student ID is entered here

        System.out.println("Enter your profile picture path");
        ppath = sc.next();  // Assuming the profile picture path is entered here

        System.out.println("Enter your contact number");
        contact = sc.nextInt();  // Assuming the contact number is entered here

        // SQL query to update profile
        String sql = "UPDATE students s JOIN users u ON s.student_id = u.user_id " +
                "SET u.Contact_number = ?, u.profile_picture = ? " +
                "WHERE s.registration_number = ?";

        try {
            PreparedStatement pst = con.prepareStatement(sql);

            // Set the values for the parameters in the query
            pst.setInt(1, contact);  // Set contact number
            pst.setString(2, ppath);  // Set profile picture path
            pst.setString(3, stid);   // Set student ID

            // Execute the update query
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Profile updated successfully");
            } else {
                System.out.println("Profile update failed");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void seeAttendance(Connection con) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your student id");
        String stid = sc.next();

        String sql = "SELECT course_code,session_date,course_name,session_type ,attendance_status FROM attendance_summary WHERE student_id=?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, stid);
            ResultSet rs = pst.executeQuery();
            boolean found=false;
            while (rs.next()) {
                found = true;
                String c_code = rs.getString("course_code");
                String c_name = rs.getString("course_name");
                String session_type = rs.getString("session_type");
                String session_date= rs.getString("session_date");
                String attendanceStatus= rs.getString("attendance_status");
                System.out.println(c_code + " " + attendanceStatus+" "+session_type+" "+session_date);
            }

            if (!found) {
                System.out.println("Attendance not found");
            }
        } catch (SQLException e) {
            System.out.println("error in getting attendance");
        }
    }
    public void seeMedicalDetails(Connection con) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your student id");
        String stid = sc.next();

        String sql = "SELECT course_id,student_id,submission_date,aprproval_status FROM medical_requests_view WHERE student_id=?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, stid);
            ResultSet rs = pst.executeQuery();
            boolean found=false;
            while (rs.next()) {
                found = true;
                String c_code = rs.getString("course_id");
                String stuid = rs.getString("student_id");
                String subdate = rs.getString("submission_date");
                String description = rs.getString("aprproval_status");
                System.out.println(c_code + " " + stuid + " " + subdate + " " + description);
            }

            if (!found) {
                System.out.println("Medical not found");
            }
        } catch (SQLException e) {
            System.out.println("error in see Medical");
        }
    }
    public void seeCourseDetails(Connection con) {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Enter Your Department(ICT/ET/BST)");
//        String dep=sc.next().toUpperCase();//avoid the case sensitivity

        String sql = "SELECT course_code,course_name,credits FROM course_details_view";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
//            pst.setString(1, dep);
            ResultSet rs = pst.executeQuery();
            boolean found=false;
            while (rs.next()) {
                found = true;
                String c_code = rs.getString("course_code");
                String c_name = rs.getString("course_name");
                int credits = rs.getInt("credits");
                System.out.println(c_code + " " + c_name+" "+credits);
            }

            if (!found) {
                System.out.println("course not found");
            }
        } catch (SQLException e) {
            System.out.println("error in see course details");
        }


    }

    public void seeNotice(Connection con) {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Enter your Department (ICT/ET/BST)");
//        String department = sc.next();


        String sql = "SELECT title,content FROM notice_view";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
//            pst.setString(1, department);
            ResultSet rs = pst.executeQuery();
            boolean found=false;
            while (rs.next()) {
                found = true;
                String title = rs.getString("title");
                String description = rs.getString("content");
                System.out.println(title + " " + description);
            }

            if (!found) {
                System.out.println("Notice not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in see notice");
        }
    }

    public void seeGPA(Connection con) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter you Student Id");
        String stid = sc.next();


        String sql = "SELECT semester,sgpa ,cgpa FROM gpa WHERE student_id=?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, stid);
            ResultSet rs = pst.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                String semester = rs.getString("semester");
                float cgpa = rs.getFloat("cgpa");
                float sgpa = rs.getFloat("sgpa");
                System.out.println(semester + " " + cgpa + " " +sgpa);
            }

            if (!found) {
                System.out.println("GPA not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in see GPA");
        }
    }
    public void seeTimetable(Connection con) {
//        String department;
//        Connection con;
//        String level;
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Select your department(ICT/BST/ET)");
//        department = sc.next().toUpperCase();
//        System.out.println("Select your Level(I/II/II/IV)");
//        level=sc.next();
        DbConnection db = new DbConnection();
        con=db.fetchConnection();//A
        if (con != null) {
            String sql = "SELECT course_code,course_name, location,start_time,end_time FROM timetable WHERE department =? and level=?";
            try {
                PreparedStatement pst = con.prepareStatement(sql);
//                pst.setString(1, department);
//                pst.setString(2,level);
                ResultSet rs = pst.executeQuery();
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    String sub_code = rs.getString("course_code");
                    String sub_name = rs.getString("course_name");
                    String start_time = rs.getString("start_time");
                    String end_time = rs.getString("end_time");
                    String location = rs.getString("location");
                    System.out.println(sub_code + " " + location+ sub_name + " " + start_time+" "+end_time);
                }

                if (!found) {
                    System.out.println("timetable not found");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("error in getting timetable");
            }
            finally{
                try{
                    con.close();
                }catch (SQLException e){
                    e.printStackTrace();
                    System.out.println("error in closing connection");
                }
            }
        }
    }
}




