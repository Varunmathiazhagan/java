import java.awt.image.BufferedImage;

/**
 * Represents a piece of digital artwork in the gallery
 */
public class Artwork {
    private String title;
    private String artist;
    private String description;
    private BufferedImage image;
    
    public Artwork(String title, String artist, String description, BufferedImage image) {
        this.title = title;
        this.artist = artist;
        this.description = description;
        this.image = image;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getArtist() {
        return artist;
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
