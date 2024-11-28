package tests;

import connection.Database;
import info.Categoria;
import info.Dificultad;
import jugar.Menu;
import preguntas.Pregunta;
import preguntas.PreguntaOpcionMultiple;

import java.io.FileNotFoundException;

public class test {
    public static void main(String[] args) throws FileNotFoundException{
        Database db = new Database("TriviaGame");
        db.createDatabase();
        Pregunta banco;
        db.createTable("multiplechoice", 4);
        PreguntaOpcionMultiple preguntas;

        banco = new Pregunta(db.getURL(), db.getUSER(), db.getPASSWORD(), db.getName());
        preguntas = new PreguntaOpcionMultiple("src/Sports_Trivia.txt", Dificultad.FACIL, Categoria.DEPORTES);
        preguntas.createQuestion();
        banco.addQuestion(preguntas);
        banco.storeQuestions("multiplechoice");

        preguntas = new PreguntaOpcionMultiple("src/Science_Trivia.txt", Dificultad.FACIL, Categoria.CIENCIA);
        preguntas.createQuestion();
        banco.addQuestion(preguntas);
        banco.storeQuestions("multiplechoice");

        preguntas = new PreguntaOpcionMultiple("src/Geography_Trivia.txt", Dificultad.FACIL, Categoria.GEOGRAFIA);
        preguntas.createQuestion();
        banco.addQuestion(preguntas);
        banco.storeQuestions("multiplechoice");

        preguntas = new PreguntaOpcionMultiple("src/Entertainment_Trivia.txt", Dificultad.FACIL, Categoria.ENTRETENIMIENTO);
        preguntas.createQuestion();
        banco.addQuestion(preguntas);
        banco.storeQuestions("multiplechoice");

        Menu jugar = new Menu(db);
        jugar.startGame();
    }
}
