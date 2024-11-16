package Questions.Geo;

import Questions.QuestionsAndAnswers;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DAO_Geografi implements Serializable {
   private List<QuestionsAndAnswers> geoQuestions = new ArrayList<>();

    public DAO_Geografi() {
        String readLine;
        try (BufferedReader reader = new BufferedReader(new FileReader("src/Questions/Geo/GeoQuestions"));)
        {
            while ((readLine = reader.readLine()) != null) {
                String[] strArray = readLine.split(", ");
                geoQuestions.add(new QuestionsAndAnswers(strArray[0], strArray[1], strArray[2], strArray[3], strArray[4]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

     public List<QuestionsAndAnswers> getRandomizedList(){
        List<QuestionsAndAnswers> listOfRandomized= geoQuestions;
         Collections.shuffle(listOfRandomized);
        return listOfRandomized;
     }


}
