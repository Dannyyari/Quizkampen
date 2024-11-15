import javax.swing.*;
import java.awt.*;

public class GameInterface {

    // Huvudkomponenter
    private static JFrame frame;
    private static JPanel mainPanel, categoryPanel, questionPanel;
    private static JLabel questionLabel;

    public static void main(String[] args) {
        // Skapa huvudfönstret
        frame = new JFrame("Game Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // Huvudpanelen
        mainPanel = createMainPanel();

        // Kategoripanelen
        categoryPanel = createCategoryPanel();

        // Frågepanelen
        questionPanel = createQuestionPanel();

        // Lägg till huvudpanelen till fönstret
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Skapa huvudpanelen med spel- och poänginformation
    private static JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Toppanel med spelartur och poäng
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        JLabel turnLabel = new JLabel("Din tur", SwingConstants.CENTER);
        JLabel scoreLabel = new JLabel("0 - 0", SwingConstants.CENTER);
        JPanel playerPanel = new JPanel(new GridLayout(1, 2));
        playerPanel.add(new JLabel("Spelare 1", SwingConstants.CENTER));
        playerPanel.add(new JLabel("Motståndare", SwingConstants.CENTER));

        topPanel.add(turnLabel);
        topPanel.add(scoreLabel);
        topPanel.add(playerPanel);

        // Bottenpanel med "Spela"-knappen
        JPanel bottomPanel = new JPanel();
        JButton playButton = new JButton("Spela");
        bottomPanel.add(playButton);

        // Klickhändelse för "Spela"-knappen
        playButton.addActionListener(e -> switchToPanel(categoryPanel));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Skapa kategoripanelen med tre knappar
    private static JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel categoryLabel = new JLabel("Välj Kategori", SwingConstants.CENTER);
        panel.add(categoryLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        // Skapa knappar för olika kategorier
        JButton sportButton = new JButton("Sport");
        JButton musicButton = new JButton("Musik");
        JButton scienceButton = new JButton("Vetenskap");

        // Lägg till klickhändelser för varje knapp
        sportButton.addActionListener(e -> showQuestion("Sport"));
        musicButton.addActionListener(e -> showQuestion("Musik"));
        scienceButton.addActionListener(e -> showQuestion("Vetenskap"));

        // Lägg till knappar till panelen
        buttonPanel.add(sportButton);
        buttonPanel.add(musicButton);
        buttonPanel.add(scienceButton);

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    // Skapade en metod för att konfiguera och initiera frågepanelen
    private static JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("", SwingConstants.CENTER);
        panel.add(questionLabel, BorderLayout.CENTER);

        // skapade en knapp så man kan gå tillbaka till kategori
        JButton backButton = new JButton("Tillbaka");
        backButton.addActionListener(e -> switchToPanel(categoryPanel));
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    // För att rätt frågor ska visa sig baserat på vald kategori man väljer
    private static void showQuestion(String category) {
        switch (category) {
            case "Sport":
                questionLabel.setText("Vilket land uppfann sporten?");
                break;
            case "Musik":
                questionLabel.setText("Vem är känd som 'Kungen av Pop'?");
                break;
            case "Vetenskap":
                questionLabel.setText("Vad är H2O kemisk formel för?");
                break;
        }
        switchToPanel(questionPanel);
    }

    // För att Byta panelerna enklare
    private static void switchToPanel(JPanel panel) {
        frame.getContentPane().removeAll();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }
}
