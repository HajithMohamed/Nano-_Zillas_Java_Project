package org.example.mini_project_java.Models;

import java.sql.Connection;
import java.util.Scanner;
public class Undergraduate{
    public static void main(String[] args) {
        System.out.println("Welcome to Undergrauate Page");
        int choice;

        do{
            System.out.println("1.Update your profile");
            System.out.println("2.See the attendance");
            System.out.println("3.See the Medical Details");
            System.out.println("4.See their course details ");
            System.out.println("5.see the grades and GPA");
            System.out.println("6.See their timetables ");
            System.out.println("7.See notices");
            System.out.println("8.Exit");


            UndergraduateDemo ud = new UndergraduateDemo();
            System.out.println("Enter the choice");
            Scanner sc = new Scanner(System.in);
            choice = sc.nextInt();
            DbConnection db = new DbConnection();
            switch (choice) {
                case 1:

                    Connection con=db.fetchConnection();
                    if(con !=null)
                        ud.studentExists();

                    else
                        System.out.println("Couldn't connect to database");
                    break;
                case 2:
                    System.out.println("See the attendance");
                    break;
                case 3:
                    System.out.println("See the Medical Details");
                    break;
                case 4:
                    System.out.println("See their course details");
                    break;
                case 5:
                    System.out.println("See their timetables");
                    break;
                case 6:
                    System.out.println("See notices");
                    break;
                case 7:
                    System.out.println("See their timetables");
                    break;
                case 8:
                    System.out.println("Existing.........");
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;

            }
        }while(choice != 8);


    }
}
