// Admin Portal JavaScript - Live Dashboard with Real-time Updates

// Configuration
const CONFIG = {
    serverUrl: 'ws://localhost:8888', // WebSocket for real-time updates
    refreshInterval: 5000, // 5 seconds
    soundEnabled: true,
    autoRefresh: true
};

// State Management
const state = {
    students: [],
    questions: [],
    results: [],
    activeQuizzes: 0,
    serverConnected: false,
    startTime: Date.now()
};

// Local Storage Keys
const STORAGE_KEYS = {
    QUESTIONS: 'quizmaster_questions',
    RESULTS: 'quizmaster_results'
};

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeAdmin();
    loadDataFromStorage();
    startAutoRefresh();
    updateServerUptime();
});

// Initialize Admin Portal
function initializeAdmin() {
    console.log('🎓 Initializing QuizMaster Admin Portal...');
    updateServerStatus(true);
    addActivityLog('Admin portal initialized');
    
    // Simulate server connection
    setTimeout(() => {
        updateServerStatus(true);
        addActivityLog('Connected to QuizMaster Server on port 8888');
    }, 1000);
}

// Tab Navigation
function showTab(tabName) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    
    // Remove active class from all nav items
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
    });
    
    // Show selected tab
    document.getElementById(tabName).classList.add('active');
    
    // Add active class to clicked nav item
    event.target.closest('.nav-item').classList.add('active');
    
    // Refresh data for the tab
    switch(tabName) {
        case 'dashboard':
            updateDashboard();
            break;
        case 'students':
            refreshStudents();
            break;
        case 'questions':
            loadQuestions();
            break;
        case 'results':
            refreshResults();
            break;
    }
}

// Update Server Status
function updateServerStatus(connected) {
    state.serverConnected = connected;
    const statusDot = document.querySelector('.status-dot');
    const statusText = document.getElementById('statusText');
    
    if (connected) {
        statusDot.classList.remove('disconnected');
        statusText.textContent = 'Server Online';
    } else {
        statusDot.classList.add('disconnected');
        statusText.textContent = 'Server Offline';
    }
}

// Activity Log
function addActivityLog(message, type = 'info') {
    const feed = document.getElementById('activityFeed');
    const time = new Date().toLocaleTimeString();
    
    const activityItem = document.createElement('div');
    activityItem.className = 'activity-item';
    activityItem.innerHTML = `
        <span class="activity-time">${time}</span>
        <span class="activity-text">${message}</span>
    `;
    
    feed.insertBefore(activityItem, feed.firstChild);
    
    // Keep only last 50 items
    if (feed.children.length > 50) {
        feed.removeChild(feed.lastChild);
    }
    
    // Play sound notification
    if (CONFIG.soundEnabled && type === 'important') {
        playNotificationSound();
    }
}

// Dashboard Updates
function updateDashboard() {
    // Update statistics
    document.getElementById('totalStudents').textContent = state.students.length;
    
    // Update performance grid
    updatePerformanceGrid();
}

function updatePerformanceGrid() {
    const grid = document.getElementById('performanceGrid');
    grid.innerHTML = '';
    
    state.students.forEach(student => {
        const card = document.createElement('div');
        card.className = 'performance-card';
        card.innerHTML = `
            <h4>${student.name}</h4>
            <div class="performance-score">${student.currentScore || 0}</div>
            <div class="performance-progress">${student.progress || '0/0'} questions</div>
        `;
        grid.appendChild(card);
    });
    
    if (state.students.length === 0) {
        grid.innerHTML = '<p style="color: #94a3b8; text-align: center;">No active students</p>';
    }
}

// Students Management
function refreshStudents() {
    const tbody = document.getElementById('studentsTableBody');
    tbody.innerHTML = '';
    
    if (state.students.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="empty-state">No students connected</td></tr>';
        return;
    }
    
    state.students.forEach(student => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${student.id}</td>
            <td>${student.name}</td>
            <td><span class="badge badge-${student.status.toLowerCase().replace(' ', '-')}">${student.status}</span></td>
            <td>${student.connectedAt}</td>
            <td><strong>${student.currentScore || 0}</strong></td>
            <td>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: ${student.progressPercent || 0}%">
                        ${student.progress || '0/0'}
                    </div>
                </div>
            </td>
            <td>
                <button class="btn-icon" onclick="viewStudent('${student.id}')" title="View Details">👁️</button>
                <button class="btn-icon" onclick="kickStudent('${student.id}')" title="Disconnect">🚫</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function viewStudent(studentId) {
    const student = state.students.find(s => s.id === studentId);
    if (student) {
        alert(`Student Details:\n\nID: ${student.id}\nName: ${student.name}\nStatus: ${student.status}\nScore: ${student.currentScore || 0}\nProgress: ${student.progress || '0/0'}`);
    }
}

function kickStudent(studentId) {
    if (confirm('Are you sure you want to disconnect this student?')) {
        state.students = state.students.filter(s => s.id !== studentId);
        refreshStudents();
        addActivityLog(`Student ${studentId} disconnected by admin`, 'important');
        updateDashboard();
    }
}

// Questions Management
function loadQuestions() {
    const grid = document.getElementById('questionsGrid');
    grid.innerHTML = '';
    
    if (state.questions.length === 0) {
        grid.innerHTML = '<p class="empty-state">No questions found</p>';
        return;
    }
    
    state.questions.forEach((question, index) => {
        const card = document.createElement('div');
        card.className = 'question-card';
        card.innerHTML = `
            <div class="question-header">
                <span class="question-number">Question ${index + 1}</span>
                <div class="question-actions">
                    <button class="btn-icon" onclick="editQuestion(${question.id})" title="Edit">✏️</button>
                    <button class="btn-icon" onclick="deleteQuestion(${question.id})" title="Delete">🗑️</button>
                </div>
            </div>
            <div class="question-text">${question.text}</div>
            <div class="question-options">
                ${question.options.map((opt, idx) => `
                    <div class="question-option ${idx === question.correctAnswer ? 'correct' : ''}">
                        ${idx}. ${opt}
                    </div>
                `).join('')}
            </div>
            <div style="margin-top: 0.5rem; color: #64748b; font-size: 0.85rem;">
                Points: ${question.points}
            </div>
        `;
        grid.appendChild(card);
    });
}

function showAddQuestionModal() {
    document.getElementById('addQuestionModal').style.display = 'block';
}

function closeAddQuestionModal() {
    const form = document.getElementById('addQuestionForm');
    document.getElementById('addQuestionModal').style.display = 'none';
    form.reset();
    delete form.dataset.editId;
    
    // Reset modal title and button text
    document.querySelector('#addQuestionModal .modal-header h2').textContent = '➕ Add New Question';
    document.querySelector('#addQuestionForm button[type="submit"]').textContent = 'Add Question';
}

function addQuestion(event) {
    event.preventDefault();
    
    const form = event.target;
    const editId = form.dataset.editId;
    
    const questionData = {
        text: document.getElementById('questionText').value,
        options: [
            document.getElementById('option1').value,
            document.getElementById('option2').value,
            document.getElementById('option3').value,
            document.getElementById('option4').value
        ],
        correctAnswer: parseInt(document.getElementById('correctAnswer').value),
        points: parseInt(document.getElementById('points').value)
    };
    
    if (editId) {
        // Update existing question
        const index = state.questions.findIndex(q => q.id === parseInt(editId));
        if (index !== -1) {
            state.questions[index] = {
                ...state.questions[index],
                ...questionData
            };
            addActivityLog(`Question updated: "${questionData.text.substring(0, 50)}..."`, 'important');
        }
        delete form.dataset.editId;
    } else {
        // Add new question
        const newQuestion = {
            id: state.questions.length > 0 ? Math.max(...state.questions.map(q => q.id)) + 1 : 1,
            ...questionData
        };
        state.questions.push(newQuestion);
        addActivityLog(`New question added: "${questionData.text.substring(0, 50)}..."`, 'important');
    }
    
    saveQuestionsToStorage();
    closeAddQuestionModal();
    loadQuestions();
}

function editQuestion(id) {
    const question = state.questions.find(q => q.id === id);
    if (question) {
        // Populate the form with existing data
        document.getElementById('questionText').value = question.text;
        document.getElementById('option1').value = question.options[0];
        document.getElementById('option2').value = question.options[1];
        document.getElementById('option3').value = question.options[2];
        document.getElementById('option4').value = question.options[3];
        document.getElementById('correctAnswer').value = question.correctAnswer;
        document.getElementById('points').value = question.points;
        
        // Store the ID for updating
        document.getElementById('addQuestionForm').dataset.editId = id;
        
        // Change modal title and button text
        document.querySelector('#addQuestionModal .modal-header h2').textContent = '✏️ Edit Question';
        document.querySelector('#addQuestionForm button[type="submit"]').textContent = 'Update Question';
        
        // Show modal
        document.getElementById('addQuestionModal').style.display = 'block';
    }
}

function deleteQuestion(id) {
    const question = state.questions.find(q => q.id === id);
    if (question && confirm(`Are you sure you want to delete this question?\n\n"${question.text}"`)) {
        state.questions = state.questions.filter(q => q.id !== id);
        addActivityLog(`Question deleted: "${question.text.substring(0, 50)}..."`, 'important');
        saveQuestionsToStorage();
        loadQuestions();
    }
}

// Results Management
function refreshResults() {
    const tbody = document.getElementById('resultsTableBody');
    tbody.innerHTML = '';
    
    if (state.results.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="empty-state">No results available</td></tr>';
        return;
    }
    
    state.results.forEach(result => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${result.studentId}</td>
            <td>${result.studentName}</td>
            <td><strong>${result.score}/${result.maxScore}</strong></td>
            <td><strong style="color: ${result.percentage >= 70 ? '#10b981' : '#ef4444'}">${result.percentage}%</strong></td>
            <td>${result.correctAnswers}</td>
            <td>${result.timeTaken}</td>
            <td>${result.completedAt}</td>
            <td>
                <button class="btn-icon" onclick="viewResult('${result.studentId}')" title="View Details">👁️</button>
                <button class="btn-icon" onclick="downloadResult('${result.studentId}')" title="Download">📥</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function viewResult(studentId) {
    const result = state.results.find(r => r.studentId === studentId);
    if (result) {
        alert(`Quiz Result:\n\nStudent: ${result.studentName}\nScore: ${result.score}/${result.maxScore}\nPercentage: ${result.percentage}%\nCorrect Answers: ${result.correctAnswers}\nTime Taken: ${result.timeTaken}`);
    }
}

function downloadResult(studentId) {
    addActivityLog(`Downloaded result for student ${studentId}`);
    alert('Result downloaded! (This would download a PDF in the real implementation)');
}

function exportResults() {
    const csv = generateCSV();
    downloadCSV(csv, 'quiz-results.csv');
    addActivityLog('Exported all results to CSV');
}

function generateCSV() {
    let csv = 'Student ID,Name,Score,Max Score,Percentage,Correct Answers,Time Taken,Completed At\n';
    state.results.forEach(r => {
        csv += `${r.studentId},${r.studentName},${r.score},${r.maxScore},${r.percentage},${r.correctAnswers},${r.timeTaken},${r.completedAt}\n`;
    });
    return csv;
}

function downloadCSV(content, filename) {
    const blob = new Blob([content], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
}

// Quiz Controls
function startQuizForAll() {
    if (state.students.length === 0) {
        alert('No students connected!');
        return;
    }
    
    if (confirm(`Start quiz for all ${state.students.length} connected students?`)) {
        state.students.forEach(student => {
            student.status = 'Taking Quiz';
            student.progress = '0/10';
            student.progressPercent = 0;
            student.currentScore = 0;
        });
        state.activeQuizzes = state.students.length;
        addActivityLog(`Quiz started for ${state.students.length} students`, 'important');
        updateDashboard();
        refreshStudents();
        simulateQuizProgress();
    }
}

function pauseQuizForAll() {
    if (confirm('Pause all active quizzes?')) {
        addActivityLog('All quizzes paused', 'important');
        alert('All quizzes have been paused.');
    }
}

function stopQuizForAll() {
    if (confirm('Stop all active quizzes? This will end all quizzes immediately.')) {
        state.activeQuizzes = 0;
        state.students.forEach(student => {
            student.status = 'Completed';
        });
        addActivityLog('All quizzes stopped', 'important');
        updateDashboard();
        refreshStudents();
    }
}

// Data Management
function backupData() {
    const backup = {
        students: state.students,
        questions: state.questions,
        results: state.results,
        timestamp: new Date().toISOString()
    };
    
    const json = JSON.stringify(backup, null, 2);
    downloadCSV(json, `quiz-backup-${Date.now()}.json`);
    addActivityLog('Data backup created');
}

function clearResults() {
    if (confirm('Clear all quiz results? This cannot be undone.')) {
        state.results = [];
        refreshResults();
        addActivityLog('All results cleared', 'important');
        updateDashboard();
    }
}

function resetSystem() {
    if (confirm('Reset the entire system? This will clear all data and disconnect all students. This cannot be undone.')) {
        state.students = [];
        state.results = [];
        state.activeQuizzes = 0;
        addActivityLog('System reset performed', 'important');
        updateDashboard();
        refreshStudents();
        refreshResults();
        alert('System has been reset.');
    }
}

// Auto-refresh
function startAutoRefresh() {
    setInterval(() => {
        if (CONFIG.autoRefresh) {
            updateDashboard();
            
            // Simulate real-time updates
            if (state.activeQuizzes > 0) {
                updateStudentProgress();
            }
        }
    }, CONFIG.refreshInterval);
}

function updateStudentProgress() {
    state.students.forEach(student => {
        if (student.status === 'Taking Quiz') {
            // Simulate progress
            const [current, total] = student.progress.split('/').map(Number);
            if (current < total && Math.random() > 0.5) {
                const newCurrent = current + 1;
                student.progress = `${newCurrent}/${total}`;
                student.progressPercent = (newCurrent / total) * 100;
                student.currentScore += Math.floor(Math.random() * 15) + 5;
                
                if (newCurrent === total) {
                    completeStudentQuiz(student);
                }
            }
        }
    });
    
    refreshStudents();
    updatePerformanceGrid();
}

function completeStudentQuiz(student) {
    student.status = 'Completed';
    state.activeQuizzes--;
    
    // Add to results
    const result = {
        studentId: student.id,
        studentName: student.name,
        score: student.currentScore,
        maxScore: 100,
        percentage: Math.min(100, student.currentScore),
        correctAnswers: Math.floor(student.currentScore / 10),
        timeTaken: `${Math.floor(Math.random() * 15) + 5}m`,
        completedAt: new Date().toLocaleTimeString()
    };
    
    state.results.push(result);
    addActivityLog(`${student.name} completed quiz with ${result.percentage}% score`, 'important');
    updateDashboard();
}

function simulateQuizProgress() {
    // This simulates students answering questions over time
    let interval = setInterval(() => {
        if (state.activeQuizzes === 0) {
            clearInterval(interval);
        }
    }, 3000);
}

// Server Uptime
function updateServerUptime() {
    setInterval(() => {
        const uptime = Date.now() - state.startTime;
        const minutes = Math.floor(uptime / 60000);
        const hours = Math.floor(minutes / 60);
        
        if (hours > 0) {
            document.getElementById('serverUptime').textContent = `${hours}h ${minutes % 60}m`;
        } else {
            document.getElementById('serverUptime').textContent = `${minutes}m`;
        }
        
        document.getElementById('totalConnections').textContent = state.students.length + state.results.length;
        document.getElementById('activeThreads').textContent = state.students.length + 1;
    }, 1000);
}

// Notification Sound
function playNotificationSound() {
    // In a real implementation, play a notification sound
    console.log('🔔 Notification sound');
}

// Logout
function logout() {
    if (confirm('Are you sure you want to logout?')) {
        window.location.href = 'index.html';
    }
}

// Local Storage Functions
function saveQuestionsToStorage() {
    try {
        localStorage.setItem(STORAGE_KEYS.QUESTIONS, JSON.stringify(state.questions));
        console.log('Questions saved to local storage');
    } catch (error) {
        console.error('Error saving questions:', error);
        addActivityLog('Error saving questions to storage', 'error');
    }
}

function saveResultsToStorage() {
    try {
        localStorage.setItem(STORAGE_KEYS.RESULTS, JSON.stringify(state.results));
        console.log('Results saved to local storage');
    } catch (error) {
        console.error('Error saving results:', error);
    }
}

function loadQuestionsFromStorage() {
    try {
        const stored = localStorage.getItem(STORAGE_KEYS.QUESTIONS);
        if (stored) {
            state.questions = JSON.parse(stored);
            console.log('Questions loaded from local storage:', state.questions.length);
            return true;
        }
    } catch (error) {
        console.error('Error loading questions:', error);
    }
    return false;
}

function loadResultsFromStorage() {
    try {
        const stored = localStorage.getItem(STORAGE_KEYS.RESULTS);
        if (stored) {
            state.results = JSON.parse(stored);
            console.log('Results loaded from local storage');
            return true;
        }
    } catch (error) {
        console.error('Error loading results:', error);
    }
    return false;
}

function exportQuestionsToFile() {
    const dataStr = JSON.stringify(state.questions, null, 2);
    const blob = new Blob([dataStr], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `quizmaster-questions-${Date.now()}.json`;
    a.click();
    window.URL.revokeObjectURL(url);
    addActivityLog('Questions exported to file');
}

function importQuestionsFromFile(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            try {
                const imported = JSON.parse(e.target.result);
                if (Array.isArray(imported)) {
                    state.questions = imported;
                    saveQuestionsToStorage();
                    loadQuestions();
                    addActivityLog(`${imported.length} questions imported from file`, 'important');
                } else {
                    alert('Invalid file format');
                }
            } catch (error) {
                alert('Error reading file: ' + error.message);
            }
        };
        reader.readAsText(file);
    }
}

// Load Sample Data
function loadSampleData() {
    // Sample students
    state.students = [
        {
            id: 'IT21001',
            name: 'John Doe',
            status: 'Online',
            connectedAt: '10:30 AM',
            currentScore: 0,
            progress: '0/10',
            progressPercent: 0
        },
        {
            id: 'IT21002',
            name: 'Jane Smith',
            status: 'Online',
            connectedAt: '10:32 AM',
            currentScore: 0,
            progress: '0/10',
            progressPercent: 0
        },
        {
            id: 'IT21003',
            name: 'Bob Johnson',
            status: 'Online',
            connectedAt: '10:35 AM',
            currentScore: 0,
            progress: '0/10',
            progressPercent: 0
        }
    ];
    
    // Default sample questions (only if no questions in storage)
    const defaultQuestions = [
        {
            id: 1,
            text: 'What is the default port for HTTP?',
            options: ['80', '443', '8080', '3306'],
            correctAnswer: 0,
            points: 10
        },
        {
            id: 2,
            text: 'Which keyword is used to create a class in Java?',
            options: ['class', 'Class', 'interface', 'object'],
            correctAnswer: 0,
            points: 10
        },
        {
            id: 3,
            text: 'What does SQL stand for?',
            options: ['Structured Query Language', 'Simple Query Language', 'Standard Query Language', 'System Query Language'],
            correctAnswer: 0,
            points: 10
        },
        {
            id: 4,
            text: 'Which protocol is used for secure communication over a network?',
            options: ['HTTP', 'FTP', 'HTTPS', 'SMTP'],
            correctAnswer: 2,
            points: 10
        },
        {
            id: 5,
            text: 'What is the purpose of a Socket in network programming?',
            options: ['Store data', 'Establish connection between client and server', 'Compile code', 'Debug errors'],
            correctAnswer: 1,
            points: 10
        }
    ];
    
    // Only use default questions if storage is empty
    if (state.questions.length === 0) {
        state.questions = defaultQuestions;
        saveQuestionsToStorage();
    }
    
    // Sample results
    const defaultResults = [
        {
            studentId: 'IT21000',
            studentName: 'Alice Brown',
            score: 85,
            maxScore: 100,
            percentage: 85,
            correctAnswers: 8,
            timeTaken: '12m 34s',
            completedAt: '09:45 AM'
        }
    ];
    
    if (state.results.length === 0) {
        state.results = defaultResults;
        saveResultsToStorage();
    }
    
    updateDashboard();
    refreshStudents();
    loadQuestions();
    refreshResults();
}

function loadDataFromStorage() {
    // Load questions from storage or use defaults
    const hasQuestions = loadQuestionsFromStorage();
    const hasResults = loadResultsFromStorage();
    
    // Load sample data (will use stored data if available)
    loadSampleData();
    
    if (hasQuestions) {
        addActivityLog(`Loaded ${state.questions.length} questions from storage`);
    } else {
        addActivityLog(`Initialized with ${state.questions.length} default questions`);
    }
}

// Handle modal clicks
window.onclick = function(event) {
    const modal = document.getElementById('addQuestionModal');
    if (event.target === modal) {
        closeAddQuestionModal();
    }
}
