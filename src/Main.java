import Sport.DAO_Sport;
import Sport.SportQ;

import java.io.Serializable;
import java.util.List;

public class Main implements Serializable {
    public static void main(String[] args) {
        //första push
        //Up på github
        //development branch inlagd

        DAO_Sport d=new DAO_Sport();

        List<SportQ> d2 = d.getListOfRandomizedSportsQuestion();

        System.out.println(d.getListOfRandomizedSportsQuestion());
        List <List<SportQ>> d3=d.getSportQuestionsNotRandomized();
        System.out.println;

    }
}