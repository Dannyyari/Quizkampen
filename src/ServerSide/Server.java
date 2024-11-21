package ServerSide;

import Questions.DAO;
import Questions.QuestionsAndAnswers;
import Properties.RoundSettings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread implements Serializable{
    ServerSidePlayer playerOneSocket;
    ServerSidePlayer playerTwoSocket;

    private ObjectOutputStream toPlayerOne;
    private ObjectOutputStream toPlayerTwo;
    private ObjectInputStream fromPlayerOne;
    private ObjectInputStream fromPlayerTwo;

    private List<QuestionsAndAnswers> questions;
    private int currentQuestionIndex=0;
    private String pathToSport = "src/Questions/textfiles/SportQuestions";
    private String pathToGeo = "src/Questions/textfiles/GeoQuestions";
    private String pathToAnatomy = "src/Questions/textfiles/AnatomyQuestions";
    private String pathToHistory= "src/Questions/textfiles/HistoryQuestions";

    private DAO sportQuestions= new DAO("Sport", pathToSport);
    private DAO anatomyQuestions= new DAO("Anatomy", pathToAnatomy);
    private DAO geoQuestions=new DAO("Geo", pathToGeo);
    private DAO historyQuestions= new DAO("History", pathToHistory);


    static RoundSettings settings = settings = new RoundSettings();

    //variabler för resten av logiken
    private int playerOneScore;
    private int playerTwoScore;
    private final static int totalQuestions = settings.getQuestions();
    private final static int totalRounds = settings.getRounds();

    //kopplar två spelare
    public Server(ServerSidePlayer PlayerOne, ServerSidePlayer PlayerTwo) throws IOException {
        this.playerOneSocket = PlayerOne;
        this.playerTwoSocket = PlayerTwo;

            try {
                toPlayerOne = new ObjectOutputStream(playerOneSocket.getSock().getOutputStream());
                toPlayerTwo = new ObjectOutputStream(playerTwoSocket.getSock().getOutputStream());
                fromPlayerOne = new ObjectInputStream(playerOneSocket.getSock().getInputStream());
                fromPlayerTwo = new ObjectInputStream(playerTwoSocket.getSock().getInputStream());
                System.out.println("binding streams done");

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
    }


    //Frågan är ifall detta är allt som behövs?
    public void run() {
        boolean playerOneStarts= true;
        //här ska då metoder som vi skickar och hämtar från användaren. programmets "hjärna"
        try {
            while (true) {
                System.out.println("in server loop");
                for (int round = 1; round <= totalRounds ; round++) {
                    System.out.println("Runda " + round + " börjar nu!");
                    if (playerOneStarts){
                        handleRound(toPlayerOne, fromPlayerOne, toPlayerTwo,fromPlayerTwo);
                        getResault(toPlayerOne, toPlayerTwo);
                        playerOneStarts=false;
                    } else {
                    handleRound(toPlayerTwo, fromPlayerTwo, toPlayerOne, fromPlayerOne);
                        getResault(toPlayerTwo, toPlayerOne);
                        playerOneStarts=true;

                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<DAO> getListOfDAOS(){
        List<DAO> listOfDAO=new ArrayList<>();
        listOfDAO.add(sportQuestions);
        listOfDAO.add(anatomyQuestions);
        listOfDAO.add(geoQuestions);
        listOfDAO.add(historyQuestions);
        return listOfDAO;
    }

    //Skickar LIST <STRING>!!!!!
    public void sendCategoriesToClient(ObjectOutputStream oos, List<DAO> DAOS) throws IOException {
        List<String> categories = new ArrayList<>();
        for (DAO dao : DAOS) {
            categories.add(dao.getCategory());
        }

        oos.writeObject("CATEGORY");
        oos.writeObject(categories);
        oos.flush();
    }

    public void getResault(ObjectOutputStream oos1, ObjectOutputStream oos2) throws IOException {
        oos1.writeObject("POINTSOFROUND");
        //gör en totalint för att skicka samma till båda
        oos1.writeObject(playerOneScore);
        oos2.writeObject(playerTwoScore);
    }

    public void handlePlayerAnswers(ObjectOutputStream outToPlayer, ObjectInputStream inFromPlayer,
                                    List<QuestionsAndAnswers> questionsForCategory, int playerscore)
            throws IOException, ClassNotFoundException {

        outToPlayer.writeObject("QUESTIONS");
        outToPlayer.flush();


       int correctAnswers = playerscore;

        for (int i = 0; i < totalQuestions; i++) {
            QuestionsAndAnswers question = questionsForCategory.get(i); // Hämta fråga och svar
            outToPlayer.writeObject(question.getQuestion()); // Skicka frågan till spelaren
            outToPlayer.flush();

            String playerAnswer = (String) inFromPlayer.readObject(); // Ta emot spelarens svar

            // Validera spelarens svar
            if (question.getCorrectAnswer().equalsIgnoreCase(playerAnswer.trim())) {
                correctAnswers++;
                //kanske ha en checkanswer i varje metod i GUI där vi har en sträng som inparameter, om CORRECT så ska
                //knappen bli grön?
                outToPlayer.writeObject("CORRECT"); // Informera spelaren att svaret var rätt
            } else {
                outToPlayer.writeObject("WRONG"); // Informera spelaren att svaret var fel
            }
            outToPlayer.flush();
        }

        // Skicka resultatet till spelaren
        //ta bort här nere???
        outToPlayer.writeObject("RESULT");
        outToPlayer.writeObject("You got " + correctAnswers + " correct answers out of " + totalQuestions);
        outToPlayer.flush();
    }

    public void handleRound(ObjectOutputStream chooserOut, ObjectInputStream chooserIn,
                             ObjectOutputStream otherPlayerOut, ObjectInputStream otherPlayerIn)
            throws IOException, ClassNotFoundException {

        // Spelare som väljer kategori
        sendCategoriesToClient(chooserOut, getListOfDAOS());
        String chosenCategory = (String) chooserIn.readObject();

        if (!checkCategoryAnswer(chosenCategory)) {
            chooserOut.writeObject("INVALID_CATEGORY");
            chooserOut.flush();
            return;
        }

        //Placerar frågor från vald kategori in till en lista som skickas ut till klient.
        List<QuestionsAndAnswers> questionToSendToClientBasedOnCategory=
                getQuestionsByChosenCategory(chosenCategory, getListOfDAOS());

        //göra om detta så att raderna under skiftar, vi vill inte alltid lagra playerOneScore i den första.

        // Spelaren som valde svarar först
        handlePlayerAnswers(chooserOut, chooserIn, questionToSendToClientBasedOnCategory, playerOneScore);

        // Andra spelaren svarar på samma frågor
        handlePlayerAnswers(otherPlayerOut, otherPlayerIn, questionToSendToClientBasedOnCategory, playerTwoScore);

        //KANSKE
        //SCOREBOARD
        //ha en metod som skickar svar till både (chooserOutoch, otherPlayerOut)
        //har samlat poäng för aktuell runda.
        //Detta blir då total poäng.
        //Spelarens egna poäng som personen alltid ska kunna se finns i klient

    }
    public boolean checkCategoryAnswer(String categoryFromUSer){
        List<String> validCategories= List.of("Sport", "Geo", "Anatomy", "History");
        return validCategories.contains(categoryFromUSer);
    }

    //förenklad metod inne i handleRound för att ta ut frågor och alla svar i en lista som ska skickas till klienten
    public List<QuestionsAndAnswers> getQuestionsByChosenCategory(String catagory, List<DAO> daos){
        for (DAO dao : daos) {
            if (dao.getCategory().equalsIgnoreCase(catagory)) {
                return dao.getQuestionsAndAnswers();
            }

        }
        return null;

    }
//    //vi måste skicka STRING från användaren för att veta vilka frågor vi ska ge
//    public void sendQuestionsToClient(ObjectOutputStream oos, String categoryNameinputFromUser, List<DAO> DAOS) throws IOException {
//        for (DAO dao : DAOS) {
//            if (dao.getCategory().equals(categoryNameinputFromUser)) {
//                oos.writeObject("QUESTIONS");
//                oos.writeObject(dao.getQuestionsAndAnswers()); // Serialiserar och skickar frågorna
//                oos.flush();
//                return;
//            }
//        }
//        throw new IllegalArgumentException("Kategori ej hittad: " + categoryNameinputFromUser);
//    }
}


