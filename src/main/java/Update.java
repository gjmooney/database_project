package main.java;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Update {
    static final String EMP_ID = "employee_id";
    static final String GAME_ID = "game_id";
    static final String COM_ID = "company_id";
    static final List<String> columnNames = List.of(
            "employee_id",
            "name",
            "salary",
            "employment_date",
            "company_id",
            "game_id",
            "title",
            "profit",
            "genre",
            "release_date",
            "reviewer",
            "score");

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
            System.out.println("4. Rating");
            System.out.println("5. Works For");
            System.out.println("6. Works On");
            System.out.println("7. Publish");
            System.out.println("8. Return to main menu");

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
                        updateTable(connection, input, "rating");
                        break;
                    case 5:
                        updateWorksFor(connection, input);
                        break;
                    case 6:
                        updateWorksOn(connection, input);
                        break;
                    case 7:
                        updatePublish(connection, input);
                        break;
                    case 8:
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
        boolean exit = false;
        boolean validColumn = false;
        String reviewer = null;

        do {
            try {
                // Display table
                statement = connection.createStatement();
                String query = "SELECT * FROM ${table}";
                query = query.replace("${table}", tableName);
                resultSet = statement.executeQuery(query);
                Menu.displayResults(resultSet);

                // Prompt for which to update
                System.out.format("Enter the ID of the %s you would like to update?\n", tableName);
                System.out.println("Enter 0 to cancel");
                choice = input.nextInt();

                if (choice != 0) {
                    // Get the second PK for rating table
                    if (tableName.equals("rating")) {
                        reviewer = getReviewer(connection, input);
                    }

                    // CEOs don't have additional info but designers do
                    // so check if the person is a designer here and redisplay
                    // the table with their columns
                    if (tableName.equals("person") && checkAndDisplayIfDesigner(connection, choice)) {
                        tableName = "designer";
                    }

                    do {
                        // prompt for which column to update
                        System.out.println("\nWhich column would you like to update?");
                        System.out.println("Please enter column name as displayed");
                        columnToUpdate = input.next().toLowerCase();
                        if (parseColumn(columnToUpdate)) {
                            validColumn = true;
                        } else {
                            System.out.println("Please enter a valid column name");
                        }
                    } while (!validColumn);

                    // Swap back to person table to update designer name
                    if (tableName.equals("designer") && columnToUpdate.equals("name")) {
                        tableName = "person";
                    }
                    // Get new value
                    System.out.println("\nPlease enter the new value for " + columnToUpdate);
                    if (columnToUpdate.equals("release_date")) {
                        System.out.println("Date format is YYYY-MM-DD");
                    }
                    newString = input.next();

                    if (tableName.equals("rating")) {
                        updateValue(connection, choice, tableName, columnToUpdate, newString, reviewer);
                    } else {
                        updateValue(connection, choice, tableName, columnToUpdate, newString, null);
                    }
                }
                exit = true;

            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid choice.");
                input = new Scanner(System.in);
            } catch (SQLException e) {
                System.out.println("Issue updating");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("generic Please enter a valid choice.");
                input = new Scanner(System.in);

            } finally {
                try {
                    resultSet.close();
                    statement.close();
                } catch (Exception e) {
                    System.out.println("Issue updating table");
                }
            }
        } while (!exit);
    }

    private static void updateWorksFor(Connection connection, Scanner input) {
        boolean exit = false;
        int empId = -1;
        int companyId = 0;
        Statement statement = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        do {
            try {
                // Display table
                statement = connection.createStatement();
                String query = "SELECT p.employee_id, p.name, " +
                        "p2.company_id, p2.name  " +
                        "FROM works_for wf " +
                        "INNER JOIN person p ON p.employee_id = wf.employee_id " +
                        "INNER JOIN publisher p2 ON p2.company_id = wf.company_id;";
                resultSet = statement.executeQuery(query);
                Menu.displayResults(resultSet);

                // Prompt for which to update
                System.out.println("Enter the ID of the employee you would like to update?\n");
                System.out.println("Enter 0 to cancel");
                empId = input.nextInt();

                if (empId != 0) {

                    // prompt for which column to update
                    System.out.println("Enter the company ID of new employer");

                    companyId = input.nextInt();

                    ps = connection.prepareStatement("UPDATE works_for SET company_id = ? WHERE employee_id = ?;");

                    ps.setInt(1, companyId);
                    ps.setInt(2, empId);

                    if (ps.executeUpdate() > 0) {
                        System.out.println("Update was successful");
                        connection.commit();
                    } else {
                        System.out.println("Update was unsuccessful");
                        connection.rollback();

                    }

                }
                exit = true;

            } catch (SQLException e) {
                System.out.println("Issue updating works_for");
                e.printStackTrace();
            } catch (InputMismatchException e) {
                System.out.println("Please enter valid selection");
                input = new Scanner(System.in);
            } finally {
                try {
                    resultSet.close();
                    if (ps != null) {
                        ps.close();
                    }
                    statement.close();
                } catch (Exception e) {
                    System.out.println("Issue updating works_for");
                }
            }
        } while (!exit);
    }

    private static void updateWorksOn(Connection connection, Scanner input) {
        boolean exit = false;
        int empId = -1;
        int gameId = 0;
        int columnToUpdate = -1;
        int newValue = -1;
        Statement statement = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        do {
            try {
                // Display table
                statement = connection.createStatement();
                String query = "SELECT p.employee_id, p.name, g.game_id, g.title " +
                        "FROM works_on wo, person p, game g " +
                        "WHERE p.employee_id = wo.employee_id " +
                        "AND g.game_id = wo.game_id;";
                resultSet = statement.executeQuery(query);
                Menu.displayResults(resultSet);

                // Prompt for which to update
                System.out.println("\nEnter the employee ID of the record to modify");
                empId = input.nextInt();

                System.out.println("\nEnter the game ID of the record to modify");
                System.out.println("Enter 0 to cancel");
                gameId = input.nextInt();

                if (empId != 0 && gameId != 0) {
                    System.out.println("Enter 1 to update the employee, enter 2 to update the game");
                    columnToUpdate = input.nextInt();

                    if (columnToUpdate == 1) {
                        resultSet = statement.executeQuery("SELECT * FROM person");
                        Menu.displayResults(resultSet);
                        System.out.println("Enter ID of new employee");

                        newValue = input.nextInt();

                        ps = connection.prepareStatement(
                                "UPDATE works_on SET employee_id = ? WHERE game_id = ? AND employee_id = ?;");

                        ps.setInt(1, newValue);
                        ps.setInt(2, gameId);
                        ps.setInt(3, empId);

                    } else if (columnToUpdate == 2) {
                        resultSet = statement.executeQuery("SELECT game_id, title FROM game");
                        Menu.displayResults(resultSet);
                        System.out.println("Enter ID of new game");

                        newValue = input.nextInt();

                        ps = connection.prepareStatement(
                                "UPDATE works_on SET game_id = ? WHERE game_id = ? AND employee_id = ?;");

                        ps.setInt(1, newValue);
                        ps.setInt(2, gameId);
                        ps.setInt(3, empId);
                    }

                    if (ps.executeUpdate() > 0) {
                        System.out.println("Update was successful");
                        connection.commit();
                    } else {
                        System.out.println("Update was unsuccessful");
                        connection.rollback();

                    }
                }
                exit = true;

            } catch (SQLException e) {
                System.out.println("Issue updating works_on");
                e.printStackTrace();
            } catch (InputMismatchException e) {
                System.out.println("Please enter valid selection");
                input = new Scanner(System.in);
            } finally {
                try {
                    resultSet.close();
                    if (ps != null) {
                        ps.close();
                    }
                    statement.close();
                } catch (Exception e) {
                    System.out.println("Issue updating works_on");
                }
            }
        } while (!exit);
    }

    private static void updatePublish(Connection connection, Scanner input) {
        boolean exit = false;
        int gameId = -1;
        int companyId = 0;
        Statement statement = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        do {
            try {
                // Display table
                statement = connection.createStatement();
                String query = "SELECT g.game_id, g.title, p2.company_id, " +
                        "p2.name " +
                        "FROM game g " +
                        "INNER JOIN publish p ON g.game_id = p.game_id " +
                        "INNER JOIN publisher p2 ON p2.company_id = p.company_id;";
                resultSet = statement.executeQuery(query);
                Menu.displayResults(resultSet);

                // Prompt for which to update
                System.out.println("Enter the ID of the game you would like to update?\n");
                System.out.println("Enter 0 to cancel");
                gameId = input.nextInt();

                if (gameId != 0) {
                    resultSet = statement.executeQuery("SELECT * FROM publisher");

                    System.out.println("Enter the company ID of new publisher");
                    Menu.displayResults(resultSet);

                    companyId = input.nextInt();

                    ps = connection.prepareStatement("UPDATE publish SET company_id = ? WHERE game_id = ?;");

                    ps.setInt(1, companyId);
                    ps.setInt(2, gameId);

                    if (ps.executeUpdate() > 0) {
                        System.out.println("Update was successful");
                    } else {
                        System.out.println("Update was unsuccessful");
                        connection.rollback();

                    }

                    connection.commit();
                }
                exit = true;

            } catch (SQLException e) {
                System.out.println("Issue updating publish");
                e.printStackTrace();
            } catch (InputMismatchException e) {
                System.out.println("Please enter valid selection");
                input = new Scanner(System.in);
            } finally {
                try {
                    resultSet.close();
                    if (ps != null) {
                        ps.close();
                    }
                    statement.close();
                } catch (Exception e) {
                    System.out.println("Issue updating works_for");
                }
            }
        } while (!exit);
    }

    static String getReviewer(Connection connection, Scanner input) {
        String reviewer = null;
        boolean exit = false;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        System.out.println("Please enter the name of the reviewer as well");
        do {
            try {

                reviewer = input.next();
                statement = connection.prepareStatement("SELECT reviewer FROM rating WHERE reviewer = ?;");
                statement.setString(1, reviewer);

                resultSet = statement.executeQuery();

                if (!resultSet.next()) {
                    System.out.println("Reviewer not found, please try again");
                    statement.clearParameters();
                } else {
                    exit = true;
                    return reviewer;
                }

            } catch (SQLException e) {
                System.out.println("Issue querying rating table");
                e.printStackTrace();
            } finally {
                try {
                    resultSet.close();
                    statement.close();
                } catch (SQLException e) {
                    System.out.println("Issue querying rating table");
                    e.printStackTrace();
                }
            }
        } while (!exit);
        return reviewer;
    }

    private static boolean checkAndDisplayIfDesigner(Connection connection, int employeeId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(
                    "SELECT d.employee_id, p.name, d.salary, d.employment_date  FROM designer d JOIN person p ON d.employee_id = p.employee_id  WHERE p.employee_id=?;",
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setInt(1, employeeId);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                resultSet.beforeFirst();
                Menu.displayResults(resultSet);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Issue checking if person is a designer");
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("Issue checking if person is a designer");
                e.printStackTrace();
            }
        }
        return false;
    }

    private static void updateValue(Connection connection, int key, String table, String column,
            String newValue, String reviewer) {
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
        } else if (table.equals("rating")) {
            query = query.replace("${pk}", GAME_ID);
            // Second PK for rating table
            query = query.replace("${pk} = ?", "${pk} = ? AND reviewer = ?");
        }

        // Create statement
        try {
            statement = connection.prepareStatement(query);
            // Set first PreparedStatement value based on column name
            if (column.equals("name") | column.equals("genre") | column.equals("title")) {
                statement.setString(1, newValue);

            } else if (column.equals("profit") | column.equals("salary")) {
                if (parseDouble(newValue)) {
                    statement.setDouble(1, Double.valueOf(newValue));
                } else {
                    throw new NumberFormatException();
                }

            } else if (column.equals("release_date") | column.equals("employment_date")) {
                // Parse date before setting
                if (parseDate(newValue)) {
                    statement.setDate(1, Date.valueOf(newValue));
                } else {
                    throw new NumberFormatException();
                }

            } else if (column.equals("score")) {
                if (parseInt(newValue)) {
                    statement.setInt(1, Integer.valueOf(newValue));
                    statement.setString(3, reviewer);
                } else {
                    throw new NumberFormatException();
                }

            } else if (column.equals("reviewer")) {
                statement.setString(1, newValue);
                statement.setString(3, reviewer);
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
            // e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error updating " + table + " table");
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

    private static boolean parseDouble(String value) {
        try {
            Double.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean parseInt(String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean parseColumn(String column) {

        return columnNames.contains(column);
    }
}
