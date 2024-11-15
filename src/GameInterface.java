import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GameInterface {

    // Huvudkomponenter
    private static JFrame frame;
    private static JPanel mainPanel, categoryPanel, questionPanel;
    private static JLabel questionLabel, scoreLabel;

    public static void main(String[] args) {
        // Skapar huvudfönstret
        frame = new JFrame("Game Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        frame.setBackground(new Color(50, 50, 50));

        // Huvudpanelen
        mainPanel = createMainPanel();

        // Kategoripanelen
        categoryPanel = createCategoryPanel();

        // Frågepanelen
        questionPanel = createQuestionPanel();

        // Lägger till huvudpanelen till fönstret
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Skapar huvudpanelen med spel och poänginformation
    private static JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 40, 49));

        // Toppanel med spelartur och poäng
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(57, 62, 70));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel turnLabel = new JLabel("Din Tur", SwingConstants.CENTER);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        turnLabel.setForeground(new Color(238, 238, 238));

        scoreLabel = new JLabel("Spelare: 0 - Motståndare: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        scoreLabel.setForeground(Color.LIGHT_GRAY);

        topPanel.add(turnLabel);
        topPanel.add(scoreLabel);

        // Bottenpanel med Spela knappen
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(34, 40, 49));
        JButton playButton = new JButton("Spela");
        styleButton(playButton);
        bottomPanel.add(playButton);

        // Klickhändelse för Spela knappen
        playButton.addActionListener(e -> switchToPanel(categoryPanel));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Skapar kategoripanelen
    private static JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 40, 49));

        JLabel categoryLabel = new JLabel("Välj spel kategori", SwingConstants.CENTER);
        categoryLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        categoryLabel.setForeground(new Color(0, 173, 181));
        panel.add(categoryLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBackground(new Color(34, 40, 49));
        buttonPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Skapar knappar för olika kategorier
        JButton sportButton = new JButton("Sport");
        JButton musicButton = new JButton("Musik");
        JButton scienceButton = new JButton("Vetenskap");

        styleButton(sportButton);
        styleButton(musicButton);
        styleButton(scienceButton);

        // Lägger till klickhändelser för varje knapp
        sportButton.addActionListener(e -> showQuestion("Sport"));
        musicButton.addActionListener(e -> showQuestion("Musik"));
        scienceButton.addActionListener(e -> showQuestion("Vetenskap"));

        // Lägger till knappar till panelen
        buttonPanel.add(sportButton);
        buttonPanel.add(musicButton);
        buttonPanel.add(scienceButton);

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    // Skapade en metod för att konfiguera och initiera frågepanelen
    private static JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 40, 49));

        questionLabel = new JLabel("Fråga visas här", SwingConstants.CENTER);
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        questionLabel.setForeground(Color.WHITE);
        panel.add(questionLabel, BorderLayout.CENTER);

        // skapade en knapp så man kan gå tillbaka till kategori
        JButton backButton = new JButton("Tillbaka");
        styleButton(backButton);
        backButton.addActionListener(e -> switchToPanel(categoryPanel));
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    private static void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setBackground(new Color(0, 173, 181));
        button.setForeground(Color.WHITE);
        button.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2, true),
                new EmptyBorder(10, 20, 10, 20)
        ));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 150, 157));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 173, 181));
            }
        });
    }

    // För att rätt frågor ska visa sig baserat på vald kategori man väljer
    private static void showQuestion(String category) {
        if (questionLabel != null) {
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
