package org.example.mini_project_java.Models;

public class Undergratuate extends Users{

    public Undergratuate(String username, String password, String name, String email, String role, String mobileNo) {
        super(username, password, name, email, role,password);
    }

    public Undergratuate(String username, String password, String name, String email, String role, String mobile_no, String profilePicture) {
        super(username, password, name, email, role, mobile_no, profilePicture);
    }

    @Override
    public void updateProfile() {
        // Specific implementation for Admin
        System.out.println("Admin profile updated.");
    }
}
