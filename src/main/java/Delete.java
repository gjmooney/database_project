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
            System.out.println("\nLet's delete an entry!");
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
                        deleteGame(connection, input);
                        break;
                    case 3:
                        deletePublisher(connection, input);
                        break;
                    case 4:
                        exit = true;
                        break;
                    default:
                        break;
                }

                connection.commit();

            } catch (InputMismatchException e) {
                input = new Scanner(System.in);
                System.out.println("Please enter a valid selection");
            } catch (SQLException e) {
                System.out.println("Issue deleting entry");
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    System.out.println("Issue rolling back changes");
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        } while (!exit);
    }

    private static void deletePerson(Connection connection, Scanner input) throws SQLException {
        // display all persons
        // ask which to delete
        // remove them from ceo/designer
        // then delete them from person
        int person = 0;
        Statement statement = null;
        ResultSet resultSet = null;
        boolean exit = false;

        // Get employee_id to delete
        System.out.println("Who would you like to delete?");
        System.out.println("Enter 0 if you don't want to remove anyone");

        // Create statement
        statement = connection.createStatement();

        // Make query
        resultSet = statement.executeQuery("SELECT employee_id, name " +
                "FROM person ");

        do {
            // Display results
            Menu.displayResults(resultSet);
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

        if (person != 0) {
            checkTable(connection, "works_for", String.valueOf(person), 1);
            checkTable(connection, "works_on", String.valueOf(person), 1);
            checkTable(connection, "ceo", String.valueOf(person), 1);
            checkTable(connection, "designer", String.valueOf(person), 1);
            removeFromTable(connection, "person", String.valueOf(person), 1);
        }
    }

    private static void deleteGame(Connection connection, Scanner input) throws SQLException {

        int game = 0;
        Statement statement = null;
        ResultSet resultSet = null;
        boolean exit = false;

        // Get game_id to delete
        System.out.println("Which game would you like to delete?");
        System.out.println("Enter 0 if you don't want to remove any");

        // Create statement
        statement = connection.createStatement();

        // Make query
        resultSet = statement.executeQuery("SELECT game_id, title " +
                "FROM game ");

        do {
            // Display results
            Menu.displayResults(resultSet);
            try {
                game = input.nextInt();
                if (game != 0) {
                    exit = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid choice.");
                input = new Scanner(System.in);
            }
        } while (!exit);

        if (game != 0) {
            checkTable(connection, "works_on", String.valueOf(game), 2);
            checkTable(connection, "rating", String.valueOf(game), 2);
            checkTable(connection, "publish", String.valueOf(game), 2);
            removeFromTable(connection, "game", String.valueOf(game), 2);
        }
    }

    private static void deletePublisher(Connection connection, Scanner input) throws SQLException {
        // display all persons
        // ask which to delete
        // remove them from ceo/designer
        // then delete them from person
        int publisher = 0;
        Statement statement = null;
        ResultSet resultSet = null;
        boolean exit = false;

        // Get employee_id to delete
        System.out.println("Which would you like to delete?");
        System.out.println("Enter 0 if you don't want to remove one");

        // Create statement
        statement = connection.createStatement();

        // Make query
        resultSet = statement.executeQuery("SELECT company_id, name " +
                "FROM publisher ");

        do {
            // Display results
            Menu.displayResults(resultSet);
            try {
                publisher = input.nextInt();
                if (publisher != 0) {
                    exit = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid choice.");
                input = new Scanner(System.in);
            }
        } while (!exit);

        if (publisher != 0) {
            checkTable(connection, "works_for", String.valueOf(publisher), 3);
            checkTable(connection, "publish", String.valueOf(publisher), 3);
            removeFromTable(connection, "publisher", String.valueOf(publisher), 3);
        }
    }

    /*
     * Identifier - 1 = person, 2 = game, 3 = publisher
     */
    private static void checkTable(Connection connection, String tableName, String keyId, int identifier) {

        String query = "SELECT ${key} FROM ${table} WHERE ${key}=${id}";
        if (identifier == 1) {
            query = query.replace("${key}", "employee_id");
        } else if (identifier == 2) {
            query = query.replace("${key}", "game_id");
        } else if (identifier == 3) {
            query = query.replace("${key}", "company_id");
        }
        query = query.replace("${table}", tableName);
        query = query.replace("${id}", keyId);
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                // remove from table
                removeFromTable(connection, tableName, keyId, identifier);
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

    private static void removeFromTable(Connection connection, String tableName, String keyId, int identifier) {
        String query = "DELETE FROM ${table} WHERE ${key}=${id}";
        if (identifier == 1) {
            query = query.replace("${key}", "employee_id");
        } else if (identifier == 2) {
            query = query.replace("${key}", "game_id");
        } else if (identifier == 3) {
            query = query.replace("${key}", "company_id");
        }
        query = query.replace("${table}", tableName);
        query = query.replace("${id}", keyId);
        Statement statement = null;

        try {
            statement = connection.createStatement();

            if (statement.executeUpdate(query) > 0) {
                System.out.println("ID " + keyId + " was removed from " + tableName);
            } else {
                System.out.println(keyId + " was not removed from " + tableName);
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

}
