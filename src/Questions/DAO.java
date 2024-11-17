package Questions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DAO implements Serializable {
    List<QuestionsAndAnswers> QnA=new ArrayList<>();

    public DAO(String path){
        try (BufferedReader reader=new BufferedReader(new FileReader(path))){
            String temp;
            while ((temp=reader.readLine())!=null){
                String[] strArray=temp.split(", ");
                QnA.add(new QuestionsAndAnswers(strArray[0], strArray[1], strArray[2], strArray[3], strArray[4] ));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<String> getListOfAnswers(QuestionsAndAnswers question){
        List<String> listOfAnswers= new ArrayList<>();
        listOfAnswers.add(question.getCorrectAnswer());
        listOfAnswers.add(question.getAnswer2());
        listOfAnswers.add(question.getAnswer3());
        listOfAnswers.add(question.getAnswer4());
        return listOfAnswers;
    }
    public List<QuestionsAndAnswers> getListOfRandomizedSportsQuestion(){
        List<QuestionsAndAnswers> shuffledQuestions = new ArrayList<>(QnA);
        Collections.shuffle(shuffledQuestions);
        return shuffledQuestions;
    }
}
