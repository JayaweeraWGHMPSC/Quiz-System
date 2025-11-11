package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * QuizResult Model Class
 * Stores the complete result of a student's quiz attempt
 * Implements Serializable for network transmission
 */
public class QuizResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String studentName;
    private int totalScore;
    private int maxScore;
    private int correctAnswers;
    private int totalQuestions;
    private Date completionDate;
    private long timeTaken; // in milliseconds
    private List<Answer> answers;

    public QuizResult() {
        this.answers = new ArrayList<>();
        this.completionDate = new Date();
    }

    public QuizResult(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.answers = new ArrayList<>();
        this.completionDate = new Date();
        this.totalScore = 0;
        this.maxScore = 0;
        this.correctAnswers = 0;
        this.totalQuestions = 0;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    public double getPercentage() {
        if (maxScore == 0)
            return 0.0;
        return (totalScore * 100.0) / maxScore;
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", totalScore=" + totalScore +
                ", maxScore=" + maxScore +
                ", correctAnswers=" + correctAnswers +
                ", totalQuestions=" + totalQuestions +
                ", percentage=" + String.format("%.2f", getPercentage()) + "%" +
                ", completionDate=" + completionDate +
                '}';
    }
}
