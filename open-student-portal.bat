@echo off
echo ================================
echo   Student Portal Launcher
echo ================================
echo.
echo Opening Student Portal in your default browser...
echo.

start "" "frontend\index.html"

echo.
echo Student Portal opened!
echo.
echo Students can now:
echo   1. Login with their Student ID
echo   2. Take the quiz
echo   3. View their results
echo.
echo Note: For full functionality with the Java server,
echo       make sure the server is running (run-server.bat)
echo.
pause
