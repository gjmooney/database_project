package main.java;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Delete {
    public static void deleteMenu(Scanner input, Connection connection) {
        int choice = 0;
        boolean exit = false;
        do {
            System.out.println();
            System.out.println("Let's delete an entry!");
            System.out.println("Which table would you like to delete from?");
            System.out.println("-------------------------------------");
            System.out.println("1. Person");
            System.out.println("2. Game");
            System.out.println("3. Publisher");
            System.out.println("4. Return to main menu");

            try {
                choice = input.nextInt();

                switch (choice) {
                    case 1:
                        deletePerson(connection, input);
                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:
                        exit = true;
                        break;
                    default:
                        break;
                }

            } catch (InputMismatchException e) {
                input = new Scanner(System.in);
                System.out.println("Please enter a valid selection");
            }
        } while (!exit);
    }

    private static void deletePerson(Connection connection, Scanner input) {
        // display all persons
        // ask which to delete
        // remove them from ceo/designer
        // then delete them from person
        int person = 0;

        // Get employee_id to delete
        System.out.println("Who would you like to delete?");
        person = displayPersons(connection, input);

        checkTable(connection, "works_for", String.valueOf(person));
        checkTable(connection, "works_on", String.valueOf(person));
        checkTable(connection, "ceo", String.valueOf(person));
        checkTable(connection, "designer", String.valueOf(person));
        removeFromTable(connection, "person", String.valueOf(person));

    }

    private static void checkTable(Connection connection, String tableName, String empId) {

        String query = "SELECT employee_id FROM ${table} WHERE employee_id=${id}";
        query = query.replace("${table}", tableName);
        query = query.replace("${id}", empId);
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                // remove from table
                removeFromTable(connection, tableName, empId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("Issue closing resources");
                e.printStackTrace();
            }
        }

    }

    private static void removeFromTable(Connection connection, String tableName, String empId) {
        String query = "DELETE FROM ${table} WHERE employee_id=${id}";
        query = query.replace("${table}", tableName);
        query = query.replace("${id}", empId);
        Statement statement = null;

        try {
            statement = connection.createStatement();

            if (statement.executeUpdate(query) > 0) {
                System.out.println("Employee with ID " + empId + " was removed from " + tableName);
            } else {
                System.out.println(empId + " was not removed from " + tableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Issue closing resources");
                e.printStackTrace();
            }
        }
    }

    private static int displayPersons(Connection connection, Scanner input) {
        Statement statement = null;
        ResultSet resultSet = null;
        boolean exit = false;
        int person = 0;

        try {
            // Create statement
            statement = connection.createStatement();

            // Make query
            resultSet = statement.executeQuery("SELECT employee_id, name " +
                    "FROM person ");

            do {
                // Display results
                Query.displayResults(resultSet);
                try {
                    person = input.nextInt();
                    if (person != 0) {
                        exit = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid choice.");
                    input = new Scanner(System.in);
                }
            } while (!exit);
        } catch (Exception e) {
            System.out.println("Issue making display person query");
            e.printStackTrace();
        }

        return person;
    }

}
