@echo off
REM Compile all Java files

echo ========================================
echo  Compiling QuizMaster Connect
echo ========================================

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile all Java files with UTF-8 encoding
echo.
echo Compiling model classes...
javac -encoding UTF-8 -d bin src/models/*.java

echo Compiling utility classes...
javac -encoding UTF-8 -cp bin -d bin src/utils/*.java

echo Compiling server classes...
javac -encoding UTF-8 -cp bin -d bin src/server/*.java

echo Compiling client classes...
javac -encoding UTF-8 -cp bin -d bin src/client/*.java

echo Compiling admin classes...
javac -encoding UTF-8 -cp bin -d bin src/admin/*.java

echo.
echo ========================================
echo  Compilation Complete!
echo ========================================
echo.

pause
