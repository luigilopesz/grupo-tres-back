package br.insper.grupo_tres.connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties; 
import java.io.InputStream; 
import java.io.IOException; 

public class DatabaseConnector {

    public static void main(String[] args) {

        Properties props = new Properties();
        InputStream input = null;
        Connection connection = null;

        try {
            
            String filename = "db.properties";
            input = DatabaseConnector.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                System.err.println("Sorry, unable to find " + filename);
                return; 
            }

            
            props.load(input);

            
            String jdbcUrl = props.getProperty("db.url", "");
            String username = props.getProperty("db.username", "");
            String password = props.getProperty("db.password", ""); 

            
            if (jdbcUrl.isEmpty() || username.isEmpty() || password.isEmpty()) {
                 System.err.println("Error: db.url, db.username, or db.password not found in db.properties");
                 return;
            }

            
            Class.forName("org.postgresql.Driver");

            System.out.println("Connecting to database using properties...");
            
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            if (connection != null) {
                System.out.println("Connected successfully!");
                
                connection.close();
                System.out.println("Connection closed.");
            } else {
                System.out.println("Failed to make connection!");
            }

        } catch (IOException ex) {
            System.err.println("Error reading properties file: " + ex.getMessage());
            ex.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection Failed! Check output console");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
             System.err.println("PostgreSQL JDBC Driver not found!");
             e.printStackTrace();
        } finally {
            
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Connection closed in finally block.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}