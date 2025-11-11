package utils;

import models.Answer;
import models.Question;
import models.QuizResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MEMBER 4: RESULT EVALUATION AND REAL-TIME SCORE UPDATE
 * 
 * QuizEvaluator Class
 * Handles real-time evaluation of quiz answers
 * - Evaluates student answers against correct answers
 * - Updates scores in real-time
 * - Maintains thread-safe score tracking
 * - Generates comprehensive quiz results
 */
public class QuizEvaluator {

    private List<Question> questions;
    private Map<String, QuizResult> activeResults; // studentId -> QuizResult
    private DataPersistence dataPersistence;

    // Singleton instance
    private static QuizEvaluator instance;

    private QuizEvaluator() {
        this.activeResults = new ConcurrentHashMap<>();
        this.dataPersistence = DataPersistence.getInstance();
        this.questions = dataPersistence.loadQuestions();
    }

    public static synchronized QuizEvaluator getInstance() {
        if (instance == null) {
            instance = new QuizEvaluator();
        }
        return instance;
    }

    /**
     * Initialize a new quiz session for a student
     */
    public synchronized void initializeQuizSession(String studentId, String studentName) {
        QuizResult result = new QuizResult(studentId, studentName);
        result.setTotalQuestions(questions.size());

        // Calculate max possible score
        int maxScore = 0;
        for (Question q : questions) {
            maxScore += q.getPoints();
        }
        result.setMaxScore(maxScore);

        activeResults.put(studentId, result);
        System.out.println("[QuizEvaluator] Initialized quiz session for: " + studentName + " (ID: " + studentId + ")");
    }

    /**
     * Evaluate a single answer in real-time
     * Returns true if answer is correct, false otherwise
     */
    public synchronized boolean evaluateAnswer(Answer answer) {
        String studentId = answer.getStudentId();
        QuizResult result = activeResults.get(studentId);

        if (result == null) {
            System.err.println("[QuizEvaluator] No active quiz session for student: " + studentId);
            return false;
        }

        // Find the question
        Question question = findQuestionById(answer.getQuestionId());
        if (question == null) {
            System.err.println("[QuizEvaluator] Question not found: " + answer.getQuestionId());
            return false;
        }

        // Add answer to result
        result.addAnswer(answer);

        // Check if answer is correct
        boolean isCorrect = answer.getSelectedAnswerIndex() == question.getCorrectAnswerIndex();

        if (isCorrect) {
            // Update score in real-time
            result.setCorrectAnswers(result.getCorrectAnswers() + 1);
            result.setTotalScore(result.getTotalScore() + question.getPoints());

            System.out.println(String.format(
                    "[QuizEvaluator] ✓ CORRECT - Student: %s, Question: %d, Score: +%d points (Total: %d/%d)",
                    studentId, answer.getQuestionId(), question.getPoints(),
                    result.getTotalScore(), result.getMaxScore()));
        } else {
            System.out.println(
                    String.format("[QuizEvaluator] ✗ INCORRECT - Student: %s, Question: %d (Selected: %d, Correct: %d)",
                            studentId, answer.getQuestionId(),
                            answer.getSelectedAnswerIndex(), question.getCorrectAnswerIndex()));
        }

        // Notify about real-time score update
        notifyScoreUpdate(studentId, result);

        return isCorrect;
    }

    /**
     * Finalize quiz result when student completes the quiz
     */
    public synchronized QuizResult finalizeQuizResult(String studentId, long startTime) {
        QuizResult result = activeResults.get(studentId);

        if (result == null) {
            System.err.println("[QuizEvaluator] No active quiz session for student: " + studentId);
            return null;
        }

        // Calculate time taken
        long timeTaken = System.currentTimeMillis() - startTime;
        result.setTimeTaken(timeTaken);

        // Save to persistent storage
        dataPersistence.saveResult(result);

        // Remove from active results
        activeResults.remove(studentId);

        System.out.println(
                String.format("[QuizEvaluator] ★ QUIZ COMPLETED - Student: %s, Score: %d/%d (%.2f%%), Time: %d seconds",
                        studentId, result.getTotalScore(), result.getMaxScore(),
                        result.getPercentage(), timeTaken / 1000));

        return result;
    }

    /**
     * Get current result for a student (real-time)
     */
    public synchronized QuizResult getCurrentResult(String studentId) {
        return activeResults.get(studentId);
    }

    /**
     * Get all active quiz sessions
     */
    public synchronized Map<String, QuizResult> getActiveResults() {
        return new ConcurrentHashMap<>(activeResults);
    }

    /**
     * Get all completed results from persistent storage
     */
    public synchronized Map<String, QuizResult> getAllCompletedResults() {
        return dataPersistence.loadResults();
    }

    /**
     * Find question by ID
     */
    private Question findQuestionById(int questionId) {
        for (Question q : questions) {
            if (q.getQuestionId() == questionId) {
                return q;
            }
        }
        return null;
    }

    /**
     * Notify about score update (can be extended to push notifications)
     */
    private void notifyScoreUpdate(String studentId, QuizResult result) {
        // This method can be extended to send real-time notifications
        // to admin panel or connected clients
        System.out.println(String.format("[QuizEvaluator] Real-time update - %s: %d/%d points (%.1f%%)",
                result.getStudentName(), result.getTotalScore(),
                result.getMaxScore(), result.getPercentage()));
    }

    /**
     * Reload questions from persistent storage
     */
    public synchronized void reloadQuestions() {
        this.questions = dataPersistence.loadQuestions();
        System.out.println("[QuizEvaluator] Reloaded " + questions.size() + " questions.");
    }

    /**
     * Get statistics for all completed quizzes
     */
    public synchronized String getStatistics() {
        Map<String, QuizResult> allResults = dataPersistence.loadResults();

        if (allResults.isEmpty()) {
            return "No quiz results available.";
        }

        int totalAttempts = allResults.size();
        double avgScore = 0;
        int highestScore = 0;
        String topStudent = "";

        for (QuizResult result : allResults.values()) {
            avgScore += result.getPercentage();
            if (result.getTotalScore() > highestScore) {
                highestScore = result.getTotalScore();
                topStudent = result.getStudentName();
            }
        }

        avgScore /= totalAttempts;

        StringBuilder stats = new StringBuilder();
        stats.append("\n========== QUIZ STATISTICS ==========\n");
        stats.append(String.format("Total Attempts: %d\n", totalAttempts));
        stats.append(String.format("Average Score: %.2f%%\n", avgScore));
        stats.append(String.format("Top Student: %s (%d points)\n", topStudent, highestScore));
        stats.append(String.format("Active Sessions: %d\n", activeResults.size()));
        stats.append("====================================\n");

        return stats.toString();
    }
}
