package Questions;

import java.io.Serializable;

/*
 Klassen QuestionsAndAnswers representerar en fråga med flera svarsalternativ.
 Den är utformad för att vara serialiserbar, vilket gör det möjligt att skicka
 instanser av denna klass över ett nätverk eller spara dem till en fil.

 Funktioner:
 - Håller en fråga (question) och fyra svarsalternativ.
 - Identifierar det korrekta svaret bland de fyra alternativen.
 - Ger tillgång till frågan och svaren via getter-metoder.
 - Innehåller en toString-metod för att representera frågan och det korrekta svaret i textformat.

 Klassen används för att skapa frågor i ett quiz eller frågesportspel.
 */

public class QuestionsAndAnswers implements Serializable {
    // Filens innehåll som ska delas upp in i dessa variablerx
    private final String question;
    private final String correctAnswer;
    private final String answer2;
    private final String answer3;
    private final String answer4;

    // Konstruktor för att initiera frågan och alla svarsalternativ.
    public QuestionsAndAnswers(String question, String correctAnswer, String answer2, String answer3, String answer4) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
    }

    // Getter för frågor och svar för att sedan placera på paneler
    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getAnswer2() {
        return answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public String getAnswer4() {
        return answer4;
    }


    @Override
    public String toString() {
        return "Fråga: " + question + "\nRätt svar: " + correctAnswer;
    }
}