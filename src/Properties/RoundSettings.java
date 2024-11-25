package Properties;

import java.io.*;
import java.util.Properties;

/*
 Klassen RoundSettings hanterar inställningar för ett spel, såsom antal rundor
 och frågor per runda. Den använder en egenskapsfil (properties-fil) för att läsa in
 dessa inställningar, vilket möjliggör enkel konfiguration utan att ändra källkoden.

 Funktioner:
 - Läser inställningar från en egenskapsfil (Properties_prop) vid programstart.
 - Tillhandahåller standardvärden (2 rundor och 2 frågor) om egenskapsfilen saknas
   eller inte innehåller rätt värden.
 - Exponerar inställningarna via getter-metoder för att användas i spelets logik.

 Används för att konfigurera spelet dynamiskt baserat på användarens preferenser
 eller förinställda värden.
 */

public class RoundSettings {
    private int rounds = 2;     // Standardvärde för antal rundor.
    private int questions = 2;  // Standardvärde för antal frågor per runda.

    // Konstruktor som läser inställningarna från en egenskapsfil.
    public RoundSettings() {
        Properties properties = new Properties(); // Skapar en Properties-instans.
        try (FileInputStream file = new FileInputStream("src/Properties/Properties_prop")) {
            // Laddar egenskaper från den angivna filen.
            properties.load(file);

            // Hämtar värdet för "Rounds" från egenskapsfilen eller använder standardvärdet "2".
            rounds = Integer.parseInt(properties.getProperty("Rounds", "2"));

            // Hämtar värdet för "Questions" från egenskapsfilen eller använder standardvärdet "2".
            questions = Integer.parseInt(properties.getProperty("Questions", "2"));
        } catch (IOException e) {
            // Hanterar fel som kan uppstå vid läsning av egenskapsfilen.
            System.err.println("Fel vid laddning av inställningar: " + e.getMessage());
        }
    }

    // Getter-metod för att hämta antalet rundor.
    public int getRounds() {
        return rounds;
    }

    // Getter-metod för att hämta antalet frågor per runda.
    public int getQuestions() {
        return questions;
    }
}