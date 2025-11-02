@echo off
setlocal ENABLEDELAYEDEXPANSION

rem ============================================
rem Build and Run: Virtual Gallery (AWT) - both versions
rem - Compiles non-DB and DB-enabled apps
rem - Downloads JDBC driver if missing
rem - Launches both apps (each in its own window)
rem ============================================

set DRIVER=mysql-connector-j-8.0.33.jar

echo [1/4] Ensuring MySQL JDBC driver is available...
if not exist "%DRIVER%" (
  if exist "download_driver.ps1" (
    echo   Driver not found. Attempting download via PowerShell script...
    powershell -ExecutionPolicy Bypass -File .\download_driver.ps1
  ) else (
    echo   Driver not found and download script is missing.
    echo   You can download it from:
    echo     https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
  )
)

echo.
echo [2/4] Compiling non-DB version...
javac VirtualGallery.java Artwork.java
if errorlevel 1 goto :build_fail

echo.
echo [3/4] Compiling DB-enabled version...
javac -cp ".;%DRIVER%" VirtualGalleryDB.java DatabaseManager.java ArtworkWithId.java Artwork.java
if errorlevel 1 goto :build_fail

echo.
echo [4/4] Launching applications...
rem Launch both apps in separate windows so they don't block each other
start "VirtualGallery (Non-DB)" cmd /c java VirtualGallery

if exist "%DRIVER%" (
  start "VirtualGallery (DB)" cmd /c java -cp ".;%DRIVER%" VirtualGalleryDB
) else (
  echo   Skipping DB run because %DRIVER% was not found.
)

echo.
echo Done. Windows should open for each app. You can close them normally.
goto :eof

:build_fail
echo.
echo Build failed. Please review the compiler errors above.
exit /b 1
