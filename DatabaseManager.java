import java.sql.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;

/**
 * Handles all database operations for the Virtual Art Gallery
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12805282";
    private static final String DB_USER = "sql12805282";
    private static final String DB_PASSWORD = "VnhJ9yyexK";
    
    private Connection connection;
    
    public DatabaseManager() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }
    
    /**
     * Establish connection to the database
     */
    public boolean connect() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to database successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to connect to database!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Create the database and table if they don't exist
     */
    public void initializeDatabase() {
        try {
            // Connect to the remote database directly (database already exists)
            if (connect()) {
                // Create artworks table (compatible with older MySQL versions)
                String createTableSQL = "CREATE TABLE IF NOT EXISTS artworks (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "artist VARCHAR(255) NOT NULL, " +
                    "description TEXT, " +
                    "image_data LONGBLOB, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
                
                Statement createStmt = connection.createStatement();
                createStmt.executeUpdate(createTableSQL);
                System.out.println("Table 'artworks' ready");
                createStmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database!");
            e.printStackTrace();
        }
    }
    
    /**
     * CREATE - Insert a new artwork into the database
     */
    public int insertArtwork(Artwork artwork) {
        String sql = "INSERT INTO artworks (title, artist, description, image_data) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, artwork.getTitle());
            pstmt.setString(2, artwork.getArtist());
            pstmt.setString(3, artwork.getDescription());
            
            // Convert BufferedImage to byte array
            byte[] imageBytes = imageToBytes(artwork.getImage());
            pstmt.setBytes(4, imageBytes);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated ID
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Artwork inserted successfully with ID: " + id);
                    return id;
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error inserting artwork!");
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * READ - Get all artworks from the database
     */
    public ArrayList<ArtworkWithId> getAllArtworks() {
        ArrayList<ArtworkWithId> artworks = new ArrayList<>();
        String sql = "SELECT id, title, artist, description, image_data FROM artworks ORDER BY id";

        if (connection == null) {
            System.err.println("No database connection available - returning empty artwork list");
            return artworks;
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String artist = rs.getString("artist");
                String description = rs.getString("description");
                byte[] imageBytes = rs.getBytes("image_data");

                // Convert byte array to BufferedImage
                BufferedImage image = bytesToImage(imageBytes);

                ArtworkWithId artwork = new ArtworkWithId(id, title, artist, description, image);
                artworks.add(artwork);
            }

            System.out.println("Retrieved " + artworks.size() + " artworks from database");
        } catch (SQLException | IOException e) {
            System.err.println("Error retrieving artworks!");
            e.printStackTrace();
        }

        return artworks;
    }
    
    /**
     * READ - Get a single artwork by ID
     */
    public ArtworkWithId getArtworkById(int id) {
        String sql = "SELECT id, title, artist, description, image_data FROM artworks WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String title = rs.getString("title");
                String artist = rs.getString("artist");
                String description = rs.getString("description");
                byte[] imageBytes = rs.getBytes("image_data");
                BufferedImage image = bytesToImage(imageBytes);
                
                return new ArtworkWithId(id, title, artist, description, image);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error retrieving artwork by ID!");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * UPDATE - Update an existing artwork
     */
    public boolean updateArtwork(int id, Artwork artwork) {
        String sql = "UPDATE artworks SET title = ?, artist = ?, description = ?, image_data = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, artwork.getTitle());
            pstmt.setString(2, artwork.getArtist());
            pstmt.setString(3, artwork.getDescription());
            
            byte[] imageBytes = imageToBytes(artwork.getImage());
            pstmt.setBytes(4, imageBytes);
            pstmt.setInt(5, id);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Artwork updated successfully (ID: " + id + ")");
                return true;
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error updating artwork!");
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Convenience: Insert artwork from a BufferedImage and fields
     */
    public int insertArtwork(String title, String artist, String description, BufferedImage image) {
        return insertArtwork(new Artwork(title, artist, description, image));
    }
    
    /**
     * Convenience: Insert artwork from an image File
     */
    public int insertArtworkFromFile(String title, String artist, String description, File imageFile) {
        try {
            BufferedImage img = ImageIO.read(imageFile);
            return insertArtwork(title, artist, description, img);
        } catch (IOException e) {
            System.err.println("Error reading image file: " + imageFile);
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Convenience: Update artwork by ID with fields and BufferedImage
     */
    public boolean updateArtwork(int id, String title, String artist, String description, BufferedImage image) {
        return updateArtwork(id, new Artwork(title, artist, description, image));
    }
    
    /**
     * Convenience: Update artwork by ID using an image File
     */
    public boolean updateArtworkFromFile(int id, String title, String artist, String description, File imageFile) {
        try {
            BufferedImage img = ImageIO.read(imageFile);
            return updateArtwork(id, title, artist, description, img);
        } catch (IOException e) {
            System.err.println("Error reading image file for update: " + imageFile);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * DELETE - Delete an artwork by ID
     */
    public boolean deleteArtwork(int id) {
        String sql = "DELETE FROM artworks WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Artwork deleted successfully (ID: " + id + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting artwork!");
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * DELETE ALL - Remove all artworks from the table
     */
    public boolean deleteAllArtworks() {
        String sql = "TRUNCATE TABLE artworks";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("All artworks deleted (table truncated)");
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting all artworks!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * DELETE SAMPLES - Remove artworks added as samples (by artist name pattern)
     */
    public boolean deleteSampleArtworks() {
        String sql = "DELETE FROM artworks WHERE artist LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "Digital Artist %");
            int rows = pstmt.executeUpdate();
            System.out.println("Sample artworks deleted: " + rows);
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting sample artworks!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get total count of artworks
     */
    public int getArtworkCount() {
        String sql = "SELECT COUNT(*) as count FROM artworks";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting artwork count!");
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Convert BufferedImage to byte array
     */
    private byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
    
    /**
     * Convert byte array to BufferedImage
     */
    private BufferedImage bytesToImage(byte[] imageBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        return ImageIO.read(bais);
    }
    
    /**
     * Close the database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection!");
            e.printStackTrace();
        }
    }
}
