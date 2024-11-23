package Properties;

import java.io.*;
import java.util.Properties;

public class RoundSettings {
    private int rounds = 2;
    private int questions = 2;

    public RoundSettings() {
        Properties properties = new Properties();
        try (FileInputStream file = new FileInputStream("src/Properties/Properties_prop")) {
            properties.load(file);
            rounds = Integer.parseInt(properties.getProperty("Rounds", "2"));
            questions = Integer.parseInt(properties.getProperty("Questions", "2"));
        } catch (IOException e) {
            System.err.println("Fel vid laddning av inställningar: " + e.getMessage());
        }
    }

    public int getRounds() {
        return rounds;
    }

    public int getQuestions() {
        return questions;
    }
}