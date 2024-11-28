package preguntas;

import info.Categoria;
import info.Dificultad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Pregunta} class is responsible for storing multiple-choice questions
 * in a specified database table. It holds a list of {@code PreguntaOpcionMultiple}
 * objects, each representing a multiple-choice question.
 */
public class Pregunta {
    // List to hold multiple-choice questions
    List<PreguntaOpcionMultiple> preguntas = new ArrayList<>();

    // Database URL
    private final String URL;

    // Database user name
    private final String USER;

    // Database password
    private final String PASSWORD;

    /**
     * Constructs a new {@code Pregunta} instance with specified database connection details.
     * @param URL the JDBC URL for the database connection
     * @param USER the database user name
     * @param PASSWORD the database password
     */
    public Pregunta(String URL, String USER, String PASSWORD, String DB_NAME) {
        this.URL = URL;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
    }

    /**
     * Adds a {@code PreguntaOpcionMultiple} object to the list of questions.
     * @param pregunta the multiple-choice question to add
     */
    public void addQuestion(PreguntaOpcionMultiple pregunta) {
        this.preguntas.add(pregunta);
    }

    /**
     * Stores all questions from the list into a specified database table. The method constructs
     * and executes SQL INSERT statements for each question.
     * @param table the name of the database table where questions will be stored
     */
    public void storeQuestions(String table) {
        Connection conn = null;
        Statement stmt = null;
        Boolean flag = true;
        try {
            for (int i = 0; i < preguntas.get(0).getQuestion().size(); i++) {
                try {
                    conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    stmt = conn.createStatement();
                    String query;
                    query = "INSERT INTO " + table +
                            " (question,correct_answer,";
                    for (int j = 1; j <= preguntas.get(0).getChoices().get(0).size(); j++) {
                        query += "choice" + j + ",";
                    }
                    query += "difficulty,category) VALUES ('" + preguntas.get(0).getQuestion().get(i) + "','" + preguntas.get(0).getCorrectAnswer().get(i) + "',";
                    for (int j = 0; j < preguntas.get(0).getChoices().get(0).size(); j++) {
                        query += "'" + preguntas.get(0).getChoices().get(i).get(j);
                        if (j < preguntas.get(0).getChoices().get(0).size() - 1) query += "',";
                    }
                    query += "','" + preguntas.get(0).getDificulty() + "','" + preguntas.get(0).getCategory() + "')";
                    stmt.executeUpdate(query);

                } catch (SQLException e) {
                    System.out.println("No connection to Database.");
                    flag = false;
                }
            }
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (flag) System.out.println("Questions stored in database table");
        preguntas = new ArrayList<>();
    }
}
