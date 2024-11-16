import Questions.Geo.DAO_Geografi;
import Questions.QuestionsAndAnswers;
import Questions.Sport.DAO_Sport;


import java.io.Serializable;
import java.util.List;

public class Main implements Serializable {


    public Main(){
        //ropar på databasen
        DAO_Sport SportFrågor = new DAO_Sport();

        //fördel med denna metod så kan vi kontrollera mer vilken fråga det blir
        List<QuestionsAndAnswers> sport = SportFrågor.getListOfRandomizedSportsQuestion();
        //tar ut en fråga
        QuestionsAndAnswers valdFråga=sport.get(0);

        String fråga= valdFråga.getQuestion();
        String svar=valdFråga.getCorrectAnswer();
        String felsvar=valdFråga.getAnswer2();


        System.out.println( ".."+ fråga+ " "+ svar+ " ..." + felsvar);
        //fördel med denna metod så kan vi kontrollera mer vilken fråga det blir
    }

    public static void main(String[] args) {

        //ropar på databas
      DAO_Geografi geo=new DAO_Geografi();

      //tar ut fråga + svar
      List<QuestionsAndAnswers> geografi=geo.getRandomizedList();
      //tar ut en av 2 frågor
      QuestionsAndAnswers valdFråga=geografi.get(0);
      //deklarerar frågan till sträng så vi kan ta ut och då ha detta i textfield.setText
      String frowga=valdFråga.getQuestion();
      String correct=valdFråga.getCorrectAnswer();

        System.out.println(frowga +" " +correct);
    }
}