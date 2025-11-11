// QuizMaster Connect - Frontend JavaScript
// Note: This is a demonstration frontend. In a real implementation,
// you would need a WebSocket or HTTP bridge to connect to the Java server.

// Application State
let currentQuestion = 0;
let questions = [];
let answers = [];
let selectedAnswer = null;
let studentInfo = {};
let score = 0;
let startTime = null;

// Screen Management
function showScreen(screenId) {
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.remove('active');
    });
    document.getElementById(screenId).classList.add('active');
}

function showMessage(elementId, message, type) {
    const messageEl = document.getElementById(elementId);
    messageEl.textContent = message;
    messageEl.className = `message ${type}`;
    messageEl.style.display = 'block';
}

// Login Form Handler
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    studentInfo = {
        serverAddress: document.getElementById('serverAddress').value,
        serverPort: document.getElementById('serverPort').value,
        studentId: document.getElementById('studentId').value,
        studentName: document.getElementById('studentName').value
    };
    
    // Simulate connection (in real implementation, connect to Java server)
    showMessage('loginMessage', 'Connecting to server...', 'success');
    
    setTimeout(() => {
        // Load sample questions for demo
        loadSampleQuestions();
        startQuiz();
    }, 1000);
});

// Load Sample Questions (Demo)
function loadSampleQuestions() {
    questions = [
        {
            questionId: 1,
            questionText: "What is the default port for HTTP?",
            options: ["80", "443", "8080", "3306"],
            correctAnswerIndex: 0,
            category: "Networking",
            points: 10
        },
        {
            questionId: 2,
            questionText: "Which Java class is used for server-side socket programming?",
            options: ["Socket", "ServerSocket", "DatagramSocket", "MulticastSocket"],
            correctAnswerIndex: 1,
            category: "Java",
            points: 10
        },
        {
            questionId: 3,
            questionText: "What does TCP stand for?",
            options: ["Transfer Control Protocol", "Transmission Control Protocol", 
                     "Transport Communication Protocol", "Technical Control Protocol"],
            correctAnswerIndex: 1,
            category: "Networking",
            points: 10
        },
        {
            questionId: 4,
            questionText: "Which layer of OSI model handles routing?",
            options: ["Physical", "Data Link", "Network", "Transport"],
            correctAnswerIndex: 2,
            category: "Networking",
            points: 10
        },
        {
            questionId: 5,
            questionText: "What is the purpose of ObjectInputStream in Java?",
            options: ["Read text data", "Serialize objects", "Deserialize objects", "Write binary data"],
            correctAnswerIndex: 2,
            category: "Java",
            points: 10
        },
        {
            questionId: 6,
            questionText: "Which method is used to accept client connections?",
            options: ["connect()", "accept()", "listen()", "bind()"],
            correctAnswerIndex: 1,
            category: "Java",
            points: 10
        },
        {
            questionId: 7,
            questionText: "What is multithreading?",
            options: ["Running multiple programs", "Running multiple threads simultaneously", 
                     "Running multiple servers", "Running multiple clients"],
            correctAnswerIndex: 1,
            category: "Java",
            points: 10
        },
        {
            questionId: 8,
            questionText: "What is the range of port numbers?",
            options: ["0-1023", "1024-49151", "0-65535", "1-1000"],
            correctAnswerIndex: 2,
            category: "Networking",
            points: 10
        },
        {
            questionId: 9,
            questionText: "Which keyword is used for thread synchronization?",
            options: ["async", "synchronized", "lock", "atomic"],
            correctAnswerIndex: 1,
            category: "Java",
            points: 10
        },
        {
            questionId: 10,
            questionText: "What is a socket?",
            options: ["Hardware component", "Endpoint for network communication", 
                     "Database connection", "Memory location"],
            correctAnswerIndex: 1,
            category: "Networking",
            points: 10
        }
    ];
}

// Start Quiz
function startQuiz() {
    showScreen('quizScreen');
    startTime = Date.now();
    currentQuestion = 0;
    answers = [];
    score = 0;
    
    document.getElementById('studentInfo').textContent = 
        `Welcome, ${studentInfo.studentName} (${studentInfo.studentId})`;
    
    loadQuestion();
}

// Load Question
function loadQuestion() {
    const question = questions[currentQuestion];
    selectedAnswer = null;
    
    // Update header
    document.getElementById('questionCounter').textContent = 
        `Question ${currentQuestion + 1} of ${questions.length}`;
    document.getElementById('currentScore').textContent = 
        `Score: ${score}/${questions.length * 10}`;
    
    // Update question
    document.getElementById('questionCategory').textContent = question.category;
    document.getElementById('questionPoints').textContent = `${question.points} points`;
    document.getElementById('questionText').textContent = question.questionText;
    
    // Load options
    const optionsContainer = document.getElementById('optionsContainer');
    optionsContainer.innerHTML = '';
    
    question.options.forEach((option, index) => {
        const optionDiv = document.createElement('div');
        optionDiv.className = 'option';
        optionDiv.innerHTML = `
            <span class="option-number">${index + 1}</span>
            <span class="option-text">${option}</span>
        `;
        optionDiv.addEventListener('click', () => selectOption(index));
        optionsContainer.appendChild(optionDiv);
    });
    
    // Update buttons
    document.getElementById('prevButton').disabled = currentQuestion === 0;
    document.getElementById('nextButton').disabled = true;
    document.getElementById('submitButton').disabled = false;
    
    // Clear message
    document.getElementById('quizMessage').style.display = 'none';
}

// Select Option
function selectOption(index) {
    selectedAnswer = index;
    
    // Update UI
    document.querySelectorAll('.option').forEach((opt, i) => {
        if (i === index) {
            opt.classList.add('selected');
        } else {
            opt.classList.remove('selected');
        }
    });
}

// Submit Answer
document.getElementById('submitButton').addEventListener('click', () => {
    if (selectedAnswer === null) {
        showMessage('quizMessage', 'Please select an answer!', 'error');
        return;
    }
    
    const question = questions[currentQuestion];
    const isCorrect = selectedAnswer === question.correctAnswerIndex;
    
    // Store answer
    answers.push({
        questionId: question.questionId,
        selectedAnswer: selectedAnswer,
        isCorrect: isCorrect
    });
    
    // Update score
    if (isCorrect) {
        score += question.points;
        showMessage('quizMessage', `✓ Correct! +${question.points} points`, 'success');
    } else {
        showMessage('quizMessage', `✗ Incorrect. The correct answer is: ${question.options[question.correctAnswerIndex]}`, 'error');
    }
    
    // Update buttons
    document.getElementById('submitButton').disabled = true;
    
    if (currentQuestion < questions.length - 1) {
        document.getElementById('nextButton').disabled = false;
    } else {
        // Last question - show finish button
        document.getElementById('nextButton').textContent = 'Finish Quiz';
        document.getElementById('nextButton').disabled = false;
    }
});

// Next Button
document.getElementById('nextButton').addEventListener('click', () => {
    if (currentQuestion < questions.length - 1) {
        currentQuestion++;
        loadQuestion();
    } else {
        // Finish quiz
        showResults();
    }
});

// Previous Button
document.getElementById('prevButton').addEventListener('click', () => {
    if (currentQuestion > 0) {
        currentQuestion--;
        loadQuestion();
    }
});

// Show Results
function showResults() {
    const timeTaken = Math.floor((Date.now() - startTime) / 1000);
    const correctAnswers = answers.filter(a => a.isCorrect).length;
    const totalQuestions = questions.length;
    const maxScore = totalQuestions * 10;
    const percentage = (score / maxScore * 100).toFixed(2);
    
    // Calculate grade
    let grade;
    if (percentage >= 90) grade = "A+ (Excellent!)";
    else if (percentage >= 80) grade = "A (Very Good!)";
    else if (percentage >= 70) grade = "B (Good!)";
    else if (percentage >= 60) grade = "C (Satisfactory)";
    else if (percentage >= 50) grade = "D (Pass)";
    else grade = "F (Fail)";
    
    // Display results
    document.getElementById('resultStudent').textContent = 
        `${studentInfo.studentName} (${studentInfo.studentId})`;
    document.getElementById('resultScore').textContent = `${score} / ${maxScore}`;
    document.getElementById('resultCorrect').textContent = `${correctAnswers} / ${totalQuestions}`;
    document.getElementById('resultPercentage').textContent = `${percentage}%`;
    document.getElementById('resultGrade').textContent = grade;
    document.getElementById('resultTime').textContent = `${timeTaken} seconds`;
    
    showScreen('resultsScreen');
}

// Retake Quiz
document.getElementById('retakeButton').addEventListener('click', () => {
    showScreen('loginScreen');
    document.getElementById('loginForm').reset();
    document.getElementById('loginMessage').style.display = 'none';
});

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    showScreen('loginScreen');
});
