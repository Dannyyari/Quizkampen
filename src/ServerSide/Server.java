package ServerSide;

import Client.GameGUI;
import Questions.DAO;
import Questions.RoundSettings;
import Questions.Sub.DAO.DAO_Anatomy;
import Questions.Sub.DAO.DAO_Geografi;
import Questions.Sub.DAO.DAO_Sport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Server extends Thread {
    Socket socketPlayerOne;
    Socket socketPlayerTwo;

    private static DAO database;
    static RoundSettings settings= settings = new RoundSettings();

    GameGUI game=new GameGUI();

    private static int totalQuestions= settings.getQuestions();
    private static int totalRounds = settings.getRounds();

    private PrintWriter toP1Writer;
    private PrintWriter toP2Writer;
    private BufferedReader fromP1;
    private BufferedReader fromP2;

    //

//try(PrintWriter toUser= new PrintWriter(socket.getOutputStream(), true);
//    BufferedReader fromUser=new BufferedReader(new InputStreamReader(socket.getInputStream()));


    //kopplar två spelare
    public Server(Socket socketPlayer1, Socket socketPlayer2){
        this.socketPlayerOne= socketPlayer1;
        this.socketPlayerTwo= socketPlayer2;

            try {
                toP1Writer=new PrintWriter(socketPlayer1.getOutputStream(), true);
                toP2Writer=new PrintWriter(socketPlayer1.getOutputStream(), true);
                fromP1=new BufferedReader(new InputStreamReader(socketPlayer1.getInputStream()));
                fromP2=new BufferedReader(new InputStreamReader(socketPlayer2.getInputStream()));

            } catch (Exception e) {
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

}
