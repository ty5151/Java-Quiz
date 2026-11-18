import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

class Question {
    private String text;
    private String[] options;
    private int correctIndex;

    public Question(String text, String[] options, int correctIndex) {
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getText() { return text; }
    public String[] getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
    
    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctIndex;
    }
}

class Quiz {
    private String name;
    private int timeLimitMinutes;
    private List<Question> questions;

    public Quiz(String name, int timeLimitMinutes) {
        this.name = name;
        this.timeLimitMinutes = timeLimitMinutes;
        this.questions = new ArrayList<>();
    }

    public String getName() { return name; }
    public int getTimeLimitMinutes() { return timeLimitMinutes; }
    public List<Question> getQuestions() { return questions; }
    
    public void addQuestion(Question q) {
        questions.add(q);
    }
}

class QuizResult {
    private String username;
    private String quizName;
    private int score;
    private int total;
    private List<String> details;

    public QuizResult(String username, String quizName, int total) {
        this.username = username;
        this.quizName = quizName;
        this.total = total;
        this.score = 0;
        this.details = new ArrayList<>();
    }

    public int getScore() { return score; }
    public int getTotal() { return total; }
    public String getUsername() { return username; }
    public String getQuizName() { return quizName; }

    public void addDetail(boolean correct, String detailString) {
        if (correct) score++;
        details.add(detailString);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User: ").append(username).append(" | Quiz: ").append(quizName)
          .append(" | Score: ").append(score).append("/").append(total).append("\n");
        for (String d : details) sb.append("   - ").append(d).append("\n");
        sb.append("--------------------------------------------------\n");
        return sb.toString();
    }
}

class QuizManager {
    private Map<String, Quiz> quizzes = new HashMap<>();
    private List<QuizResult> results = new ArrayList<>();
    
    private final String FILE_NAME = "saved_quizzes.txt";
    private final String DELIMITER = ";;;";

    public QuizManager() {
        loadQuizzesFromFile();
    }

    public void saveQuiz(Quiz quiz) {
        quizzes.put(quiz.getName(), quiz);
        saveQuizzesToFile(); 
    }

    public Quiz getQuiz(String name) { return quizzes.get(name); }
    public String[] getQuizNames() { return quizzes.keySet().toArray(new String[0]); }
    public boolean hasQuizzes() { return !quizzes.isEmpty(); }

    public void saveResult(QuizResult result) { results.add(result); }
    public List<QuizResult> getResults() { return results; }

    private void saveQuizzesToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Quiz q : quizzes.values()) {
                bw.write("QUIZ" + DELIMITER + q.getName() + DELIMITER + q.getTimeLimitMinutes() + "\n");
                for (Question question : q.getQuestions()) {
                    bw.write("Q" + DELIMITER + question.getText() + DELIMITER 
                        + question.getOptions()[0] + DELIMITER + question.getOptions()[1] + DELIMITER
                        + question.getOptions()[2] + DELIMITER + question.getOptions()[3] + DELIMITER
                        + question.getCorrectIndex() + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    private void loadQuizzesFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return; 

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            Quiz currentQuiz = null;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; 
                String[] parts = line.split(DELIMITER);
                
                if (parts[0].equals("QUIZ") && parts.length >= 2) {
                    int timeLimit = (parts.length >= 3) ? Integer.parseInt(parts[2]) : 10;
                    currentQuiz = new Quiz(parts[1], timeLimit);
                    quizzes.put(currentQuiz.getName(), currentQuiz);
                } else if (parts[0].equals("Q") && parts.length >= 7 && currentQuiz != null) {
                    String[] opts = {parts[2], parts[3], parts[4], parts[5]};
                    int correctIdx = Integer.parseInt(parts[6]);
                    currentQuiz.addQuestion(new Question(parts[1], opts, correctIdx));
                }
            }
        } catch (Exception e) {
            System.out.println("File parsing safely skipped due to previous formatting changes: " + e.getMessage());
        }
    }
}

public class QuizPlatform extends JFrame {
    
    private QuizManager quizManager;
    private JPanel mainPanel;

    public QuizPlatform() {
        quizManager = new QuizManager();
        
        setTitle("Practical Quiz Platform");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        showMainMenu();
        setVisible(true);
    }

    private void switchView(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showMainMenu() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JButton btnNew = new JButton("Start a New Quiz");
        JButton btnSaved = new JButton("Saved Quizzes");
        JButton btnPractice = new JButton("Practice Quiz");
        JButton btnStats = new JButton("Current Performance");

        btnNew.addActionListener(e -> setupNewQuiz());
        btnSaved.addActionListener(e -> showSavedQuizzesView());
        btnPractice.addActionListener(e -> setupPracticeQuiz());
        btnStats.addActionListener(e -> showPerformanceView());

        panel.add(btnNew);
        panel.add(btnSaved);
        panel.add(btnPractice);
        panel.add(btnStats);

        switchView(panel);
    }

    private void setupNewQuiz() {
        JPanel prompt = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField countField = new JTextField();
        JTextField timeField = new JTextField("10");

        prompt.add(new JLabel("Quiz Name:")); prompt.add(nameField);
        prompt.add(new JLabel("Number of Questions:")); prompt.add(countField);
        prompt.add(new JLabel("Time Limit (Minutes):")); prompt.add(timeField);

        if (JOptionPane.showConfirmDialog(this, prompt, "New Quiz Setup", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                int count = Integer.parseInt(countField.getText().trim());
                int time = Integer.parseInt(timeField.getText().trim());
                
                if (name.isEmpty() || count <= 0 || time <= 0) throw new Exception();
                if (quizManager.getQuiz(name) != null) {
                    JOptionPane.showMessageDialog(this, "Quiz name already exists!");
                    return;
                }
                showQuizEditorView(name, count, time, null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid text and numbers.");
            }
        }
    }

    private void showQuizEditorView(String quizName, int count, int timeLimit, Quiz existingQuiz) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        List<JTextField[]> questionInputs = new ArrayList<>();
        List<JComboBox<String>> answerInputs = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            JPanel qPanel = new JPanel(new GridLayout(6, 2));
            qPanel.setBorder(BorderFactory.createTitledBorder("Question " + (i + 1)));

            JTextField[] fields = new JTextField[]{new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField()};
            JComboBox<String> answerDrop = new JComboBox<>(new String[]{"Option 1", "Option 2", "Option 3", "Option 4"});

            if (existingQuiz != null && i < existingQuiz.getQuestions().size()) {
                Question eq = existingQuiz.getQuestions().get(i);
                fields[0].setText(eq.getText());
                fields[1].setText(eq.getOptions()[0]);
                fields[2].setText(eq.getOptions()[1]);
                fields[3].setText(eq.getOptions()[2]);
                fields[4].setText(eq.getOptions()[3]);
                answerDrop.setSelectedIndex(eq.getCorrectIndex());
            }

            qPanel.add(new JLabel("Question:")); qPanel.add(fields[0]);
            qPanel.add(new JLabel("Option 1:")); qPanel.add(fields[1]);
            qPanel.add(new JLabel("Option 2:")); qPanel.add(fields[2]);
            qPanel.add(new JLabel("Option 3:")); qPanel.add(fields[3]);
            qPanel.add(new JLabel("Option 4:")); qPanel.add(fields[4]);
            qPanel.add(new JLabel("Correct:"));  qPanel.add(answerDrop);

            questionInputs.add(fields);
            answerInputs.add(answerDrop);
            container.add(qPanel);
        }

        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("Save Quiz");
        JButton cancelBtn = new JButton("Cancel / Back");

        saveBtn.addActionListener(e -> {
            Quiz quiz = new Quiz(quizName, timeLimit);
            for (int i = 0; i < count; i++) {
                JTextField[] f = questionInputs.get(i);
                String[] opts = {f[1].getText(), f[2].getText(), f[3].getText(), f[4].getText()};
                quiz.addQuestion(new Question(f[0].getText(), opts, answerInputs.get(i).getSelectedIndex()));
            }
            quizManager.saveQuiz(quiz);
            JOptionPane.showMessageDialog(this, "Saved Successfully to file!");
            showMainMenu();
        });

        cancelBtn.addActionListener(e -> showMainMenu());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(new JLabel(" Editing: " + quizName + " (" + timeLimit + " mins)", SwingConstants.CENTER), BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        wrap.add(scrollPane, BorderLayout.CENTER);
        
        wrap.add(btnPanel, BorderLayout.SOUTH);
        switchView(wrap);
    }

    private void showSavedQuizzesView() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        String[] quizNames = quizManager.getQuizNames();
        
        if (quizNames.length == 0) {
            JLabel emptyLabel = new JLabel("No quizzes saved yet.");
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            container.add(emptyLabel);
        } else {
            for (String name : quizNames) {
                JPanel row = new JPanel(new BorderLayout(10, 10));
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY), 
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                
                JLabel nameLabel = new JLabel(name);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
                
                JButton modifyBtn = new JButton("Modify");
                modifyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                modifyBtn.addActionListener(e -> {
                    Quiz q = quizManager.getQuiz(name);
                    showQuizEditorView(q.getName(), q.getQuestions().size(), q.getTimeLimitMinutes(), q);
                });
                
                row.add(nameLabel, BorderLayout.CENTER);
                row.add(modifyBtn, BorderLayout.EAST);
                container.add(row);
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Manage Saved Quizzes", SwingConstants.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(header, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> showMainMenu());
        btnPanel.add(backBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        switchView(panel);
    }

    private void setupPracticeQuiz() {
        if (!quizManager.hasQuizzes()) {
            JOptionPane.showMessageDialog(this, "No quizzes exist yet!");
            return;
        }

        JPanel prompt = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField userField = new JTextField();
        JComboBox<String> quizDrop = new JComboBox<>(quizManager.getQuizNames());
        
        prompt.add(new JLabel("Your Name:")); prompt.add(userField);
        prompt.add(new JLabel("Select Quiz:")); prompt.add(quizDrop);

        if (JOptionPane.showConfirmDialog(this, prompt, "Practice Setup", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String user = userField.getText().trim();
            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name required.");
                return;
            }
            showActivePracticeView(user, quizManager.getQuiz((String) quizDrop.getSelectedItem()));
        }
    }

    private void showActivePracticeView(String username, Quiz quiz) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        List<JRadioButton[]> radioInputs = new ArrayList<>();

        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            Question q = quiz.getQuestions().get(i);
            JPanel qPanel = new JPanel(new GridLayout(5, 1));
            qPanel.setBorder(BorderFactory.createTitledBorder("Q" + (i + 1) + ": " + q.getText()));

            ButtonGroup bg = new ButtonGroup();
            JRadioButton[] rbs = new JRadioButton[4];
            for (int j = 0; j < 4; j++) {
                rbs[j] = new JRadioButton(q.getOptions()[j]);
                bg.add(rbs[j]);
                qPanel.add(rbs[j]);
            }
            radioInputs.add(rbs);
            container.add(qPanel);
        }

        final int[] timeLeft = {quiz.getTimeLimitMinutes() * 60};
        JLabel timerLabel = new JLabel("Time Remaining: " + formatTime(timeLeft[0]));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.RED);
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton submitBtn = new JButton("Submit Answers");
        
        final javax.swing.Timer[] countdown = new javax.swing.Timer[1];
        
        countdown[0] = new javax.swing.Timer(1000, e -> {
            timeLeft[0]--;
            timerLabel.setText("Time Remaining: " + formatTime(timeLeft[0]));
            if (timeLeft[0] <= 0) {
                countdown[0].stop();
                JOptionPane.showMessageDialog(this, "Time's up! Auto-submitting quiz.");
                submitBtn.doClick();
            }
        });
        countdown[0].start();

        JPanel btnPanel = new JPanel();
        JButton cancelBtn = new JButton("Cancel / Back");

        submitBtn.addActionListener(e -> {
            countdown[0].stop(); 
            
            QuizResult result = new QuizResult(username, quiz.getName(), quiz.getQuestions().size());
            int[] selectedIndices = new int[quiz.getQuestions().size()];
            
            for (int i = 0; i < quiz.getQuestions().size(); i++) {
                Question q = quiz.getQuestions().get(i);
                int selectedIdx = -1;
                for (int j = 0; j < 4; j++) {
                    if (radioInputs.get(i)[j].isSelected()) selectedIdx = j;
                }
                
                selectedIndices[i] = selectedIdx; 

                if (selectedIdx == -1) {
                    result.addDetail(false, "Q" + (i+1) + ": Unanswered (Correct: " + q.getOptions()[q.getCorrectIndex()] + ")");
                } else if (q.isCorrect(selectedIdx)) {
                    result.addDetail(true, "Q" + (i+1) + ": Correct");
                } else {
                    result.addDetail(false, "Q" + (i+1) + ": Incorrect (You chose: " + q.getOptions()[selectedIdx] + ", Correct: " + q.getOptions()[q.getCorrectIndex()] + ")");
                }
            }
            
            quizManager.saveResult(result);
            showReviewView(quiz, selectedIndices, result.getScore()); 
        });

        cancelBtn.addActionListener(e -> {
            countdown[0].stop();
            showMainMenu();
        });

        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        JPanel wrap = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("   Quiz: " + quiz.getName()), BorderLayout.WEST);
        topPanel.add(timerLabel, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        wrap.add(topPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        wrap.add(scrollPane, BorderLayout.CENTER);
        
        wrap.add(btnPanel, BorderLayout.SOUTH);
        switchView(wrap);
    }

    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    private void showReviewView(Quiz quiz, int[] selectedAnswers, int score) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            Question q = quiz.getQuestions().get(i);
            int userChoice = selectedAnswers[i];
            
            JPanel qPanel = new JPanel(new GridLayout(5, 1));
            qPanel.setBorder(BorderFactory.createTitledBorder("Q" + (i + 1) + ": " + q.getText()));

            for (int j = 0; j < 4; j++) {
                JLabel optLabel = new JLabel("  " + q.getOptions()[j]);
                optLabel.setOpaque(true);
                
                if (j == q.getCorrectIndex()) {
                    optLabel.setBackground(new Color(153, 255, 153)); 
                    optLabel.setText(optLabel.getText() + " (Correct Answer)");
                } 
                else if (j == userChoice) {
                    optLabel.setBackground(new Color(255, 153, 153)); 
                    optLabel.setText(optLabel.getText() + " (Your Wrong Answer)");
                }
                
                qPanel.add(optLabel);
            }
            container.add(qPanel);
        }

        JPanel btnPanel = new JPanel();
        JButton finishBtn = new JButton("Finish Review & Return to Menu");
        finishBtn.addActionListener(e -> showMainMenu());
        btnPanel.add(finishBtn);

        JPanel wrap = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Final Score: " + score + " / " + quiz.getQuestions().size(), SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        wrap.add(headerLabel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        wrap.add(scrollPane, BorderLayout.CENTER);
        
        wrap.add(btnPanel, BorderLayout.SOUTH);
        switchView(wrap);
    }

    // --- View: VISUAL PERFORMANCE DASHBOARD ---
    private void showPerformanceView() {
        JPanel mainWrapper = new JPanel(new BorderLayout(10, 10));
        mainWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. STATS HEADER
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        List<QuizResult> results = quizManager.getResults();
        
        int totalAttempts = results.size();
        double totalPct = 0;
        int maxPct = 0;
        
        for (QuizResult r : results) {
            int pct = (int) Math.round((double) r.getScore() / r.getTotal() * 100);
            totalPct += pct;
            if (pct > maxPct) maxPct = pct;
        }
        int avgPct = totalAttempts > 0 ? (int) Math.round(totalPct / totalAttempts) : 0;

        statsPanel.add(createStatBox("Total Attempts", String.valueOf(totalAttempts)));
        statsPanel.add(createStatBox("Average Score", avgPct + "%"));
        statsPanel.add(createStatBox("Highest Score", maxPct + "%"));

        // 2. HISTORY LIST (Cards with Progress Bars)
        JPanel historyContainer = new JPanel();
        historyContainer.setLayout(new BoxLayout(historyContainer, BoxLayout.Y_AXIS));

        if (totalAttempts == 0) {
            JLabel empty = new JLabel("No performance records yet for this session.");
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            historyContainer.add(empty);
        } else {
            // Loop backwards to show most recent at the top
            for (int i = results.size() - 1; i >= 0; i--) {
                QuizResult r = results.get(i);
                historyContainer.add(createResultCard(r));
                historyContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(historyContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Recent Activity"));

        // 3. BOTTOM BUTTONS
        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> showMainMenu());
        JPanel btnPanel = new JPanel();
        btnPanel.add(backBtn);

        mainWrapper.add(statsPanel, BorderLayout.NORTH);
        mainWrapper.add(scrollPane, BorderLayout.CENTER);
        mainWrapper.add(btnPanel, BorderLayout.SOUTH);

        switchView(mainWrapper);
    }


    private JPanel createStatBox(String title, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        p.setBackground(new Color(245, 245, 250));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.DARK_GRAY);
        
        JLabel valLabel = new JLabel(value, SwingConstants.CENTER);
        valLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valLabel.setForeground(new Color(40, 100, 200));
        
        p.add(titleLabel, BorderLayout.NORTH);
        p.add(valLabel, BorderLayout.CENTER);
        return p;
    }

    // Helper: Creates individual rows showing Username, Quiz Name, and a Color-Coded Progress Bar
    private JPanel createResultCard(QuizResult r) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        // Force the card height so they stack nicely in BoxLayout
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JLabel infoLabel = new JLabel("<html><b>" + r.getUsername() + "</b> took <i>" + r.getQuizName() + "</i></html>");
        
        JPanel scorePanel = new JPanel(new BorderLayout(15, 0));
        JLabel scoreLabel = new JLabel(r.getScore() + " / " + r.getTotal());
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        int pct = (int) Math.round((double) r.getScore() / r.getTotal() * 100);
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(pct);
        bar.setStringPainted(true);
        bar.setPreferredSize(new Dimension(200, 20));
        
        // Color coding based on percentage
        if (pct >= 80) bar.setForeground(new Color(50, 200, 50)); // Green
        else if (pct >= 50) bar.setForeground(new Color(255, 165, 0)); // Orange
        else bar.setForeground(new Color(220, 50, 50)); // Red

        scorePanel.add(scoreLabel, BorderLayout.WEST);
        scorePanel.add(bar, BorderLayout.CENTER);

        card.add(infoLabel, BorderLayout.NORTH);
        card.add(scorePanel, BorderLayout.CENTER);
        
        return card;
    }

    // ==========================================
    // MAIN EXECUTION
    // ==========================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizPlatform());
    }
}