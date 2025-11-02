@echo off
echo ============================================
echo Virtual Art Gallery - Database Setup
echo ============================================
echo.
echo Step 1: Download MySQL JDBC Driver
echo Please download from: https://dev.mysql.com/downloads/connector/j/
echo Or direct link: https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
echo.
echo Save it to: %CD%
echo Filename should be: mysql-connector-j-8.0.33.jar
echo.
echo Step 2: After downloading, run the application with:
echo java -cp ".;mysql-connector-j-8.0.33.jar" VirtualGalleryDB
echo.
echo ============================================
echo Current database connection:
echo Host: sql12.freesqldatabase.com
echo Database: sql12805282
echo ============================================
pause
