package ServerSide;

import Questions.DAO;
import Questions.QuestionsAndAnswers;
import Questions.RoundSettings;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class Server extends Thread {
    Socket playerOneSocket;
    Socket playerTwoSocket;

    private List<QuestionsAndAnswers> questions;
    private int currentQuestionIndex; // FRÅGA! Ska inte index börja från 0 för att veta var man är?

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
    public Server(Socket socketPlayerOne, Socket socketPlayerTwo) throws IOException {
        this.playerOneSocket = socketPlayerOne;
        this.playerTwoSocket = socketPlayerTwo;

            try {
                toPlayerOne = new ObjectOutputStream(playerOneSocket.getOutputStream());
                toPlayerTwo = new ObjectOutputStream(playerTwoSocket.getOutputStream());
                fromPlayerOne = new ObjectInputStream(playerOneSocket.getInputStream());
                fromPlayerTwo = new ObjectInputStream(playerTwoSocket.getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
    }

    public void run(){
        //här ska då metoder som vi skickar och hämtar från användaren. programmets "hjärna"
        try {
            sendQuestionToPlayers();
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
            setDatabase(chosenCategory);

            //OKLART HUR DETTA SKA IMPLEMENTERAS

//            frame.remove(categoryPanel);
//            frame.add(questionPanel, BorderLayout.CENTER);
//            loadQuestion();
//            frame.revalidate();
//            frame.repaint();
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


    public static void setDatabase(String category) {
        String pathToSport = "src/Questions/textfiles/SportQuestions";
        String pathToGeo = "src/Questions/textfiles/GeoQuestions";
        String pathToAnatomy = "src/Questions/textfiles/AnatomyQuestions";
        switch (category) {
            case "Sport":
                database = new DAO(pathToSport);
                break;
            case "Geografi":
                database = new DAO(pathToGeo);
                break;
            case "Anatomy":
                database = new DAO(pathToAnatomy);
                break;
            default:
                System.out.println("Ogiltig kategori");
                return;
        }
    }


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
        if (playerTwoAnswer == question.getCorrectAnswer()) {
            toPlayerTwo.writeObject("Rätt Svar!");
        } else {
            toPlayerTwo.writeObject("Fel svar! Rätt svar är: " + question.getCorrectAnswer());
        }

        currentQuestionIndex++;
    }
}

    /*
            // JAG TROR INTE DET HÄR BEHÖVS, KOMMENTERAR UT FOR NOW.
            String questionText = question.getQuestion();
            String[] answers = new String[]{
                    question.getCorrectAnswer(),
                    question.getAnswer2(),
                    question.getAnswer3(),
                    question.getAnswer4()};

            // Skicka frågan och svarsalternativen till båda spelarna
            toPlayerOneWriter.println(questionText);
            toPlayerTwoWriter.println(questionText);
            for (String answer : answers) {
                toPlayerOneWriter.println(answer);
                toPlayerTwoWriter.println(answer);
            }
        }
    }
    */

