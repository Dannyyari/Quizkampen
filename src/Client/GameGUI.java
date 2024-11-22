package Client;

import Questions.QuestionsAndAnswers;
import Properties.RoundSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class GameGUI extends Thread implements Serializable {

    //ServerKlientArk
    private ObjectOutputStream outToServer;
    private ObjectInputStream inFromServer;
    private BufferedReader readerBuff;
    private InetAddress iadr = InetAddress.getLoopbackAddress();
    int port = 55555;


    //frågor och svar
    private List<String> categoryList;
    private List<QuestionsAndAnswers> questionsList;


    // Huvudkomponenter
    private static JFrame frame;
    private static JPanel mainPanel, categoryPanel, questionPanel;
    private static JLabel questionLabel;
    private static JButton categoryButton1, categoryButton2, categoryButton3, categoryButton4;
    private static JButton answerButton1, answerButton2, answerButton3, answerButton4;

    private int score;

    static RoundSettings settings = new RoundSettings();
    private static int totalQuestions= settings.getQuestions();
    private static int totalRounds = settings.getRounds();
    private static int currentQuestionIndex = 0;
    private static int currentRound = 1; // Spårar nuvarande runda
    //ska detta vara 0?
    // Totalt antal rundor (baserat på vad som står i properties)

    public GameGUI() throws IOException, ClassNotFoundException {
        System.out.println("in constructor");
         Socket clientSocket = new Socket(iadr, port);
        System.out.println("socket ready");
            outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            inFromServer = new ObjectInputStream(clientSocket.getInputStream());
            readerBuff =new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.start();

    }

    public void run(){
        try {
            while (true){
                System.out.println("client in run loop");
                Object fromServer=inFromServer.readObject();
                if (fromServer instanceof String s) {
                    if (s.equals("CATEGORY")) {
                        categoryList= (List<String>) inFromServer.readObject();
                     //   categoryList.add(readerBuff.readLine());
                        createCategoryPanel(categoryList);
                        //ha en outputstream till server med svar(Sträng)
                    }
                    if (s.equals("QUESTIONS")) {

                        if (fromServer instanceof QuestionsAndAnswers qna) {
                            questionsList=(List <QuestionsAndAnswers>)inFromServer.readObject();
                            //questionGUI, tar in list av questionsandanswers som inparameter
                            //SKICKA TILLBAKA STRÄNG TILL SERVER MED VALD SVAR
                            if (readerBuff.readLine()=="CORRECT"){
                                score++;
                            }
                            else{
                                return;
                            }
                            //Hur ska vi föra dessa in i GUI??
                            //kommer detta funka? Fråga Sigrun?
                        }
                    }if (s.equals("RESAULT")){

                    }
            }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("in main");
        GameGUI g = new GameGUI();
        System.out.println("last in man");
        // new GameGUI().start();

       // SwingUtilities.invokeLater(GameGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Game Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(new BorderLayout());

        frame.setVisible(true);
    }


    public void sendCategorySelection(ObjectOutputStream outputStream, String selectedCategory) throws IOException {
        outputStream.writeObject(selectedCategory); // Skicka kategori som sträng
        outputStream.flush();
    }

    public List<QuestionsAndAnswers> receiveQuestionsFromServer(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        return (List<QuestionsAndAnswers>) inputStream.readObject(); // Avserialiserar frågelistan
    }
    

    //FRÅGA SIGRUN, kan panelerna skicka ut svar?
    // Skapa kategoripanelen
    private static JPanel createCategoryPanel(List <String> categoryList) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel categoryLabel = new JLabel("Välj Kategori", SwingConstants.CENTER);
        panel.add(categoryLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        
        categoryButton1 = new JButton(categoryList.get(0));
        categoryButton2= new JButton(categoryList.get(1));
        categoryButton3 = new JButton(categoryList.get(2));
        categoryButton4 = new JButton(categoryList.get(3));


        buttonPanel.add(categoryButton1);
        buttonPanel.add(categoryButton2);
        buttonPanel.add(categoryButton3);
        buttonPanel.add(categoryButton4);

        panel.add(buttonPanel, BorderLayout.CENTER);

        //ACTIONLISTNER TILL SERVER SKICKA STRÄNG!!!

        return panel;
    }
    // Skapa huvudpanelen med spel- och poänginformation (inklusive cirklarna)
    private static JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Toppanel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 1));

        // Spelartur Label
        JLabel turnLabel = new JLabel("Din tur", SwingConstants.CENTER);

        // Poäng Label
        JLabel scoreLabel = new JLabel("0 - 0", SwingConstants.CENTER);

        // Spelarnamn Panel
        JPanel playerPanel = new JPanel(new GridLayout(1, 2));
        JLabel player1Label = new JLabel("Spelare 1", SwingConstants.CENTER);
        JLabel player2Label = new JLabel("Motståndare", SwingConstants.CENTER);
        playerPanel.add(player1Label);
        playerPanel.add(player2Label);

        // Panel för cirklar under Spelare 1 och Motståndare
      //  JPanel circlesPanel = getCirclesPanel();

        // Lägg till komponenter till toppanelen
        topPanel.add(turnLabel);
        topPanel.add(scoreLabel);
        topPanel.add(playerPanel);

        // Mittenpanel med cirklar
        JPanel middlePanel = new JPanel(new BorderLayout());
        //middlePanel.add(circlesPanel, BorderLayout.CENTER);

        // Bottenpanel med "Spela"-knappen
        JPanel bottomPanel = new JPanel();
        JButton playButton = new JButton("Spela");
        playButton.addActionListener(e -> {
            frame.remove(panel);
            frame.add(categoryPanel, BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
        });
        bottomPanel.add(playButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(middlePanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Skapa frågepanelen
    private static JPanel createQuestionPanel(List <QuestionsAndAnswers> questions) {
        JPanel panel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("", SwingConstants.CENTER);
        panel.add(questionLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        answerButton1 = new JButton();
        answerButton2 = new JButton();
        answerButton3 = new JButton();
        answerButton4 = new JButton();

        ActionListener answerButtonListener = e -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < totalQuestions) {
               // loadQuestion();
            } else {
                handleEndOfRound();
            }
        };

        buttonPanel.add(answerButton1);
        buttonPanel.add(answerButton2);
        buttonPanel.add(answerButton3);
        buttonPanel.add(answerButton4);

        answerButton1.addActionListener(answerButtonListener);
        answerButton2.addActionListener(answerButtonListener);
        answerButton3.addActionListener(answerButtonListener);
        answerButton4.addActionListener(answerButtonListener);

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }


    // Hantera slutet av en runda
    private static void handleEndOfRound() {
        JOptionPane.showMessageDialog(frame, "Runda " + currentRound + " är över!");
        currentQuestionIndex = 0;
        currentRound++;
        if (currentRound <= totalRounds) {
            frame.remove(questionPanel);
            frame.add(categoryPanel, BorderLayout.CENTER);
        } else{
            JOptionPane.showMessageDialog(frame, "Spelet är över! Tack för att du spelade!");
            frame.remove(questionPanel);
            frame.add(mainPanel, BorderLayout.CENTER);
        }
        frame.revalidate();
        frame.repaint();
    }
    /*
    // Skapa cirkelpanelen för poängvisning
    private static JPanel getCirclesPanel() {
        JPanel circlesPanel = new JPanel(new GridLayout(2, 2));

        // Cirklar för Spelare 1, rad 1
        JPanel player1CirclesPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel player1Circle1 = new JLabel("O");
        JLabel player1Circle2 = new JLabel("O");
        player1CirclesPanel1.add(player1Circle1);
        player1CirclesPanel1.add(player1Circle2);

        // Cirklar för Motståndare, rad 1
        JPanel player2CirclesPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel player2Circle1 = new JLabel("O");
        JLabel player2Circle2 = new JLabel("O");
        player2CirclesPanel1.add(player2Circle1);
        player2CirclesPanel1.add(player2Circle2);

        // Cirklar för Spelare 1, rad 2
        JPanel player1CirclesPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel player1Circle3 = new JLabel("O");
        JLabel player1Circle4 = new JLabel("O");
        player1CirclesPanel2.add(player1Circle3);
        player1CirclesPanel2.add(player1Circle4);

        // Cirklar för Motståndare, rad 2
        JPanel player2CirclesPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel player2Circle3 = new JLabel("O");
        JLabel player2Circle4 = new JLabel("O");
        player2CirclesPanel2.add(player2Circle3);
        player2CirclesPanel2.add(player2Circle4);

        // Lägg till alla cirkelpaneler
        circlesPanel.add(player1CirclesPanel1);
        circlesPanel.add(player2CirclesPanel1);
        circlesPanel.add(player1CirclesPanel2);
        circlesPanel.add(player2CirclesPanel2);

        return circlesPanel;
    }

     */


}