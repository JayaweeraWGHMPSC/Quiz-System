package models;

import java.io.Serializable;

/**
 * Answer Model Class
 * Represents a student's answer to a question
 * Implements Serializable for network transmission
 */
public class Answer implements Serializable {
    private static final long serialVersionUID = 1L;

    private int questionId;
    private int selectedAnswerIndex;
    private String studentId;
    private long timestamp;

    public Answer() {
        this.timestamp = System.currentTimeMillis();
    }

    public Answer(int questionId, int selectedAnswerIndex, String studentId) {
        this.questionId = questionId;
        this.selectedAnswerIndex = selectedAnswerIndex;
        this.studentId = studentId;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getSelectedAnswerIndex() {
        return selectedAnswerIndex;
    }

    public void setSelectedAnswerIndex(int selectedAnswerIndex) {
        this.selectedAnswerIndex = selectedAnswerIndex;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "questionId=" + questionId +
                ", selectedAnswerIndex=" + selectedAnswerIndex +
                ", studentId='" + studentId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
