@echo off
rem Bien dich va chay bo test (khong anh huong data\club.db).
chcp 65001 >nul
cd /d "%~dp0"
if exist out-test rmdir /s /q out-test
mkdir out-test
dir /s /b src\*.java test\*.java > out-test\sources.txt
javac -encoding UTF-8 -cp "lib/*" -d out-test @out-test\sources.txt
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -cp "out-test;lib/*" clubmanagement.TestRunner
set RESULT=%errorlevel%
rmdir /s /q out-test
exit /b %RESULT%

