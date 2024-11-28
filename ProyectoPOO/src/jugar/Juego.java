package jugar;

import connection.Database;
import info.Categoria;
import info.Dificultad;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * The {@code Juego} class creates and controls the game interface for a trivia game.
 * It manages the game window, displays questions and choices, and tracks user scores.
 */
public class Juego {
    private JFrame game;
    private JPanel choices;
    private JLabel title_label;
    private JLabel current_points;
    private JLabel max_points_general;
    private JLabel max_user_points;
    private final String title;
    private final Categoria category;
    private final Dificultad difficulty;
    private final Database db;
    private int points;
    private final String username;
    int questions_cont;

    /**
     * Constructs a new {@code Juego} object for managing the trivia game.
     * It sets up the game window and loads initial data.
     * @param title the title of the game window
     * @param category the category of questions to be used in the game
     * @param difficulty the difficulty level of the questions
     * @param db the database connection used to fetch questions and manage scores
     * @param user the username of the player
     */
    public Juego(String title, Categoria category, Dificultad difficulty, Database db, String user) {
        this.title = title;
        this.category = category;
        this.difficulty = difficulty;
        this.db = db;
        this.username = user;
        this.points = 0;
        this.questions_cont = 0;
        initializeUI();
    }

    /**
     * Initializes the user interface elements of the game.
     * Sets up the layout and components of the game window.
     */
    private void initializeUI() {
        game = new JFrame(title);
        game.setSize(1280, 720);
        game.getContentPane().setBackground(new Color(20, 20, 20));
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(20, 20, 20));
        wrapper.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        title_label = new JLabel("", SwingConstants.CENTER);
        title_label.setFont(new Font("Arial", Font.BOLD, 25));
        title_label.setForeground(Color.WHITE);

        JPanel question_panel = new JPanel(new BorderLayout());
        question_panel.setBackground(new Color(20, 20, 20));
        question_panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        question_panel.add(title_label, BorderLayout.CENTER);

        choices = new JPanel(new GridLayout(0, 2, 10, 10));
        choices.setBackground(new Color(70, 70, 70));

        current_points = new JLabel("Score: " + points);
        current_points.setForeground(Color.WHITE);

        max_points_general = new JLabel();
        max_points_general.setForeground(Color.WHITE);

        max_user_points = new JLabel();
        max_user_points.setForeground(Color.WHITE);

        JPanel scorePanel = new JPanel(new GridLayout(1, 3));
        scorePanel.setBackground(new Color(20,20,20));
        scorePanel.add(current_points);
        scorePanel.add(max_points_general);
        scorePanel.add(max_user_points);
        wrapper.add(scorePanel, BorderLayout.SOUTH);

        wrapper.add(question_panel, BorderLayout.NORTH);
        wrapper.add(choices, BorderLayout.CENTER);

        game.add(wrapper);
        game.setVisible(true);

        loadMaxScores();
        loadNewQuestion();
    }

    /**
     * Loads the highest scores from the database and updates the UI.
     * It queries the maximum scores both globally and for the current user.
     */
    private void loadMaxScores() {
        try (Connection conn = DriverManager.getConnection(db.getURL(), db.getUSER(), db.getPASSWORD());
             Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM users ORDER BY points DESC LIMIT 1";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                max_points_general.setText("General max score = " + rs.getString("username") + ": " + rs.getInt("points"));
            }

            query = "SELECT MAX(points) AS max_points FROM users WHERE username = '" + this.username + "'";
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                max_user_points.setText("Your max score: " + rs.getInt("max_points"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(game, "Error accessing database: " + e.getMessage());
        }
    }

    /**
     * Updates the user's score in the database if the new score is higher than the current best.
     * @param username the username of the player
     * @param newPoints the new score to compare and potentially update in the database
     */
    private void loadNewQuestion() {
        choices.removeAll();
        try (Connection conn = DriverManager.getConnection(db.getURL(), db.getUSER(), db.getPASSWORD());
             Statement stmt = conn.createStatement()) {

            String query = this.category == Categoria.RANDOM ?
                    "SELECT * FROM " + db.getTable() + " WHERE difficulty='" + this.difficulty + "' ORDER BY RAND() LIMIT 1" :
                    "SELECT * FROM " + db.getTable() + " WHERE category='" + this.category + "' AND difficulty='" + this.difficulty + "' ORDER BY RAND() LIMIT 1";

            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                title_label.setText(rs.getString("question"));
                String correct_answer = rs.getString("correct_answer");

                Font buttonFont = new Font("Arial", Font.PLAIN, 25);
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount - 5; i++) {
                    String choice = rs.getString("choice" + i);
                    JButton button = new JButton(choice);
                    button.setFont(buttonFont);
                    button.addActionListener(e -> {
                        if (choice.equals(correct_answer)) {
                            points += 100;
                            current_points.setText("Score: " + points);
                            updatePointsIfNecessary(username, points);
                            questions_cont++;
                            if (questions_cont < 10) {
                                loadNewQuestion();
                            } else {
                                JOptionPane.showMessageDialog(game,
                                        "You have answered " + points / 100 + " questions correctly!!\n" +
                                                "You scored " + points + " points",
                                        "Game Over",
                                        JOptionPane.INFORMATION_MESSAGE);
                                game.dispose();
                                System.exit(0);
                            }
                        } else {
                            JOptionPane.showMessageDialog(game,
                                    "Wrong Answer!",
                                    "Try Again",
                                    JOptionPane.INFORMATION_MESSAGE);
                            questions_cont++;
                            if (questions_cont < 10) {
                                loadNewQuestion();
                            } else {
                                JOptionPane.showMessageDialog(game,
                                        "You have answered " + points / 100 + " questions correctly!!\n" +
                                                "You scored " + points + " points",
                                        "Game Over",
                                        JOptionPane.INFORMATION_MESSAGE);
                                game.dispose();
                                System.exit(0);
                            }
                        }
                    });
                    choices.add(button);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(game, "Error accessing database: " + e.getMessage());
        }
        choices.revalidate();
        choices.repaint();
    }

    /**
     * Compares and updates the user's points in the database if the new points are higher
     * than the previously recorded high score for that user.
     * @param username the username of the player
     * @param newPoints the newly calculated points after answering a question correctly
     */
    private void updatePointsIfNecessary(String username, int newPoints) {
        String selectQuery = "SELECT points FROM users WHERE username = ?";
        String updateQuery = "UPDATE users SET points = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(db.getURL(), db.getUSER(), db.getPASSWORD());
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            selectStmt.setString(1, username);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                int currentPoints = rs.getInt("points");
                if (newPoints > currentPoints) {
                    // Only prepare and execute update statement if needed
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, newPoints);
                        updateStmt.setString(2, username);
                        updateStmt.executeUpdate();
                    } // This closes updateStmt automatically
                }
            }
            rs.close(); // Ensure ResultSet is closed here
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
