package Questions.Sport;

import java.io.*;
import java.util.*;

public class DAO_Sport implements Serializable{
    private List<SportQ> sportQuestions=new ArrayList<>();

    public DAO_Sport() {
        String readLine;
        try(BufferedReader reader=new BufferedReader(new FileReader("src/Questions/Sport/SportQuestions"));)
        {
           while((readLine=reader.readLine())!=null){
               String [] strArray= readLine.split(", ");
               sportQuestions.add(new SportQ(strArray[0], strArray[1], strArray[2], strArray[3],strArray[4]));
           }
        }catch (FileNotFoundException e){
           e.printStackTrace();
        }catch (IOException e){
           e.printStackTrace();
        }
    }

    //Metod som randomiserar listan av frågor vi har
    public List<SportQ> getListOfRandomizedSportsQuestion(){
        List<SportQ> shuffledQuestions = new ArrayList<>(sportQuestions);
        Collections.shuffle(shuffledQuestions);
        return shuffledQuestions;
    }

    //Tar ut frågan
    public String getSportQuestionInString(List<SportQ> randomizedList){
         randomizedList = getListOfRandomizedSportsQuestion();
         SportQ chosenQuestion= randomizedList.get(0);
        return chosenQuestion.getQuestion();
    }

    public List<String> getListOfAnswers(){
        List <SportQ> randomizedlist=getListOfRandomizedSportsQuestion();
        SportQ chosenQuestion = randomizedlist.get(0);
        return chosenQuestion.getAllAnswersInListFormat();
    }
//sorterad lista, behövs nog ej
//    public List <SportQ> getSportQuestions(){
//        return sportQuestions;
//    }


}
