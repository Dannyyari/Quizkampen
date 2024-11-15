package Sport;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DAO_Sport implements Serializable{
    List<List<SportQ>> sportQuestions=new ArrayList<>();
    List<SportQ> q1=new ArrayList<>();
    List<SportQ> q2=new ArrayList<>();





    public DAO_Sport() {
        String readLine;
       try(BufferedReader reader=new BufferedReader(new FileReader("src/Sport/SportQuestions"));)
       {
           while((readLine=reader.readLine())!=null){
               String [] strArray= readLine.split(", ");
               q1.add(new SportQ(strArray[0], strArray[1], strArray[2], strArray[3],strArray[4]));
               q2.add(new SportQ(strArray[0], strArray[1], strArray[2], strArray[3],strArray[4]));
           }
           sportQuestions.add(q1);
           sportQuestions.add(q2);


       }catch (FileNotFoundException e){
           e.printStackTrace();
       }catch (IOException e){
           e.printStackTrace();
       }
    }
    public List<SportQ> getListOfRandomizedSportsQuestion(){
        Random random =new Random();
        List<SportQ> randomizedList = sportQuestions.get(random.nextInt(sportQuestions.size()));
        Collections.shuffle(randomizedList);
        return randomizedList;
    }

    public List <List<SportQ>> getSportQuestionsNotRandomized(){
        return sportQuestions;
    }
    public String correctAnswer(){
        String answer=
    }

}
