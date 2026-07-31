package com.examseating.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection - Utility class to manage JDBC connections to MySQL.
 * 
 * Reads connection parameters from db.properties on the application classpath:
 *   db.driver  - JDBC driver class name
 *   db.url     - Database URL
 *   db.user    - Database username
 *   db.password - Database password
 * 
 * Usage:
 *   Connection conn = DBConnection.getConnection();
 *   // use connection...
 *   conn.close();  // or use try-with-resources
 */
public class DBConnection {

    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static boolean loaded = false;

    /**
     * Loads database properties from src/db.properties, deployed to WEB-INF/classes.
     * Called once on first connection request (lazy initialization).
     */
    private static synchronized void loadProperties() {
        if (loaded) return;
        
        try {
            Properties props = new Properties();
            
            // Load db.properties from WEB-INF/classes via the application classloader.
            InputStream input = DBConnection.class.getClassLoader()
                    .getResourceAsStream("db.properties");
            
            if (input == null) {
                // Fallback: try loading from WEB-INF via the thread context classloader
                input = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("db.properties");
            }
            
            if (input == null) {
                // Final fallback: try relative path within WEB-INF
                input = DBConnection.class.getClassLoader()
                        .getResourceAsStream("../db.properties");
            }
            
            if (input != null) {
                props.load(input);
                input.close();
            } else {
                // If properties file not found, use defaults
                System.err.println("[DBConnection] WARNING: db.properties not found. Using defaults.");
            }
            
            String driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            URL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/exam_seating_db");
            USER = props.getProperty("db.user", "root");
            PASSWORD = props.getProperty("db.password", "root");
            
            // Load the JDBC driver class
            Class.forName(driver);
            
            loaded = true;
            System.out.println("[DBConnection] Database connection configured successfully.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] ERROR: MySQL JDBC driver not found. " +
                    "Make sure mysql-connector-j.jar is in WEB-INF/lib/");
            throw new RuntimeException("MySQL JDBC driver not found", e);
        } catch (Exception e) {
            System.err.println("[DBConnection] ERROR: Failed to load db.properties - " + e.getMessage());
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    /**
     * Returns a new JDBC Connection to the configured MySQL database.
     * Caller is responsible for closing the connection (use try-with-resources).
     * 
     * @return a live Connection object
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        if (!loaded) {
            loadProperties();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
