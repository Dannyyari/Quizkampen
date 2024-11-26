package Client;

import Questions.QuestionsAndAnswers;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
Klassen GameGUI hanterar användargränssnittet för ett quizspel och möjliggör interaktion
mellan spelaren och spelets logik. Den ansvarar för att presentera olika skärmar och hantera
användarinmatningar.

Funktioner:
- Skapar GUI med hjälp av Java Swing, med paneler för kategorival, frågesvar och slutresultat.
- Hanterar anslutningen till servern via sockets för att skicka och ta emot speldata.
- Dynamiskt uppdaterar knappar, etiketter och vyer baserat på serverns tillstånd och spelprogress.
- Lyssnar på server meddelanden i en separat tråd och visar rätt innehåll (kategorier, frågor eller resultat).
- Behandlar användar inmatningar och skickar val till servern för att hantera spelets logik.
- Ger visuell feedback vid rätt eller fel svar genom att färgmarkera knappar och visa meddelanden.

 */

// REPO:  https://github.com/Dannyyari/Quizkampen


// Definierar variabler för att hantera spelarnamn, serverkommunikation,
// aktuella frågor, och gränssnittskomponenter som hanterar spelets olika vyer.
public class GameGUI {
    private final String playerName;
    private Socket clientSocket;
    private ObjectOutputStream outToServer;
    private ObjectInputStream inFromServer;

    private QuestionsAndAnswers currentQuestion;
    private List<String> categoryList;

    private JFrame frame;
    private JPanel mainContainer;
    private CardLayout cardLayout;

    // Konstruktor som initierar spelarnamn, startar nätverksanslutning, bygger GUI,
    // och startar en tråd för att lyssna på servern. Hanterar fel vid anslutningsproblem.
    public GameGUI(String playerName) {
        this.playerName = playerName;
        try {
            initializeNetwork();
            initializeGUI();
            startNetworkThread();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Kunde inte ansluta till servern.", "Fel", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Initierar nätverksanslutningen genom att skapa en socket till servern,
    // samt instanserar strömmar för att skicka och ta emot data.
    private void initializeNetwork() throws IOException {
        InetAddress serverAddress = InetAddress.getLoopbackAddress();
        int port = 55553;

        clientSocket = new Socket(serverAddress, port);
        outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("Ansluten till servern!");
    }

    // Skapar och konfigurerar huvudfönstret för GUI, lägger till paneler för kategorier,
    // frågor och vänteläge med hjälp av CardLayout för att byta mellan vyer.
    private void initializeGUI() {
        frame = new JFrame("QuizKampen - " + playerName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(createCategoryPanel(), "Category");
        mainContainer.add(createQuestionPanel(), "Question");
        mainContainer.add(new JPanel(), "Waiting"); // Placeholder för vänteläge

        frame.add(mainContainer);
        frame.setVisible(true);
    }

    // Skapar en panel för att visa kategoriväljaren, med en etikett som instruerar användaren att välja en kategori.
    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Välj en kategori", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(label, BorderLayout.NORTH);

        // Skapar en panel med fyra knappar för kategoriurval,
        // där varje knapp skickar vald kategori (i strängformat) till servern när den klickas.
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        for (int i = 0; i < 4; i++) {
            JButton button = new JButton();
            button.setEnabled(true); //ska den vara false eller true
            button.addActionListener(e -> {
                try {
                    String buttontext = button.getText();
                    outToServer.writeObject(buttontext);
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

    // Uppdaterar knapparna för kategoriurval baserat på en lista av kategorier,
    // aktiverar dem och sätter rätt text, samt inaktiverar knappar om det inte finns tillräckligt med kategorier.
    private void updateCategoryButtons(List<String> categories) {
        JPanel categoryPanel = (JPanel) mainContainer.getComponent(0);
        JPanel buttonPanel = (JPanel) categoryPanel.getComponent(1);

        Component[] buttons = buttonPanel.getComponents();
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
    }

    // Laddar en fråga och svar från ett QuestionsAndAnswers objekt,
    // visar frågan och blandar svaren, och uppdaterar knapparna i GUI med de nya svarsalternativen.
    private void loadQuestion(QuestionsAndAnswers question) {
        this.currentQuestion = question;
        JPanel questionPanel = (JPanel) mainContainer.getComponent(1);
        JLabel questionLabel = (JLabel) questionPanel.getComponent(0);
        questionLabel.setText(question.getQuestion());

        List<String> answers = new ArrayList<>();
        answers.add(question.getCorrectAnswer());
        answers.add(question.getAnswer2());
        answers.add(question.getAnswer3());
        answers.add(question.getAnswer4());
        Collections.shuffle(answers);

        JPanel buttonPanel = (JPanel) questionPanel.getComponent(1);
        Component[] buttons = buttonPanel.getComponents();

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] instanceof JButton button) {
                button.setText(answers.get(i));
                button.setEnabled(true);
            }
        }
    }

    // Skapar en panel för att visa frågan och svarsalternativen,
    // med en etikett för frågan och en panel för att arrangera svarsknapparna i ett rutnät.
    private JPanel createQuestionPanel() {
        List<JButton> buttons = new ArrayList<>();
        JPanel panel = new JPanel(new BorderLayout());
        JLabel questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(questionLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Skapar knappar för svarsalternativ, skickar valt svar till servern,
        // och ger visuell feedback genom att färga knapparna grön eller röd beroende på om svaret är korrekt eller ej.
        for (int i = 0; i < 4; i++) {
            JButton button = new JButton();
            buttons.add(button);
            button.addActionListener(e -> {
                try {
                    outToServer.writeObject(button.getText());
                    outToServer.flush();
                    String correctAnswer = currentQuestion.getCorrectAnswer();
                    boolean isCorrect = correctAnswer.equals(button.getText());

                    // Rensar tidigare knappfärger
                    buttons.forEach(b -> b.setBackground(UIManager.getColor("Button.background")));

                    // Disablar knappar efter svar
                    buttons.forEach(b -> b.setEnabled(false));

                    if (isCorrect) {
                        System.out.println("Right answer");
                        // Byter färg på rätt knapp till grön
                        button.setBackground(Color.GREEN);
                    } else {
                        System.out.println("Wrong answer");
                        // Ifall vi trycker på fel så visar alla fel röda och rätt blir grön
                        buttons.forEach(b -> {
                            if (b.getText().equals(correctAnswer)) {
                                b.setBackground(Color.GREEN); // Correct answer button
                            } else {
                                b.setBackground(Color.RED); // Wrong answer buttons
                            }
                        });
                    }

                    // Skapar en timer för att återställa knappfärger och aktivera knappar efter en kort fördröjning,
                    // samt säkerställer att knapparna uppdateras korrekt efter att ett svar har valts.
                    Timer timer = new Timer(1200, evt -> {
                        buttons.get(0).setBackground(UIManager.getColor("Button.background"));
                        buttons.get(1).setBackground(UIManager.getColor("Button.background"));
                        buttons.get(2).setBackground(UIManager.getColor("Button.background"));
                        buttons.get(3).setBackground(UIManager.getColor("Button.background"));
                        buttons.forEach(b -> b.setEnabled(true));

                    });
                    timer.setRepeats(false);
                    timer.start();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            buttonPanel.add(button);
        }
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
        return panel;
    }


    // Skapar och visar en panel för att visa slutresultatet av spelet,
    // inklusive vinnarmeddelande och resultaten för båda spelarna, samt en knapp för att stänga spelet.
    private void createFinalResultPanel(String winnerMessage, String playerOneResults, String playerTwoResults) {
        JPanel finalResultPanel = new JPanel(new BorderLayout());

        // Titel som visar vem som vann
        JLabel winnerLabel = new JLabel(winnerMessage, SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        winnerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        finalResultPanel.add(winnerLabel, BorderLayout.NORTH);

        JPanel resultsPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 1 rad, 2 kolumner
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Spelare 1-resultat
        JTextArea playerOneTextArea = new JTextArea();
        playerOneTextArea.setEditable(false);
        playerOneTextArea.setText(playerOneResults);
        playerOneTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane playerOneScrollPane = new JScrollPane(playerOneTextArea);
        resultsPanel.add(playerOneScrollPane);

        // Spelare 2-resultat
        JTextArea playerTwoTextArea = new JTextArea();
        playerTwoTextArea.setEditable(false);
        playerTwoTextArea.setText(playerTwoResults);
        playerTwoTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane playerTwoScrollPane = new JScrollPane(playerTwoTextArea);
        resultsPanel.add(playerTwoScrollPane);

        finalResultPanel.add(resultsPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Stäng");
        closeButton.addActionListener(e -> System.exit(0));
        finalResultPanel.add(closeButton, BorderLayout.SOUTH);

        mainContainer.add(finalResultPanel, "FinalResult");
        cardLayout.show(mainContainer, "FinalResult");
    }


    // Startar en bakgrundstråd som kontinuerligt lyssnar på servermeddelanden och hanterar
    // olika speltillstånd (kategori, frågor, poäng, resultat) genom att uppdatera GUI baserat på mottagna data.
    private void startNetworkThread() {
        new Thread(() -> {
            try {
                while (true) {
                    Object fromServer = inFromServer.readObject();

                    if (fromServer instanceof String message) {
                        switch (message) {
                            case "STATE_CATEGORY" -> {
                                // Server ger oss en lista av kategorier
                                categoryList = (List<String>) inFromServer.readObject();
                                updateCategoryButtons(categoryList);
                                cardLayout.show(mainContainer, "Category");
                            }
                            case "STATE_QUESTIONS" -> {
                                // Server ger oss en fråga och alla svar
                                QuestionsAndAnswers question = (QuestionsAndAnswers) inFromServer.readObject();
                                Thread.sleep(1200);
                                loadQuestion(question);
                                cardLayout.show(mainContainer, "Question");
                            }
                            case "STATE_POINTSOFROUND" -> {
                                // Server ger oss resultat för bägge spelare denna runda
                                String resultMessage = (String) inFromServer.readObject();
                                JOptionPane.showMessageDialog(frame, resultMessage, "Rundresultat", JOptionPane.INFORMATION_MESSAGE);
                            }
                            case "STATE_RESULT" -> {
                                // Ger oss resultat för denna runda
                                String finalResult = (String) inFromServer.readObject();
                                JOptionPane.showMessageDialog(frame, finalResult, "Rundresultat", JOptionPane.INFORMATION_MESSAGE);

                            }
                            case "STATE_FINAL_RESULT" -> {
                                //Ger oss slutresultat
                                String winnerMessage = (String) inFromServer.readObject();
                                String playerOneResults = (String) inFromServer.readObject();
                                String playerTwoResults = (String) inFromServer.readObject();
                                createFinalResultPanel(winnerMessage, playerOneResults, playerTwoResults);
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Anslutningen till servern bröts.", "Fel", JOptionPane.ERROR_MESSAGE);
                frame.dispose();
            }
        }).start();
    }

    // Startar programmet genom att be användaren ange sitt namn i en dialogruta.
    // Om ett giltigt namn anges skapas ett nytt GameGUI-objekt, annars visas ett felmeddelande.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String playerName = JOptionPane.showInputDialog(null, "Vad heter du?", "Ange ditt namn", JOptionPane.QUESTION_MESSAGE);
            if (playerName != null && !playerName.trim().isEmpty()) {
                new GameGUI(playerName);
            } else {
                JOptionPane.showMessageDialog(null, "Du måste ange ett namn för att spela.", "Fel", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}