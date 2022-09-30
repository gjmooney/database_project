package main.java;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Update {
    static final String EMP_ID = "employee_id";
    static final String GAME_ID = "game_id";
    static final String COM_ID = "company_id";

    public static void updateMenu(Scanner input, Connection connection) {
        int choice = 0;
        boolean exit = false;
        do {
            System.out.println();
            System.out.println("\nLet's update an entry!");
            System.out.println("Which table would you like to update?");
            System.out.println("-------------------------------------");
            System.out.println("1. Person");
            System.out.println("2. Game");
            System.out.println("3. Publisher");
            System.out.println("4. Return to main menu");

            try {
                choice = input.nextInt();

                switch (choice) {
                    case 1:
                        updateTable(connection, input, "person");
                        break;
                    case 2:
                        updateTable(connection, input, "game");
                        break;
                    case 3:
                        updateTable(connection, input, "publisher");
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

    private static void updateTable(Connection connection, Scanner input, String tableName) {
        Statement statement = null;
        ResultSet resultSet = null;
        int choice = 0;
        String columnToUpdate;
        String newString;
        boolean repeat = false;

        try {
            // Display person table
            statement = connection.createStatement();
            String query = "SELECT * FROM ${table}";
            query = query.replace("${table}", tableName);
            resultSet = statement.executeQuery(query);
            Menu.displayResults(resultSet);

            do {
                // Prompt for who to update
                System.out.format("Which %s would you like to update?\n", tableName);
                System.out.println("Enter 0 to cancel");
                choice = input.nextInt();

                // CEOs don't have additional info but designers do
                // so check if the person is a designer here and redisplay
                // the table with their columns
                if (tableName.equals("person") && checkIfDesigner(connection, choice)) {
                    tableName = "designer";
                }

                if (choice != 0) {
                    // prompt for which column to update
                    System.out.println("Which column would you like to update?");
                    System.out.println("Please enter column name as displayed");
                    columnToUpdate = input.next().toLowerCase();

                    // TODO: parse column name

                    // Swap back to person table to update designer name
                    if (tableName.equals("designer") && columnToUpdate.equals("name")) {
                        tableName = "person";
                    }
                    // Get new value
                    System.out.println("Please enter the new value for " + columnToUpdate);
                    if (columnToUpdate.equals("release_date")) {
                        System.out.println("Date format is YYYY-MM-DD");
                    }
                    newString = input.next();
                    updateValue(connection, choice, tableName, columnToUpdate, newString);
                }
            } while (repeat);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid choice.");
            input = new Scanner(System.in);

        } catch (SQLException e) {
            System.out.println("Issue updating");
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private static boolean checkIfDesigner(Connection connection, int employeeId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(
                    "SELECT d.employee_id, p.name, d.salary, d.employment_date  FROM designer d JOIN person p ON d.employee_id = p.employee_id  WHERE p.employee_id=?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setInt(1, employeeId);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                resultSet.beforeFirst();
                Menu.displayResults(resultSet);
                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    private static void updateValue(Connection connection, int key, String table, String column,
            String newValue) {
        PreparedStatement statement = null;

        // Get query template set up
        String query = "UPDATE ${table} SET ${column} = ? WHERE ${pk} = ? ";
        query = query.replace("${table}", table);
        query = query.replace("${column}", column);

        // Change PK field based on table
        if (table.equals("person") | table.equals("designer")) {
            query = query.replace("${pk}", EMP_ID);
        } else if (table.equals("game")) {
            query = query.replace("${pk}", GAME_ID);
        } else if (table.equals("publisher")) {
            query = query.replace("${pk}", COM_ID);
        }

        // Create statement
        try {
            statement = connection.prepareStatement(query);
            // Set first PreparedStatement value based on column name
            if (column.equals("name") | column.equals("genre") | column.equals("title")) {
                statement.setString(1, newValue);
            } else if (column.equals("profit") | column.equals("salary")) {
                statement.setDouble(1, Double.valueOf(newValue));
            } else if (column.equals("release_date") | column.equals("employment_date")) {
                // Parse date before setting
                if (parseDate(newValue)) {
                    statement.setDate(1, Date.valueOf(newValue));
                } else {
                    throw new NumberFormatException();
                }
            }

            // set the PK
            statement.setInt(2, key);

            // Execute the update
            if (statement.executeUpdate() > 0) {
                connection.commit();
                System.out.format("Changed %s to %s", column, newValue);
            } else {
                connection.rollback();
                System.out.println("Error updating");
            }
        } catch (NumberFormatException e) {
            System.out.println("Bad number format");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error updating person");
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

    static boolean parseDate(String date) {
        // Check for YYYY-MM-DD format
        return date.matches("^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$");

    }
}
