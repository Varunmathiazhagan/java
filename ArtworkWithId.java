import java.awt.image.BufferedImage;

/**
 * Extends Artwork class to include database ID
 */
public class ArtworkWithId extends Artwork {
    private int id;
    
    public ArtworkWithId(int id, String title, String artist, String description, BufferedImage image) {
        super(title, artist, description, image);
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
}
