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
    // Klart.
    ServerSidePlayer playerOneSocket;
    ServerSidePlayer playerTwoSocket;

    // Klart
    private final ObjectOutputStream toPlayerOne;
    private final ObjectOutputStream toPlayerTwo;
    private final ObjectInputStream fromPlayerOne;
    private final ObjectInputStream fromPlayerTwo;

    // Klart
    private final String pathToSport = "src/Questions/textfiles/SportQuestions";
    private final String pathToGeo = "src/Questions/textfiles/GeoQuestions";
    private final String pathToAnatomy = "src/Questions/textfiles/AnatomyQuestions";
    private final String pathToHistory = "src/Questions/textfiles/HistoryQuestions";

    // Klart
    private final DAO sportQuestions = new DAO("Sport", pathToSport);
    private final DAO anatomyQuestions = new DAO("Anatomy", pathToAnatomy);
    private final DAO geoQuestions = new DAO("Geography", pathToGeo);
    private final DAO historyQuestions = new DAO("History", pathToHistory);

    // Klart.
    private boolean playerOneStarts = true;

    // Klart. Variabler för resten av logiken.
    private int playerOneScore = 0;
    private int playerTwoScore = 0;

    private final List<Integer> playerOneRoundScores = new ArrayList<>();
    private final List<Integer> playerTwoRoundScores = new ArrayList<>();


    // Jag är lite osäker här.
    private List<QuestionsAndAnswers> questions;
    private final int currentQuestionIndex = 0;
    static RoundSettings settings = settings = new RoundSettings();

    private final static int totalQuestions = settings.getQuestions();
    private final static int totalRounds = settings.getRounds();

    // Klart. Kopplar två spelare.
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

    // Klart. Kommentar ska läggas in.
    public void run() {
        try {
            while (true) {
                System.out.println("Currently in serverGame-loop!");

                for (int round = 1; round <= totalRounds; round++) {
                    System.out.println("Runda " + round + " börjar nu!");
                    if (playerOneStarts) {
                        handleRound(toPlayerOne, fromPlayerOne, toPlayerTwo, fromPlayerTwo);
                        playerOneStarts = false;
                    } else {
                        handleRound(toPlayerTwo, fromPlayerTwo, toPlayerOne, fromPlayerOne);
                        playerOneStarts = true;
                    }
                    // Skickar resultat efter varje runda.
                    getResult(round);
                }
                // Skickar slutresultatet efter alla rundor.
                sendFinalResults();
                break;
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Klart. Kommentar ska läggas in.
    public List<DAO> getListOfDAOS() {
        List<DAO> listOfDAO = new ArrayList<>();
        listOfDAO.add(sportQuestions);
        listOfDAO.add(anatomyQuestions);
        listOfDAO.add(geoQuestions);
        listOfDAO.add(historyQuestions);
        return listOfDAO;
    }

    // Klart. Skickar List <String> (!)
    public void sendCategoriesToClient(ObjectOutputStream oos, List<DAO> DAOS) throws IOException {
        List<String> categories = new ArrayList<>();
        for (DAO dao : DAOS) {
            categories.add(dao.getCategory());
        }
        oos.writeObject("STATE_CATEGORY");
        oos.writeObject(categories);
        oos.flush();
    }

    public boolean checkCategoryAnswer(String categoryFromUSer) {
        List<String> validCategories = List.of("Sport", "Geography", "Anatomy", "History");
        return validCategories.contains(categoryFromUSer);
    }

    // Förenklad metod inne i handleRound för att ta ut frågor och alla svar i en lista som ska skickas till klienten
    public List<QuestionsAndAnswers> getQuestionsByChosenCategory(String category, List<DAO> daos) throws IOException {
        for (DAO dao : daos) {
            if (dao.getCategory().equalsIgnoreCase(category)) {
                return dao.getQuestionsAndAnswers();
            }
        }
        return null;
    }

    // Klart. Kommentar ska läggas in.
    public void getResult(int currentRound) throws IOException {
        String scoreBoardP1 =
                "Du fick: " + playerOneScore + " poäng. \n" +
                        "Motståndare fick: " + playerTwoScore + " poäng. \n" +
                        "Rond " + currentRound + " av " + totalRounds;

        String scoreBoardP2 =
                "Du fick: " + playerTwoScore + " poäng. \n" +
                        "Motståndare fick: " + playerOneScore + " poäng. \n" +
                        "Rond " + currentRound + " av " + totalRounds;

        toPlayerOne.writeObject("STATE_POINTSOFROUND");
        toPlayerTwo.writeObject("STATE_POINTSOFROUND");

        toPlayerOne.writeObject(scoreBoardP1);
        toPlayerTwo.writeObject(scoreBoardP2);

        toPlayerOne.flush();
        toPlayerTwo.flush();
    }

    // Nästan klart, titta längst ner. Ska lägga kommentar.
    public void handlePlayerAnswers(
            ObjectOutputStream outToPlayer,
            ObjectInputStream inFromPlayer,
            List<QuestionsAndAnswers> questionsForCategory,
            boolean isPlayerOne)
            throws IOException, ClassNotFoundException {

        int correctAnswers = 0;
        for (int i = 0; i < totalQuestions; i++) {
            outToPlayer.writeObject("STATE_QUESTIONS");
            outToPlayer.flush();

            QuestionsAndAnswers question = questionsForCategory.get(i); // Hämta fråga och svar
            outToPlayer.writeObject(question); // Skicka frågan till spelaren
            outToPlayer.flush();

            String playerAnswer = (String) inFromPlayer.readObject(); // Ta emot spelarens svar
            // Validera spelarens svar
            if (question.getCorrectAnswer().equalsIgnoreCase(playerAnswer.trim())) {
                correctAnswers++;

                outToPlayer.writeObject("CORRECT"); // Informera spelaren att svaret var rätt
            } else {
                outToPlayer.writeObject("WRONG"); // Informera spelaren att svaret var fel
            }
            outToPlayer.flush();
        }
        if (isPlayerOne) {
            playerOneScore += correctAnswers;
            playerOneRoundScores.add(correctAnswers); // Lägg till poäng för rundan
        } else {
            playerTwoScore += correctAnswers;
            playerTwoRoundScores.add(correctAnswers); // Lägg till poäng för rundan
        }
        // Skicka resultatet till spelaren
        //ta bort här nere???
        outToPlayer.writeObject("STATE_RESULT");
        outToPlayer.writeObject("Du fick " + correctAnswers + " rätta svar av " + totalQuestions + " antal frågor.");
        outToPlayer.flush();
    }

    // Nästan klart, tror jag.
    public void handleRound(
            ObjectOutputStream chooserOut,
            ObjectInputStream chooserIn,
            ObjectOutputStream otherPlayerOut,
            ObjectInputStream otherPlayerIn)
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
        } else {
            handlePlayerAnswers(chooserOut, chooserIn, questionToSendToClientBasedOnCategory, false);
            // Andra spelaren svarar på samma frågor
            handlePlayerAnswers(otherPlayerOut, otherPlayerIn, questionToSendToClientBasedOnCategory, true);
        }
    }

    // Nästan klart, försöker fixa lite till i metoden kan komma att ändras.
    private void sendFinalResults() throws IOException {
        // Bygg resultatsträngarna för spelare 1 och spelare 2
        StringBuilder resultPlayerOne = new StringBuilder();
        StringBuilder resultPlayerTwo = new StringBuilder();

        resultPlayerOne.append("Spelare 1:\n\n");
        resultPlayerTwo.append("Spelare 2:\n\n");

        for (int round = 0; round < totalRounds; round++) {
            resultPlayerOne.append("Rond ").append(round + 1).append("\n");
            resultPlayerOne.append(playerOneRoundScores.get(round))
                    .append(" poäng av ").append(totalQuestions).append(" poäng.\n\n");

            resultPlayerTwo.append("Rond ").append(round + 1).append("\n");
            resultPlayerTwo.append(playerTwoRoundScores.get(round))
                    .append(" poäng av ").append(totalQuestions).append(" poäng.\n\n");
        }

        // Totalt
        resultPlayerOne.append("Totalt\n")
                .append(playerOneScore).append(" poäng av ").append(totalRounds * totalQuestions).append(" poäng.\n");

        resultPlayerTwo.append("Totalt\n")
                .append(playerTwoScore).append(" poäng av ").append(totalRounds * totalQuestions).append(" poäng.\n");

        // Vinnarmeddelanden
        String winnerMessagePlayerOne;
        String winnerMessagePlayerTwo;

        if (playerOneScore > playerTwoScore) {
            winnerMessagePlayerOne = "Du vann, grattis till vinsten!";
            winnerMessagePlayerTwo = "Du förlorade, men bra spelat!";
        } else if (playerOneScore < playerTwoScore) {
            winnerMessagePlayerOne = "Du förlorade, men bra spelat!";
            winnerMessagePlayerTwo = "Du vann, grattis till vinsten!";
        } else {
            winnerMessagePlayerOne = "Det blev oavgjort! Bra spelat!";
            winnerMessagePlayerTwo = "Det blev oavgjort! Bra spelat!";
        }

        // Skicka vinnarmeddelande och resultat till båda spelarna
        toPlayerOne.writeObject("STATE_FINAL_RESULT");
        toPlayerOne.writeObject(winnerMessagePlayerOne);
        toPlayerOne.writeObject(resultPlayerOne.toString());
        toPlayerOne.writeObject(resultPlayerTwo.toString());
        toPlayerOne.flush();

        toPlayerTwo.writeObject("STATE_FINAL_RESULT");
        toPlayerTwo.writeObject(winnerMessagePlayerTwo);
        toPlayerTwo.writeObject(resultPlayerOne.toString());
        toPlayerTwo.writeObject(resultPlayerTwo.toString());
        toPlayerTwo.flush();
    }
}