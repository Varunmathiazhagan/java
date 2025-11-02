-- Virtual Art Gallery Database Setup
-- Run this script in MySQL if you want to manually create the database

CREATE DATABASE IF NOT EXISTS art_gallery;

USE art_gallery;

CREATE TABLE IF NOT EXISTS artworks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    description TEXT,
    image_data LONGBLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Optional: Insert some initial data
-- Note: image_data would need to be actual binary data
-- This is just to show the structure

-- Display table structure
DESCRIBE artworks;

-- Sample queries for CRUD operations:

-- CREATE (Insert)
-- INSERT INTO artworks (title, artist, description, image_data) VALUES ('Title', 'Artist', 'Description', ?);

-- READ (Select All)
-- SELECT * FROM artworks;

-- READ (Select One)
-- SELECT * FROM artworks WHERE id = 1;

-- UPDATE
-- UPDATE artworks SET title = 'New Title', artist = 'New Artist' WHERE id = 1;

-- DELETE
-- DELETE FROM artworks WHERE id = 1;

-- Count artworks
-- SELECT COUNT(*) FROM artworks;
