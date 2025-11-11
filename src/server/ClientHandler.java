package server;

import models.*;
import utils.QuizEvaluator;
import utils.DataPersistence;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * MEMBER 2: MULTITHREADING FOR MULTIPLE STUDENTS
 * 
 * ClientHandler Class
 * Handles individual client connections in separate threads
 * - Each client gets its own thread for concurrent processing
 * - Uses ObjectInputStream and ObjectOutputStream for communication
 * - Processes client requests independently
 * - Thread-safe operations with server resources
 */
public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private QuizServer server;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private String studentId;
    private String studentName;
    private boolean isConnected;
    private long quizStartTime;

    public ClientHandler(Socket socket, QuizServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.isConnected = true;
    }

    /**
     * Main thread execution method
     * Each client runs in its own thread for concurrent processing
     */
    @Override
    public void run() {
        try {
            // Initialize streams - ObjectInputStream/ObjectOutputStream for object transfer
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("[ClientHandler] Thread started for client: " +
                    clientSocket.getInetAddress().getHostAddress());

            // Handle client messages in a loop
            while (isConnected) {
                try {
                    // Read message from client
                    Object obj = input.readObject();

                    if (obj instanceof Message) {
                        Message message = (Message) obj;

                        // Process message in this thread
                        handleMessage(message);
                    }

                } catch (ClassNotFoundException e) {
                    System.err.println("[ClientHandler] Unknown object received: " + e.getMessage());
                } catch (EOFException e) {
                    System.out.println("[ClientHandler] Client disconnected: " + studentId);
                    break;
                } catch (IOException e) {
                    if (isConnected) {
                        System.err.println("[ClientHandler] Error reading from client: " + e.getMessage());
                    }
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("[ClientHandler] Error initializing streams: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Handle different types of messages from client
     * Thread-safe message processing
     */
    private synchronized void handleMessage(Message message) {
        System.out.println("[ClientHandler] Processing message: " + message.getType() +
                " from " + (studentId != null ? studentId : "unknown"));

        switch (message.getType()) {
            case Message.CONNECT:
                handleConnect(message);
                break;

            case Message.GET_QUESTIONS:
                handleGetQuestions();
                break;

            case Message.SUBMIT_ANSWER:
                handleSubmitAnswer(message);
                break;

            case Message.GET_RESULT:
                handleGetResult();
                break;

            case Message.DISCONNECT:
                handleDisconnect();
                break;

            default:
                sendErrorMessage("Unknown message type: " + message.getType());
        }
    }

    /**
     * Handle client connection
     */
    private void handleConnect(Message message) {
        try {
            User user = (User) message.getData();
            this.studentId = user.getUserId();
            this.studentName = user.getUsername();
            this.quizStartTime = System.currentTimeMillis();

            // Register with server (thread-safe)
            server.registerClient(studentId, this);

            // Initialize quiz session
            server.getQuizEvaluator().initializeQuizSession(studentId, studentName);

            // Send success response
            Message response = new Message(Message.SUCCESS, null,
                    "Connected successfully! Welcome " + studentName);
            sendMessage(response);

            System.out.println("[ClientHandler] Student connected: " + studentName + " (ID: " + studentId + ")");

        } catch (Exception e) {
            sendErrorMessage("Connection failed: " + e.getMessage());
        }
    }

    /**
     * Handle get questions request
     */
    private void handleGetQuestions() {
        try {
            DataPersistence dataPersistence = server.getDataPersistence();
            List<Question> questions = dataPersistence.getQuestions();

            // Create a copy of questions without correct answers (for security)
            List<Question> clientQuestions = new java.util.ArrayList<>();
            for (Question q : questions) {
                Question clientQ = new Question(
                        q.getQuestionId(),
                        q.getQuestionText(),
                        q.getOptions(),
                        -1, // Hide correct answer from client
                        q.getCategory(),
                        q.getPoints());
                clientQuestions.add(clientQ);
            }

            Message response = new Message(Message.SUCCESS, clientQuestions,
                    "Questions retrieved successfully");
            sendMessage(response);

            System.out.println("[ClientHandler] Sent " + questions.size() + " questions to " + studentId);

        } catch (Exception e) {
            sendErrorMessage("Failed to retrieve questions: " + e.getMessage());
        }
    }

    /**
     * Handle answer submission
     */
    private void handleSubmitAnswer(Message message) {
        try {
            Answer answer = (Answer) message.getData();
            answer.setStudentId(studentId);

            // Evaluate answer (thread-safe)
            QuizEvaluator evaluator = server.getQuizEvaluator();
            boolean isCorrect = evaluator.evaluateAnswer(answer);

            // Get current result
            QuizResult currentResult = evaluator.getCurrentResult(studentId);

            // Send response with evaluation result
            Message response = new Message(Message.SUCCESS, isCorrect,
                    isCorrect ? "Correct answer!" : "Incorrect answer.");

            // Include current score in response
            if (currentResult != null) {
                response.setMessage(String.format("%s Current Score: %d/%d",
                        response.getMessage(),
                        currentResult.getTotalScore(),
                        currentResult.getMaxScore()));
            }

            sendMessage(response);

        } catch (Exception e) {
            sendErrorMessage("Failed to submit answer: " + e.getMessage());
        }
    }

    /**
     * Handle get result request
     */
    private void handleGetResult() {
        try {
            QuizEvaluator evaluator = server.getQuizEvaluator();

            // Finalize quiz result
            QuizResult result = evaluator.finalizeQuizResult(studentId, quizStartTime);

            if (result != null) {
                Message response = new Message(Message.SUCCESS, result,
                        "Quiz completed! Your final score: " + result.getTotalScore() +
                                "/" + result.getMaxScore());
                sendMessage(response);

                System.out.println("[ClientHandler] Sent final result to " + studentId);
            } else {
                sendErrorMessage("Failed to retrieve result");
            }

        } catch (Exception e) {
            sendErrorMessage("Failed to get result: " + e.getMessage());
        }
    }

    /**
     * Handle client disconnect
     */
    private void handleDisconnect() {
        System.out.println("[ClientHandler] Disconnect request from: " + studentId);
        disconnect();
    }

    /**
     * Send message to client
     * Thread-safe method
     */
    public synchronized void sendMessage(Message message) {
        try {
            if (output != null) {
                output.writeObject(message);
                output.flush();
            }
        } catch (IOException e) {
            System.err.println("[ClientHandler] Error sending message to " + studentId + ": " + e.getMessage());
        }
    }

    /**
     * Send error message to client
     */
    private void sendErrorMessage(String errorMsg) {
        Message errorMessage = new Message(Message.ERROR, null, errorMsg);
        sendMessage(errorMessage);
    }

    /**
     * Disconnect and cleanup resources
     */
    public void disconnect() {
        if (!isConnected) {
            return;
        }

        isConnected = false;

        try {
            // Unregister from server
            if (studentId != null) {
                server.unregisterClient(studentId);
            }

            // Close streams
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }

            // Close socket
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }

            System.out.println("[ClientHandler] Client disconnected and cleaned up: " +
                    (studentId != null ? studentId : "unknown"));

        } catch (IOException e) {
            System.err.println("[ClientHandler] Error during disconnect: " + e.getMessage());
        }
    }

    /**
     * Get student ID
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Check if client is connected
     */
    public boolean isConnected() {
        return isConnected;
    }
}
