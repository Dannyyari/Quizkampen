import Questions.Sub.DAO.DAO_Geografi;
import Questions.QuestionsAndAnswers;
import Questions.Sub.DAO.DAO_Sport;


import java.io.Serializable;
import java.util.List;

public class Main implements Serializable {


    public Main(){
        //ropar på databasen
       // DAO_Sport SportFrågor = new DAO_Sport();

        //fördel med denna metod så kan vi kontrollera mer vilken fråga det blir
       // List<QuestionsAndAnswers> sport = SportFrågor.getListOfRandomizedSportsQuestion();
        //tar ut en fråga
     //   QuestionsAndAnswers valdFråga=sport.get(0);

//        String fråga= valdFråga.getQuestion();
//        String svar=valdFråga.getCorrectAnswer();
//        String felsvar=valdFråga.getAnswer2();
//
//
//        System.out.println( ".."+ fråga+ " "+ svar+ " ..." + felsvar);
        //fördel med denna metod så kan vi kontrollera mer vilken fråga det blir
    }

    public static void main(String[] args) {
//        GameInterface i = new GameInterface();
    }
}