package Client;

import Questions.DAO;
import Questions.QuestionsAndAnswers;
import Questions.RoundSettings;
import Questions.Sub.DAO.DAO_Anatomy;
import Questions.Sub.DAO.DAO_Geografi;
import Questions.Sub.DAO.DAO_Sport;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameGUI {
    private Client client;

    public GameGUI(Client client) {
        this.client = client;
    }
    public GameGUI(){}

    // Huvudkomponenter
    private static JFrame frame;
    private static JPanel mainPanel, categoryPanel, questionPanel;
    private static JLabel questionLabel, scoreLabel;
    private static JButton answerButton1, answerButton2, answerButton3, answerButton4;

    private static DAO database;

    static RoundSettings settings;
    private static int totalQuestions= settings.getQuestions();
    private static int totalRounds = settings.getRounds();
    private static int currentQuestionIndex = 0;
    private static int currentRound = 1; // Spårar nuvarande runda

  /*  public static void main(String[] args) {
        // Skapa huvudfönstret
        frame = new JFrame("Game Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(new BorderLayout());

        // Huvudpanelen
        mainPanel = createMainPanel();

        // Kategoripanelen
        categoryPanel = createCategoryPanel();

        // Frågepanelen
        questionPanel = createQuestionPanel();

        // Lägg till huvudpanelen till fönstret
        frame.add(mainPanel, BorderLayout.CENTER);

        // Visa huvudfönstret
        frame.setVisible(true);
    }*/

    // Skapa huvudpanelen med spel- och poänginformation (inklusive cirklarna)
    private static JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 181, 181));

        // Toppanel med spelartur och poäng
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(0, 181, 181));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel turnLabel = new JLabel("Din Tur", SwingConstants.CENTER);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        turnLabel.setForeground(new Color(16, 13, 13));

        scoreLabel = new JLabel("Spelare: 0 - Motståndare: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        scoreLabel.setForeground(Color.LIGHT_GRAY);

        topPanel.add(turnLabel);
        topPanel.add(scoreLabel);

        // Bottenpanel med Spela knappen
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(51, 58, 56));
        JButton playButton = new JButton("Spela");
        styleButton(playButton);
        bottomPanel.add(playButton);


        playButton.addActionListener(e -> switchToPanel(categoryPanel));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setBackground(new Color(0, 181, 181));
        button.setForeground(Color.WHITE);
        button.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2, true),
                new EmptyBorder(10, 20, 10, 20)
        ));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(7, 241, 46));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 181, 181));
            }
        });
    }

    private static void switchToPanel(JPanel panel) {
        frame.getContentPane().removeAll();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

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

    // Skapa kategoripanelen
    private static JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(183, 239, 41));

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

            frame.remove(categoryPanel);
            frame.add(questionPanel, BorderLayout.CENTER);
            loadQuestion();
            frame.revalidate();
            frame.repaint();
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
    //skapande av kategorival
    public static void setDatabase(String category) {
        String pathToSport = "src/Questions/textfiles/SportQuestions";
        String pathToGeo = "src/Questions/textfiles/GeoQuestions";
        String pathToAnatomy = "src/Questions/textfiles/AnatomyQuestions";
        switch (category) {
            case "Sport":
                database = new DAO("src/Questions/textfiles/SportQuestions");
                break;
            case "Geografi":
                database = new DAO("src/Questions/textfiles/GeoQuestions");
                break;
            case "Anatomy":
                database = new DAO("src/Questions/textfiles/AnatomyQuestions");
                break;
            default:
                System.out.println("Ogiltig kategori");
                return;
        }
    }

    private static JPanel createQuestionPanel() {
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
                loadQuestion();
            } else {
                handleEndOfRound();
            }
        };

        answerButton1.addActionListener(answerButtonListener);
        answerButton2.addActionListener(answerButtonListener);
        answerButton3.addActionListener(answerButtonListener);
        answerButton4.addActionListener(answerButtonListener);

        buttonPanel.add(answerButton1);
        buttonPanel.add(answerButton2);
        buttonPanel.add(answerButton3);
        buttonPanel.add(answerButton4);

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    // Ladda aktuell fråga
    private static void loadQuestion() {
        QuestionsAndAnswers question = database.getCurrentQuestion();
        questionLabel.setText(question.getQuestion());


        answerButton1.setText(question.getCorrectAnswer());
        answerButton2.setText(question.getAnswer2());
        answerButton3.setText(question.getAnswer3());
        answerButton4.setText(question.getAnswer4());


        resetButtonColors();
    }

    private static void resetButtonColors() {
        answerButton1.setBackground(new Color(0, 181, 181));
        answerButton2.setBackground(new Color(0, 181, 181));
        answerButton3.setBackground(new Color(0, 181, 181));
        answerButton4.setBackground(new Color(0, 181, 181));
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
}