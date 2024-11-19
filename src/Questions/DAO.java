package Questions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DAO implements Serializable {
    List<QuestionsAndAnswers> QnA=new ArrayList<>();
    private int currentIndex=0;
    private String path;

    public DAO(String path){
        this.path=path;
        loader();
    }

    private void loader(){
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


    //Enbart denna fil då vi vill att Server skickar List of Question and Answer
    public List<QuestionsAndAnswers> getInstaceOfQuestionsAndAnswersSPORT(DAO dao){
        dao= new DAO("src/Questions/textfiles/SportQuestions");
        return QnA;
    }
}
