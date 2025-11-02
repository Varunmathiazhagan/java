# Virtual Art Gallery - MySQL Database Integration

## Prerequisites

1. **MySQL Server** - Install MySQL Server (version 5.7 or higher)
   - Download from: https://dev.mysql.com/downloads/mysql/
   - During installation, note your root password

2. **MySQL JDBC Driver** - Download MySQL Connector/J
   - Download from: https://dev.mysql.com/downloads/connector/j/
   - Or use Maven/Gradle (recommended)

## Setup Instructions

### Step 1: Install MySQL JDBC Driver

#### Option A: Manual Installation
1. Download `mysql-connector-java-8.x.x.jar`
2. Place it in your project directory or add to CLASSPATH

#### Option B: Using Command Line
```bash
# For Windows, add to CLASSPATH:
set CLASSPATH=%CLASSPATH%;path\to\mysql-connector-java-8.x.x.jar
```

### Step 2: Configure Database Connection

Edit `DatabaseManager.java` and update these lines:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/art_gallery";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password_here"; // Change this!
```

### Step 3: Database Setup

The application will automatically create the database and table on first run, but you can also manually set it up:

```sql
mysql -u root -p
source database_setup.sql
```

Or the application creates it automatically when you run `VirtualGalleryDB`.

### Step 4: Compile and Run

```bash
# Compile with MySQL driver in classpath
javac -cp ".;mysql-connector-java-8.x.x.jar" VirtualGalleryDB.java DatabaseManager.java ArtworkWithId.java Artwork.java

# Run with MySQL driver in classpath
java -cp ".;mysql-connector-java-8.x.x.jar" VirtualGalleryDB
```

**Note for Linux/Mac:** Use `:` instead of `;` in classpath:
```bash
javac -cp ".:mysql-connector-java-8.x.x.jar" VirtualGalleryDB.java DatabaseManager.java ArtworkWithId.java Artwork.java
java -cp ".:mysql-connector-java-8.x.x.jar" VirtualGalleryDB
```

## Features - CRUD Operations

### CREATE
- Click **"Add Artwork"** button to add new artwork
- Click **"Load Samples"** to populate with 5 sample artworks

### READ
- Artworks are automatically loaded from database on startup
- Click **"Refresh"** to reload from database
- Navigate with Previous/Next buttons

### UPDATE
- (Can be extended - currently not implemented in UI)
- Update via `DatabaseManager.updateArtwork(id, artwork)`

### DELETE
- Click **"Delete Current"** to remove the currently displayed artwork
- Confirms before deletion

## Database Structure

### Table: artworks
- `id` - INT AUTO_INCREMENT PRIMARY KEY
- `title` - VARCHAR(255) - Artwork title
- `artist` - VARCHAR(255) - Artist name
- `description` - TEXT - Artwork description
- `image_data` - LONGBLOB - Image stored as binary data
- `created_at` - TIMESTAMP - Auto-generated creation time
- `updated_at` - TIMESTAMP - Auto-updated modification time

## Troubleshooting

### Error: ClassNotFoundException - com.mysql.cj.jdbc.Driver
**Solution:** MySQL JDBC driver not in classpath. Add it to compilation and runtime classpath.

### Error: Access denied for user 'root'@'localhost'
**Solution:** Check your MySQL password in `DatabaseManager.java`

### Error: Unknown database 'art_gallery'
**Solution:** The app creates this automatically. Ensure MySQL is running and you have CREATE DATABASE privileges.

### Error: Communications link failure
**Solution:** 
- Ensure MySQL server is running
- Check the port (default 3306)
- Verify firewall settings

## Manual Database Access

```bash
# Login to MySQL
mysql -u root -p

# Use the database
USE art_gallery;

# View all artworks (without image data)
SELECT id, title, artist, description, created_at FROM artworks;

# Count artworks
SELECT COUNT(*) FROM artworks;

# Delete all artworks
TRUNCATE TABLE artworks;
```

## Files

- `VirtualGalleryDB.java` - Main application with database integration
- `DatabaseManager.java` - Handles all database CRUD operations
- `ArtworkWithId.java` - Artwork model with database ID
- `Artwork.java` - Base artwork model
- `database_setup.sql` - SQL script for manual setup

## Advanced Usage

### Loading Images from Files

Modify the add dialog or create a file chooser to load actual image files:

```java
BufferedImage img = ImageIO.read(new File("path/to/image.jpg"));
Artwork artwork = new Artwork("Title", "Artist", "Description", img);
dbManager.insertArtwork(artwork);
```

### Batch Import

Create a method to import multiple images from a directory and store in the database.

## Performance Notes

- Images are stored as BLOB in database (can be large)
- Consider storing file paths instead for better performance with many large images
- Add pagination for galleries with hundreds of artworks
- Consider adding image thumbnails for faster loading

Enjoy your database-powered virtual art gallery! 🎨
