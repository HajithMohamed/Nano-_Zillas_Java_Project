package org.example.mini_project_java.Models;

public class Lecture extends Users {

    public Lecture(String username, String password, String name, String email, String role, String mobileNo) {
        super(username, password, name, email, role,password);
    }

    @Override
    public void updateProfile() {
        // Specific implementation for Admin
        System.out.println("Admin profile updated.");
    }
}
