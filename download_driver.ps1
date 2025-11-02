# Quick Download Script for MySQL JDBC Driver
# Run this in PowerShell to download the MySQL connector

$url = "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar"
$output = "mysql-connector-j-8.0.33.jar"

Write-Host "Downloading MySQL JDBC Driver..." -ForegroundColor Green
try {
    Invoke-WebRequest -Uri $url -OutFile $output
    Write-Host "Download complete! File saved as: $output" -ForegroundColor Green
    Write-Host ""
    Write-Host "Now you can run:" -ForegroundColor Yellow
    Write-Host "java -cp `".;mysql-connector-j-8.0.33.jar`" VirtualGalleryDB" -ForegroundColor Cyan
} catch {
    Write-Host "Download failed. Please download manually from:" -ForegroundColor Red
    Write-Host $url -ForegroundColor Yellow
}
