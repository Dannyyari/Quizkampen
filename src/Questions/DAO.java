package Questions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DAO implements Serializable {
    List<QuestionsAndAnswers> QuestionsAndAnswers = new ArrayList<>();
    private String path;
    private String category;


    public DAO(String category, String path){
        this.category = category;
        this.path = path;
        loader();
    }

    private void loader(){
        try (BufferedReader reader = new BufferedReader(new FileReader(path))){
            String temp;
            while ((temp = reader.readLine()) !=null){
                String[] strArray = temp.split(", ");
                QuestionsAndAnswers.add(new QuestionsAndAnswers(strArray[0], strArray[1], strArray[2], strArray[3], strArray[4]));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //Så vi kan skicka kategori namn till klient
    public String getCategory() {
        return category;
    }

    public List<QuestionsAndAnswers> getQuestionsAndAnswers() {
        return QuestionsAndAnswers;
    }
}
