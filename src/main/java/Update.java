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
                        updatePerson(connection, input);
                        break;
                    case 2:
                        updateGame(connection, input);
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

    private static void updatePerson(Connection connection, Scanner input) {
        // display the table
        // ask which to update
        // prompt which they want to update - don't change IDs
        // ask for new value

        Statement statement = null;
        ResultSet resultSet = null;
        int choice = 0;
        String newValue;

        try {
            // Display person table
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM person");
            Menu.displayResults(resultSet);

            // Prompt for who to update
            System.out.println("Which person would you like to update?");
            System.out.println("Enter 0 to cancel");
            choice = input.nextInt();

            if (choice != 0) {
                // Person table can only have name for a column
                // Prompt for new value
                System.out.println("What would you like the new name to be?");
                newValue = input.next();

                updateString(connection, choice, "person", "name", newValue);
            }

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

    private static void updateGame(Connection connection, Scanner input) {
        Statement statement = null;
        ResultSet resultSet = null;
        int choice = 0;
        String columnToUpdate;
        String newString;
        boolean repeat = false;

        try {
            // Display person table
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM game");
            Menu.displayResults(resultSet);

            do {
                // Prompt for who to update
                System.out.println("Which game would you like to update?");
                System.out.println("Enter 0 to cancel");
                choice = input.nextInt();

                if (choice != 0) {
                    // prompt for which column to update
                    System.out.println("Which column would you like to update?");
                    System.out.println("Please enter column name as displayed");
                    columnToUpdate = input.next().toLowerCase();

                    // TODO: parse column name

                    // Get new value
                    System.out.println("Please enter the new value");
                    if (columnToUpdate.equals("release_date")) {
                        System.out.println("Date format is YYYY-MM-DD");
                    }
                    newString = input.next();
                    updateString(connection, choice, "game", columnToUpdate, newString);
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

    private static void updateString(Connection connection, int key, String table, String column,
            String newValue) {
        PreparedStatement statement = null;

        // Get query template set up
        String query = "UPDATE ${table} SET ${column} = ? WHERE ${pk} = ? ";
        query = query.replace("${table}", table);
        query = query.replace("${column}", column);

        // Change PK field based on table
        if (table.equals("person")) {
            query = query.replace("${pk}", EMP_ID);
        } else if (table.equals("game")) {
            query = query.replace("${pk}", GAME_ID);
        }

        // Create statement
        try {
            statement = connection.prepareStatement(query);
            // Set first PreparedStatement value based on column name
            if (column.equals("name") | column.equals("genre")) {
                statement.setString(1, newValue);
            } else if (column.equals("profit")) {
                statement.setDouble(1, Double.valueOf(newValue));
            } else if (column.equals("release_date")) {
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

    private static boolean parseDate(String date) {
        return date.matches("^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$");

    }

    /*
     * private static void updateDouble(Connection connection, int primaryKey,
     * String table, String columnToUpdate,
     * Double newValue) {
     * PreparedStatement statement = null;
     * 
     * // Get query template set up
     * String query = "UPDATE ${table} SET ${column} = ? WHERE ${pk} = ? ";
     * 
     * query = query.replace("${table}", table);
     * query = query.replace("${column}", columnToUpdate);
     * if (table.equals("game")) {
     * query = query.replace("${pk}", GAME_ID);
     * }
     * 
     * // Create statement
     * try {
     * statement = connection.prepareStatement(query);
     * statement.setDouble(1, newValue);
     * statement.setInt(2, primaryKey);
     * 
     * if (statement.executeUpdate() > 0) {
     * connection.commit();
     * System.out.format("Changed %s to %s", columnToUpdate, newValue);
     * } else {
     * connection.rollback();
     * System.out.println("Error updating");
     * }
     * } catch (SQLException e) {
     * System.out.println("Error updating person");
     * e.printStackTrace();
     * } finally {
     * try {
     * statement.close();
     * } catch (SQLException e) {
     * System.out.println("Issue closing resources");
     * e.printStackTrace();
     * }
     * }
     * 
     * }
     */
}
