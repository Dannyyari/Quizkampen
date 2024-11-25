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

    private int rounds;
    private int questions;

    public RoundSettings() {
        Properties properties = new Properties();
        try (FileInputStream file = new FileInputStream("src/Properties/Properties_prop")) {
            properties.load(file); //Laddar från filen till properties
        } catch (IOException e) {
            System.out.println("Fel vid laddning av inställningar: " + e.getMessage());
        }
        //placerar antalet rundor från properties_prop filen in till dessa 2 variabler
        rounds = Integer.parseInt(properties.getProperty("Rounds", "2"));
        questions = Integer.parseInt(properties.getProperty("Questions", "2"));
    }

    //Getter för att hämta antalet rundor och antal frågor
    public int getRounds() {
        return rounds;
    }

    public int getQuestions() {
        return questions;
    }
}