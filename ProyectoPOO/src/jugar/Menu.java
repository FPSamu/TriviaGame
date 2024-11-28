package jugar;

import connection.Database;
import info.Categoria;
import info.Dificultad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * The {@code Menu} class creates and manages the main menu interface for the trivia game.
 * It allows users to select game settings and start the game.
 */
public class Menu extends JFrame {
    private final Database db;

    /**
     * Constructs a new {@code Menu} instance with the specified database connection.
     * @param db The {@code Database} object for database operations.
     */
    public Menu(Database db) {
        this.db = db;
    }

    /**
     * Initializes and displays the game menu interface. This includes components for selecting
     * difficulty and category, entering a username, and starting the game.
     */
    public void startGame() {
        JFrame menu = new JFrame("Trivia Game Menu");
        menu.setSize(1280, 720);
        menu.getContentPane().setBackground(new Color(20, 20, 20));

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setBackground(new Color(20, 20, 20));
        wrapper.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JPanel title_panel = new JPanel();
        title_panel.setLayout(new BorderLayout());
        title_panel.setBackground(new Color(20, 20, 20));
        title_panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel title_label = new JLabel("Trivia Game", SwingConstants.CENTER);
        title_label.setFont(new Font("Arial", Font.BOLD, 50));
        title_label.setForeground(Color.WHITE);

        title_panel.add(title_label, BorderLayout.CENTER);
        menu.add(title_panel, BorderLayout.NORTH);

        JPanel content_panel = new JPanel();
        content_panel.setLayout(new BoxLayout(content_panel, BoxLayout.Y_AXIS));
        content_panel.setBackground(new Color(70, 70, 70));
        content_panel.setBorder(BorderFactory.createEmptyBorder(90, 30, 0, 0));

        // Instructions
        JLabel instruction_categories = new JLabel("Seleccione la dificultad y la categoría que quiera jugar", SwingConstants.CENTER);
        instruction_categories.setFont(new Font("Arial", Font.BOLD, 30));
        instruction_categories.setForeground(Color.WHITE);
        instruction_categories.setAlignmentX(Component.CENTER_ALIGNMENT);
        content_panel.add(instruction_categories);

        content_panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Dropdowns for category and difficulty selection
        JComboBox<Categoria> category_dropdown = new JComboBox<>(Categoria.values());
        category_dropdown.setFont(new Font("Arial", Font.PLAIN, 20));
        category_dropdown.setPreferredSize(new Dimension(300, 50));

        JComboBox<Dificultad> difficulty_dropdown = new JComboBox<>(Dificultad.values());
        difficulty_dropdown.setFont(new Font("Arial", Font.PLAIN, 20));
        difficulty_dropdown.setPreferredSize(new Dimension(300, 50));

        JPanel combo_box_wrapper = new JPanel();
        combo_box_wrapper.setBackground(new Color(70, 70, 70));
        combo_box_wrapper.add(category_dropdown);
        combo_box_wrapper.add(difficulty_dropdown);
        content_panel.add(combo_box_wrapper);

        // User input field
        JTextField user = new JTextField();
        user.setPreferredSize(new Dimension(300, 50));
        user.setFont(new Font("Arial", Font.PLAIN, 20));
        user.setMaximumSize(user.getPreferredSize());
        content_panel.add(user);

        // Start game button
        JButton start_button = new JButton("Empezar Juego");
        start_button.setFont(new Font("Arial", Font.BOLD, 20));
        start_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        start_button.setBackground(new Color(88, 118, 151));
        start_button.setForeground(Color.WHITE);
        start_button.setFocusPainted(false);
        content_panel.add(start_button);

        // Button action to start the game
        start_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Categoria selectedCategory = (Categoria) category_dropdown.getSelectedItem();
                Dificultad selectedDifficulty = (Dificultad) difficulty_dropdown.getSelectedItem();
                menu.setVisible(false);
                String user_str = user.getText();

                // Check if user exists or insert new user
                try (Connection conn = DriverManager.getConnection(db.getURL(), db.getUSER(), db.getPASSWORD());
                     Statement stmt = conn.createStatement()) {
                    String checkQuery = "SELECT COUNT(*) FROM users WHERE username = '" + user_str + "'";
                    ResultSet rs = stmt.executeQuery(checkQuery);
                    if (rs.next() && rs.getInt(1) == 0) {
                        stmt.executeUpdate("INSERT INTO users (username, points) VALUES ('" + user_str + "', 0)");
                    }
                } catch (SQLException err) {
                    err.printStackTrace();
                }

                // Launch the game window
                Juego juego = new Juego("Trivia Game", selectedCategory, selectedDifficulty, db, user_str);
            }
        });

        wrapper.add(content_panel, BorderLayout.CENTER);
        menu.add(wrapper);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu.setVisible(true);
    }
}
