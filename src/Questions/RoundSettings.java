package Questions;

import java.io.*;
import java.util.Properties;

public class RoundSettings {
    int rounds;
    int questions;

    public RoundSettings(){
        Properties prop=new Properties();
        try(FileInputStream file=new FileInputStream("src/Questions/Properties")) {

            prop.load(file);
            rounds= Integer.parseInt(prop.getProperty("Rounds", "2"));
            questions=Integer.parseInt(prop.getProperty("Questions", "2"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public int getRounds() {
        return rounds;
    }

    public int getQuestions() {
        return questions;
    }
}
