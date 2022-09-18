package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {

        Connection connection = null;
        int choice = 0;
        Scanner input = new Scanner(System.in);
        boolean exit = false;

        // Connect to DB
        try {
            connection = connect(args[0], args[1], args[2], args[3]);

        // Display menu
        do {
            System.out.println();
            System.out.println("Welcome to Games DB");
            System.out.println("Please select a valid option (1, 2, 3, 4, or 5).");
            System.out.println("1. Start a Query");
            System.out.println("2. Insert a DB entry");
            System.out.println("3. Update a DB entry");
            System.out.println("4. Delete a DB entry");
            System.out.println("5. Quit");
            System.out.println();

            choice = input.nextInt();
            switch (choice) {
                case 1:
                    
                    break;
                case 2:
                    
                    break;
                case 3:
                    
                    break;
                case 4:
                    
                    break;
                case 5:
                    System.out.println("BUH BYE!");
                    input.close();
                    exit = true;
                    break;
            
                default:
                    break;
            }
        } while (!exit);
            
        } catch (InputMismatchException e) {
            input = new Scanner(System.in);
            System.out.println("Please enter a valid selection (1, 2, 3, 4, or 5)");
        } 
        catch (SQLException e) {
            // TODO: handle exception
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }        
    }

    public static Connection connect(String url, String user, String password, String driver) throws SQLException, ClassNotFoundException{
        Connection connection = null;

        // Load the JDBC driver
        Class.forName(driver);
        
        // Establish connection to DB
        connection = DriverManager.getConnection(url, user, password);

        return connection;
}
}
