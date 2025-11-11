# QuizMaster Connect - Quick Start Guide

## 🚀 Getting Started in 3 Steps

### Step 1: Compile the Project
Open PowerShell or Command Prompt in the project directory and run:
```powershell
.\compile.bat
```

### Step 2: Start the Server
```powershell
.\run-server.bat
```
The server will start on port 8888.

### Step 3: Connect a Client
Open a new terminal and run:
```powershell
.\run-client.bat
```

## 📚 What You'll Learn

This project demonstrates 5 key network programming concepts, each implemented by a different team member:

1. **Socket Communication** - TCP/IP client-server connections
2. **Multithreading** - Handling multiple clients simultaneously
3. **Admin Management** - GUI for system control
4. **Real-time Processing** - Immediate answer evaluation
5. **Data Persistence** - File-based storage

## 🎯 Testing Multiple Clients

To test concurrent connections:
1. Start the server once
2. Open multiple terminals
3. Run `run-client.bat` in each terminal
4. Each client can take the quiz independently

## 🔍 Sample Quiz Questions

The system includes 10 sample questions about:
- Network programming (HTTP, TCP, OSI model)
- Java programming (ServerSocket, ObjectInputStream, synchronization)

## 📊 View Results

### Option 1: Admin Panel
```powershell
.\run-admin.bat
```
Navigate to the "Results" tab to see all student scores.

### Option 2: Server Console
Type `stats` in the server console to see quiz statistics.

## 💡 Tips

- Use "localhost" as the server address for local testing
- Default port is 8888
- You can run the server and multiple clients on the same machine
- Check the `data` folder for stored questions and results

## 🐛 Common Issues

**Issue**: "Port already in use"  
**Fix**: Close any existing server instance or change the port in QuizServer.java

**Issue**: "Connection refused"  
**Fix**: Make sure the server is running before starting clients

**Issue**: Compilation errors  
**Fix**: Ensure you have JDK 8 or higher installed

## 📖 Full Documentation

See README.md for complete documentation including:
- Detailed architecture
- Network programming concepts
- Code examples
- Team member contributions
- Future enhancements

---

Happy Learning! 🎓
