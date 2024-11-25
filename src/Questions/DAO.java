package Questions;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 Klassen DAO (Data Access Object) hanterar inläsning och lagring av frågor och svar
 från en fil till en lista av QuestionsAndAnswers-objekt. Den används för att
 separera logiken för datahantering från andra delar av applikationen.

 Funktioner:
 - Laddar frågor från en fil som innehåller data i CSV-format (komma-separerade värden).
 - Skapar en lista av QuestionsAndAnswers-objekt baserat på filens innehåll.
 - Ger tillgång till frågorna via en oföränderlig lista.
 - Lagrar en kategori som identifierar vilken typ av frågor som hanteras.

 Används som en del av ett frågesportsystem där frågorna hanteras från en specifik källa.
 */

public class DAO {
    private final List<QuestionsAndAnswers> questionsAndAnswers = new ArrayList<>(); // Lista för att lagra frågorna.
    private final String category; // Kategori för frågorna, t.ex. "Historia" eller "Sport".

    // Konstruktor som initierar DAO med en kategori och läser frågor från en angiven fil.
    public DAO(String category, String path) {
        this.category = category;
        loadQuestions(path); // Ladda frågorna från filen vid instansiering.
    }

    // Privat metod för att läsa frågor från en fil och skapa QuestionsAndAnswers-objekt.
    private void loadQuestions(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            // Läser filen rad för rad.
            while ((line = reader.readLine()) != null) {
                // Dela upp raden i delar baserat på ", " som separator.
                String[] parts = line.split(", ");

                // Kontrollera att raden har exakt fem delar (en fråga och fyra svar).
                if (parts.length == 5) {
                    // Skapa ett nytt QuestionsAndAnswers-objekt och lägg till det i listan.
                    questionsAndAnswers.add(new QuestionsAndAnswers(parts[0], parts[1], parts[2], parts[3], parts[4]));
                } else {
                    // Felmeddelande om en rad har ett ogiltigt format.
                    System.err.println("Ogiltigt format: " + line);
                }
            }
        } catch (IOException e) {
            // Felhantering om något går fel vid läsning av filen.
            System.err.println("Fel vid laddning av frågor: " + e.getMessage());
        }
    }

    // Getter-metod för att hämta kategorin för frågorna.
    public String getCategory() {
        return category;
    }

    // Getter-metod för att hämta den oföränderliga listan av frågor och svar.
    public List<QuestionsAndAnswers> getQuestionsAndAnswers() {
        return Collections.unmodifiableList(questionsAndAnswers); // Gör listan oföränderlig.
    }
}