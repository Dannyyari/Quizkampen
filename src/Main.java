import Questions.Geo.DAO_Geografi;
import Questions.Geo.GeoQ;
import Questions.Sport.DAO_Sport;
import Questions.Sport.SportQ;


import java.io.Serializable;
import java.util.List;

public class Main implements Serializable {


    public Main(){
        DAO_Sport SportFrågor = new DAO_Sport();

        //fördel med denna metod så kan vi kontrollera mer vilken fråga det blir
        List<SportQ> sport = SportFrågor.getListOfRandomizedSportsQuestion();
        SportQ valdFråga=sport.get(0);

        String welcome= valdFråga.getWelcomeMessage();
        String fråga= valdFråga.getQuestion();
        String svar=valdFråga.getCorrectanswer();
        String felsvar=valdFråga.getAnswer2();

        System.out.println(welcome+ ".."+ fråga+ " "+ svar+ " ..." + felsvar);
        //fördel med denna metod så kan vi kontrollera mer vilken fråga det blir
    }

    public static void main(String[] args) {
      DAO_Geografi geo=new DAO_Geografi();
      List<GeoQ> geografi=geo.getRandomizedList();
      GeoQ valdFråga=geografi.get(0);
      String frowga=valdFråga.getQuestion();
      String correct=valdFråga.getCorrectAnswer();

        System.out.println(frowga +" " +correct);
    }
}