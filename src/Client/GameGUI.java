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
        frame = new JFrame("Quiz Game - " + playerName);
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

    private void resetGUI () {
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

        for (int i = 0; i<buttons.length; i++) {
            if (buttons[i] instanceof JButton button) {
                if(i < categories.size()) {
                button.setText(categories.get(i));
                button.setEnabled(true);
                } else{
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


    private void startNetworkThread() {
        new Thread(() -> {
            try {
                while (true) {
                    Object fromServer = inFromServer.readObject();

                    if (fromServer instanceof String message) {
                        switch (message) {
                            case "YOUR_TURN" -> {
                                resetGUI(); // Återställ GUI-komponenterna
                                String message2 = (String) inFromServer.readObject();
                                JOptionPane.showMessageDialog(frame, message2, "Din tur", JOptionPane.INFORMATION_MESSAGE);
                                cardLayout.show(mainContainer, "Category");
                            }
                            case "CATEGORY" -> {
                                categoryList = (List<String>) inFromServer.readObject();
                                System.out.println(playerName + " mottog kategorier: " + categoryList);
                                updateCategoryButtons(categoryList);
                                cardLayout.show(mainContainer, "Category");
                            }
                            case "QUESTIONS" -> {
                                questionsList = (List<QuestionsAndAnswers>) inFromServer.readObject();
                                currentQuestionIndex = 0;
                                loadQuestion(questionsList.get(currentQuestionIndex));
                                cardLayout.show(mainContainer, "Question");
                            }
                            case "FINAL_RESULT" -> {
                                String finalResult = (String) inFromServer.readObject();
                                JOptionPane.showMessageDialog(frame, finalResult);
                                frame.dispose();
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