package org.example.mini_project_java.Models;
import org.w3c.dom.ls.LSOutput;

import java.sql.* ;
import java.util.Scanner;

public class UndergraduateDemo {
    String stid;
    Scanner sc = new Scanner(System.in);
    String ppath;
    int contact;

    public void updateProfile(Connection con) {
        DbConnection db = new DbConnection();

        System.out.println("Enter your student id");
        stid = sc.next();
        sc.nextLine();
        if (studentExists(con)) {
            System.out.println("Enter your profile picture path");
            ppath = sc.nextLine();

            System.out.println("Enter your contact number");
            contact = sc.nextInt();


            String sql = "UPDATE undergraduate SET ContactNumber=?,ProfilePath=?  WHERE StudentId=?";
            try {
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, contact);
                pst.setString(2, ppath);
                pst.setString(3, stid);

                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Profile updated successfully");
                } else
                    System.out.println("Profile update failed");

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }


    }

    private boolean studentExists(Connection con) {
        String sql = "select StudentId from undergraduate where StudentId =?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, stid);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void seeAttendance(Connection con) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your student id");
        String stid = sc.next();

        String sql = "SELECT course_code,attendance_percentage FROM attendance WHERE Student_id=?";
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

        String sql = "SELECT c_code,stid,subdate,description FROM medical_records WHERE Student_id=?";
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

    }


