package ServerSide;

import Client.GameGUI;
import Questions.DAO;
import Questions.QuestionsAndAnswers;
import Questions.RoundSettings;
import Questions.Sub.DAO.DAO_Anatomy;
import Questions.Sub.DAO.DAO_Geografi;
import Questions.Sub.DAO.DAO_Sport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    Socket socketPlayerOne;
    Socket socketPlayerTwo;

    private static DAO database;
    static RoundSettings settings= settings = new RoundSettings();

    private GameGUI game=new GameGUI();


    //variabler för resten av logiken
    private List<QuestionsAndAnswers> questions;
    private int currentQuestionIndex;
    private int player1Score;
    private int player2Score;
    private static int totalQuestions= settings.getQuestions();
    private static int totalRounds = settings.getRounds();

    //Behövs nog bytas till ObjectOutputStream
    private PrintWriter toP1Writer;
    private PrintWriter toP2Writer;

    //dessa två måste någ justeras då vi kommer skicka Seraliserade objekt?
    //Titta igenom denna fråga och se ifall vi behöver ändra


    //Behöver nog bytas till ObjectInputStream
    private BufferedReader fromP1;
    private BufferedReader fromP2;

//try(PrintWriter toUser= new PrintWriter(socket.getOutputStream(), true);
//    BufferedReader fromUser=new BufferedReader(new InputStreamReader(socket.getInputStream()));


    //kopplar två spelare
    public Server(Socket socketPlayer1, Socket socketPlayer2){
        this.socketPlayerOne= socketPlayer1;
        this.socketPlayerTwo= socketPlayer2;

            try {
                this.toP1Writer = new PrintWriter(socketPlayer1.getOutputStream(), true);
                this.toP2Writer = new PrintWriter(socketPlayer1.getOutputStream(), true);
                this.fromP1 = new BufferedReader(new InputStreamReader(socketPlayer1.getInputStream()));
                this.fromP2 = new BufferedReader(new InputStreamReader(socketPlayer2.getInputStream()));

            //här ska då metoder som vi skickar och hämtar från användaren. programmets "hjärna"

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }

    public void run(){
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
                database = new DAO_Sport(pathToSport);
                break;
            case "Geografi":
                database = new DAO_Geografi(pathToGeo);
                break;
            case "Anatomy":
                database = new DAO_Anatomy(pathToAnatomy);
                break;
            default:
                System.out.println("Ogiltig kategori");
                return;
        }
    }


    //insprererad av chaGPT, väldigt oklart om detta är OK sätt att skicka?
    //kommer behövas ändras
    private void sendQuestionToPlayers() {
        if (currentQuestionIndex < questions.size()) {
            QuestionsAndAnswers question = questions.get(currentQuestionIndex);
            String questionText = question.getQuestion();
            String[] answers = new String[]{
                    question.getCorrectAnswer(),
                    question.getAnswer2(),
                    question.getAnswer3(),
                    question.getAnswer4()
            };

            // Skicka frågan och svarsalternativen till båda spelarna

            toP1Writer.println(questionText);
            toP2Writer.println(questionText);
            for (String answer : answers) {
                toP1Writer.println(answer);
                toP2Writer.println(answer);
            }
        }
    }


}
