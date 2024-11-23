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
    private BufferedReader readingfromserver;
    private ObjectOutputStream outToServer;
    private ObjectInputStream inFromServer;
    private BufferedReader readerBuff;
    private InetAddress iadr = InetAddress.getLoopbackAddress();
    int port = 55555;


    //frågor och svar
    private List<String> categoryList;
    private List<QuestionsAndAnswers> questionsList;

    // Huvudkomponenter
    private JFrame frame;
    private JPanel mainContainer;
    private CardLayout cardLayout;
    private static JLabel questionLabel;
    private static JButton categoryButton1, categoryButton2, categoryButton3, categoryButton4;
    private static JButton answerButton1, answerButton2, answerButton3, answerButton4;
    InetAddress serverAddress = InetAddress.getLoopbackAddress();

    private int score;

    static RoundSettings settings = new RoundSettings();
    private static int totalQuestions = settings.getQuestions();
    private static int totalRounds = settings.getRounds();
    private static int currentQuestionIndex = 0;
    private static int currentRound = 1; // Spårar nuvarande runda
    //ska detta vara 0?
    // Totalt antal rundor (baserat på vad som står i properties)

    public GameGUI() throws IOException, ClassNotFoundException {
        try {
            initializeNetwork();
            initializeGUI();
            run();
            this.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeNetwork() throws IOException {
        Socket clientSocket = new Socket(serverAddress, port);
        outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("Ansluten till servern!");
    }

    private void initializeGUI() {
        frame = new JFrame("Quizkampen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(createCategoryPanel(), "Category");
        mainContainer.add(createQuestionPanel(), "Question");

        frame.add(mainContainer);
        frame.setVisible(true);
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Välj en kategori", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        for (int i = 0; i < 4; i++) {
            JButton button = new JButton();
            button.setEnabled(false);
            button.addActionListener(e -> {
                try {
                    outToServer.writeObject(button.getText());
                    outToServer.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            buttonPanel.add(button);
        }
        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(questionLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        for (int i = 0; i < 4; i++) {
            JButton button = new JButton();
            button.addActionListener(e -> {
                try {
                    outToServer.writeObject(button.getText());
                    outToServer.flush();
                    currentQuestionIndex++;
                    if (currentQuestionIndex < questionsList.size()) {
                        loadQuestion(questionsList.get(currentQuestionIndex));
                    } else {
                        cardLayout.show(mainContainer, "Category");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            buttonPanel.add(button);
        }
        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    private void resetGUI() {
        JPanel categoryPanel = (JPanel) mainContainer.getComponent(0);
        JPanel buttonPanel = (JPanel) categoryPanel.getComponent(1);
        for (Component button : buttonPanel.getComponents()) {
            if (button instanceof JButton btn) {
                btn.setText("");
                btn.setEnabled(false);
            }
        }

        JPanel questionPanel = (JPanel) mainContainer.getComponent(1);
        JPanel questionButtonPanel = (JPanel) questionPanel.getComponent(1);
        for (Component button : questionButtonPanel.getComponents()) {
            if (button instanceof JButton btn) {
                btn.setText("");
                btn.setEnabled(false);
            }
        }

    }

    private void updateCategoryButtons(List<String> categories) {
        JPanel categoryPanel = (JPanel) mainContainer.getComponent(0);
        JPanel buttonPanel = (JPanel) categoryPanel.getComponent(1);

        Component[] buttons = buttonPanel.getComponents();
        if (categories.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Inga kategorier är tillgängliga.. Vänta på servern", "Fel", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] instanceof JButton button) {
                if (i < categories.size()) {
                    button.setText(categories.get(i));
                    button.setEnabled(true);
                } else {
                    button.setText("");
                    button.setEnabled(false);
                }
            }
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
        System.out.println("kategoriknapparna är uppdaterade: " + categories);
    }

    private void loadQuestion(QuestionsAndAnswers question) {
        JPanel questionPanel = (JPanel) mainContainer.getComponent(1);
        JLabel questionLabel = (JLabel) questionPanel.getComponent(0);
        questionLabel.setText(question.getQuestion());

        JPanel buttonPanel = (JPanel) questionPanel.getComponent(1);
        Component[] buttons = buttonPanel.getComponents();
        String[] answers = {
                question.getCorrectAnswer(),
                question.getAnswer2(),
                question.getAnswer3(),
                question.getAnswer4()
        };

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] instanceof JButton button) {
                button.setText(answers[i]);
                button.setEnabled(true);
            }
        }
    }

    /*
        public void run(){
            try {
                while (true) {

                    Object fromServer = inFromServer.readObject();
                    if (fromServer instanceof String state) {
                        System.out.println("Mottagen state från servern" + state);
                        if (state.equals("YOUR_TURN")){
                            resetGUI(); // Återställ GUI-komponenterna
                            String message2 = (String) inFromServer.readObject();
                            JOptionPane.showMessageDialog(frame, message2, "Din tur", JOptionPane.INFORMATION_MESSAGE);
                            cardLayout.show(mainContainer, "Category");
                        }
                        if (state.equals("STATE_CATEGORY")) {;
                            System.out.println("State question, välj");
                            categoryList = (List<String>) inFromServer.readObject();
                            System.out.println(" mottog kategorier: " + categoryList);
                            updateCategoryButtons(categoryList);
                            cardLayout.show(mainContainer, "Category");
                            //categoryList.add(readerBuff.readLine());
                            //Hur ska vi skapa GUI här?
                            //createCategoryPanel(outToServer,categoryList); <---------------------------------------------------------------------------------------
                            //catagoryPanel ska skicka tillbaka en sträng, kanske placera in en oos i konstuktor?
                            //ha en outputstream till server med svar(Sträng)
                        }
                        if (state.equals("STATE_QUESTIONS")) {
                            System.out.println("SVARA PÅ FRÅGAN");
                            for (int i = 0; i <totalQuestions ; i++) {
                                questionsList = (List<QuestionsAndAnswers>) inFromServer.readObject();
                                loadQuestion(questionsList.get(i));
                                cardLayout.show(mainContainer, "Question");
    //                          String q1 = readingfromserver.readLine();
    //                          outToServer.writeObject(q1);
    //                          questionsList = (List<QuestionsAndAnswers>) inFromServer.readObject();
                                //questionGUI, tar in list av questionsandanswers som inparameter
                                //SKICKA TILLBAKA STRÄNG TILL SERVER MED VALD SVAR

                                //här kollar vi ifall servern skickar tillbaka Correct så ska vi plussa på våra poäng.
                                //Kanske onödig men kan vara bra att ha för att hålla koll på det vi gjort

                                //Hur ska vi föra dessa in i GUI??
                                //kommer detta funka? Fråga Sigrun?
                            }

                            if (state.equals("STATE_RESULT")) {

                          //      String point =readingfromserver.readLine();
                                String point =(String) fromServer;
                                System.out.println("Poäng från server: " + point);
                        }
                        if (state.equals("CORRECT")) {
                            score++;;
                            System.out.println("du har svarat rätt, aktuell POÄNG::: " + score);
                        }

                        }
                        if (state.equals("STATE_POINTSOFROUND")){
                            //kan ta state som en string och spara den som en sträng roundPintState. Då tar ponintGUI emot kanske en sträng bara
                            //Vi skickar iväg en sträng
                            System.out.println(state);
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
        }*/
    public void run() {
        try {
            while (true) {
                Object fromServer = inFromServer.readObject();

                if (fromServer instanceof String state) {
                    System.out.println("Mottagen state från servern" + state);
                    switch (state) {
                    /*    case "YOUR_TURN" -> {
                            resetGUI(); // Återställ GUI-komponenterna
                            String message2 = (String) inFromServer.readObject();
                            JOptionPane.showMessageDialog(frame, message2, "Din tur", JOptionPane.INFORMATION_MESSAGE);
                            cardLayout.show(mainContainer, "Category");
                        }*/
                        case "STATE_CATEGORY" -> {
                            resetGUI();
                            categoryList = (List<String>) inFromServer.readObject();
                            System.out.println(" mottog kategorier: " + categoryList);
                            updateCategoryButtons(categoryList);
                            cardLayout.show(mainContainer, "Category");
                        }
                        case "STATE_QUESTIONS" -> {
                            questionsList = (List<QuestionsAndAnswers>) inFromServer.readObject();
                            currentQuestionIndex = 0;
                            loadQuestion(questionsList.get(currentQuestionIndex));
                            cardLayout.show(mainContainer, "Question");
                            if (state.equals("STATE_RESULT")) {
                                String point = (String) fromServer;
                                System.out.println("Poäng från server: " + point);
                            }
                            if (state.equals("CORRECT")) {
                                score++;
                                ;
                                System.out.println("du har svarat rätt, aktuell POÄNG::: " + score);
                            }

                        }
                        case "STATE_POINTSOFROUND" -> {
                            //kan ta state som en string och spara den som en sträng roundPintState. Då tar ponintGUI emot kanske en sträng bara
                            //Vi skickar iväg en sträng
                            System.out.println(state);
                        }

                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //FRÅGA SIGRUN, kan panelerna skicka ut svar?
    // Skapa kategoripanelen
    // Skapa huvudpanelen med spel- och poänginformation (inklusive cirklarna)


    //----------------------SIGRUN VISADE---------------------------
    /*
    public void actionPerformed(ActionEvent e){
        if (e.getSource == categoryButton1){
            objOutWriter.write(categoryButton1.getText())
        }

    }*/
  /*  public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String playerName = JOptionPane.showInputDialog(null, "Vad heter du?", "Ange ditt namn", JOptionPane.QUESTION_MESSAGE);
            if (playerName != null && !playerName.trim().isEmpty()) {
                new NewGUI(playerName);
            } else {
                JOptionPane.showMessageDialog(null, "Du måste ange ett namn för att spela.", "Fel", JOptionPane.ERROR_MESSAGE);
            }
        });
    }*/
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GameGUI();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
