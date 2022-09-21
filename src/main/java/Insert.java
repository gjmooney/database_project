package main.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

public class Insert {
    public static void insertMenu(Scanner input, Connection connection) {

        int choice = 0;
        boolean exit = false;
        do {
            System.out.println();
            System.out.println("Let's make an insert!");
            System.out.println("Which table would you like to add to?");
            System.out.println("-------------------------------------");
            System.out.println("1. Person");
            System.out.println("2. Game");
            System.out.println("3. Publisher");
            System.out.println("4. Return to main menu");

            try {
                choice = input.nextInt();

                switch (choice) {
                    case 1:
                        personInsertMenu(connection, input);
                        break;
                    case 2:
                        // doQuery(connection, secondQuery);
                        break;
                    case 3:
                        // doQuery(connection, thirdQuery);
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

    private static void personInsertMenu(Connection connection, Scanner input) {
        // get the persons name
        // insert into people
        // prompt for role - ceo, designer, both
        // insert into role table
        // prompt if they worked on a game
        // display games in table to choose from or none
        // add to works_on
        // ask who they work for
        String name = null;
        int employeeId = 0;
        double salary = 0.0;
        String startDate = null;
        int role = 0;
        int publisher = 0;
        int nextPrompt = 0;
        LinkedList<Integer> games = new LinkedList<>();
        boolean exit = false;

        do {

            try {
                System.out.println("What's this persons name?");
                name = input.nextLine();
                System.out.println("Please enter their employee id");
                employeeId = input.nextInt();
                System.out.println("What's this persons role?");
                System.out.println("-------------------------");
                System.out.println("1. CEO");
                System.out.println("2. Designer");
                // System.out.println("3. Both");
                role = input.nextInt();

                if (role == 2) {
                    System.out.println("Please enter their salary:");
                    salary = input.nextDouble();
                    System.out.println("Please enter their start date (YYYY-MM-DD)");
                    startDate = input.nextLine();
                }

                System.out.println("Enter 1 if they have worked on a game in our DB");
                nextPrompt = input.nextInt();
                if (nextPrompt == 1) {
                    System.out.println("-----------------------------");
                    displayGames(connection, input, games);
                    nextPrompt = 0;
                }

                System.out.println("Enter 1 if they work for a publisher in our DB");
                nextPrompt = input.nextInt();
                if (nextPrompt == 1) {
                    System.out.println("-----------------------------");
                    publisher = displayPublishers(connection, input);
                    nextPrompt = 0;
                }
                exit = true;

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid choice.");
                input = new Scanner(System.in);
            }

        } while (!exit);

        // Insert into person
        insertIntoPerson(
                connection,
                "person",
                name,
                String.valueOf(employeeId),
                null,
                null,
                null,
                null);

        if (role == 1) {
            // Insert into CEO
            insertIntoPerson(
                    connection,
                    "ceo",
                    null,
                    String.valueOf(employeeId),
                    null,
                    null,
                    null,
                    null);
        } else if (role == 2) {
            // Insert into designer
            insertIntoPerson(
                    connection,
                    "designer",
                    null,
                    String.valueOf(employeeId),
                    String.valueOf(salary),
                    startDate,
                    null,
                    null);
        }

        // Insert into works_on
        insertIntoWorksOn(connection, employeeId, games);

        // Insert into works_for
        if (publisher != 0) {
            insertIntoPerson(connection,
                    "works_for",
                    null,
                    String.valueOf(employeeId),
                    null, startDate,
                    String.valueOf(publisher),
                    null);
        }

        /*
         * insertIntoPerson(connection, "person", name, String.valueOf(employeeId),
         * String.valueOf(salary), startDate,
         * String.valueOf(publisher), games);
         */
    }

    private static LinkedList<Integer> displayGames(Connection connection, Scanner input, LinkedList<Integer> games) {
        Statement statement = null;
        ResultSet resultSet = null;
        boolean exit = false;

        try {
            // Create statement
            statement = connection.createStatement();

            // Make query
            resultSet = statement.executeQuery("SELECT game_id, title " +
                    "FROM game ");

            do {
                System.out.println("Enter game IDs of games worked on");
                // Display results
                Query.displayResults(resultSet);
                try {
                    games.add(input.nextInt());
                    System.out.println("Enter 0 when all games selected");
                    if (input.nextInt() == 0) {
                        exit = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid choice.");
                    input = new Scanner(System.in);
                }
            } while (!exit);
        } catch (Exception e) {
            System.out.println("Issue making games query");
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
        return games;
    }

    private static int displayPublishers(Connection connection, Scanner input) {
        Statement statement = null;
        ResultSet resultSet = null;
        boolean exit = false;
        int publisher = 0;

        try {
            // Create statement
            statement = connection.createStatement();

            // Make query
            resultSet = statement.executeQuery("SELECT company_id, name " +
                    "FROM publisher ");

            do {
                // Display results
                Query.displayResults(resultSet);
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
        } catch (Exception e) {
            System.out.println("Issue making query");
            e.printStackTrace();
        }

        return publisher;
    }

    private static void insertIntoPerson(
            Connection connection,
            String tableName,
            String name,
            String employeeId,
            String salary,
            String startDate,
            String companyId,
            LinkedList<Integer> games) {
        Statement statement = null;
        ResultSet resultSet = null;

        String query = "INSERT INTO ${table} (${columns}) VALUES (${values}) ";
        query.replace("${table}", tableName);
        String values;

        // Replace query params based on table name
        switch (tableName) {
            case "person":
                query.replace("${columns}", "employee_id, name");
                values = name + ", " + employeeId;
                query.replace("${values}", values);
                break;
            case "ceo":
                query.replace("${columns}", "employee_id");
                query.replace("${values}", employeeId);
                break;
            case "designer":
                query.replace("${columns}", "employee_id, salary, start_date");
                values = employeeId + ", " + salary + ", " + startDate;
                query.replace("${values}", values);
                break;
            case "works_for":
                query.replace("${columns}", "employee_id, company_id");
                values = employeeId + ", " + companyId;
                query.replace("${values}", values);
                break;

            default:
                System.out.println("Something went wrong");
                break;
        }
        try {
            // Create statement
            statement = connection.createStatement();

            // Make query
            resultSet = statement.executeQuery(query);

            // Display results
            Query.displayResults(resultSet);
        } catch (Exception e) {
            System.out.println("Issue making query");
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

    private static void insertIntoWorksOn(Connection connection, int employeeId, LinkedList<Integer> games) {

        PreparedStatement statement = null;
        try {
            // Create statement
            statement = connection.prepareStatement("INSERT INTO works_on (employee_id, game_id) VALUES (?, ?)");

            // Make an update
            for (Integer gameId : games) {
                statement.setInt(1, employeeId);
                statement.setInt(2, gameId);
                if (statement.executeUpdate() > 0) {
                    System.out.println("SUCCESS");
                } else {
                    System.out.println("NO GOOD");
                }
                statement.clearParameters();
            }
        } catch (Exception e) {
            System.out.println("Issue making update");
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
