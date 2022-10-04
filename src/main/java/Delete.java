package main.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
            System.out.println("4. Rating");
            System.out.println("5. Works For");
            System.out.println("6. Works On");
            System.out.println("7. Return to main menu");

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
                        deleteRating(connection, input);
                        break;
                    case 5:
                        removeFromWorksFor(connection, input);
                        break;
                    case 6:
                        removeFromWorksOn(connection, input);
                        break;
                    case 7:
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

        // Get employee_id to delete
        System.out.println("Who would you like to delete?");
        System.out.println("Enter 0 if you don't want to remove anyone");

        // Create statement
        statement = connection.createStatement();

        // Make query
        resultSet = statement.executeQuery("SELECT employee_id, name " +
                "FROM person ");

        // Display results
        Menu.displayResults(resultSet);
        try {
            person = input.nextInt();
            if (person != 0) {
                checkTable(connection, "works_for", String.valueOf(person), 1);
                checkTable(connection, "works_on", String.valueOf(person), 1);
                checkTable(connection, "ceo", String.valueOf(person), 1);
                checkTable(connection, "designer", String.valueOf(person), 1);
                removeFromTable(connection, "person", String.valueOf(person), 1);
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid choice.");
            input = new Scanner(System.in);
        }

    }

    private static void deleteGame(Connection connection, Scanner input) throws SQLException {

        int game = 0;
        Statement statement = null;
        ResultSet resultSet = null;

        // Get game_id to delete
        System.out.println("Which game would you like to delete?");
        System.out.println("Enter 0 if you don't want to remove any");

        // Create statement
        statement = connection.createStatement();

        // Make query
        resultSet = statement.executeQuery("SELECT game_id, title " +
                "FROM game ");

        // Display results
        Menu.displayResults(resultSet);
        try {
            game = input.nextInt();
            if (game != 0) {
                checkTable(connection, "works_on", String.valueOf(game), 2);
                checkTable(connection, "rating", String.valueOf(game), 2);
                checkTable(connection, "publish", String.valueOf(game), 2);
                removeFromTable(connection, "game", String.valueOf(game), 2);
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid choice.");
            input = new Scanner(System.in);
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

        // Get employee_id to delete
        System.out.println("Which would you like to delete?");
        System.out.println("Enter 0 if you don't want to remove one");

        // Create statement
        statement = connection.createStatement();

        // Make query
        resultSet = statement.executeQuery("SELECT company_id, name " +
                "FROM publisher ");

        // Display results
        Menu.displayResults(resultSet);
        try {
            publisher = input.nextInt();
            if (publisher != 0) {
                checkTable(connection, "works_for", String.valueOf(publisher), 3);
                checkTable(connection, "publish", String.valueOf(publisher), 3);
                removeFromTable(connection, "publisher", String.valueOf(publisher), 3);
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid choice.");
            input = new Scanner(System.in);
        }
    }

    private static void deleteRating(Connection connection, Scanner input) throws SQLException {
        // display all ratings
        // ask rating to delete
        // remove them from rating
        int gameId = 0;
        String reviewer = null;
        Statement statement = null;
        ResultSet resultSet = null;

        // Get employee_id to delete
        System.out.println("Which rating would you like to delete?");
        System.out.println("Enter 0 if you don't want to remove one");

        if (gameId != 0) {
            // Create statement
            statement = connection.createStatement();

            // Make query
            resultSet = statement.executeQuery("SELECT * " +
                    "FROM rating ");

            // Display results
            Menu.displayResults(resultSet);
            try {
                // get game_id
                gameId = input.nextInt();

                // get second PK
                reviewer = Update.getReviewer(connection, input);

                if (gameId != 0) {
                    removeFromRatingTable(connection, reviewer, gameId);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid choice.");
                input = new Scanner(System.in);
            }
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

    /*
     * Identifier: 1 - person, 2 - game, 3 - publisher
     */
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

    private static void removeFromRatingTable(Connection connection, String reviewer, int keyId) {
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("DELETE FROM rating WHERE game_id = ? AND reviewer = ?");
            statement.setInt(1, keyId);
            statement.setString(2, reviewer);

            if (statement.executeUpdate() > 0) {
                System.out.println("Rating was removed from rating table");
            } else {
                System.out.println("Rating was not removed from table");
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

    private static void removeFromWorksFor(Connection connection, Scanner input) {
        int empId = 0;
        Statement statement = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        boolean exit = false;

        do {
            try {
                // Create statement
                statement = connection.createStatement();

                // Make query
                resultSet = statement.executeQuery("SELECT * " +
                        "FROM works_for ");

                // Display results
                Menu.displayResults(resultSet);
                // Get employee_id to delete
                System.out.println("Enter employee ID to remove from employment record");
                System.out.println("Enter 0 if you don't want to remove one");
                // get game_id
                empId = input.nextInt();

                if (empId != 0) {
                    ps = connection.prepareStatement("DELETE FROM works_for WHERE employee_id = ?");
                    ps.setInt(1, empId);

                    if (ps.executeUpdate() > 0) {
                        System.out.println("Employment record was removed from works_for table");
                        connection.commit();
                    } else {
                        System.out.println("Employment record was not removed from table");
                        connection.rollback();
                    }
                }
                exit = true;

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid choice.");
                input = new Scanner(System.in);
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
        } while (!exit);

    }

    private static void removeFromWorksOn(Connection connection, Scanner input) {
        boolean exit = false;
        int empId = -1;
        int gameId = 0;
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
                        "AND g.game_id = wo.game_id; ";
                resultSet = statement.executeQuery(query);
                Menu.displayResults(resultSet);

                // Prompt for which to update
                System.out.println("\nEnter the employee ID of the record to modify");
                empId = input.nextInt();

                System.out.println("\nEnter the game ID of the record to modify");
                System.out.println("Enter 0 to cancel");
                gameId = input.nextInt();

                if (empId != 0 && gameId != 0) {

                    ps = connection.prepareStatement(
                            "DELETE FROM works_on WHERE game_id = ? AND employee_id = ?;");
                    ps.setInt(1, gameId);
                    ps.setInt(2, empId);

                    if (ps.executeUpdate() > 0) {
                        System.out.println("Deleting was successful");
                        connection.commit();
                    } else {
                        System.out.println("Deleting was unsuccessful");
                        connection.rollback();

                    }
                }
                exit = true;

            } catch (SQLException e) {
                System.out.println("Issue deleting from works_on");
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

}
