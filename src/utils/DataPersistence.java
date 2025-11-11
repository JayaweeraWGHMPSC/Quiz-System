package utils;

import models.Question;
import models.QuizResult;
import models.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * MEMBER 5: DATA PERSISTENCE
 * 
 * DataPersistence Class
 * Handles all file I/O operations for the quiz system
 * - Load questions from JSON/text files
 * - Save quiz results to files
 * - Load quiz results from files
 * - Manage data synchronization
 */
public class DataPersistence {

    private static final String DATA_DIR = "data";
    private static final String QUESTIONS_FILE = DATA_DIR + "/questions.txt";
    private static final String RESULTS_FILE = DATA_DIR + "/results.txt";
    private static final String USERS_FILE = DATA_DIR + "/users.txt";

    // Singleton instance
    private static DataPersistence instance;

    // Thread-safe data structures
    private List<Question> questions;
    private Map<String, QuizResult> results;
    private Map<String, User> users;

    private DataPersistence() {
        questions = Collections.synchronizedList(new ArrayList<>());
        results = Collections.synchronizedMap(new HashMap<>());
        users = Collections.synchronizedMap(new HashMap<>());
        initializeDataDirectory();
    }

    public static synchronized DataPersistence getInstance() {
        if (instance == null) {
            instance = new DataPersistence();
        }
        return instance;
    }

    /**
     * Initialize data directory if it doesn't exist
     */
    private void initializeDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            System.out.println("[DataPersistence] Data directory initialized: " + DATA_DIR);
        } catch (IOException e) {
            System.err.println("[DataPersistence] Error creating data directory: " + e.getMessage());
        }
    }

    /**
     * Load questions from file
     * File format:
     * questionId|questionText|option1,option2,option3,option4|correctIndex|category|points
     */
    public synchronized List<Question> loadQuestions() {
        questions.clear();
        File file = new File(QUESTIONS_FILE);

        if (!file.exists()) {
            System.out.println("[DataPersistence] Questions file not found. Creating sample questions...");
            createSampleQuestions();
            return new ArrayList<>(questions);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }

                try {
                    String[] parts = line.split("\\|");
                    if (parts.length == 6) {
                        int questionId = Integer.parseInt(parts[0].trim());
                        String questionText = parts[1].trim();
                        List<String> options = Arrays.asList(parts[2].split(","));
                        // Trim options
                        options = new ArrayList<>(options);
                        for (int i = 0; i < options.size(); i++) {
                            options.set(i, options.get(i).trim());
                        }
                        int correctIndex = Integer.parseInt(parts[3].trim());
                        String category = parts[4].trim();
                        int points = Integer.parseInt(parts[5].trim());

                        Question question = new Question(questionId, questionText, options,
                                correctIndex, category, points);
                        questions.add(question);
                    }
                } catch (Exception e) {
                    System.err.println("[DataPersistence] Error parsing question: " + line);
                }
            }
            System.out.println("[DataPersistence] Loaded " + questions.size() + " questions from file.");
        } catch (IOException e) {
            System.err.println("[DataPersistence] Error loading questions: " + e.getMessage());
            createSampleQuestions();
        }

        return new ArrayList<>(questions);
    }

    /**
     * Create sample questions for testing
     */
    private void createSampleQuestions() {
        questions.clear();

        questions.add(new Question(1, "What is the default port for HTTP?",
                Arrays.asList("80", "443", "8080", "3306"), 0, "Networking", 10));

        questions.add(new Question(2, "Which Java class is used for server-side socket programming?",
                Arrays.asList("Socket", "ServerSocket", "DatagramSocket", "MulticastSocket"), 1, "Java", 10));

        questions.add(new Question(3, "What does TCP stand for?",
                Arrays.asList("Transfer Control Protocol", "Transmission Control Protocol",
                        "Transport Communication Protocol", "Technical Control Protocol"),
                1, "Networking", 10));

        questions.add(new Question(4, "Which layer of OSI model handles routing?",
                Arrays.asList("Physical", "Data Link", "Network", "Transport"), 2, "Networking", 10));

        questions.add(new Question(5, "What is the purpose of ObjectInputStream in Java?",
                Arrays.asList("Read text data", "Serialize objects", "Deserialize objects", "Write binary data"),
                2, "Java", 10));

        questions.add(new Question(6, "Which method is used to accept client connections?",
                Arrays.asList("connect()", "accept()", "listen()", "bind()"), 1, "Java", 10));

        questions.add(new Question(7, "What is multithreading?",
                Arrays.asList("Running multiple programs", "Running multiple threads simultaneously",
                        "Running multiple servers", "Running multiple clients"),
                1, "Java", 10));

        questions.add(new Question(8, "What is the range of port numbers?",
                Arrays.asList("0-1023", "1024-49151", "0-65535", "1-1000"), 2, "Networking", 10));

        questions.add(new Question(9, "Which keyword is used for thread synchronization?",
                Arrays.asList("async", "synchronized", "lock", "atomic"), 1, "Java", 10));

        questions.add(new Question(10, "What is a socket?",
                Arrays.asList("Hardware component", "Endpoint for network communication",
                        "Database connection", "Memory location"),
                1, "Networking", 10));

        saveQuestions();
        System.out.println("[DataPersistence] Created " + questions.size() + " sample questions.");
    }

    /**
     * Save questions to file
     */
    public synchronized void saveQuestions() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(QUESTIONS_FILE))) {
            writer.write("# Quiz Questions\n");
            writer.write(
                    "# Format: questionId|questionText|option1,option2,option3,option4|correctIndex|category|points\n");
            writer.write("#\n");

            for (Question q : questions) {
                String line = String.format("%d|%s|%s|%d|%s|%d\n",
                        q.getQuestionId(),
                        q.getQuestionText(),
                        String.join(",", q.getOptions()),
                        q.getCorrectAnswerIndex(),
                        q.getCategory(),
                        q.getPoints());
                writer.write(line);
            }
            System.out.println("[DataPersistence] Saved " + questions.size() + " questions to file.");
        } catch (IOException e) {
            System.err.println("[DataPersistence] Error saving questions: " + e.getMessage());
        }
    }

    /**
     * Save quiz result to file
     */
    public synchronized void saveResult(QuizResult result) {
        results.put(result.getStudentId(), result);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESULTS_FILE, true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String line = String.format("%s|%s|%d|%d|%d|%d|%.2f|%s\n",
                    result.getStudentId(),
                    result.getStudentName(),
                    result.getTotalScore(),
                    result.getMaxScore(),
                    result.getCorrectAnswers(),
                    result.getTotalQuestions(),
                    result.getPercentage(),
                    sdf.format(result.getCompletionDate()));
            writer.write(line);
            System.out.println("[DataPersistence] Result saved for student: " + result.getStudentId());
        } catch (IOException e) {
            System.err.println("[DataPersistence] Error saving result: " + e.getMessage());
        }
    }

    /**
     * Load all quiz results from file
     */
    public synchronized Map<String, QuizResult> loadResults() {
        results.clear();
        File file = new File(RESULTS_FILE);

        if (!file.exists()) {
            System.out.println("[DataPersistence] Results file not found.");
            return new HashMap<>(results);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    String[] parts = line.split("\\|");
                    if (parts.length == 8) {
                        QuizResult result = new QuizResult();
                        result.setStudentId(parts[0].trim());
                        result.setStudentName(parts[1].trim());
                        result.setTotalScore(Integer.parseInt(parts[2].trim()));
                        result.setMaxScore(Integer.parseInt(parts[3].trim()));
                        result.setCorrectAnswers(Integer.parseInt(parts[4].trim()));
                        result.setTotalQuestions(Integer.parseInt(parts[5].trim()));

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        result.setCompletionDate(sdf.parse(parts[7].trim()));

                        results.put(result.getStudentId(), result);
                    }
                } catch (Exception e) {
                    System.err.println("[DataPersistence] Error parsing result: " + line);
                }
            }
            System.out.println("[DataPersistence] Loaded " + results.size() + " results from file.");
        } catch (IOException e) {
            System.err.println("[DataPersistence] Error loading results: " + e.getMessage());
        }

        return new HashMap<>(results);
    }

    /**
     * Get all questions
     */
    public synchronized List<Question> getQuestions() {
        return new ArrayList<>(questions);
    }

    /**
     * Get all results
     */
    public synchronized Map<String, QuizResult> getResults() {
        return new HashMap<>(results);
    }

    /**
     * Add a new question
     */
    public synchronized void addQuestion(Question question) {
        questions.add(question);
        saveQuestions();
    }

    /**
     * Update an existing question
     */
    public synchronized void updateQuestion(Question question) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getQuestionId() == question.getQuestionId()) {
                questions.set(i, question);
                saveQuestions();
                return;
            }
        }
    }

    /**
     * Delete a question
     */
    public synchronized void deleteQuestion(int questionId) {
        questions.removeIf(q -> q.getQuestionId() == questionId);
        saveQuestions();
    }
}
