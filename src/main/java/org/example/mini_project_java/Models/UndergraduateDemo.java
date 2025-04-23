package org.example.mini_project_java.Models;

import org.example.mini_project_java.Database.DbConnection;
import org.w3c.dom.ls.LSOutput;

import java.sql.* ;
import java.util.Scanner;

public class UndergraduateDemo {
    String stid;
    Scanner sc = new Scanner(System.in);
    String ppath;
    int contact;
    DbConnection db = new DbConnection();
   Connection  con= db.fetchConnection();
    public void updateProfile(Connection con) {
        System.out.println("Enter your student id");
        stid = sc.next();
        sc.nextLine();

        // Step 1: Retrieve current profile data
        String selectSql = "SELECT ContactNumber, Profile,Email,stid ,address FROM undergraduate WHERE StudentId=?";
        try {
            PreparedStatement selectPst = con.prepareStatement(selectSql);
            selectPst.setString(1, stid);
            ResultSet rs = selectPst.executeQuery();

            if (rs.next()) {
                // Step 2: Display current information
                int currentContact = rs.getInt("ContactNumber");
                String currentProfilePath = rs.getString("ProfilePath");

                System.out.println("Current Contact Number: " + currentContact);
                System.out.println("Current Profile Picture Path: " + currentProfilePath);

                // Step 3: Allow user to update information
                System.out.println("Enter your new profile picture path (or press Enter to keep current):");
                String newPath = sc.nextLine();
                if (newPath.isEmpty()) {
                    newPath = currentProfilePath; // Keep current if no new input
                }

                System.out.println("Enter your new contact number (or press Enter to keep current):");
                String newContactInput = sc.nextLine();
                int newContact = currentContact; // Default to current contact
                if (!newContactInput.isEmpty()) {
                    newContact = Integer.parseInt(newContactInput); // Update if new input is provided
                }

                // Step 4: Update the profile
                String updateSql = "UPDATE undergraduate SET ContactNumber=?, ProfilePath=? WHERE StudentId=?";
                PreparedStatement updatePst = con.prepareStatement(updateSql);
                updatePst.setInt(1, newContact);
                updatePst.setString(2, newPath);
                updatePst.setString(3, stid);

                int rowsAffected = updatePst.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Profile updated successfully");
                } else {
                    System.out.println("Profile update failed");
                }

            } else {
                System.out.println("No profile found for the given student id.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid contact number format.");
        }
    }

    public void seeAttendance(Connection con) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your student id");
        String stid = sc.next();

        String sql = "SELECT course_code,course_name ,attendance_percentage FROM attendance WHERE Student_id=?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, stid);
            ResultSet rs = pst.executeQuery();
            boolean found=false;
            while (rs.next()) {
                found = true;
                String c_code = rs.getString("course_code");
                float attendance = rs.getFloat("attendance_percentage");
                System.out.println(c_code + " " + attendance);
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

        String sql = "SELECT c_code,stid,subdate,description FROM medical_records WHERE Stid=?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, stid);
            ResultSet rs = pst.executeQuery();
            boolean found=false;
            while (rs.next()) {
                found = true;
                String c_code = rs.getString("c_code");
                String stuid = rs.getString("stid");
                String subdate = rs.getString("subdate");
                String description = rs.getString("description");
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
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Your Department(ICT/ET/BST)");
        String dep=sc.next().toUpperCase();//avoid the case sensitivity

        String sql = "SELECT c_code,c_name FROM courses WHERE department=?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, dep);
            ResultSet rs = pst.executeQuery();
            boolean found=false;
            while (rs.next()) {
                found = true;
                String c_code = rs.getString("c_code");
                String c_name = rs.getString("c_name");
                System.out.println(c_code + " " + c_name);
            }

            if (!found) {
                System.out.println("course not found");
            }
        } catch (SQLException e) {
            System.out.println("error in see course details");
        }


    }

    public void seeNotice(Connection con) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your Department (ICT/ET/BST)");
        String department = sc.next();


        String sql = "SELECT title,description ,date_posted FROM notice WHERE department=?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, department);
            ResultSet rs = pst.executeQuery();
            boolean found=false;
            while (rs.next()) {
                found = true;
                String title = rs.getString("title");
                String description = rs.getString("description");
                String date_posted = rs.getString("date_posted");
                System.out.println(title + " " + description + " " + date_posted);
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


        String sql = "SELECT c_code,c_name ,GPA FROM grade WHERE stid=?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, stid);
            ResultSet rs = pst.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                String c_code = rs.getString("c_code");
                String c_name = rs.getString("c_name");
                float GPA = rs.getFloat("GPA");
                System.out.println(c_code + " " + c_name + " " + GPA);
            }

            if (!found) {
                System.out.println("GPA not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("error in see GPA");
        }
    }}




