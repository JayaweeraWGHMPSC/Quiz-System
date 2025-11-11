@echo off
echo ================================
echo   Admin Portal Launcher
echo ================================
echo.
echo Opening Admin Portal in your default browser...
echo.

start "" "frontend\admin.html"

echo.
echo Admin Portal opened!
echo.
echo You can now:
echo   1. View live dashboard with real-time stats
echo   2. Manage connected students
echo   3. Add/edit/delete questions
echo   4. View quiz results
echo   5. Control quiz sessions
echo.
echo Note: For full functionality with the Java server,
echo       make sure the server is running (run-server.bat)
echo.
pause
