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
        studentName: document.getElementById('studentName').value,
        studentId: 'STU-' + Date.now() // Auto-generate ID
    };
    
    // Load questions and start quiz
    showMessage('loginMessage', 'Loading quiz...', 'success');
    
    setTimeout(() => {
        loadSampleQuestions();
        startQuiz();
    }, 500);
});

// Load Sample Questions (Demo)
function loadSampleQuestions() {
    // Try to load questions from admin portal's storage
    try {
        const stored = localStorage.getItem('quizmaster_questions');
        if (stored) {
            const adminQuestions = JSON.parse(stored);
            if (adminQuestions && adminQuestions.length > 0) {
                // Convert admin format to student format
                questions = adminQuestions.map(q => ({
                    questionId: q.id,
                    questionText: q.text,
                    options: q.options,
                    correctAnswerIndex: q.correctAnswer,
                    category: 'Quiz',
                    points: q.points
                }));
                return;
            }
        }
    } catch (error) {
        console.log('No questions in storage, using defaults');
    }
    
    // Default questions if none in storage
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
    answers = new Array(questions.length).fill(null);
    score = 0;
    
    document.getElementById('studentInfo').textContent = 
        `Welcome, ${studentInfo.studentName} (${studentInfo.studentId})`;
    
    // Update header stats
    document.getElementById('totalQuestions').textContent = questions.length;
    document.getElementById('answeredCount').textContent = '0';
    
    loadAllQuestions();
}

// Load All Questions
function loadAllQuestions() {
    const container = document.getElementById('allQuestionsContainer');
    container.innerHTML = '';
    
    questions.forEach((question, qIndex) => {
        const questionDiv = document.createElement('div');
        questionDiv.className = 'single-question';
        questionDiv.id = `question-${qIndex}`;
        
        let optionsHTML = '';
        question.options.forEach((option, oIndex) => {
            optionsHTML += `
                <div class="option" data-question="${qIndex}" data-option="${oIndex}">
                    <span class="option-number">${oIndex + 1}</span>
                    <span class="option-text">${option}</span>
                </div>
            `;
        });
        
        questionDiv.innerHTML = `
            <div class="question-number">Question ${qIndex + 1}</div>
            <div class="question-text">${question.questionText}</div>
            <div class="options">
                ${optionsHTML}
            </div>
        `;
        
        container.appendChild(questionDiv);
    });
    
    // Add click handlers to all options
    document.querySelectorAll('.option').forEach(option => {
        option.addEventListener('click', function() {
            const qIndex = parseInt(this.dataset.question);
            const oIndex = parseInt(this.dataset.option);
            selectOption(qIndex, oIndex);
        });
    });
}

// Select Option
function selectOption(questionIndex, optionIndex) {
    // Store the answer
    answers[questionIndex] = optionIndex;
    
    // Update UI for this question
    const questionDiv = document.getElementById(`question-${questionIndex}`);
    questionDiv.querySelectorAll('.option').forEach((opt, i) => {
        if (i === optionIndex) {
            opt.classList.add('selected');
        } else {
            opt.classList.remove('selected');
        }
    });
    
    // Update answered count
    const answeredCount = answers.filter(a => a !== null).length;
    document.getElementById('answeredCount').textContent = answeredCount;
    
    // Show submit button if all questions are answered
    if (answeredCount === questions.length) {
        document.getElementById('submitSection').style.display = 'block';
        // Scroll to submit button
        document.getElementById('submitSection').scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
}

// Submit All Answers
document.getElementById('submitAllButton').addEventListener('click', () => {
    // Check if all questions are answered
    if (answers.some(a => a === null)) {
        showMessage('quizMessage', 'Please answer all questions before submitting!', 'error');
        return;
    }
    
    // Calculate results
    score = 0;
    questions.forEach((question, index) => {
        const isCorrect = answers[index] === question.correctAnswerIndex;
        if (isCorrect) {
            score += question.points;
        }
    });
    
    showResults();
});

// Show Results
function showResults() {
    const timeTaken = Math.floor((Date.now() - startTime) / 1000);
    
    // Count correct answers
    let correctAnswers = 0;
    questions.forEach((question, index) => {
        if (answers[index] === question.correctAnswerIndex) {
            correctAnswers++;
        }
    });
    
    const totalQuestions = questions.length;
    const maxScore = questions.reduce((sum, q) => sum + q.points, 0);
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
    document.getElementById('resultStudent').textContent = studentInfo.studentName;
    document.getElementById('resultScore').textContent = `${score} / ${maxScore}`;
    document.getElementById('resultCorrect').textContent = `${correctAnswers} / ${totalQuestions}`;
    document.getElementById('resultPercentage').textContent = `${percentage}%`;
    document.getElementById('resultGrade').textContent = grade;
    document.getElementById('resultTime').textContent = `${timeTaken} seconds`;
    
    showScreen('resultsScreen');
}

// Try Again
document.getElementById('retakeButton').addEventListener('click', () => {
    showScreen('loginScreen');
    document.getElementById('loginForm').reset();
    document.getElementById('loginMessage').style.display = 'none';
});

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    showScreen('loginScreen');
});
