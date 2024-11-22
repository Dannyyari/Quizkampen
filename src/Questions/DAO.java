package Questions;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DAO {
    private final List<QuestionsAndAnswers> questionsAndAnswers = new ArrayList<>();
    private final String category;

    public DAO(String category, String path) {
        this.category = category;
        loadQuestions(path);
    }

    private void loadQuestions(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length == 5) {
                    questionsAndAnswers.add(new QuestionsAndAnswers(parts[0], parts[1], parts[2], parts[3], parts[4]));
                } else {
                    System.err.println("Ogiltigt format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Fel vid laddning av frågor: " + e.getMessage());
        }
    }

    public String getCategory() {
        return category;
    }

    public List<QuestionsAndAnswers> getQuestionsAndAnswers() {
        return Collections.unmodifiableList(questionsAndAnswers);
    }
}
