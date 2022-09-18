package main.java;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Query {
    public static void QueryMenu (Scanner input, Connection connection) {

        int choice = 0;
        boolean exitQuery = false;
        do {
            System.out.println();
            System.out.println("Let's make a query!");
            System.out.println("-------------------");
            System.out.println("1. See all games and their publishers");
            System.out.println("5. ");

            try {
                choice = input.nextInt();

                switch (choice) {
                    case 1:
                        FirstQuery(connection);
                        break;
                
                    default:
                        break;
                }
                
            } catch (InputMismatchException e) {
                input = new Scanner(System.in);
                System.out.println("Please enter a valid selection");
            }
        } while (!exitQuery);
        input.close();
    }

    private static void FirstQuery(Connection connection) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // Create statement
            statement = connection.createStatement();

            // Make query 
            resultSet = statement.executeQuery("SELECT title, profit, genre, " + "release_date, publisher.name AS \'publisher\' " +
            "FROM game " +
            "INNER JOIN publish ON game.game_id = publish.game_id " +
            "INNER JOIN publisher ON publisher.company_id = publish.company_id;");

            // Display results
            DisplayResults(resultSet);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static void DisplayResults(ResultSet resultSet) {

    }
    
}
