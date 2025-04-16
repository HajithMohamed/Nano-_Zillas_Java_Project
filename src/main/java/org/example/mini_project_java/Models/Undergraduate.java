package org.example.mini_project_java.Models;



import java.sql.* ;
import java.util.Scanner;
public class Undergraduate{
    public Connection con;

    public  static void main(String[] args) {
        UndergraduateDemo ud = new UndergraduateDemo();
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


            //Connection is a Java object from the JDBC (Java Database Connectivity):
            System.out.println("Enter the choice");
            Scanner sc = new Scanner(System.in);
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    Undergraduate x= new Undergraduate();
                    if(x.con !=null){
                        ud.updateProfile(x.con);}
                    break;
                case 2:
                    x= new Undergraduate();
                    System.out.println("See the attendance");
                    if(x.con !=null){
                        ud.seeAttendance(x.con);}
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
    public  Undergraduate () {
        DbConnection db = new DbConnection();
        this. con= db.fetchConnection();//Assign this connection to the con variable that belongs to the object.”

    }
}
