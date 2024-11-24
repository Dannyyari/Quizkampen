package Client;

import Questions.QuestionsAndAnswers;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class GameGUI {
    private final String playerName;
    private Socket clientSocket;
    private ObjectOutputStream outToServer;
    private ObjectInputStream inFromServer;

    private JFrame frame;
    private JPanel mainContainer;
    private CardLayout cardLayout;

    private List<String> categoryList;
    private List<QuestionsAndAnswers> questionsList;
    private int currentQuestionIndex;

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

    private void initializeNetwork() throws IOException {
        InetAddress serverAddress = InetAddress.getLoopbackAddress();
        int port = 55553;

        clientSocket = new Socket(serverAddress, port);
        outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("Ansluten till servern!");
    }

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




    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Välj en kategori", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        for (int i = 0; i < 4; i++) {
            JButton button = new JButton();
            button.setEnabled(true); //ska den vara false eller true
            button.addActionListener(e -> {
                try {
                    String buttontext=button.getText();
                    outToServer.writeObject(buttontext);
                    outToServer.flush();
                    System.out.println("Försöker trycka på knapp.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            buttonPanel.add(button);
        }
        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

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
    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
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
                    if (currentQuestionIndex <= questionsList.size()) {
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
        panel.revalidate();
        panel.repaint();
        return panel;
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

    private JPanel createResultPanel(String playerName, int playerScore, String opponentName, int opponentScore) {
        JPanel resultPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Resultat", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resultPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        contentPanel.add(new JLabel(playerName + ":", SwingConstants.RIGHT));
        contentPanel.add(new JLabel(playerScore + " poäng", SwingConstants.LEFT));

        contentPanel.add(new JLabel(opponentName + ":", SwingConstants.RIGHT));
        contentPanel.add(new JLabel(opponentScore + " poäng", SwingConstants.LEFT));

        JLabel winnerLabel = new JLabel("", SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        if (playerScore > opponentScore) {
            winnerLabel.setText(playerName + " vann!");
        } else if (playerScore < opponentScore) {
            winnerLabel.setText(opponentName + " vann!");
        } else {
            winnerLabel.setText("Det blev oavgjort!");
        }

        contentPanel.add(new JLabel("Vinnare:", SwingConstants.RIGHT));
        contentPanel.add(winnerLabel);

        resultPanel.add(contentPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Stäng");
        closeButton.addActionListener(e -> System.exit(0));
        resultPanel.add(closeButton, BorderLayout.SOUTH);

        return resultPanel;
    }

    private void startNetworkThread() {
        new Thread(() -> {
            try {
                while (true) {
                    Object fromServer = inFromServer.readObject();

                    if (fromServer instanceof String message) {
                        switch (message) {
                            case "STATE_CATEGORY" -> {
                                categoryList = (List<String>) inFromServer.readObject();
                                updateCategoryButtons(categoryList);
                                cardLayout.show(mainContainer, "Category");
                            }
                            case "STATE_QUESTIONS" -> {
                                QuestionsAndAnswers question= (QuestionsAndAnswers) inFromServer.readObject();
                          //     questionsList = (QuestionsAndAnswers quest) inFromServer.readObject();
                                loadQuestion(question);
                                cardLayout.show(mainContainer, "Question");
                            }
                            case "STATE_POINTSOFROUND" -> {
                                String resultMessage = (String) inFromServer.readObject();
                                JOptionPane.showMessageDialog(frame, resultMessage, "Rundresultat", JOptionPane.INFORMATION_MESSAGE);
                            }
                            case "STATE_RESULT" -> {
                                String finalResult = (String) inFromServer.readObject();
                                JOptionPane.showMessageDialog(frame, finalResult, "Rundresultat", JOptionPane.INFORMATION_MESSAGE);
                                //frame.getContentPane().add(createResultPanel("Du", 0, "Motståndare", 0), "Result");
                                //cardLayout.show(mainContainer, "Result");
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Anslutningen till servern bröts.", "Fel", JOptionPane.ERROR_MESSAGE);
                frame.dispose();
            }
        }).start();
    }

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