package main.java;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Query {
    public static void queryMenu(Scanner input, Connection connection) {
        // Pre-made queries
        String firstQuery = "SELECT title, profit, genre, " + "release_date, publisher.name AS \'publisher\' " +
                "FROM game " +
                "INNER JOIN publish ON game.game_id = publish.game_id " +
                "INNER JOIN publisher ON publisher.company_id = publish.company_id;";

        String secondQuery = "SELECT person.name, publisher.name AS 'publisher' " +
                "FROM ceo " +
                "INNER JOIN person ON person.employee_id = ceo.employee_id " +
                "INNER JOIN works_for ON works_for.employee_id = ceo.employee_id " +
                "INNER JOIN publisher ON works_for.company_id = publisher.company_id " +
                "WHERE publisher.name = \"Square Enix\";";

        String thirdQuery = "SELECT p.name, d.salary, g.title " +
                "FROM person p, designer d, works_on w, game g " +
                "WHERE p.employee_id = d.employee_id " +
                "AND w.employee_id = d.employee_id " +
                "AND w.game_id = g.game_id " +
                "AND g.title = 'Final Fantasy VII';";

        String fourthQuery = "SELECT p.name " +
                "FROM publisher p " +
                "INNER JOIN publish pu ON p.company_id=pu.company_id " +
                "INNER JOIN game g ON g.game_id=pu.game_id " +
                "WHERE g.title='Death Stranding';";

        int choice = 0;
        boolean exitQuery = false;
        do {
            System.out.println();
            System.out.println("\nLet's make a query!");
            System.out.println("-------------------");
            System.out.println("1. See all games and their publishers");
            System.out.println("2. Get the CEO of Square Enix");
            System.out.println("3. Get the name and salary of everyone that worked on Final Fantasy VII");
            System.out.println("4. Get the publisher of Death Stranding");
            System.out.println("5. List all persons");
            System.out.println("6. List all games");
            System.out.println("7. List all publishers");
            System.out.println("8. Return to main menu");

            try {
                choice = input.nextInt();

                switch (choice) {
                    case 1:
                        doQuery(connection, firstQuery);
                        break;
                    case 2:
                        doQuery(connection, secondQuery);
                        break;
                    case 3:
                        doQuery(connection, thirdQuery);
                        break;
                    case 4:
                        doQuery(connection, fourthQuery);
                        break;
                    case 5:
                        personInfo(connection, input);
                        break;
                    case 6:
                        gameInfo(connection, input);
                        break;
                    case 7:
                        publisherInfo(connection, input);
                        break;
                    case 8:
                        exitQuery = true;
                        break;
                    default:
                        break;
                }

            } catch (InputMismatchException e) {
                input = new Scanner(System.in);
                System.out.println("Please enter a valid selection");
            }
        } while (!exitQuery);
    }

    private static void personInfo(Connection connection, Scanner input) {
        boolean exit = false;
        int choice = -1;
        String query = null;
        doQuery(connection, "SELECT * FROM person");

        do {
            try {
                System.out.println("\nEnter ID of person to see what games they've worked on ");
                System.out.println("Enter 0 to go back");
                choice = input.nextInt();

                if (choice != 0) {
                    query = "SELECT g.title FROM game g, works_on wo WHERE wo.employee_id=${id} AND wo.game_id = g.game_id";
                    query = query.replace("${id}", String.valueOf(choice));
                    doQuery(connection, query);
                } else {
                    exit = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid choice.");
                input = new Scanner(System.in);
            }

        } while (!exit);
    }

    private static void gameInfo(Connection connection, Scanner input) {
        boolean exit = false;
        int choice = -1;
        String query = null;
        doQuery(connection, "SELECT * FROM game");

        do {
            System.out.println("\nEnter 1 to see the games publisher");
            System.out.println("Enter 2 to see the games reviews");
            System.out.println("Enter 0 to go back");
            choice = input.nextInt();
            try {
                if (choice == 1) {
                    choice = -1;
                    System.out.println("\nEnter ID of a game to see which company published the game");
                    System.out.println("Enter 0 to go back");
                    choice = input.nextInt();

                    if (choice != 0) {
                        query = "SELECT p.name  FROM publisher p, publish p2 WHERE p2.game_id=${id} AND p.company_id  = p2.company_id";
                        query = query.replace("${id}", String.valueOf(choice));
                        doQuery(connection, query);
                    } else {
                        exit = true;
                    }

                } else if (choice == 2) {
                    choice = -1;
                    System.out.println("\nEnter ID of a game to see the games reviews");
                    System.out.println("Enter 0 to go back");
                    choice = input.nextInt();

                    if (choice != 0) {
                        query = "SELECT g.title, r.reviewer, r.score " +
                                "FROM  game g " +
                                "INNER JOIN rating r ON g.game_id = r.game_id " +
                                "WHERE g.game_id = ${id}";
                        query = query.replace("${id}", String.valueOf(choice));
                        doQuery(connection, query);
                    } else {
                        exit = true;
                    }
                } else {
                    exit = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid choice.");
                input = new Scanner(System.in);
            }

        } while (!exit);
    }

    private static void publisherInfo(Connection connection, Scanner input) {
        boolean exit = false;
        int choice = -1;
        String query = null;
        doQuery(connection, "SELECT * FROM publisher");

        do {
            try {
                System.out.println("\nEnter ID of a publisher to see which who works there");
                System.out.println("Enter 0 to go back");
                choice = input.nextInt();

                if (choice != 0) {
                    query = "SELECT p2.name FROM publisher p INNER JOIN works_for wf ON p.company_id = wf.company_id INNER JOIN person p2 ON p2.employee_id = wf.employee_id WHERE p.company_id = ${id};";
                    query = query.replace("${id}", String.valueOf(choice));
                    doQuery(connection, query);
                } else {
                    exit = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid choice.");
                input = new Scanner(System.in);
            }

        } while (!exit);
    }

    private static void doQuery(Connection connection, String query) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // Create statement
            statement = connection.createStatement();

            // Make query
            resultSet = statement
                    .executeQuery(query);

            // Display results
            Menu.displayResults(resultSet);
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

}
