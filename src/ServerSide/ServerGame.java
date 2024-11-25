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

/**
 * ServerGame - Hanterar spelets logik och interaktion mellan två spelare (spelets hjärna).
 * <p>
 * Denna klass ansvarar för att koordinera spelets flöde mellan två anslutna spelare.
 * Den hanterar kommunikation via objektströmmar, skickar och tar emot frågor, samt uppdaterar poäng.
 * <p>
 * Funktioner:
 * - Hanterar spelrundor, turordning och poängberäkning.
 * - Hämtar frågor baserat på kategorier.
 * - Validerar spelarnas svar och tilldelar poäng.
 * - Skickar resultat efter varje runda och vid spelets slut.
 * <p>
 * Klassen använder DAO för att hämta frågor och RoundSettings för spelets inställningar.
 * <p>
 * Klassen skickar STATE till klienten så klienten ska veta vad som ska visas i GUI
 */

public class ServerGame extends Thread implements Serializable {
    ServerSidePlayer playerOneSocket;
    ServerSidePlayer playerTwoSocket;

    //Objectoutput & input för att skicka serialiserade objekt
    private final ObjectOutputStream toPlayerOne;
    private final ObjectOutputStream toPlayerTwo;
    private final ObjectInputStream fromPlayerOne;
    private final ObjectInputStream fromPlayerTwo;

    private final String pathToSport = "src/Questions/textfiles/SportQuestions";
    private final String pathToGeo = "src/Questions/textfiles/GeoQuestions";
    private final String pathToAnatomy = "src/Questions/textfiles/AnatomyQuestions";
    private final String pathToHistory = "src/Questions/textfiles/HistoryQuestions";

    private final DAO sportQuestions = new DAO("Sport", pathToSport);
    private final DAO anatomyQuestions = new DAO("Anatomy", pathToAnatomy);
    private final DAO geoQuestions = new DAO("Geography", pathToGeo);
    private final DAO historyQuestions = new DAO("History", pathToHistory);

    // boolean för att hålla koll på rundor
    private boolean playerOneStarts = true;

    private int playerOneScore = 0;
    private int playerTwoScore = 0;

    private final List<Integer> playerOneRoundScores = new ArrayList<>();
    private final List<Integer> playerTwoRoundScores = new ArrayList<>();

    static RoundSettings settings = settings = new RoundSettings();
    private final static int totalQuestions = settings.getQuestions();
    private final static int totalRounds = settings.getRounds();

    // Kopplar samman två spelare och initierar objektströmmar för kommunikation.
    public ServerGame(ServerSidePlayer PlayerOne, ServerSidePlayer PlayerTwo) throws IOException {
        this.playerOneSocket = PlayerOne;
        this.playerTwoSocket = PlayerTwo;

        //Skickar och tar emot data
        try {
            toPlayerOne = new ObjectOutputStream(playerOneSocket.getSock().getOutputStream());
            toPlayerTwo = new ObjectOutputStream(playerTwoSocket.getSock().getOutputStream());
            fromPlayerOne = new ObjectInputStream(playerOneSocket.getSock().getInputStream());
            fromPlayerTwo = new ObjectInputStream(playerTwoSocket.getSock().getInputStream());
            System.out.println("binding streams done");
        } catch (IOException e) {
            System.err.println("Fel vid initialisering av objektströmmar: " + e.getMessage());
            throw new IOException("Gick ej att skapa strömmarna för spelare", e);
        }
    }

    // Kör alla rundor i spelet och hanterar omgångarna för båda spelarna.
    // Efter alla rundor skickas slutresultatet till spelarna.
    public void run() {
        try {
            while (true) {
                //vi börjar från runda 1 för att det ska vara snyggt vid utskrift
                for (int round = 1; round <= totalRounds; round++) {
                    System.out.println("Runda " + round + " börjar nu!");
                    //Hanterar spelrunda, detta ska köras först då den som ansluter först blir första spelare
                    if (playerOneStarts) {
                        handleRound(toPlayerOne, fromPlayerOne, toPlayerTwo, fromPlayerTwo);
                        playerOneStarts = false;  //gör om till false så vid nästa körning ska spelare 2 få spela
                    } else {
                        //byter plats på vem som börjar
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("ClassNotFoundException under rundhantering: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException under run:" + e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println("Okänt fel: " + e.getMessage());
        }
    }

    // Skapar och returnerar en lista av DAO-objekt som representerar frågekategorier.
    public List<DAO> getListOfDAOS() {
        List<DAO> listOfDAO = new ArrayList<>();
        listOfDAO.add(sportQuestions);
        listOfDAO.add(anatomyQuestions);
        listOfDAO.add(geoQuestions);
        listOfDAO.add(historyQuestions);
        return listOfDAO;
    }

    //Kollar igenom ifall den kategorin användaren matar in finns att välja mellan.
    public boolean checkCategoryAnswer(String categoryFromUSer) {
        List<String> validCategories = List.of("Sport", "Geography", "Anatomy", "History");
        return validCategories.contains(categoryFromUSer);
    }

    //itererar över lista av databaser för att matcha med vilken kategori användaren matar in.
    // Hämtar sedan frågor för den valda kategorin från den motsvarande DAO.
    public List<QuestionsAndAnswers> getQuestionsByChosenCategory(String category, List<DAO> daos) throws IOException {
        for (DAO dao : daos) {
            if (dao.getCategory().equalsIgnoreCase(category)) {
                return dao.getQuestionsAndAnswers();
            }
        }
        return null;
    }

    // Hanterar en runda av spelet genom att skicka kategori och frågor till spelarna samt att räkna deras poäng.
    public void handleRound(
            ObjectOutputStream chooserOut,
            ObjectInputStream chooserIn,
            ObjectOutputStream otherPlayerOut,
            ObjectInputStream otherPlayerIn)
            throws IOException, ClassNotFoundException {

        //skickar <String> av kategorier till klient
        sendCategoriesToClient(chooserOut, getListOfDAOS());
        //strängformat av vad vi får från klienten
        String chosenCategory = (String) chooserIn.readObject();

        //Onödig metod i dagsläget men kan vara bra ifall man vill bygga på programmet
        //Kanske om man vill mata in kategori via text.
        if (!checkCategoryAnswer(chosenCategory)) {
            chooserOut.writeObject("INVALID_CATEGORY");
            chooserOut.flush();
            return;
        }

        //Placerar frågor från vald kategori in till en lista som ska skickas ut till klient.
        List<QuestionsAndAnswers> questionToSendToClientBasedOnCategory =
                getQuestionsByChosenCategory(chosenCategory, getListOfDAOS());

        //Nedan hanteras frågor och dess svar från användaren. All validering av svar samt poänghantering sker på serversida
        if (playerOneStarts) {
            // Spelaren som valde svarar först
            handlePlayerAnswers(chooserOut, chooserIn, questionToSendToClientBasedOnCategory, true);
            // Andra spelaren svarar på samma frågor
            handlePlayerAnswers(otherPlayerOut, otherPlayerIn, questionToSendToClientBasedOnCategory, false);
        } else {
            //Denna boolean är för att kunna flippa spelet så poäng lagras på korrekt spelare
            handlePlayerAnswers(chooserOut, chooserIn, questionToSendToClientBasedOnCategory, false);
            // Andra spelaren svarar på samma frågor
            handlePlayerAnswers(otherPlayerOut, otherPlayerIn, questionToSendToClientBasedOnCategory, true);
        }
    }

    // Skickar en lista <String> med tillgängliga kategorier till spelaren.
    public void sendCategoriesToClient(ObjectOutputStream oos, List<DAO> DAOS) throws IOException {
        List<String> categories = new ArrayList<>();
        for (DAO dao : DAOS) {
            categories.add(dao.getCategory());
        }
        oos.writeObject("STATE_CATEGORY");
        oos.writeObject(categories);
        oos.flush();
    }

    // Hanterar spelarens svar på varje fråga och uppdaterar deras poäng baserat på svaren.
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
        outToPlayer.writeObject("STATE_RESULT");
        outToPlayer.writeObject("Du fick " + correctAnswers + " rätta svar av " + totalQuestions + " antal frågor.");
        outToPlayer.flush();
    }

    // Skickar poängresultaten från den aktuella omgången till båda spelarna.
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

    // Skickar slutgiltiga resultatet och vinnarmeddelanden till båda spelarna.
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