package main.java;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Query {
    public static void queryMenu(Scanner input, Connection connection) {

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
                        firstQuery(connection);
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

    private static void firstQuery(Connection connection) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // Create statement
            statement = connection.createStatement();

            // Make query
            resultSet = statement
                    .executeQuery("SELECT title, profit, genre, " + "release_date, publisher.name AS \'publisher\' " +
                            "FROM game " +
                            "INNER JOIN publish ON game.game_id = publish.game_id " +
                            "INNER JOIN publisher ON publisher.company_id = publish.company_id;");

            // Display results
            displayResults(resultSet);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static void displayResults(ResultSet resultSet) {
        System.out.println("display");
        try {

            // Get number of columns
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columns = rsmd.getColumnCount();
            String[] columnNames = new String[columns];

            // Get column names
            for (int i = 1; i <= columns; i++) {
                columnNames[i - 1] = rsmd.getColumnName(i);
            }

            // Create header row
            StringBuilder headers = new StringBuilder();
            for (String column : columnNames) {
                headers.append(column + "\t\t ");
            }

            System.out.println(headers);

            while (resultSet.next()) {
                System.out.format("%-18s", resultSet.getInt("profit"));
                System.out.format("%-18s", resultSet.getString("genre"));
                System.out.format("%-18s", resultSet.getDate("release_date"));
                System.out.format("%-18s", resultSet.getString("title"));
                System.out.println(resultSet.getString("publisher"));
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

    }

}