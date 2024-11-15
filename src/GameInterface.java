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

    public static void main(String[] args) {
        // Skapa huvudfönstret
        frame = new JFrame("Game Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 350);
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

        // Lägg till komponenter till toppanelen
        topPanel.add(turnLabel);
        topPanel.add(scoreLabel);
        topPanel.add(playerPanel);

        // Mittenpanel med två rader av cirklar
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 1));

        for (int i = 0; i < 2; i++) {
            JPanel roundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            for (int j = 0; j < 4; j++) {
                JLabel circleLabel = new JLabel("O");
                circleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                roundPanel.add(circleLabel);
            }
            middlePanel.add(roundPanel);
        }

        // Bottenpanel med "Spela"-knappen
        JPanel bottomPanel = new JPanel();
        JButton playButton = new JButton("Spela");
        bottomPanel.add(playButton);

        // Klickhändelse för "Spela"-knappen
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.remove(panel);
                frame.add(categoryPanel, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(middlePanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Skapa kategoripanelen
    private static JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Rubrik "Välj Kategori"
        JLabel categoryLabel = new JLabel("Välj Kategori", SwingConstants.CENTER);
        panel.add(categoryLabel, BorderLayout.NORTH);

        // Panel med kategoriknappar
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));

        // Kategoriknappar
        JButton sportButton = new JButton("Sport");
        JButton musicButton = new JButton("Musik");
        JButton scienceButton = new JButton("Vetenskap");

        // Klickhändelse för kategoriknapparna
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

    // Skapa frågepanelen med fyra svarsknappar
    private static JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Fråga
        questionLabel = new JLabel("", SwingConstants.CENTER);
        panel.add(questionLabel, BorderLayout.NORTH);

        // Panel för svarsknappar
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 10, 10));

        // Svarsknappar
        answerButton1 = new JButton();
        answerButton2 = new JButton();
        answerButton3 = new JButton();
        answerButton4 = new JButton();

        // Klickhändelse för svarsknapparna
        ActionListener answerButtonListener = e -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.length) {
                loadQuestion();
            } else {
                // Om alla frågor är besvarade
                JOptionPane.showMessageDialog(frame, "Runda 1 är över!");
                frame.remove(questionPanel);
                frame.add(mainPanel, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
                currentQuestionIndex = 0; // Återställ frågeindex
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
        questionLabel.setText(questions[currentQuestionIndex]);
        answerButton1.setText(answers[currentQuestionIndex][0]);
        answerButton2.setText(answers[currentQuestionIndex][1]);
        answerButton3.setText(answers[currentQuestionIndex][2]);
        answerButton4.setText(answers[currentQuestionIndex][3]);
    }
}