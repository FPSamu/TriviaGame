package preguntas;

import info.Categoria;
import info.Dificultad;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The {@code PreguntaOpcionMultiple} class extends {@code Pregunta} to manage multiple-choice
 * questions. It reads questions from a specified file, storing questions, choices, and correct answers.
 */
public class PreguntaOpcionMultiple extends Pregunta {
    private List<String> questions;          // List to store the questions
    private List<String> choices;            // List to store choices for all questions (unused)
    private List<String> correctAnswers;     // List to store the correct answers for each question
    private List<List<String>> choices_Question; // List of lists, where each list contains choices for a specific question
    private Dificultad difficulty;           // Difficulty level of the questions
    private Categoria category;              // Category of the questions
    private File file;                       // File from which questions are read

    /**
     * Constructs a {@code PreguntaOpcionMultiple} object with the specified file path,
     * difficulty, and category.
     * @param file_path Path to the file containing the questions and answers.
     * @param difficulty Difficulty level of the questions.
     * @param category Category of the questions.
     */
    public PreguntaOpcionMultiple(String file_path, Dificultad difficulty, Categoria category) {
        super("", "", "", "");  // Note: Database connection details are passed as empty strings here
        this.file = new File(file_path);
        this.difficulty = difficulty;
        this.category = category;
        this.questions = new ArrayList<>();
        this.correctAnswers = new ArrayList<>();
        this.choices_Question = new ArrayList<>();
    }

    /**
     * Reads questions, answers, and choices from the file. Each line in the file
     * is expected to be formatted as "question;correct_answer;choice1;choice2;...;choiceN".
     * This method populates the respective lists with these details.
     * @throws FileNotFoundException if the specified file is not found.
     */
    public void createQuestion() throws FileNotFoundException {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";", -1);
                this.questions.add(parts[0]);
                this.correctAnswers.add(parts[1]);
                List<String> currentChoices = new ArrayList<>();
                for (int i = 2; i < parts.length; i++) {
                    currentChoices.add(parts[i]);
                }
                this.choices_Question.add(currentChoices);
            }
        } catch (FileNotFoundException e) {
            throw e;
        }
    }

    // Getter methods for accessing the questions, choices, and other attributes

    public List<String> getCorrectAnswer() { return correctAnswers; }

    public List<String> getQuestion() { return questions; }

    public List<List<String>> getChoices() { return choices_Question; }

    public Dificultad getDificulty() { return difficulty; }

    public Categoria getCategory() { return category; }
}
