package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        System.out.println("main");
        Connection connection = null;

        // Connect to DB
        try {
            connection = connect(args[0], args[1], args[2], args[3]);
            displayMenu(connection);

        } catch (SQLException e) {
            System.out.println("Problem connecting to DB");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Can't find classes");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Super broke");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    System.out.println("closing connection to DB");
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void displayMenu(Connection connection) {
        int choice = 0;
        Scanner input = new Scanner(System.in);
        boolean exit = false;

        try {
            do {
                choice = 0;
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
                        Query.queryMenu(input, connection);
                        break;
                    case 2:
                        Insert.insertMenu(input, connection);
                        break;
                    case 3:
                        Update.updateMenu(input, connection);
                        break;
                    case 4:
                        Delete.deleteMenu(input, connection);
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
    }

    private static Connection connect(String url, String user, String password, String driver)
            throws SQLException, ClassNotFoundException {
        Connection connection = null;

        // Load the JDBC driver
        Class.forName(driver);

        // Establish connection to DB
        connection = DriverManager.getConnection(url, user, password);

        // Turn off auto commit
        connection.setAutoCommit(false);

        return connection;
    }

    public static void displayResults(ResultSet resultSet) {
        System.out.println("display");
        try {

            // Get number of columns
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columns = rsmd.getColumnCount();
            String[] columnNames = new String[columns];

            // Get column names
            for (int i = 1; i <= columns; i++) {
                columnNames[i - 1] = rsmd.getColumnLabel(i);
            }

            // Create header row
            StringBuilder headers = new StringBuilder();
            for (String column : columnNames) {
                headers.append(String.format("%-20s", column).toUpperCase());
            }

            // Print header row
            System.out.println(headers);
            for (int i = 0; i < columns * 20; i++) {
                System.out.print("-");
            }
            System.out.println();

            // Print results
            Object obj = null;
            while (resultSet.next()) {
                for (int i = 1; i <= columns; i++) {
                    obj = resultSet.getObject(i);
                    if (obj != null) {
                        System.out.format("%-20s", resultSet.getObject(i).toString());
                    } else {
                        System.out.print("\t\t");
                    }
                }
                System.out.println();
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

    }
}
