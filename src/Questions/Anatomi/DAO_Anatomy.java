package Questions.Anatomi;

import Questions.QuestionsAndAnswers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DAO_Anatomy implements Serializable {
    List<QuestionsAndAnswers> anatomyQuestion=new ArrayList<>();

    public DAO_Anatomy(){
        String readLine;
        try(BufferedReader reader=new BufferedReader(new FileReader("src/Questions/Anatomi/AnatomyQuestions"));){
            while ((readLine=reader.readLine())!=null){
                String[] strArray=readLine.split(", ");
                anatomyQuestion.add(new QuestionsAndAnswers(strArray[0], strArray[1], strArray[2], strArray[3], strArray[4] ));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public List<QuestionsAndAnswers> getRandomizedList(){
        List<QuestionsAndAnswers> listOfRandomized= anatomyQuestion;
        Collections.shuffle(listOfRandomized);
        return listOfRandomized;
    }

}
