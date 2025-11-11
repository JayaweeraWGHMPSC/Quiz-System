package server;

import models.*;
import utils.DataPersistence;
import utils.QuizEvaluator;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MEMBER 1: SOCKET-BASED CLIENT-SERVER COMMUNICATION
 * 
 * QuizServer Class
 * Main server that handles client connections using ServerSocket
 * - Creates ServerSocket and listens for client connections
 * - Accepts incoming connections and creates ClientHandler threads
 * - Manages connected clients
 * - Provides server control (start/stop)
 */
public class QuizServer {

    private static final int DEFAULT_PORT = 8888;
    private int port;
    private ServerSocket serverSocket;
    private AtomicBoolean isRunning;

    // Thread-safe data structures
    private Map<String, ClientHandler> connectedClients;
    private DataPersistence dataPersistence;
    private QuizEvaluator quizEvaluator;

    // Server statistics
    private int totalConnections;

    public QuizServer(int port) {
        this.port = port;
        this.isRunning = new AtomicBoolean(false);
        this.connectedClients = new ConcurrentHashMap<>();
        this.dataPersistence = DataPersistence.getInstance();
        this.quizEvaluator = QuizEvaluator.getInstance();
        this.totalConnections = 0;
    }

    /**
     * Start the quiz server
     */
    public void start() {
        if (isRunning.get()) {
            System.out.println("[QuizServer] Server is already running!");
            return;
        }

        try {
            // Create ServerSocket - Core of Member 1's implementation
            serverSocket = new ServerSocket(port);
            isRunning.set(true);

            System.out.println("╔════════════════════════════════════════════════╗");
            System.out.println("║     QuizMaster Connect Server Started          ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.println("Server is listening on port: " + port);
            System.out.println("Server IP: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Waiting for student connections...\n");

            // Load questions
            dataPersistence.loadQuestions();

            // Accept client connections in a loop
            while (isRunning.get()) {
                try {
                    // Accept incoming connection - Blocking call
                    Socket clientSocket = serverSocket.accept();
                    totalConnections++;

                    // Log connection details
                    String clientIP = clientSocket.getInetAddress().getHostAddress();
                    int clientPort = clientSocket.getPort();

                    System.out.println(String.format("[QuizServer] New connection #%d from %s:%d",
                            totalConnections, clientIP, clientPort));

                    // Create ClientHandler thread for this client (Member 2)
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    Thread clientThread = new Thread(clientHandler);
                    clientThread.start();

                } catch (SocketException e) {
                    if (!isRunning.get()) {
                        System.out.println("[QuizServer] Server stopped accepting connections.");
                    } else {
                        System.err.println("[QuizServer] Socket error: " + e.getMessage());
                    }
                } catch (IOException e) {
                    if (isRunning.get()) {
                        System.err.println("[QuizServer] Error accepting connection: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("[QuizServer] Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stop the quiz server
     */
    public void stop() {
        if (!isRunning.get()) {
            System.out.println("[QuizServer] Server is not running!");
            return;
        }

        System.out.println("\n[QuizServer] Shutting down server...");
        isRunning.set(false);

        // Close all client connections
        for (ClientHandler client : connectedClients.values()) {
            client.disconnect();
        }
        connectedClients.clear();

        // Close server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("[QuizServer] Server stopped successfully.");
        } catch (IOException e) {
            System.err.println("[QuizServer] Error closing server socket: " + e.getMessage());
        }
    }

    /**
     * Register a connected client
     */
    public synchronized void registerClient(String studentId, ClientHandler handler) {
        connectedClients.put(studentId, handler);
        System.out.println(String.format("[QuizServer] Client registered: %s (Total clients: %d)",
                studentId, connectedClients.size()));
    }

    /**
     * Unregister a disconnected client
     */
    public synchronized void unregisterClient(String studentId) {
        connectedClients.remove(studentId);
        System.out.println(String.format("[QuizServer] Client unregistered: %s (Total clients: %d)",
                studentId, connectedClients.size()));
    }

    /**
     * Get list of connected clients
     */
    public synchronized List<String> getConnectedClients() {
        return new ArrayList<>(connectedClients.keySet());
    }

    /**
     * Get server status information
     */
    public String getServerStatus() {
        StringBuilder status = new StringBuilder();
        status.append("\n========== SERVER STATUS ==========\n");
        status.append("Status: ").append(isRunning.get() ? "RUNNING" : "STOPPED").append("\n");
        status.append("Port: ").append(port).append("\n");
        status.append("Total Connections: ").append(totalConnections).append("\n");
        status.append("Active Clients: ").append(connectedClients.size()).append("\n");

        if (!connectedClients.isEmpty()) {
            status.append("\nConnected Students:\n");
            for (String studentId : connectedClients.keySet()) {
                status.append("  - ").append(studentId).append("\n");
            }
        }

        status.append("==================================\n");
        return status.toString();
    }

    /**
     * Broadcast message to all connected clients
     */
    public synchronized void broadcastMessage(Message message) {
        System.out.println("[QuizServer] Broadcasting message to " + connectedClients.size() + " clients");
        for (ClientHandler client : connectedClients.values()) {
            client.sendMessage(message);
        }
    }

    /**
     * Check if server is running
     */
    public boolean isRunning() {
        return isRunning.get();
    }

    /**
     * Get quiz evaluator instance
     */
    public QuizEvaluator getQuizEvaluator() {
        return quizEvaluator;
    }

    /**
     * Get data persistence instance
     */
    public DataPersistence getDataPersistence() {
        return dataPersistence;
    }

    /**
     * Main method to start the server
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        // Check if custom port is provided
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port: " + DEFAULT_PORT);
            }
        }

        final QuizServer server = new QuizServer(port);

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[QuizServer] Shutdown signal received...");
            server.stop();
        }));

        // Start server in a new thread
        Thread serverThread = new Thread(() -> server.start());
        serverThread.start();

        // Server console commands
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\n=== Server Commands ===");
            System.out.println("status  - Show server status");
            System.out.println("clients - List connected clients");
            System.out.println("stats   - Show quiz statistics");
            System.out.println("stop    - Stop the server");
            System.out.println("=======================\n");

            while (server.isRunning()) {
                System.out.print("Server> ");
                String command = scanner.nextLine().trim().toLowerCase();

                switch (command) {
                    case "status":
                        System.out.println(server.getServerStatus());
                        break;

                    case "clients":
                        List<String> clients = server.getConnectedClients();
                        System.out.println("\nConnected Clients: " + clients.size());
                        for (String client : clients) {
                            System.out.println("  - " + client);
                        }
                        System.out.println();
                        break;

                    case "stats":
                        System.out.println(server.getQuizEvaluator().getStatistics());
                        break;

                    case "stop":
                        server.stop();
                        System.exit(0);
                        break;

                    case "help":
                        System.out.println("\n=== Server Commands ===");
                        System.out.println("status  - Show server status");
                        System.out.println("clients - List connected clients");
                        System.out.println("stats   - Show quiz statistics");
                        System.out.println("stop    - Stop the server");
                        System.out.println("=======================\n");
                        break;

                    default:
                        if (!command.isEmpty()) {
                            System.out.println("Unknown command. Type 'help' for available commands.");
                        }
                }
            }
        }
    }
}
