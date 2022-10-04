1. Run 'create_db.sql' script in preferred DB program to build the database
2. Run 'populate_db.sql' script in preferred DB program to populate the database
3. Run 'ant compile' in terminal while in project folder to build src files
4. Run program with 'java -cp lib/mysql-connector-java-8.0.30.jar:classes main.java.Menu "jdbc:mysql://localhost/ser322_games_db?leautoReconnect=true&useSSL=false&useLegacyDatetimeCode=false" root password com.mysql.cj.jdbc.Driver'
