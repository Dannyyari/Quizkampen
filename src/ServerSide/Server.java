package ServerSide;

import Questions.DAO;
import Questions.QuestionsAndAnswers;
import Properties.RoundSettings;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server extends Thread {
    private final ServerSidePlayer playerOne;
    private final ServerSidePlayer playerTwo;

    private final ObjectOutputStream toPlayerOne;
    private final ObjectOutputStream toPlayerTwo;
    private final ObjectInputStream fromPlayerOne;
    private final ObjectInputStream fromPlayerTwo;

    private final List<DAO> daos;
    private final RoundSettings roundSettings;

    private final Map<String, Integer> playerOneScores = new HashMap<>();
    private final Map<String, Integer> playerTwoScores = new HashMap<>();

    public Server(ServerSidePlayer playerOne, ServerSidePlayer playerTwo) throws IOException {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;

        toPlayerOne = new ObjectOutputStream(playerOne.getSock().getOutputStream());
        fromPlayerOne = new ObjectInputStream(playerOne.getSock().getInputStream());

        toPlayerTwo = new ObjectOutputStream(playerTwo.getSock().getOutputStream());
        fromPlayerTwo = new ObjectInputStream(playerTwo.getSock().getInputStream());

        daos = List.of(
                new DAO("Sport", "src/Questions/textfiles/SportQuestions"),
                new DAO("Geo", "src/Questions/textfiles/GeoQuestions"),
                new DAO("Anatomy", "src/Questions/textfiles/AnatomyQuestions"),
                new DAO("History", "src/Questions/textfiles/HistoryQuestions")
        );

        roundSettings = new RoundSettings();
        System.out.println("Inställningar laddade: " + roundSettings.getRounds() + " ronder, " + roundSettings.getQuestions() + " frågor per runda.");
    }

    @Override
    public void run() {
        try {
            for (int round = 1; round <= roundSettings.getRounds(); round++) {
                System.out.println("Startar runda " + round);

                // Spelare 1 väljer kategori och båda svarar
                System.out.println("Spelare 1 väljer kategori...");
                handleRound(playerOne, toPlayerOne, fromPlayerOne, playerTwo, toPlayerTwo, playerOneScores, playerTwoScores);

                // Spelare 2 väljer kategori och båda svarar
                System.out.println("Spelare 2 väljer kategori...");
                handleRound(playerTwo, toPlayerTwo, fromPlayerTwo, playerOne, toPlayerOne, playerTwoScores, playerOneScores);
            }

            // Skicka slutresultaten när alla ronder är klara
            sendFinalResults();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ett fel uppstod i servern: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleRound(ServerSidePlayer activePlayer, ObjectOutputStream toActive, ObjectInputStream fromActive,
                             ServerSidePlayer waitingPlayer, ObjectOutputStream toWaiting,
                             Map<String, Integer> activePlayerScores, Map<String, Integer> waitingPlayerScores)
            throws IOException, ClassNotFoundException {
        // Skicka kategorier till aktiv spelare
        System.out.println("Skickar kategorier till " + activePlayer.getName());
        sendCategories(toActive);

        // Vänta på val av kategori
        String selectedCategory = (String) fromActive.readObject();
        System.out.println(activePlayer.getName() + " valde kategori: " + selectedCategory);

        // Informera väntande spelare om vald kategori
        toWaiting.writeObject("INFO");
        toWaiting.writeObject(activePlayer.getName() + " valde: " + selectedCategory);
        toWaiting.flush();

        // Hämta frågor från vald kategori
        List<QuestionsAndAnswers> questions = getQuestionsByCategory(selectedCategory);
        List<QuestionsAndAnswers> limitedQuestions = new ArrayList<>(questions.subList(0, Math.min(questions.size(), roundSettings.getQuestions())));

        // Spela rundan för aktiv spelare
        System.out.println(activePlayer.getName() + " börjar svara...");
        int activeScore = playRound(limitedQuestions, toActive, fromActive);

        // Spela rundan för väntande spelare
        System.out.println(waitingPlayer.getName() + " börjar svara...");
        int waitingScore = playRound(limitedQuestions, toWaiting, fromWaitingPlayer(waitingPlayer));

        // Uppdatera poäng
        activePlayerScores.put(selectedCategory, activeScore);
        waitingPlayerScores.put(selectedCategory, waitingScore);

        // Informera loggen
        System.out.println("Runda slutförd för kategorin: " + selectedCategory);
    }

    private void sendCategories(ObjectOutputStream out) throws IOException {
        List<String> categories = new ArrayList<>();
        for (DAO dao : daos) {
            categories.add(dao.getCategory());
        }
        System.out.println("Skickar kategorier: " + categories);
        out.writeObject("CATEGORY");
        out.writeObject(categories);
        out.flush();
    }

    private List<QuestionsAndAnswers> getQuestionsByCategory(String category) {
        return daos.stream()
                .filter(dao -> dao.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .map(DAO::getQuestionsAndAnswers)
                .orElse(new ArrayList<>());
    }

    private int playRound(List<QuestionsAndAnswers> questions, ObjectOutputStream out, ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        out.writeObject("QUESTIONS");
        out.writeObject(questions);
        out.flush();

        int score = 0;
        for (QuestionsAndAnswers question : questions) {
            out.writeObject("QUESTION");
            out.writeObject(question.getQuestion());
            out.writeObject(new String[]{
                    question.getCorrectAnswer(),
                    question.getAnswer2(),
                    question.getAnswer3(),
                    question.getAnswer4()
            });
            out.flush();

            String playerAnswer = (String) in.readObject();
            if (playerAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                score++;
                out.writeObject("CORRECT");
            } else {
                out.writeObject("WRONG");
            }
            out.flush();
        }
        return score;
    }

    private void sendFinalResults() throws IOException {
        String playerOneResult = buildResultMessage(playerOneScores);
        String playerTwoResult = buildResultMessage(playerTwoScores);

        toPlayerOne.writeObject("FINAL_RESULT");
        toPlayerOne.writeObject(playerOneResult);
        toPlayerOne.flush();

        toPlayerTwo.writeObject("FINAL_RESULT");
        toPlayerTwo.writeObject(playerTwoResult);
        toPlayerTwo.flush();
    }

    private String buildResultMessage(Map<String, Integer> scores) {
        StringBuilder result = new StringBuilder("Slutresultat:\n");
        scores.forEach((category, score) -> result.append(category).append(": ").append(score).append(" poäng\n"));
        return result.toString();
    }

    private ObjectInputStream fromWaitingPlayer(ServerSidePlayer player) {
        return player == playerOne ? fromPlayerTwo : fromPlayerOne;
    }
}
