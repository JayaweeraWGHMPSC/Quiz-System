package client;

import models.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

/**
 * StudentClient Class
 * Client application for students to connect to quiz server
 * - Connect to server using IP and port
 * - Receive and display questions
 * - Submit answers
 * - View final results
 */
public class StudentClient {

    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private String studentId;
    private String studentName;
    private boolean isConnected;

    private List<Question> questions;
    private int currentQuestionIndex;

    public StudentClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.isConnected = false;
        this.currentQuestionIndex = 0;
    }

    /**
     * Connect to the quiz server
     */
    public boolean connect(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;

        try {
            System.out.println("Connecting to server at " + serverAddress + ":" + serverPort + "...");

            // Create socket connection
            socket = new Socket(serverAddress, serverPort);

            // Initialize streams
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());

            // Send connection message
            User user = new User(studentId, studentName, "STUDENT");
            Message connectMessage = new Message(Message.CONNECT, user);
            output.writeObject(connectMessage);
            output.flush();

            // Wait for response
            Message response = (Message) input.readObject();

            if (Message.SUCCESS.equals(response.getType())) {
                isConnected = true;
                System.out.println("✓ " + response.getMessage());
                return true;
            } else {
                System.err.println("✗ Connection failed: " + response.getMessage());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }

    /**
     * Request questions from server
     */
    @SuppressWarnings("unchecked")
    public boolean requestQuestions() {
        if (!isConnected) {
            System.err.println("Not connected to server!");
            return false;
        }

        try {
            // Send request for questions
            Message request = new Message(Message.GET_QUESTIONS, null);
            output.writeObject(request);
            output.flush();

            // Receive questions
            Message response = (Message) input.readObject();

            if (Message.SUCCESS.equals(response.getType())) {
                questions = (List<Question>) response.getData();
                System.out.println("✓ Received " + questions.size() + " questions");
                return true;
            } else {
                System.err.println("✗ Failed to get questions: " + response.getMessage());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error requesting questions: " + e.getMessage());
            return false;
        }
    }

    /**
     * Submit an answer to the server
     */
    public boolean submitAnswer(int questionId, int selectedAnswer) {
        if (!isConnected) {
            System.err.println("Not connected to server!");
            return false;
        }

        try {
            // Create answer object
            Answer answer = new Answer(questionId, selectedAnswer, studentId);

            // Send answer to server
            Message message = new Message(Message.SUBMIT_ANSWER, answer);
            output.writeObject(message);
            output.flush();

            // Receive evaluation result
            Message response = (Message) input.readObject();

            if (Message.SUCCESS.equals(response.getType())) {
                Boolean isCorrect = (Boolean) response.getData();
                System.out.println(response.getMessage());
                return isCorrect;
            } else {
                System.err.println("✗ Failed to submit answer: " + response.getMessage());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error submitting answer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get final quiz result
     */
    public QuizResult getResult() {
        if (!isConnected) {
            System.err.println("Not connected to server!");
            return null;
        }

        try {
            // Request final result
            Message request = new Message(Message.GET_RESULT, null);
            output.writeObject(request);
            output.flush();

            // Receive result
            Message response = (Message) input.readObject();

            if (Message.SUCCESS.equals(response.getType())) {
                QuizResult result = (QuizResult) response.getData();
                System.out.println("✓ " + response.getMessage());
                return result;
            } else {
                System.err.println("✗ Failed to get result: " + response.getMessage());
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error getting result: " + e.getMessage());
            return null;
        }
    }

    /**
     * Disconnect from server
     */
    public void disconnect() {
        if (!isConnected) {
            return;
        }

        try {
            // Send disconnect message
            Message message = new Message(Message.DISCONNECT, null);
            output.writeObject(message);
            output.flush();

            // Close connections
            if (output != null)
                output.close();
            if (input != null)
                input.close();
            if (socket != null)
                socket.close();

            isConnected = false;
            System.out.println("Disconnected from server.");

        } catch (Exception e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }

    /**
     * Display a question
     */
    private void displayQuestion(Question question, int questionNumber) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Question " + questionNumber + " of " + questions.size());
        System.out.println("Category: " + question.getCategory() + " | Points: " + question.getPoints());
        System.out.println("=".repeat(60));
        System.out.println(question.getQuestionText());
        System.out.println();

        List<String> options = question.getOptions();
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        System.out.println();
    }

    /**
     * Display final result
     */
    private void displayResult(QuizResult result) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           QUIZ COMPLETED - FINAL RESULTS");
        System.out.println("=".repeat(60));
        System.out.println("Student ID: " + result.getStudentId());
        System.out.println("Name: " + result.getStudentName());
        System.out.println();
        System.out.println("Total Score: " + result.getTotalScore() + " / " + result.getMaxScore());
        System.out.println("Correct Answers: " + result.getCorrectAnswers() + " / " + result.getTotalQuestions());
        System.out.println("Percentage: " + String.format("%.2f%%", result.getPercentage()));
        System.out.println();

        double percentage = result.getPercentage();
        String grade;
        if (percentage >= 90)
            grade = "A+ (Excellent!)";
        else if (percentage >= 80)
            grade = "A (Very Good!)";
        else if (percentage >= 70)
            grade = "B (Good!)";
        else if (percentage >= 60)
            grade = "C (Satisfactory)";
        else if (percentage >= 50)
            grade = "D (Pass)";
        else
            grade = "F (Fail)";

        System.out.println("Grade: " + grade);
        System.out.println("Time Taken: " + (result.getTimeTaken() / 1000) + " seconds");
        System.out.println("Completion Date: " + result.getCompletionDate());
        System.out.println("=".repeat(60));
        System.out.println("\nThank you for participating in the quiz!");
    }

    /**
     * Run the quiz in console mode
     */
    public void runQuiz() {
        Scanner scanner = new Scanner(System.in);

        try {
            // Get questions
            if (!requestQuestions()) {
                System.out.println("Failed to start quiz. Exiting...");
                return;
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.println("           QUIZ STARTED");
            System.out.println("=".repeat(60));
            System.out.println("Total Questions: " + questions.size());
            System.out.println("Instructions: Enter the number (1-4) of your answer choice.");
            System.out.println("=".repeat(60));

            // Go through each question
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                displayQuestion(question, i + 1);

                // Get answer from user
                int answer = -1;
                boolean validInput = false;

                while (!validInput) {
                    System.out.print("Your answer (1-" + question.getOptions().size() + "): ");
                    try {
                        answer = scanner.nextInt() - 1;
                        if (answer >= 0 && answer < question.getOptions().size()) {
                            validInput = true;
                        } else {
                            System.out.println("Invalid input! Please enter a number between 1 and "
                                    + question.getOptions().size());
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input! Please enter a number.");
                        scanner.nextLine(); // Clear buffer
                    }
                }

                // Submit answer
                submitAnswer(question.getQuestionId(), answer);

                // Small delay for better UX
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.println("All questions completed! Calculating final score...");
            System.out.println("=".repeat(60));

            // Get and display final result
            QuizResult result = getResult();
            if (result != null) {
                displayResult(result);
            }

        } finally {
            disconnect();
        }
    }

    /**
     * Main method to run client
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     QuizMaster Connect - Student Client        ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        // Get server details
        System.out.print("Enter server IP address (default: localhost): ");
        String serverAddress = scanner.nextLine().trim();
        if (serverAddress.isEmpty()) {
            serverAddress = "localhost";
        }

        System.out.print("Enter server port (default: 8888): ");
        String portStr = scanner.nextLine().trim();
        int port = 8888;
        if (!portStr.isEmpty()) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port. Using default: 8888");
            }
        }

        // Get student details
        System.out.print("Enter your Student ID: ");
        String studentId = scanner.nextLine().trim();

        System.out.print("Enter your Name: ");
        String studentName = scanner.nextLine().trim();

        // Create and connect client
        StudentClient client = new StudentClient(serverAddress, port);

        if (client.connect(studentId, studentName)) {
            System.out.println("\nConnection successful! Starting quiz...\n");
            client.runQuiz();
        } else {
            System.out.println("\nFailed to connect to server. Please check the server address and port.");
        }

        scanner.close();
    }
}
