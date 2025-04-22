package org.example.mini_project_java.Models;
import java.sql.* ;
import java.util.Scanner;
            public class TimetableViewer{
                String department;
                Connection con;
                String level;
                    public void seeTimetable() {
                        Scanner sc = new Scanner(System.in);
                        System.out.println("Select your department(ICT/BST/ET)");
                        department = sc.next().toUpperCase();
                        System.out.println("Select your Level(I/II/II/IV)");
                        level=sc.next();
                        DbConnection db = new DbConnection();
                        con=db.fetchConnection();//A
                        if (con != null) {
                            String sql = "SELECT sub_code,sub_name, sessions ,department FROM timetable WHERE department =? and level=?";
                            try {
                                PreparedStatement pst = con.prepareStatement(sql);
                                pst.setString(1, department);
                                pst.setString(2,level);
                                ResultSet rs = pst.executeQuery();
                                boolean found = false;
                                while (rs.next()) {
                                    found = true;
                                    String sub_code = rs.getString("sub_code");
                                    String sub_name = rs.getString("sub_name");
                                    String session = rs.getString("sessions");
                                    System.out.println(sub_code + " " + sub_name + " " + session);
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
                public static void main(String[] args){
                    TimetableViewer timetableViewer = new TimetableViewer();
                        timetableViewer.seeTimetable();


                }
                    }


