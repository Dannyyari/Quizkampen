package ServerSide;

import Properties.RoundSettings;
import Questions.DAO;
import Questions.QuestionsAndAnswers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServerGame extends Thread implements Serializable {
    ServerSidePlayer playerOneSocket;
    ServerSidePlayer playerTwoSocket;

    private final ObjectOutputStream toPlayerOne;
    private final ObjectOutputStream toPlayerTwo;
    private final ObjectInputStream fromPlayerOne;
    private final ObjectInputStream fromPlayerTwo;

    private List<QuestionsAndAnswers> questions;
    private final int currentQuestionIndex = 0;
    private final String pathToSport = "src/Questions/textfiles/SportQuestions";
    private final String pathToGeo = "src/Questions/textfiles/GeoQuestions";
    private final String pathToAnatomy = "src/Questions/textfiles/AnatomyQuestions";
    private final String pathToHistory = "src/Questions/textfiles/HistoryQuestions";

    private final DAO sportQuestions = new DAO("Sport", pathToSport);
    private final DAO anatomyQuestions = new DAO("Anatomy", pathToAnatomy);
    private final DAO geoQuestions = new DAO("Geo", pathToGeo);
    private final DAO historyQuestions = new DAO("History", pathToHistory);

    private boolean playerOneStarts = true;
    static RoundSettings settings = settings = new RoundSettings();

    //variabler för resten av logiken
    private int playerOneScore=0;
    private int playerOneScoreR2=0;
    private int playerTwoScore=0;
    private int playerTwoScoreR2=0;
    private final static int totalQuestions = settings.getQuestions();
    private final static int totalRounds = settings.getRounds();

    //kopplar två spelare
    public ServerGame( ServerSidePlayer PlayerOne, ServerSidePlayer PlayerTwo) throws IOException {

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

        //här ska då metoder som vi skickar och hämtar från användaren. programmets "hjärna"
        try {
            while (true) {
                System.out.println("in serverGame loop");
                for (int round = 1; round <= totalRounds; round++) {
                    System.out.println("Runda " + round + " börjar nu!");
                    if (playerOneStarts) {
                        handleRound(toPlayerOne, fromPlayerOne, toPlayerTwo, fromPlayerTwo);
                        //skapa metod som skickar totala resultat för båda spelare
                        playerOneStarts = false;
                    } else {
                        handleRound(toPlayerTwo, fromPlayerTwo, toPlayerOne, fromPlayerOne);
                        playerOneStarts = true;
                    }
                    getResault(round);
                }
                break;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DAO> getListOfDAOS() {
        List<DAO> listOfDAO = new ArrayList<>();
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

        oos.writeObject("STATE_CATEGORY");
        oos.writeObject(categories);
        oos.flush();
    }

    public void getResault(int currentRound) throws IOException {
        String scoreBoardP1 = "Du fick: " + playerOneScore + " poäng, din motståndare fick: " + playerTwoScore + " poäng " +
                "på rond " + currentRound + " av " + totalRounds;

        String scoreBoardP2 = "Du fick: " + playerTwoScore + " din motståndare fick: " + playerOneScore + " poäng "+
                "på rond " + currentRound + " av " + totalRounds;

        toPlayerOne.writeObject("STATE_POINTSOFROUND");
        toPlayerTwo.writeObject("STATE_POINTSOFROUND");
        //gör en totalint för att skicka samma till båda
        toPlayerOne.writeObject(scoreBoardP1);
        toPlayerTwo.writeObject(scoreBoardP2);

        toPlayerOne.flush();
        toPlayerTwo.flush();
    }

    public void handlePlayerAnswers(ObjectOutputStream outToPlayer, ObjectInputStream inFromPlayer,
                                    List<QuestionsAndAnswers> questionsForCategory, boolean isPlayerOne) throws IOException, ClassNotFoundException {
        int correctAnswers = 0;

        for (int i = 0; i < totalQuestions; i++) {
            outToPlayer.writeObject("STATE_QUESTIONS");
            outToPlayer.flush();

            QuestionsAndAnswers question = questionsForCategory.get(i); // Hämta fråga och svar
            outToPlayer.writeObject(question);// Skicka frågan till spelaren
            outToPlayer.flush();

            String playerAnswer = (String) inFromPlayer.readObject(); // Ta emot spelarens svar
            // Validera spelarens svar
            if (question.getCorrectAnswer().equalsIgnoreCase(playerAnswer.trim())) {
                correctAnswers++;
                //kanske ha en checkanswer i varje metod i GUI där vi har en sträng som inparameter, om CORRECT så ska
                //knappen bli grön?
                outToPlayer.writeObject("CORRECT");// Informera spelaren att svaret var rätt
            } else {
                outToPlayer.writeObject("WRONG"); // Informera spelaren att svaret var fel
            }
            outToPlayer.flush();
        }

        if (isPlayerOne) {
            playerOneScore += correctAnswers;
        } else {
            playerTwoScore += correctAnswers;
        }

        // Skicka resultatet till spelaren
        //ta bort här nere???
        outToPlayer.writeObject("STATE_RESULT");
        outToPlayer.writeObject("You got " + correctAnswers + " correct answers out of " + totalQuestions + " questions");
        outToPlayer.flush();
    }

    public void handleRound(ObjectOutputStream chooserOut, ObjectInputStream chooserIn,
                            ObjectOutputStream otherPlayerOut, ObjectInputStream otherPlayerIn)
            throws IOException, ClassNotFoundException {

        // Spelare som väljer kategori
        sendCategoriesToClient(chooserOut, getListOfDAOS());
        String chosenCategory = (String) chooserIn.readObject();
        System.out.println(chosenCategory);

        if (!checkCategoryAnswer(chosenCategory)) {
            chooserOut.writeObject("INVALID_CATEGORY");
            chooserOut.flush();
            return;
        }
        System.out.println("innan vi skickar frågor");
        //Placerar frågor från vald kategori in till en lista som skickas ut till klient.
        List<QuestionsAndAnswers> questionToSendToClientBasedOnCategory =
                getQuestionsByChosenCategory(chosenCategory, getListOfDAOS());

        //göra om detta så att raderna under skiftar, vi vill inte alltid lagra playerOneScore i den första.
        System.out.println("innan vi väljer vem som spelar");
        if (playerOneStarts) {
            System.out.println("Spelare 1 startar");
            // Spelaren som valde svarar först
            handlePlayerAnswers(chooserOut, chooserIn, questionToSendToClientBasedOnCategory, true);
            // Andra spelaren svarar på samma frågor
            handlePlayerAnswers(otherPlayerOut, otherPlayerIn, questionToSendToClientBasedOnCategory, false);
        }else {
            handlePlayerAnswers(chooserOut, chooserIn, questionToSendToClientBasedOnCategory, false);
            // Andra spelaren svarar på samma frågor
            handlePlayerAnswers(otherPlayerOut, otherPlayerIn, questionToSendToClientBasedOnCategory, true);
        }
    }
    public boolean checkCategoryAnswer(String categoryFromUSer) {
        List<String> validCategories = List.of("Sport", "Geo", "Anatomy", "History");
        return validCategories.contains(categoryFromUSer);
    }

    //förenklad metod inne i handleRound för att ta ut frågor och alla svar i en lista som ska skickas till klienten
    public List<QuestionsAndAnswers> getQuestionsByChosenCategory(String catagory, List<DAO> daos) throws IOException {
        for (DAO dao : daos) {
            if (dao.getCategory().equalsIgnoreCase(catagory)) {
                return dao.getQuestionsAndAnswers();
            }

        }
        return null;

    }


    //KANSKE
    //SCOREBOARD
    //ha en metod som skickar svar till både (chooserOutoch, otherPlayerOut)
    //har samlat poäng för aktuell runda.
    //Detta blir då total poäng.
    //Spelarens egna poäng som personen alltid ska kunna se finns i klient

}