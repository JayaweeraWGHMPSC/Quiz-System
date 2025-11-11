package models;

import java.io.Serializable;

/**
 * Message Model Class
 * Represents communication messages between client and server
 * Implements Serializable for network transmission
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    // Message Types
    public static final String CONNECT = "CONNECT";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String GET_QUESTIONS = "GET_QUESTIONS";
    public static final String SUBMIT_ANSWER = "SUBMIT_ANSWER";
    public static final String GET_RESULT = "GET_RESULT";
    public static final String GET_ALL_RESULTS = "GET_ALL_RESULTS";
    public static final String START_QUIZ = "START_QUIZ";
    public static final String END_QUIZ = "END_QUIZ";
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    private String type;
    private Object data;
    private String message;
    private long timestamp;

    public Message() {
        this.timestamp = System.currentTimeMillis();
    }

    public Message(String type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public Message(String type, Object data, String message) {
        this.type = type;
        this.data = data;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
