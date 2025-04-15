package org.example.mini_project_java.Models;
import org.w3c.dom.ls.LSOutput;

import java.sql.* ;
import java.util.Scanner;

public class UndergraduateDemo {
    int  stid;
    public void updateProfile() {
        DbConnection db = new DbConnection();
        Connection con = db.fetchConnection();

//Connection is a Java object from the JDBC (Java Database Connectivity):

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your student id");
        stid = sc.nextInt();
        if (!studentExists()) {
            System.out.println("Student does not exist");
            return;
        }
        System.out.println("Enter your profile picture path");
        String ppath = sc.nextLine();

        System.out.println("Enter your contact number");
        int contact = sc.nextInt();


        String sql = "UPDATE undergraduates SET contact=?  WHERE studentid=?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, contact);
            pst.setString(2, ppath);
            pst.setInt(3, stid);

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Profile updated successfully");
            } else
                System.out.println("Profile update failed");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    public boolean studentExists() {
        DbConnection db = new DbConnection();
        Connection con = db.fetchConnection();
        String sql = "select stdid from undergraduates where studentid=?";
        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, stid);
            ResultSet rs = pst.executeQuery();
            rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}