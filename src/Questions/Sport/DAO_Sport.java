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

    //Metod som randomiserar listan av frågor vi har
    public List<QuestionsAndAnswers> getListOfRandomizedSportsQuestion(){
        List<QuestionsAndAnswers> shuffledQuestions = new ArrayList<>(sportQuestions);
        Collections.shuffle(shuffledQuestions);
        return shuffledQuestions;
    }

    //Tar ut frågan
    public String getSportQuestionInString(List<QuestionsAndAnswers> randomizedList){
         randomizedList = getListOfRandomizedSportsQuestion();
         QuestionsAndAnswers chosenQuestion= randomizedList.get(0);
        return chosenQuestion.getQuestion();
    }



}
