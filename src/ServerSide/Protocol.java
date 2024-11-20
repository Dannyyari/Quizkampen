package ServerSide;
import Questions.QuestionsAndAnswers;

import java.io.Serializable;
import java.util.List;

public class Protocol implements Serializable {

    /*
    har den bara ifall danny inte kan få sitt sätt att funka, det här är mer som en backup
     */

    // Enum för att representera de olika typerna av meddelanden som skickas
    public enum Type {
        CATEGORY,      // För att skicka kategori
        QUESTIONS,     // För att skicka frågor
        ANSWER,        // För att skicka svar
        STATE,         // För att skicka spelets tillstånd
        ERROR          // För att skicka felmeddelanden
    }

    // Enum för att representera spelets tillstånd
    public enum GameState {
        STATE_WAITING,   // När spelare 1 väntar på att spelare 2 ska ansluta
        STATE_READY,     // När spelare 2 är ansluten
        CATEGORY_CHOSEN, // När en kategori har valts
        GET_QUESTIONS,   // När frågor ska skickas
        GET_ANSWER,      // När ett svar ska tas emot från klienten
        FINISH           // När spelet är slut
    }

    private Type type;         // Typen av meddelande
    private Object payload;     // Själva data (kan vara lista med kategorier, frågor eller svar)

    public Protocol(Type type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public Type getType() {
        return type;
    }


    public Object getPayload() {
        return payload;
    }


    public void setType(Type type) {
        this.type = type;
    }


    public void setPayload(Object payload) {
        this.payload = payload;
    }

    // Metod för att skapa meddelande för att skicka kategorier
    public static Protocol createCategoryMessage(List<String> categories) {
        return new Protocol(Type.CATEGORY, categories);
    }

    // Metod för att skapa meddelande för att skicka frågor
    public static Protocol createQuestionsMessage(List<QuestionsAndAnswers> questions) {
        return new Protocol(Type.QUESTIONS, questions);
    }

    // Metod för att skapa meddelande för att skicka ett svar
    public static Protocol createAnswerMessage(Object answer) {
        return new Protocol(Type.ANSWER, answer);
    }

    // Metod för att skapa meddelande för att skicka spelets tillstånd (GameState)
    public static Protocol createStateMessage(GameState gameState) {
        return new Protocol(Type.STATE, gameState);
    }

    // Metod för att skapa meddelande för att skicka fel
    public static Protocol createErrorMessage(String errorMessage) {
        return new Protocol(Type.ERROR, errorMessage);
    }

    // ToString-metod för att skriva ut meddelandets innehåll
    @Override
    public String toString() {
        return "Protocol{" +
                "type=" + type +
                ", payload=" + payload +
                '}';
    }
}