package Questions.Sport;

import Questions.QuestionsAndAnswers;

import java.io.*;
import java.util.*;

public class DAO_Sport implements Serializable{
    private List<QuestionsAndAnswers> sportQuestions=new ArrayList<>();

    public DAO_Sport() {
        String readLine;
        try(BufferedReader reader=new BufferedReader(new FileReader("src/Questions/Sport/SportQuestions"));)
        {
           while((readLine=reader.readLine())!=null){
               String [] strArray= readLine.split(", ");
               sportQuestions.add(new QuestionsAndAnswers(strArray[0], strArray[1], strArray[2], strArray[3],strArray[4]));
           }
        }catch (FileNotFoundException e){
           e.printStackTrace();
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

    //Metod som randomiserar listan av frågor vi har
    public List<QuestionsAndAnswers> getListOfRandomizedSportsQuestion(){
        List<QuestionsAndAnswers> shuffledQuestions = new ArrayList<>(sportQuestions);
        Collections.shuffle(shuffledQuestions);
        return shuffledQuestions;
    }


}
