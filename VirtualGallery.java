import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VirtualGallery extends Frame {
    private ArrayList<Artwork> artworks;
    private int currentIndex = 0;
    private Canvas artCanvas;
    private Panel controlPanel;
    private Panel infoPanel;
    private Label titleLabel;
    private Label artistLabel;
    private Label descriptionLabel;
    private Button prevButton, nextButton, zoomInButton, zoomOutButton;
    private double zoomLevel = 1.0;
    
    public VirtualGallery() {
        super("Virtual Art Gallery");
        
        // Initialize artwork collection
        initializeArtworks();
        
        // Setup the frame
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 240));
        
        // Create components
        createArtCanvas();
        createInfoPanel();
        createControlPanel();
        
        // Add window listener
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
                System.exit(0);
            }
        });
        
        // Set frame properties
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Display first artwork
        displayCurrentArtwork();
    }
    
    private void initializeArtworks() {
        artworks = new ArrayList<>();
        
        // Add sample artworks with generated images
        artworks.add(new Artwork("Abstract Sunset", "Digital Artist 1", 
            "A beautiful abstract representation of sunset colors", 
            createSampleImage(1)));
        
        artworks.add(new Artwork("Geometric Harmony", "Digital Artist 2", 
            "Modern geometric patterns in vibrant colors", 
            createSampleImage(2)));
        
        artworks.add(new Artwork("Ocean Dreams", "Digital Artist 3", 
            "Serene ocean waves with gradient blues", 
            createSampleImage(3)));
        
        artworks.add(new Artwork("Digital Forest", "Digital Artist 4", 
            "Abstract forest with digital textures", 
            createSampleImage(4)));
        
        artworks.add(new Artwork("Cosmic Journey", "Digital Artist 5", 
            "Space-inspired digital artwork", 
            createSampleImage(5)));
    }
    
    private BufferedImage createSampleImage(int type) {
        BufferedImage img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        switch(type) {
            case 1: // Abstract Sunset
                GradientPaint gradient1 = new GradientPaint(0, 0, new Color(255, 140, 0),
                    0, 400, new Color(255, 69, 0));
                g2d.setPaint(gradient1);
                g2d.fillRect(0, 0, 600, 400);
                g2d.setColor(new Color(255, 215, 0, 180));
                for (int i = 0; i < 10; i++) {
                    g2d.fillOval(50 + i * 50, 100 + i * 20, 100, 100);
                }
                break;
                
            case 2: // Geometric Harmony
                g2d.setColor(new Color(50, 50, 50));
                g2d.fillRect(0, 0, 600, 400);
                Color[] colors = {Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.GREEN};
                for (int i = 0; i < 4; i++) {
                    g2d.setColor(new Color(colors[i].getRed(), colors[i].getGreen(), 
                        colors[i].getBlue(), 150));
                    g2d.fillPolygon(
                        new int[]{100 + i * 100, 200 + i * 100, 150 + i * 100},
                        new int[]{300, 300, 100},
                        3
                    );
                }
                break;
                
            case 3: // Ocean Dreams
                GradientPaint gradient2 = new GradientPaint(0, 0, new Color(0, 191, 255),
                    0, 400, new Color(0, 0, 139));
                g2d.setPaint(gradient2);
                g2d.fillRect(0, 0, 600, 400);
                g2d.setColor(new Color(255, 255, 255, 100));
                for (int i = 0; i < 5; i++) {
                    g2d.fillArc(0, 50 + i * 80, 600, 50, 0, 180);
                }
                break;
                
            case 4: // Digital Forest
                GradientPaint gradient3 = new GradientPaint(0, 0, new Color(34, 139, 34),
                    0, 400, new Color(0, 100, 0));
                g2d.setPaint(gradient3);
                g2d.fillRect(0, 0, 600, 400);
                g2d.setColor(new Color(139, 69, 19));
                for (int i = 0; i < 8; i++) {
                    g2d.fillRect(50 + i * 70, 200, 30, 200);
                    g2d.setColor(new Color(0, 128, 0, 180));
                    g2d.fillOval(35 + i * 70, 150, 60, 80);
                    g2d.setColor(new Color(139, 69, 19));
                }
                break;
                
            case 5: // Cosmic Journey
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, 600, 400);
                // Stars
                g2d.setColor(Color.WHITE);
                for (int i = 0; i < 100; i++) {
                    int x = (int)(Math.random() * 600);
                    int y = (int)(Math.random() * 400);
                    g2d.fillOval(x, y, 2, 2);
                }
                // Planets
                GradientPaint planetGrad = new GradientPaint(200, 150, new Color(255, 100, 0),
                    250, 200, new Color(200, 50, 0));
                g2d.setPaint(planetGrad);
                g2d.fillOval(200, 150, 100, 100);
                
                g2d.setPaint(new GradientPaint(400, 250, new Color(100, 100, 255),
                    450, 300, new Color(50, 50, 200)));
                g2d.fillOval(400, 250, 80, 80);
                break;
        }
        
        g2d.dispose();
        return img;
    }
    
    private void createArtCanvas() {
        artCanvas = new Canvas() {
            public void paint(Graphics g) {
                if (artworks.size() > 0) {
                    Artwork current = artworks.get(currentIndex);
                    BufferedImage img = current.getImage();
                    
                    int canvasWidth = getWidth();
                    int canvasHeight = getHeight();
                    
                    // Calculate scaled dimensions
                    int imgWidth = (int)(img.getWidth() * zoomLevel);
                    int imgHeight = (int)(img.getHeight() * zoomLevel);
                    
                    // Center the image
                    int x = (canvasWidth - imgWidth) / 2;
                    int y = (canvasHeight - imgHeight) / 2;
                    
                    // Draw background
                    g.setColor(new Color(30, 30, 30));
                    g.fillRect(0, 0, canvasWidth, canvasHeight);
                    
                    // Draw image
                    g.drawImage(img, x, y, imgWidth, imgHeight, this);
                    
                    // Draw border around image
                    g.setColor(Color.WHITE);
                    ((Graphics2D)g).setStroke(new BasicStroke(2));
                    g.drawRect(x - 2, y - 2, imgWidth + 4, imgHeight + 4);
                }
            }
        };
        artCanvas.setBackground(new Color(30, 30, 30));
        add(artCanvas, BorderLayout.CENTER);
    }
    
    private void createInfoPanel() {
        infoPanel = new Panel();
        infoPanel.setLayout(new GridLayout(4, 1, 5, 5));
        infoPanel.setBackground(new Color(250, 250, 250));
        
        Label heading = new Label("Artwork Information", Label.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 16));
        heading.setForeground(new Color(50, 50, 50));
        
        titleLabel = new Label("", Label.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 100, 200));
        
        artistLabel = new Label("", Label.CENTER);
        artistLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        descriptionLabel = new Label("", Label.CENTER);
        descriptionLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        
        infoPanel.add(heading);
        infoPanel.add(titleLabel);
        infoPanel.add(artistLabel);
        infoPanel.add(descriptionLabel);
        
        add(infoPanel, BorderLayout.NORTH);
    }
    
    private void createControlPanel() {
        controlPanel = new Panel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(new Color(200, 200, 200));
        
        prevButton = new Button("◄ Previous");
        prevButton.setFont(new Font("Arial", Font.BOLD, 12));
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previousArtwork();
            }
        });
        
        nextButton = new Button("Next ►");
        nextButton.setFont(new Font("Arial", Font.BOLD, 12));
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nextArtwork();
            }
        });
        
        zoomInButton = new Button("Zoom In (+)");
        zoomInButton.setFont(new Font("Arial", Font.PLAIN, 12));
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomIn();
            }
        });
        
        zoomOutButton = new Button("Zoom Out (-)");
        zoomOutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomOut();
            }
        });
        
        Label countLabel = new Label("Use arrow keys to navigate", Label.CENTER);
        countLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        
        controlPanel.add(prevButton);
        controlPanel.add(zoomOutButton);
        controlPanel.add(zoomInButton);
        controlPanel.add(nextButton);
        controlPanel.add(countLabel);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        // Add keyboard navigation
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        previousArtwork();
                        break;
                    case KeyEvent.VK_RIGHT:
                        nextArtwork();
                        break;
                    case KeyEvent.VK_PLUS:
                    case KeyEvent.VK_EQUALS:
                        zoomIn();
                        break;
                    case KeyEvent.VK_MINUS:
                        zoomOut();
                        break;
                }
            }
        });
    }
    
    private void displayCurrentArtwork() {
        if (artworks.size() > 0) {
            Artwork current = artworks.get(currentIndex);
            titleLabel.setText("Title: " + current.getTitle());
            artistLabel.setText("Artist: " + current.getArtist());
            descriptionLabel.setText(current.getDescription());
            
            // Reset zoom when changing artwork
            zoomLevel = 1.0;
            artCanvas.repaint();
            
            // Update button states
            prevButton.setEnabled(currentIndex > 0);
            nextButton.setEnabled(currentIndex < artworks.size() - 1);
        }
    }
    
    private void previousArtwork() {
        if (currentIndex > 0) {
            currentIndex--;
            displayCurrentArtwork();
        }
    }
    
    private void nextArtwork() {
        if (currentIndex < artworks.size() - 1) {
            currentIndex++;
            displayCurrentArtwork();
        }
    }
    
    private void zoomIn() {
        if (zoomLevel < 3.0) {
            zoomLevel += 0.2;
            artCanvas.repaint();
        }
    }
    
    private void zoomOut() {
        if (zoomLevel > 0.4) {
            zoomLevel -= 0.2;
            artCanvas.repaint();
        }
    }
    
    public static void main(String[] args) {
        new VirtualGallery();
    }
}
