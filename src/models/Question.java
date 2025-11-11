package models;

import java.io.Serializable;
import java.util.List;

/**
 * Question Model Class
 * Represents a quiz question with multiple choice options
 * Implements Serializable for network transmission
 */
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    private int questionId;
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex;
    private String category;
    private int points;

    public Question() {
    }

    public Question(int questionId, String questionText, List<String> options,
            int correctAnswerIndex, String category, int points) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.category = category;
        this.points = points;
    }

    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", questionText='" + questionText + '\'' +
                ", options=" + options +
                ", category='" + category + '\'' +
                ", points=" + points +
                '}';
    }
}
