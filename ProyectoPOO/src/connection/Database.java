/**
 * The {@code Database} class manages the connection to a MySQL database and allows
 * creating databases, tables, and managing the schema for a trivia game application.
 * It supports operations like creating a database, a table for questions, and a users table.
 * The connection details and credentials are hardcoded for local use.
 */
package connection;

import java.sql.*;

public class Database {
    // Database name for the trivia game
    String db_name;

    // JDBC URL for connecting to the MySQL database
    private static String URL = "jdbc:mysql://localhost:3306/";

    // Default username for the MySQL connection
    private static final String USER = "root";

    // Default password for the MySQL connection (empty for local development)
    private static final String PASSWORD = "";

    // Number of choices for each question in the trivia game
    private int columns;

    // Name of the table storing trivia questions
    private String table_name;

    /**
     * Constructs a new {@code Database} object with the specified database name.
     * @param db_name the name of the database to connect to or create
     */
    public Database(String db_name) {
        this.db_name = db_name;
    }

    /**
     * Creates a new database if it does not exist.
     * The method establishes a connection to MySQL, executes the SQL statement to create
     * a new database, and appends the database name to the URL.
     */
    public void createDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            stmt = conn.createStatement();

            String query = "CREATE DATABASE IF NOT EXISTS " + db_name;
            stmt.executeUpdate(query);
            System.out.println("Database created successfully or already exists.");
            URL += db_name;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Creates a new table for storing trivia questions and their choices if it does not exist.
     * This method also truncates the table if it exists to reset its data and ensures creation
     * of a users table for tracking players.
     *
     * @param table_name the name of the table to create or verify
     * @param columns the number of choices for each question
     */
    public void createTable(String table_name, int columns) {
        this.table_name = table_name;
        this.columns = columns;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + db_name, USER, PASSWORD);
            stmt = conn.createStatement();

            String columns_desc = "id INT NOT NULL AUTO_INCREMENT, question VARCHAR(255), correct_answer VARCHAR(255), ";
            for (int i = 1; i <= columns; i++) {
                columns_desc += "choice" + i + " VARCHAR(255), ";
            }
            columns_desc += "difficulty VARCHAR(255), category VARCHAR(255)";

            String query = "CREATE TABLE IF NOT EXISTS " + table_name + " (" + columns_desc + ", PRIMARY KEY (id))";
            stmt.executeUpdate(query);
            query = "TRUNCATE TABLE " + table_name;
            stmt.executeUpdate(query);

            create_users_table();

            System.out.println("Table created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Creates a new users table if it does not exist to store player information.
     */
    private void create_users_table() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(this.URL, this.getUSER(), this.getPASSWORD());
            stmt = conn.createStatement();

            String query = "CREATE TABLE IF NOT EXISTS users (id INT NOT NULL AUTO_INCREMENT, username VARCHAR(255), points INT, PRIMARY KEY (id))";
            System.out.println(query);
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Getter methods for class fields
    public String getURL() { return URL; }
    public String getUSER() { return USER; }
    public String getPASSWORD() { return PASSWORD; }
    public String getName() { return db_name; }
    public String getTable() { return table_name; }
    public int getChoicesNumber() { return this.columns - 3; }
}
