package admin;

import models.Question;
import models.QuizResult;
import utils.DataPersistence;
import utils.QuizEvaluator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * MEMBER 3: ADMIN PANEL FOR MANAGING QUESTIONS
 * 
 * AdminPanel Class
 * GUI application for administrators to manage the quiz system
 * - View and manage quiz questions
 * - Add, edit, and delete questions
 * - Monitor quiz results in real-time
 * - View connected students
 * - Generate reports and statistics
 */
public class AdminPanel extends JFrame {

    private DataPersistence dataPersistence;
    private QuizEvaluator quizEvaluator;

    // GUI Components
    private JTabbedPane tabbedPane;
    private JTable questionsTable;
    private DefaultTableModel questionsTableModel;
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;
    private JTextArea statsTextArea;

    public AdminPanel() {
        this.dataPersistence = DataPersistence.getInstance();
        this.quizEvaluator = QuizEvaluator.getInstance();

        initializeGUI();
        loadData();
    }

    /**
     * Initialize GUI components
     */
    private void initializeGUI() {
        setTitle("QuizMaster Connect - Admin Panel");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab("Questions", createQuestionsPanel());
        tabbedPane.addTab("Results", createResultsPanel());
        tabbedPane.addTab("Statistics", createStatisticsPanel());

        add(tabbedPane);

        // Menu bar
        createMenuBar();
    }

    /**
     * Create menu bar
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshItem = new JMenuItem("Refresh All");
        refreshItem.addActionListener(e -> loadData());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Questions menu
        JMenu questionsMenu = new JMenu("Questions");
        JMenuItem addQuestionItem = new JMenuItem("Add Question");
        addQuestionItem.addActionListener(e -> showAddQuestionDialog());
        JMenuItem editQuestionItem = new JMenuItem("Edit Question");
        editQuestionItem.addActionListener(e -> showEditQuestionDialog());
        JMenuItem deleteQuestionItem = new JMenuItem("Delete Question");
        deleteQuestionItem.addActionListener(e -> deleteSelectedQuestion());
        questionsMenu.add(addQuestionItem);
        questionsMenu.add(editQuestionItem);
        questionsMenu.add(deleteQuestionItem);

        menuBar.add(fileMenu);
        menuBar.add(questionsMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Create Questions management panel
     */
    private JPanel createQuestionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Quiz Questions Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Questions table
        String[] columnNames = { "ID", "Question", "Category", "Points", "Correct Answer" };
        questionsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        questionsTable = new JTable(questionsTableModel);
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(questionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add Question");
        addButton.addActionListener(e -> showAddQuestionDialog());

        JButton editButton = new JButton("Edit Question");
        editButton.addActionListener(e -> showEditQuestionDialog());

        JButton deleteButton = new JButton("Delete Question");
        deleteButton.addActionListener(e -> deleteSelectedQuestion());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadQuestions());

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create Results viewing panel
     */
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Quiz Results");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Results table
        String[] columnNames = { "Student ID", "Name", "Score", "Max Score", "Percentage", "Correct", "Total", "Date" };
        resultsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(resultsTableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("Refresh Results");
        refreshButton.addActionListener(e -> loadResults());

        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> exportResultsToCSV());

        buttonsPanel.add(refreshButton);
        buttonsPanel.add(exportButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create Statistics panel
     */
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Quiz Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Statistics text area
        statsTextArea = new JTextArea();
        statsTextArea.setEditable(false);
        statsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(statsTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Statistics");
        refreshButton.addActionListener(e -> loadStatistics());
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Load all data
     */
    private void loadData() {
        loadQuestions();
        loadResults();
        loadStatistics();
    }

    /**
     * Load questions into table
     */
    private void loadQuestions() {
        questionsTableModel.setRowCount(0);
        List<Question> questions = dataPersistence.loadQuestions();

        for (Question q : questions) {
            Object[] row = {
                    q.getQuestionId(),
                    q.getQuestionText(),
                    q.getCategory(),
                    q.getPoints(),
                    q.getOptions().get(q.getCorrectAnswerIndex())
            };
            questionsTableModel.addRow(row);
        }

        System.out.println("[AdminPanel] Loaded " + questions.size() + " questions");
    }

    /**
     * Load results into table
     */
    private void loadResults() {
        resultsTableModel.setRowCount(0);
        Map<String, QuizResult> results = dataPersistence.loadResults();

        for (QuizResult result : results.values()) {
            Object[] row = {
                    result.getStudentId(),
                    result.getStudentName(),
                    result.getTotalScore(),
                    result.getMaxScore(),
                    String.format("%.2f%%", result.getPercentage()),
                    result.getCorrectAnswers(),
                    result.getTotalQuestions(),
                    result.getCompletionDate()
            };
            resultsTableModel.addRow(row);
        }

        System.out.println("[AdminPanel] Loaded " + results.size() + " results");
    }

    /**
     * Load statistics
     */
    private void loadStatistics() {
        String stats = quizEvaluator.getStatistics();

        // Add additional statistics
        Map<String, QuizResult> results = dataPersistence.loadResults();
        StringBuilder sb = new StringBuilder(stats);
        sb.append("\n=== Detailed Statistics ===\n\n");

        if (!results.isEmpty()) {
            sb.append("Recent Results:\n");
            List<QuizResult> sortedResults = new ArrayList<>(results.values());
            sortedResults.sort((r1, r2) -> r2.getCompletionDate().compareTo(r1.getCompletionDate()));

            int count = 0;
            for (QuizResult result : sortedResults) {
                if (count++ >= 10)
                    break;
                sb.append(String.format("  %s (%s): %d/%d (%.2f%%)\n",
                        result.getStudentName(),
                        result.getStudentId(),
                        result.getTotalScore(),
                        result.getMaxScore(),
                        result.getPercentage()));
            }
        }

        statsTextArea.setText(sb.toString());
    }

    /**
     * Show Add Question dialog
     */
    private void showAddQuestionDialog() {
        JDialog dialog = new JDialog(this, "Add Question", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Question ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Question ID:"), gbc);
        JTextField idField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        // Question Text
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Question:"), gbc);
        JTextArea questionArea = new JTextArea(3, 20);
        questionArea.setLineWrap(true);
        gbc.gridx = 1;
        panel.add(new JScrollPane(questionArea), gbc);

        // Options
        JTextField[] optionFields = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            gbc.gridx = 0;
            gbc.gridy = 2 + i;
            panel.add(new JLabel("Option " + (i + 1) + ":"), gbc);
            optionFields[i] = new JTextField(20);
            gbc.gridx = 1;
            panel.add(optionFields[i], gbc);
        }

        // Correct Answer
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Correct Answer (1-4):"), gbc);
        JTextField correctField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(correctField, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Category:"), gbc);
        JTextField categoryField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(categoryField, gbc);

        // Points
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Points:"), gbc);
        JTextField pointsField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(pointsField, gbc);

        // Buttons
        JPanel buttonsPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String questionText = questionArea.getText().trim();
                List<String> options = new ArrayList<>();
                for (JTextField field : optionFields) {
                    options.add(field.getText().trim());
                }
                int correctIndex = Integer.parseInt(correctField.getText().trim()) - 1;
                String category = categoryField.getText().trim();
                int points = Integer.parseInt(pointsField.getText().trim());

                Question question = new Question(id, questionText, options, correctIndex, category, points);
                dataPersistence.addQuestion(question);
                loadQuestions();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Question added successfully!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        panel.add(buttonsPanel, gbc);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    /**
     * Show Edit Question dialog
     */
    private void showEditQuestionDialog() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a question to edit.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Edit functionality - Select the question and use Add Question dialog with same ID to update.");
    }

    /**
     * Delete selected question
     */
    private void deleteSelectedQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a question to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int questionId = (int) questionsTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete question ID: " + questionId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dataPersistence.deleteQuestion(questionId);
            loadQuestions();
            JOptionPane.showMessageDialog(this, "Question deleted successfully!");
        }
    }

    /**
     * Export results to CSV
     */
    private void exportResultsToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Results to CSV");

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Export logic here
            JOptionPane.showMessageDialog(this, "Export functionality - Results would be saved to: " +
                    fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Main method to launch admin panel
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminPanel adminPanel = new AdminPanel();
            adminPanel.setVisible(true);
        });
    }
}
