package Questions.Anatomi;

import Questions.Geo.GeoQ;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DAO_Anatomy implements Serializable {
    List<AnatomyQ> anatomyQuestion=new ArrayList<>();

    public DAO_Anatomy(){
        String readLine;
        try(BufferedReader reader=new BufferedReader(new FileReader("src/Questions/Anatomi/AnatomyQuestions"));){
            while ((readLine=reader.readLine())!=null){
                String[] strArray=readLine.split(", ");
                anatomyQuestion.add(new AnatomyQ(strArray[0], strArray[1], strArray[2], strArray[3], strArray[4] ));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public List<AnatomyQ> getRandomizedList(){
        List<AnatomyQ> listOfRandomized= anatomyQuestion;
        Collections.shuffle(listOfRandomized);
        return listOfRandomized;
    }

}
