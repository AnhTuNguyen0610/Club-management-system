@echo off
rem Bien dich va chay chuong trinh tren Windows.
rem   run.bat             -> giao dien do hoa (Swing)
rem   run.bat --console   -> ban console cu
chcp 65001 >nul
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -cp "lib/*" -sourcepath src -d out src\clubmanagement\Main.java
if errorlevel 1 (
    echo.
    echo Bien dich that bai. Kiem tra lai loi phia tren.
    pause
    exit /b 1
)
java -Dfile.encoding=UTF-8 -cp "out;lib/*" clubmanagement.Main %*
