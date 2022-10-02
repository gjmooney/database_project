package main.java;

import java.sql.Connection;
import java.sql.Date;
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
            System.out.println("\nLet's make an insert!");
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
                        gameInsertMenu(connection, input);
                        break;
                    case 3:
                        publisherInsertMenu(connection, input);
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
        String nextPrompt = null;
        LinkedList<Integer> games = new LinkedList<>();
        boolean exit = false;

        do {

            try {
                System.out.println("What's this persons name?");
                name = input.next();
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
                    startDate = input.next();
                    if (!Update.parseDate(startDate)) {
                        throw new NumberFormatException();
                    }
                }

                // CEOs cant work on games
                System.out.println("Enter 1 if they have worked on any games in our DB");
                nextPrompt = input.next();
                if (String.valueOf(nextPrompt).equals("1")) {
                    System.out.println("-----------------------------");
                    getSeveralRows(connection, input, games, 2);
                    nextPrompt = null;
                }

                System.out.println("Enter 1 if they work for a publisher in our DB");
                nextPrompt = input.next();
                if (String.valueOf(nextPrompt).equals("1")) {
                    System.out.println("-----------------------------");
                    publisher = displayPublishers(connection, input);
                    nextPrompt = null;
                }
                exit = true;

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid choice.\n");
                input = new Scanner(System.in);
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid choice.\n");
                input = new Scanner(System.in);
            }

        } while (!exit);

        try {

            // Insert into person - has commit
            insertIntoPerson(connection, name);

            // Get the auto generated ID
            employeeId = getId(connection, name, 1, "person");

            if (role == 1) {
                // Insert into CEO
                insertIntoRole(
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
                insertIntoRole(
                        connection,
                        "designer",
                        null,
                        String.valueOf(employeeId),
                        String.valueOf(salary),
                        startDate,
                        null,
                        null);
            }

            // Commit employee table inserts
            connection.commit();

            // Insert into works_on
            if (games.size() != 0) {
                insertListIntoTable(connection, employeeId, games, "works_on", 1);
            }

            // Insert into works_for
            if (publisher != 0) {
                insertIntoRole(connection,
                        "works_for",
                        null,
                        String.valueOf(employeeId),
                        null, startDate,
                        String.valueOf(publisher),
                        null);
            }

            connection.commit();
            games.clear();
        } catch (SQLException e) {
            System.out.println("Issue with insert. Rolling back changes");
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Issue with rollback");
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private static void gameInsertMenu(Connection connection, Scanner input) {
        // get the games title
        // get profit
        // get genre
        // get release date
        // get publisher
        // get employees - ceo can't work on?
        // get rating?
        // must have designer (any number) and publisher (only one)
        String title = null;
        int keyId = 0;
        double profit = 0.0;
        String genre;
        String releaseDateString = null;
        Date releaseDate = null;
        int publisher = 0;
        LinkedList<Integer> employees = new LinkedList<>();
        boolean exit = false;

        do {

            try {
                // Basic game info
                System.out.println("What's this games title?");
                title = input.next();
                System.out.format("Please enter %s's profit: ", title);
                profit = input.nextDouble();
                System.out.format("Please enter %s's genre: ", title);
                genre = input.next();
                System.out.format("Please enter %s's release date (YYYY-MM-DD): ", title);
                releaseDateString = input.next();
                if (Update.parseDate(releaseDateString)) {
                    releaseDate = Date.valueOf(releaseDateString);
                } else {
                    throw new NumberFormatException();
                }

                // Get the publisher
                System.out.println("\nPlease select the publisher");
                publisher = displayPublishers(connection, input);

                // Get people that worked on it
                System.out.format("Please select the people that worked on %s", title);
                System.out.println();
                employees = getSeveralRows(connection, input, employees, 1);
                exit = true;

                // Insert into game table
                insertIntoGame(connection, title, profit, genre, releaseDate);

                // Quick query to get the game ID
                keyId = getId(connection, title, 2, "game");

                // Insert into publish table
                insertIntoPublish(connection, publisher, keyId);

                // Insert into works_on table
                insertListIntoTable(connection, keyId, employees, "works_on", 2);

                connection.commit();
                employees.clear();

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.\n");
                input = new Scanner(System.in);
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid amount\n");
                input = new Scanner(System.in);
            } catch (SQLException e) {
                System.out.println("SQL issue");
                e.printStackTrace();
            }

        } while (!exit);
    }

    private static void publisherInsertMenu(Connection connection, Scanner input) {
        // get the publishers name
        // get employees - must have at least one
        // get games - can be none
        String name = null;
        int keyId = 0;
        LinkedList<Integer> employees = new LinkedList<>();
        LinkedList<Integer> games = new LinkedList<>();
        boolean exit = false;

        do {

            try {
                // Basic game info
                System.out.println("What is the publishers name??");
                name = input.next();

                // Get people that work there
                // Only display people not working at a publisher already
                System.out.format("Please select the people that work at %s", name);
                employees = getSeveralRowsExclude(connection, input, employees, 1, "works_for");

                // Games require a publisher so there can't be any
                // games without one, so we don' prompt for that
                exit = true;

                // Insert into publisher table
                insertIntoPublisher(connection, name);
                // connection.commit();

                // Quick query to get the game ID
                keyId = getId(connection, name, 3, "publisher");

                // Insert into publish table
                // insertListIntoTable(connection, keyId, games, "publish", 1);

                // Insert into works_for table
                insertListIntoTable(connection, keyId, employees, "works_for", 2);

                connection.commit();
                employees.clear();
                games.clear();

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                input = new Scanner(System.in);
            } catch (SQLException e) {
                System.out.println("SQL issue");
                e.printStackTrace();
            }

        } while (!exit);
    }

    /*
     * Type: 1 - person, 2 - game, 3 - publisher
     */
    static int getId(Connection connection, String name, int type, String tableName) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT ${id} FROM ${table} WHERE ${name}=?";
        int id = 0;
        query = query.replace("${table}", tableName);

        if (type == 1) {
            query = query.replace("${id}", "employee_id");
            query = query.replace("${name}", "name");
        } else if (type == 2) {
            System.out.println("CHECK");
            query = query.replace("${id}", "game_id");
            query = query.replace("${name}", "title");
            System.out.println("QUERY " + query);
        } else {
            query = query.replace("${id}", "company_id");
            query = query.replace("${name}", "name");
        }
        statement = connection.prepareStatement(query);
        statement.setString(1, name);

        resultSet = statement.executeQuery();
        // Id should be only column
        resultSet.next();
        id = resultSet.getInt(1);
        resultSet.close();
        statement.close();
        return id;
    }

    /*
     * Type: 1 = person, 2 = game
     */
    private static LinkedList<Integer> getSeveralRows(Connection connection, Scanner input,
            LinkedList<Integer> entities, int type) {
        Statement statement = null;
        ResultSet resultSet = null;
        int next = -1;
        boolean exit = false;
        String query = "SELECT ${type}_id, ${name} FROM ${table}";

        if (type == 1) {
            query = query.replace("${type}", "employee");
            query = query.replace("${name}", "name");
            query = query.replace("${table}", "person");
        } else if (type == 2) {
            query = query.replace("${type}", "game");
            query = query.replace("${name}", "title");
            query = query.replace("${table}", "game");
        }

        try {
            // Create statement
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            // Make query
            resultSet = statement.executeQuery(query);

            do {
                System.out.println("Enter IDs of selections");
                System.out.println("Enter 0 when all entities selected");

                // Display results
                Menu.displayResults(resultSet);
                try {
                    next = input.nextInt();

                    if (next == 0) {
                        exit = true;
                    } else {
                        entities.add(next);
                    }
                    resultSet.beforeFirst();
                } catch (NumberFormatException | InputMismatchException e) {
                    // Clears the input buffer if there was a problem
                    input.next();
                    resultSet.beforeFirst();
                    System.out.println("Please enter a valid choice.");
                    input = new Scanner(System.in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (!exit);
        } catch (Exception e) {
            System.out.println("Issue making multi-row query");
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
        return entities;
    }

    /*
     * Type: 1 = person, 2 = game
     * This version of the method excludes games that have a publisher
     * and empoylees that have an employer
     * Exclude is table for the entity to not be on
     */
    private static LinkedList<Integer> getSeveralRowsExclude(Connection connection, Scanner input,
            LinkedList<Integer> entities, int type, String exclude) {
        Statement statement = null;
        ResultSet resultSet = null;
        int next = -1;
        boolean exit = false;
        String query = "SELECT ${type}_id, ${name} FROM ${table} WHERE ${type}_id NOT IN (SELECT ${type_id} FROM ${exclude})";

        query = query.replace("${exclude}", exclude);

        if (type == 1) {
            query = query.replace("${type}", "employee");
            query = query.replace("${name}", "name");
            query = query.replace("${table}", "person");
        } else if (type == 2) {
            query = query.replace("${type}", "game");
            query = query.replace("${name}", "title");
            query = query.replace("${table}", "game");
        }

        try {
            // Create statement
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            // Make query
            resultSet = statement.executeQuery(query);

            do {
                System.out.println("Enter IDs of selections");
                System.out.println("Enter 0 when all entities selected");

                // Display results
                Menu.displayResults(resultSet);
                try {
                    next = input.nextInt();

                    if (next == 0) {
                        exit = true;
                    } else {
                        entities.add(next);
                    }
                    resultSet.beforeFirst();
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid choice.");
                    input = new Scanner(System.in);
                }
            } while (!exit);
        } catch (Exception e) {
            System.out.println("Issue making multi-row query");
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
        return entities;
    }

    private static int displayPublishers(Connection connection, Scanner input) {
        Statement statement = null;
        ResultSet resultSet = null;
        boolean exit = false;
        int publisher = 0;

        try {
            // Create statement
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

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
                    resultSet.beforeFirst();
                    input = new Scanner(System.in);
                } catch (InputMismatchException e) {
                    resultSet.beforeFirst();
                    System.out.println("Please enter a valid publisher ID");
                    input = new Scanner(System.in);
                }
            } while (!exit);
        } catch (Exception e) {
            System.out.println("Issue making display publishers query");
            e.printStackTrace();
        }

        return publisher;
    }

    private static void insertIntoGame(Connection connection,
            String title,
            Double profit,
            String genre,
            Date releaseDate) {
        PreparedStatement statement = null;

        try {
            statement = connection
                    .prepareStatement("INSERT INTO game (title, profit, genre, release_date) VALUES (?, ?, ?, ?); ");
            statement.setString(1, title);
            statement.setDouble(2, profit);
            statement.setString(3, genre);
            statement.setDate(4, releaseDate);

            if (statement.executeUpdate() > 1) {
                System.out.format("Inserted %s into game table", title);
            }

            connection.commit();

        } catch (SQLException e) {
            System.out.println("Error inserting game");
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources");
                e.printStackTrace();
            }
        }
    }

    private static void insertIntoPublisher(Connection connection,
            String name) {
        PreparedStatement statement = null;

        try {
            statement = connection
                    .prepareStatement("INSERT INTO publisher (name) VALUES (?); ");
            statement.setString(1, name);

            if (statement.executeUpdate() > 1) {
                System.out.format("Inserted %s into publisher table", name);
            }

            connection.commit();

        } catch (SQLException e) {
            System.out.println("Error inserting publisher");
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources");
                e.printStackTrace();
            }
        }
    }

    private static void insertIntoPublish(Connection connection, int companyId, int gameId) {
        PreparedStatement statement = null;

        try {
            statement = connection
                    .prepareStatement("INSERT INTO publish (company_id, game_id) VALUES (?, ?); ");
            statement.setInt(1, companyId);
            statement.setInt(2, gameId);

            if (statement.executeUpdate() > 1) {
                System.out.println("Inserted into publish table");
            }

            // connection.commit();

        } catch (SQLException e) {
            System.out.println("Error inserting entry");
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources");
                e.printStackTrace();
            }
        }
    }

    private static void insertIntoPerson(
            Connection connection, String name) {
        PreparedStatement statement = null;

        try {
            // Create statement
            statement = connection.prepareStatement("INSERT INTO person (name) VALUES (?) ");
            statement.setString(1, name);

            // Make an update
            if (statement.executeUpdate() > 0) {
                System.out.println("Insert into person : SUCCESS");
            } else {
                System.out.println("Insert into person : NO GOOD");
            }
            connection.commit();

        } catch (SQLException e) {
            System.out.println("Issue making person update");
            String message = e.getMessage();
            if (message.contains("Duplicate entry")) {
                System.out.println("\nSorry, that entity is already in the DB.");
            } else if (message.contains("foreign key constraint")) {
                System.out.println("\nSorry that entity does not exist in the database");
            } else {
                e.printStackTrace();
            }
            System.out.println(e.getMessage());
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

    private static void insertIntoRole(
            Connection connection,
            String tableName,
            String name,
            String employeeId,
            String salary,
            String startDate,
            String companyId,
            LinkedList<Integer> games) {
        Statement statement = null;

        String query = "INSERT INTO ${table} (${columns}) VALUES (${values}) ";
        query = query.replace("${table}", tableName);
        String values;

        // Replace query params based on table name
        switch (tableName) {
            case "ceo":
                query = query.replace("${columns}", "employee_id");
                query = query.replace("${values}", employeeId);
                break;
            case "designer":
                query = query.replace("${columns}", "employee_id, salary, employment_date");
                values = employeeId + ", " + salary + ", '" + startDate + "'";
                query = query.replace("${values}", values);
                break;
            case "works_for":
                query = query.replace("${columns}", "employee_id, company_id");
                values = employeeId + ", " + companyId;
                query = query.replace("${values}", values);
                break;

            default:
                System.out.println("Something went wrong");
                break;
        }
        try {
            // Create statement
            statement = connection.createStatement();

            // Make an update
            if (statement.executeUpdate(query) > 0) {
                System.out.println("Insert into " + tableName + ": SUCCESS");
            } else {
                System.out.println("Insert into " + tableName + ": NO GOOD");
            }

        } catch (Exception e) {
            System.out.println("Issue making person update");
            String message = e.getMessage();
            if (message.contains("Duplicate entry")) {
                System.out.println("\nSorry, that entity is already in the DB.");
            } else if (message.contains("foreign key constraint")) {
                System.out.println("\nSorry that entity does not exist in the database");
            } else {
                e.printStackTrace();
            }
            System.out.println(e.getMessage());
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

    /*
     * type: 1 = list is of games, 2 = list is of people
     */
    private static void insertListIntoTable(Connection connection, int keyId, LinkedList<Integer> list,
            String tableName, int type) {

        PreparedStatement statement = null;
        String query = "INSERT INTO ${table} (${columns}) VALUES (?, ?)";
        query = query.replace("${table}", tableName);

        // Replace query columns based on table name
        if (tableName.equals("works_on")) {
            query = query.replace("${columns}", "employee_id, game_id");
        } else if (tableName.equals("publish")) {
            query = query.replace("${columns}", "company_id, game_id");
        } else if (tableName.equals("works_for")) {
            query = query.replace("${columns}", "employee_id, company_id");
        }
        try {
            // Create statement
            statement = connection.prepareStatement(query);

            // Make an update
            for (Integer listId : list) {
                if (type == 1) {
                    statement.setInt(1, keyId);
                    statement.setInt(2, listId);
                } else {
                    statement.setInt(1, listId);
                    statement.setInt(2, keyId);
                }

                if (statement.executeUpdate() > 0) {
                    System.out.format("Insert into %s: SUCCESS", tableName);
                } else {
                    System.out.format("Insert into %s: NO GOOD", tableName);
                }
                System.out.println();
                // connection.commit();
                statement.clearParameters();
            }
        } catch (Exception e) {
            System.out.println("Issue making works on insert");
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
