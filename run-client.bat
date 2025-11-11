@echo off
REM Run the Student Client

echo ========================================
echo  Starting QuizMaster Connect Client
echo ========================================
echo.

REM Check if compiled
if not exist "bin" (
    echo Error: Project not compiled!
    echo Please run compile.bat first.
    pause
    exit /b 1
)

REM Run client
java -cp bin client.StudentClient

pause
