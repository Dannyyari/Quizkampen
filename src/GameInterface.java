import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameInterface {

    // Huvudkomponenter
    private static JFrame frame;
    private static JPanel mainPanel, categoryPanel, questionPanel;
    private static JLabel questionLabel;
    private static JButton answerButton1, answerButton2, answerButton3, answerButton4;

    // Frågor och svar
    private static String[] questions = {
            "När var Obama president?",
            "Vilken planet är känd som den röda planeten?"
    };
    private static String[][] answers = {
            {"2008 - 2016", "2000 - 2008", "2016 - 2020", "1992 - 2000"},
            {"Mars", "Venus", "Jupiter", "Saturnus"}
    };

    private static int currentQuestionIndex = 0;
    private static int currentRound = 1; // Spårar nuvarande runda
    private static final int TOTAL_ROUNDS = 2; // Totalt antal rundor

    public static void main(String[] args) {
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
        JPanel circlesPanel = getCirclesPanel();

        // Lägg till komponenter till toppanelen
        topPanel.add(turnLabel);
        topPanel.add(scoreLabel);
        topPanel.add(playerPanel);

        // Mittenpanel med cirklar
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.add(circlesPanel, BorderLayout.CENTER);

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

    // Skapa kategoripanelen
    private static JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel categoryLabel = new JLabel("Välj Kategori", SwingConstants.CENTER);
        panel.add(categoryLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton sportButton = new JButton("Sport");
        JButton musicButton = new JButton("Musik");
        JButton scienceButton = new JButton("Vetenskap");

        ActionListener categoryButtonListener = e -> {
            frame.remove(categoryPanel);
            frame.add(questionPanel, BorderLayout.CENTER);
            loadQuestion();
            frame.revalidate();
            frame.repaint();
        };

        sportButton.addActionListener(categoryButtonListener);
        musicButton.addActionListener(categoryButtonListener);
        scienceButton.addActionListener(categoryButtonListener);

        buttonPanel.add(sportButton);
        buttonPanel.add(musicButton);
        buttonPanel.add(scienceButton);

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    // Skapa frågepanelen
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
            if (currentQuestionIndex < questions.length) {
                loadQuestion();
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

    // Ladda aktuell fråga
    private static void loadQuestion() {
        questionLabel.setText(questions[currentQuestionIndex]);
        answerButton1.setText(answers[currentQuestionIndex][0]);
        answerButton2.setText(answers[currentQuestionIndex][1]);
        answerButton3.setText(answers[currentQuestionIndex][2]);
        answerButton4.setText(answers[currentQuestionIndex][3]);
    }

    // Hantera slutet av en runda
    private static void handleEndOfRound() {
        JOptionPane.showMessageDialog(frame, "Runda " + currentRound + " är över!");
        currentQuestionIndex = 0;
        currentRound++;
        if (currentRound <= TOTAL_ROUNDS) {
            frame.remove(questionPanel);
            frame.add(categoryPanel, BorderLayout.CENTER);
        } else {
            JOptionPane.showMessageDialog(frame, "Spelet är över! Tack för att du spelade!");
            frame.remove(questionPanel);
            frame.add(mainPanel, BorderLayout.CENTER);
        }
        frame.revalidate();
        frame.repaint();
    }
}