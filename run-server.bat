@echo off
REM Run the Quiz Server

echo ========================================
echo  Starting QuizMaster Connect Server
echo ========================================
echo.

REM Check if compiled
if not exist "bin" (
    echo Error: Project not compiled!
    echo Please run compile.bat first.
    pause
    exit /b 1
)

REM Run server
echo Starting server on port 8888...
echo.
java -cp bin server.QuizServer

pause
