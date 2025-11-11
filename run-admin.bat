@echo off
REM Run the Admin Panel

echo ========================================
echo  Starting QuizMaster Connect Admin Panel
echo ========================================
echo.

REM Check if compiled
if not exist "bin" (
    echo Error: Project not compiled!
    echo Please run compile.bat first.
    pause
    exit /b 1
)

REM Run admin panel
java -cp bin admin.AdminPanel

pause
