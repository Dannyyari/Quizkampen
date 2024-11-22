package Properties;

import java.io.*;
import java.util.Properties;

public class RoundSettings {
    int rounds;
    int questions;

    public RoundSettings(){
        Properties prop=new Properties();
        try(FileInputStream file=new FileInputStream("src/Properties/Properties_prop")) {
            prop.load(file);

        } catch (FileNotFoundException e) {
            System.out.println("ej hittad fil");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("error loading" );
            throw new RuntimeException(e);
        }
        rounds= Integer.parseInt(prop.getProperty("Rounds", "2"));
        questions=Integer.parseInt(prop.getProperty("Questions", "2"));
    }

    public int getRounds() {
        return rounds;
    }

    public int getQuestions() {
        return questions;
    }
}