package ServerSide;

import Questions.DAO;
import Questions.QuestionsAndAnswers;
import Properties.RoundSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;

public class Server extends Thread {
    ServerSidePlayer playerOneSocket;
    ServerSidePlayer playerTwoSocket;


    private List<QuestionsAndAnswers> questions;
    private int currentQuestionIndex; // FRÅGA! Ska inte index börja från 0 för att veta var man är?
    private String pathToSport = "src/Questions/textfiles/SportQuestions";
    private String pathToGeo = "src/Questions/textfiles/GeoQuestions";
    private String pathToAnatomy = "src/Questions/textfiles/AnatomyQuestions";

    private DAO sportQuestions= new DAO("Sport", pathToSport);
    private DAO anatomyQuestions= new DAO("Anatomy", pathToAnatomy);
    private DAO geoQuestions=new DAO("Geography", pathToGeo);


    private ObjectOutputStream toPlayerOne;
    private ObjectOutputStream toPlayerTwo;
    private ObjectInputStream fromPlayerOne;
    private ObjectInputStream fromPlayerTwo;

    private static DAO database;


    static RoundSettings settings = settings = new RoundSettings();

    //variabler för resten av logiken
    private int playerOneScore;
    private int playerTwoScore;
    private static int totalQuestions = settings.getQuestions();
    private static int totalRounds = settings.getRounds();

    // dessa två måste någ justeras då vi kommer skicka Seraliserade objekt?
    // Titta igenom denna fråga och se ifall vi behöver ändra

    // try(PrintWriter toUser= new PrintWriter(socket.getOutputStream(), true);
    // BufferedReader fromUser=new BufferedReader(new InputStreamReader(socket.getInputStream()));


    //kopplar två spelare
    public Server(ServerSidePlayer PlayerOne, ServerSidePlayer PlayerTwo) throws IOException {
        this.playerOneSocket = PlayerOne;
        this.playerTwoSocket = PlayerTwo;

            try {
                toPlayerOne = new ObjectOutputStream(playerOneSocket.getSock().getOutputStream());
                toPlayerTwo = new ObjectOutputStream(playerTwoSocket.getSock().getOutputStream());
                fromPlayerOne = new ObjectInputStream(playerOneSocket.getSock().getInputStream());
                fromPlayerTwo = new ObjectInputStream(playerTwoSocket.getSock().getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
    }

    public void run(){
        //här ska då metoder som vi skickar och hämtar från användaren. programmets "hjärna"
        try {
            toPlayerOne.writeObject("Hej spelare 1");
            toPlayerTwo.writeObject("Hej spelare 2");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel categoryLabel = new JLabel("Välj Kategori", SwingConstants.CENTER);
        panel.add(categoryLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton sportButton = new JButton("Sport");
        JButton geoButton = new JButton("Geografi");
        JButton anatomyButton = new JButton("Anatomy");


        ActionListener categoryButtonListener = e -> {
            String chosenCategory = "";
            if (e.getSource() == sportButton) {
                chosenCategory = "Sport";
            } else if (e.getSource() == geoButton) {
                chosenCategory = "Geografi";
            } else if (e.getSource() == anatomyButton) {
                chosenCategory = "Anatomy";
            }


        };

        //oklart hur allt detta ska bort
        sportButton.addActionListener(categoryButtonListener);
        geoButton.addActionListener(categoryButtonListener);
        anatomyButton.addActionListener(categoryButtonListener);

        buttonPanel.add(sportButton);
        buttonPanel.add(geoButton);
        buttonPanel.add(anatomyButton);

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }


    //BORT men kanske behålla switch case?
//    public static void setDatabase(String category) {
//        String pathToSport = "src/Questions/textfiles/SportQuestions";
//        String pathToGeo = "src/Questions/textfiles/GeoQuestions";
//        String pathToAnatomy = "src/Questions/textfiles/AnatomyQuestions";
//        switch (category) {
//            case "Sport":
//
//                break;
//            case "Geografi":
//                database = new DAO(pathToGeo);
//                break;
//            case "Anatomy":
//                database = new DAO(pathToAnatomy);
//                break;
//            default:
//                System.out.println("Ogiltig kategori");
//                return;
//        }
//    }


    //insprererad av chaGPT, väldigt oklart om detta är OK sätt att skicka?
    //kommer behövas ändras
    private void sendQuestionToPlayers() throws IOException {
        if (currentQuestionIndex < questions.size()) {
            QuestionsAndAnswers question = questions.get(currentQuestionIndex);

            toPlayerOne.writeObject(question);
            toPlayerTwo.writeObject(question);
        }
    }

    // Ingen aning om vad "ClassNotFoundException" är men den kom till när jag skrev ".readObject."
    private void receiveAndCheckAnswersFromPlayers() throws IOException, ClassNotFoundException {
        String playerOneAnswer = (String) fromPlayerOne.readObject();
        String playerTwoAnswer = (String) fromPlayerTwo.readObject();

        QuestionsAndAnswers question = questions.get(currentQuestionIndex);

        // Vet ej varför dom blir röda, men kanske inte ens behövs?
        if (playerOneAnswer.equalsIgnoreCase(question.getCorrectAnswer()) ) {
            toPlayerOne.writeObject("Rätt Svar!");
        } else {
            toPlayerOne.writeObject("Fel svar! Rätt svar är: " + question.getCorrectAnswer());
        }

        // Vet ej varför dom blir röda, men kanske inte ens behövs?
        if (playerTwoAnswer.equalsIgnoreCase( question.getCorrectAnswer())) {
            toPlayerTwo.writeObject("Rätt Svar!");
        } else {
            toPlayerTwo.writeObject("Fel svar! Rätt svar är: " + question.getCorrectAnswer());
        }

        currentQuestionIndex++;
    }
}


