# QuizMaster Connect - Multi-User Quiz System

A comprehensive **Java Network Programming** project that implements a multi-user quiz system allowing multiple students (clients) to connect to a central quiz server, attempt quizzes in real-time, and receive evaluated results. Administrators can manage questions and view results through a dedicated admin panel.

## 🎯 Project Overview

**QuizMaster Connect** is a client-server application built with pure Java (no external frameworks) that demonstrates advanced network programming concepts including socket communication, multithreading, object serialization, and concurrent data handling.

### Key Highlights

- ✅ **Pure Java Implementation** - No external frameworks required
- ✅ **Socket-based Communication** - ServerSocket and Socket for client-server connectivity
- ✅ **Multithreading** - Concurrent handling of multiple clients
- ✅ **Object Serialization** - ObjectInputStream/ObjectOutputStream for data transfer
- ✅ **Real-time Score Updates** - Immediate feedback and evaluation
- ✅ **Data Persistence** - File-based storage for questions and results
- ✅ **GUI Admin Panel** - Swing-based interface for question management
- ✅ **Web Frontend** - HTML/CSS interface for students

## ✨ Features

### Admin Panel (Desktop & Web)
- 🚀 Start/stop the quiz server
- 📝 Manage questions (Add, Edit, Delete)
- 👥 Monitor connected clients in real-time
- 📊 View quiz results and statistics
- 📡 **Live Dashboard** - Real-time student performance tracking
- ⚙️ **Quiz Controls** - Start/pause/stop quizzes for all students
- 📈 **Performance Analytics** - View live scores and progress
- 🔔 **Activity Feed** - Monitor all system events in real-time

### Student Client (Desktop & Web)
- 🔌 Connect to server using IP and port
- 📖 Take quiz with multiple choice questions
- ⚡ Receive immediate feedback
- 🏆 View comprehensive results
- 🌐 **Web Portal** - Browser-based quiz interface

### Server
- 🎧 Accept multiple client connections
- 🔄 Handle concurrent requests with multithreading
- ✔️ Real-time answer evaluation
- 💾 Persistent data storage

## 🚀 Installation & Setup

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Windows, macOS, or Linux

### Compilation

**Windows:**
```bash
compile.bat
```

**Linux/macOS:**
```bash
javac -d bin src/models/*.java
javac -cp bin -d bin src/utils/*.java
javac -cp bin -d bin src/server/*.java
javac -cp bin -d bin src/client/*.java
javac -cp bin -d bin src/admin/*.java
```

## 📖 Usage Guide

### 1. Start the Server

**Windows:**
```bash
run-server.bat
```

**Linux/macOS:**
```bash
java -cp bin server.QuizServer
```

### 2. Run Student Client

**Windows:**
```bash
run-client.bat
```

**Linux/macOS:**
```bash
java -cp bin client.StudentClient
```

Follow the prompts to connect and take the quiz.

### 3. Run Admin Panel

**Windows:**
```bash
run-admin.bat
```

**Linux/macOS:**
```bash
java -cp bin admin.AdminPanel
```

### 4. Web Portals

#### Student Portal
**Windows:**
```bash
open-student-portal.bat
```
**Or manually:** Open `frontend/index.html` in a web browser

#### Admin Portal (Live Dashboard)
**Windows:**
```bash
open-admin-portal.bat
```
**Or manually:** Open `frontend/admin.html` in a web browser

**Admin Portal Features:**
- 📊 **Live Dashboard** - Real-time statistics and performance metrics
- 👥 **Connected Students** - Monitor all active students and their progress
- 📝 **Question Bank** - Add, edit, and delete questions with category filtering
- 🏆 **Quiz Results** - View detailed results and export to CSV
- ⚙️ **Quiz Controls** - Start/pause/stop quizzes for all students simultaneously
- 📡 **Activity Feed** - Real-time event logging
- 📈 **Performance Grid** - Live student scores and progress tracking

## 📁 Project Structure

```
Quiz-System/
├── src/
│   ├── models/         # Data models
│   ├── server/         # Server components
│   ├── client/         # Client application
│   ├── admin/          # Admin panel
│   └── utils/          # Utility classes
├── frontend/           # Web interface
├── data/               # Data files (auto-generated)
├── bin/                # Compiled classes
└── *.bat               # Batch scripts
```

## 🛠️ Technologies

- **Java SE** - Socket programming, Multithreading, I/O
- **Java Swing** - Admin GUI
- **HTML/CSS/JavaScript** - Web frontend
- **File I/O** - Data persistence

## 🔧 Network Programming Concepts

1. **ServerSocket & Socket** - Client-server communication
2. **Multithreading** - Concurrent client handling
3. **ObjectInputStream/ObjectOutputStream** - Object serialization
4. **Synchronized methods** - Thread safety
5. **ConcurrentHashMap** - Thread-safe collections
