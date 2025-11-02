import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Virtual Art Gallery with MySQL Database Integration
 */
public class VirtualGalleryDB extends Frame {
    private ArrayList<ArtworkWithId> artworks;
    private DatabaseManager dbManager;
    private int currentIndex = 0;
    private Canvas artCanvas;
    private Panel controlPanel;
    private Panel infoPanel;
    private Panel adminPanel;
    private Panel listPanel;
    private List artworkList;
    private Label titleLabel;
    private Label artistLabel;
    private Label descriptionLabel;
    private Label statusLabel;
    private Button prevButton, nextButton, zoomInButton, zoomOutButton;
    private Button addButton, deleteButton, refreshButton, loadSamplesButton, editButton, clearAllButton, clearSamplesButton;
    private double zoomLevel = 1.0;
    
    public VirtualGalleryDB() {
        super("Virtual Art Gallery - Database Edition");
        
        // Initialize database
        dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        
        // Load artworks from database
        loadArtworksFromDatabase();
        
        // Setup the frame
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 240));
        
        // Create components
        createArtCanvas();
        createInfoPanel();
        createControlPanel();
        createListPanel();
        createAdminPanel();
        
        // Add window listener
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dbManager.close();
                dispose();
                System.exit(0);
            }
        });
        
        // Set frame properties
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Display first artwork if available
        if (artworks.size() > 0) {
            displayCurrentArtwork();
        } else {
            updateStatus("No artworks in database. Click 'Load Samples' to add sample artworks.");
        }
    }
    
    private void createListPanel() {
        listPanel = new Panel();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBackground(new Color(235, 235, 235));
        Label listLabel = new Label("Artworks", Label.CENTER);
        listLabel.setFont(new Font("Arial", Font.BOLD, 12));
        artworkList = new List(20);
        artworkList.addItemListener(e -> {
            int sel = artworkList.getSelectedIndex();
            if (sel >= 0 && sel < artworks.size()) {
                currentIndex = sel;
                displayCurrentArtwork();
            }
        });
        listPanel.add(listLabel, BorderLayout.NORTH);
        listPanel.add(artworkList, BorderLayout.CENTER);
        add(listPanel, BorderLayout.WEST);
        populateArtworkList();
    }
    
    private void populateArtworkList() {
        if (artworkList == null) return;
        artworkList.removeAll();
        for (ArtworkWithId a : artworks) {
            artworkList.add("#" + a.getId() + " - " + a.getTitle());
        }
        if (currentIndex >= 0 && currentIndex < artworks.size()) {
            artworkList.select(currentIndex);
        }
    }
    private void loadArtworksFromDatabase() {
        artworks = dbManager.getAllArtworks();
        if (artworks.size() == 0) {
            currentIndex = -1;
        } else {
            currentIndex = 0;
        }
    }
    
    private void createArtCanvas() {
        artCanvas = new Canvas() {
            public void paint(Graphics g) {
                if (artworks.size() > 0 && currentIndex >= 0) {
                    ArtworkWithId current = artworks.get(currentIndex);
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
                } else {
                    // No artworks available
                    int canvasWidth = getWidth();
                    int canvasHeight = getHeight();
                    g.setColor(new Color(30, 30, 30));
                    g.fillRect(0, 0, canvasWidth, canvasHeight);
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 20));
                    String msg = "No Artworks Available";
                    FontMetrics fm = g.getFontMetrics();
                    int msgWidth = fm.stringWidth(msg);
                    g.drawString(msg, (canvasWidth - msgWidth) / 2, canvasHeight / 2);
                }
            }
        };
        artCanvas.setBackground(new Color(30, 30, 30));
        add(artCanvas, BorderLayout.CENTER);
    }
    
    private void createInfoPanel() {
        infoPanel = new Panel();
        infoPanel.setLayout(new GridLayout(5, 1, 5, 5));
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
        
        statusLabel = new Label("", Label.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        statusLabel.setForeground(new Color(0, 150, 0));
        
        infoPanel.add(heading);
        infoPanel.add(titleLabel);
        infoPanel.add(artistLabel);
        infoPanel.add(descriptionLabel);
        infoPanel.add(statusLabel);
        
        add(infoPanel, BorderLayout.NORTH);
    }
    
    private void createControlPanel() {
        controlPanel = new Panel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(new Color(200, 200, 200));
        
        prevButton = new Button("◄ Previous");
        prevButton.setFont(new Font("Arial", Font.BOLD, 12));
        prevButton.addActionListener(e -> previousArtwork());
        
        nextButton = new Button("Next ►");
        nextButton.setFont(new Font("Arial", Font.BOLD, 12));
        nextButton.addActionListener(e -> nextArtwork());
        
        zoomInButton = new Button("Zoom In (+)");
        zoomInButton.setFont(new Font("Arial", Font.PLAIN, 12));
        zoomInButton.addActionListener(e -> zoomIn());
        
        zoomOutButton = new Button("Zoom Out (-)");
        zoomOutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        zoomOutButton.addActionListener(e -> zoomOut());
        
        controlPanel.add(prevButton);
        controlPanel.add(zoomOutButton);
        controlPanel.add(zoomInButton);
        controlPanel.add(nextButton);
        
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
    
    private void createAdminPanel() {
        adminPanel = new Panel();
        adminPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        adminPanel.setBackground(new Color(220, 220, 220));
        
        Label adminLabel = new Label("Database Operations:", Label.LEFT);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 11));
        
        loadSamplesButton = new Button("Load Samples");
        loadSamplesButton.addActionListener(e -> loadSampleArtworks());
        
    addButton = new Button("Add Artwork");
    addButton.addActionListener(e -> showAddDialog());

    editButton = new Button("Edit Current");
    editButton.addActionListener(e -> showEditDialog());
        
        deleteButton = new Button("Delete Current");
        deleteButton.addActionListener(e -> deleteCurrentArtwork());
        
        refreshButton = new Button("Refresh");
        refreshButton.addActionListener(e -> refreshGallery());
        
        adminPanel.add(adminLabel);
    adminPanel.add(loadSamplesButton);
    adminPanel.add(addButton);
    adminPanel.add(editButton);
        adminPanel.add(deleteButton);
        
    clearSamplesButton = new Button("Clear Samples");
    clearSamplesButton.addActionListener(e -> confirmAndClearSamples());
    clearAllButton = new Button("Clear All");
    clearAllButton.setForeground(new Color(180, 0, 0));
    clearAllButton.addActionListener(e -> confirmAndClearAll());
    adminPanel.add(clearSamplesButton);
    adminPanel.add(clearAllButton);
        
        adminPanel.add(refreshButton);
        
        add(adminPanel, BorderLayout.EAST);
    }
    
    private void loadSampleArtworks() {
        // Create sample artworks
        Artwork[] samples = {
            new Artwork("Abstract Sunset", "Digital Artist 1", 
                "A beautiful abstract representation of sunset colors", 
                createSampleImage(1)),
            new Artwork("Geometric Harmony", "Digital Artist 2", 
                "Modern geometric patterns in vibrant colors", 
                createSampleImage(2)),
            new Artwork("Ocean Dreams", "Digital Artist 3", 
                "Serene ocean waves with gradient blues", 
                createSampleImage(3)),
            new Artwork("Digital Forest", "Digital Artist 4", 
                "Abstract forest with digital textures", 
                createSampleImage(4)),
            new Artwork("Cosmic Journey", "Digital Artist 5", 
                "Space-inspired digital artwork", 
                createSampleImage(5))
        };
        
        int count = 0;
        for (Artwork artwork : samples) {
            int id = dbManager.insertArtwork(artwork);
            if (id > 0) count++;
        }
        
        updateStatus("Loaded " + count + " sample artworks into database");
        refreshGallery();
    }
    
    private void showAddDialog() {
        Dialog dialog = new Dialog(this, "Add New Artwork", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
    dialog.setSize(520, 320);
        dialog.setLocationRelativeTo(this);
        
        Label titleLbl = new Label("Title:");
        TextField titleField = new TextField(20);
        
        Label artistLbl = new Label("Artist:");
        TextField artistField = new TextField(20);
        
        Label descLbl = new Label("Description:");
        TextField descField = new TextField(20);
        
        Label imgLbl = new Label("Image:");
        Label imgPathLbl = new Label("(none)");
        Button chooseImgBtn = new Button("Choose Image...");
        final BufferedImage[] selectedImage = new BufferedImage[1];
        chooseImgBtn.addActionListener(e -> {
            FileDialog fd = new FileDialog(dialog, "Choose Image", FileDialog.LOAD);
            fd.setVisible(true);
            String file = fd.getFile();
            String dir = fd.getDirectory();
            if (file != null && dir != null) {
                File f = new File(dir, file);
                try {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) {
                        selectedImage[0] = img;
                        imgPathLbl.setText(f.getName());
                    } else {
                        imgPathLbl.setText("Unsupported image");
                    }
                } catch (IOException ex) {
                    imgPathLbl.setText("Error loading image");
                }
            }
        });
        Panel imgRow = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        imgRow.add(chooseImgBtn);
        imgRow.add(imgPathLbl);
        
        Label typeLbl = new Label("Or Sample Type (1-5):");
        TextField typeField = new TextField("1", 5);
        
        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        
        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String artist = artistField.getText().trim();
            String desc = descField.getText().trim();
            if (title.isEmpty() || artist.isEmpty()) {
                updateStatus("Title and Artist are required");
                return;
            }
            BufferedImage imageToSave = selectedImage[0];
            if (imageToSave == null) {
                int type = 1;
                try {
                    type = Integer.parseInt(typeField.getText());
                    if (type < 1 || type > 5) type = 1;
                } catch (NumberFormatException ex) {
                    type = 1;
                }
                imageToSave = createSampleImage(type);
            }
            int id = dbManager.insertArtwork(title, artist, desc, imageToSave);
            if (id > 0) {
                updateStatus("Artwork added successfully!");
                refreshGallery();
                dialog.dispose();
            } else {
                updateStatus("Failed to add artwork!");
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(titleLbl);
        dialog.add(titleField);
        dialog.add(artistLbl);
        dialog.add(artistField);
        dialog.add(descLbl);
        dialog.add(descField);
        dialog.add(imgLbl);
        dialog.add(imgRow);
        dialog.add(typeLbl);
        dialog.add(typeField);
        dialog.add(saveBtn);
        dialog.add(cancelBtn);
        
        dialog.setVisible(true);
    }

    private void confirmAndClearSamples() {
        Dialog confirmDialog = new Dialog(this, "Clear Samples", true);
        confirmDialog.setLayout(new FlowLayout());
        confirmDialog.setSize(360, 130);
        confirmDialog.setLocationRelativeTo(this);
        Label msg = new Label("Remove all sample artworks (Digital Artist %)?");
        Button yes = new Button("Yes");
        Button no = new Button("No");
        yes.addActionListener(e -> {
            boolean ok = dbManager.deleteSampleArtworks();
            updateStatus(ok ? "Sample artworks removed" : "Failed to remove samples");
            confirmDialog.dispose();
            refreshGallery();
        });
        no.addActionListener(e -> confirmDialog.dispose());
        confirmDialog.add(msg);
        confirmDialog.add(yes);
        confirmDialog.add(no);
        confirmDialog.setVisible(true);
    }
    
    private void confirmAndClearAll() {
        Dialog confirmDialog = new Dialog(this, "Clear All Artworks", true);
        confirmDialog.setLayout(new FlowLayout());
        confirmDialog.setSize(360, 130);
        confirmDialog.setLocationRelativeTo(this);
        Label msg = new Label("This will delete ALL artworks. Continue?");
        Button yes = new Button("Yes");
        Button no = new Button("No");
        yes.addActionListener(e -> {
            boolean ok = dbManager.deleteAllArtworks();
            updateStatus(ok ? "All artworks removed" : "Failed to clear all artworks");
            confirmDialog.dispose();
            refreshGallery();
        });
        no.addActionListener(e -> confirmDialog.dispose());
        confirmDialog.add(msg);
        confirmDialog.add(yes);
        confirmDialog.add(no);
        confirmDialog.setVisible(true);
    }
    
    private void showEditDialog() {
        if (artworks.size() == 0 || currentIndex < 0) return;
        ArtworkWithId current = artworks.get(currentIndex);
        Dialog dialog = new Dialog(this, "Edit Artwork (ID: " + current.getId() + ")", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(450, 280);
        dialog.setLocationRelativeTo(this);
        
        Label titleLbl = new Label("Title:");
        TextField titleField = new TextField(current.getTitle(), 20);
        
        Label artistLbl = new Label("Artist:");
        TextField artistField = new TextField(current.getArtist(), 20);
        
        Label descLbl = new Label("Description:");
        TextField descField = new TextField(current.getDescription(), 20);
        
        Label imgLbl = new Label("Image:");
        Label imgPathLbl = new Label("(unchanged)");
        Button chooseImgBtn = new Button("Change Image...");
        final BufferedImage[] selectedImage = new BufferedImage[1];
        chooseImgBtn.addActionListener(e -> {
            FileDialog fd = new FileDialog(dialog, "Choose Image", FileDialog.LOAD);
            fd.setVisible(true);
            String file = fd.getFile();
            String dir = fd.getDirectory();
            if (file != null && dir != null) {
                File f = new File(dir, file);
                try {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) {
                        selectedImage[0] = img;
                        imgPathLbl.setText(f.getName());
                    } else {
                        imgPathLbl.setText("Unsupported image");
                    }
                } catch (IOException ex) {
                    imgPathLbl.setText("Error loading image");
                }
            }
        });
        Panel imgRow = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        imgRow.add(chooseImgBtn);
        imgRow.add(imgPathLbl);
        
        Button saveBtn = new Button("Update");
        Button cancelBtn = new Button("Cancel");
        
        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String artist = artistField.getText().trim();
            String desc = descField.getText().trim();
            if (title.isEmpty() || artist.isEmpty()) {
                updateStatus("Title and Artist are required");
                return;
            }
            BufferedImage imageToSave = selectedImage[0] != null ? selectedImage[0] : current.getImage();
            boolean ok = dbManager.updateArtwork(current.getId(), title, artist, desc, imageToSave);
            if (ok) {
                updateStatus("Artwork updated successfully!");
                refreshGallery();
                dialog.dispose();
            } else {
                updateStatus("Failed to update artwork!");
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(titleLbl);
        dialog.add(titleField);
        dialog.add(artistLbl);
        dialog.add(artistField);
        dialog.add(descLbl);
        dialog.add(descField);
        dialog.add(imgLbl);
        dialog.add(imgRow);
        dialog.add(new Label(""));
        dialog.add(new Label(""));
        dialog.add(saveBtn);
        dialog.add(cancelBtn);
        dialog.setVisible(true);
    }
    
    private void deleteCurrentArtwork() {
        if (artworks.size() > 0 && currentIndex >= 0) {
            ArtworkWithId current = artworks.get(currentIndex);
            
            Dialog confirmDialog = new Dialog(this, "Confirm Delete", true);
            confirmDialog.setLayout(new FlowLayout());
            confirmDialog.setSize(300, 120);
            confirmDialog.setLocationRelativeTo(this);
            
            Label msg = new Label("Delete '" + current.getTitle() + "'?");
            Button yesBtn = new Button("Yes");
            Button noBtn = new Button("No");
            
            yesBtn.addActionListener(e -> {
                if (dbManager.deleteArtwork(current.getId())) {
                    updateStatus("Artwork deleted successfully!");
                    refreshGallery();
                } else {
                    updateStatus("Failed to delete artwork!");
                }
                confirmDialog.dispose();
            });
            
            noBtn.addActionListener(e -> confirmDialog.dispose());
            
            confirmDialog.add(msg);
            confirmDialog.add(yesBtn);
            confirmDialog.add(noBtn);
            confirmDialog.setVisible(true);
        }
    }
    
    private void refreshGallery() {
        loadArtworksFromDatabase();
        if (artworks.size() > 0) {
            if (currentIndex >= artworks.size()) {
                currentIndex = artworks.size() - 1;
            }
            displayCurrentArtwork();
        } else {
            currentIndex = -1;
            artCanvas.repaint();
            titleLabel.setText("");
            artistLabel.setText("");
            descriptionLabel.setText("");
        }
        populateArtworkList();
        updateStatus("Gallery refreshed. Total artworks: " + artworks.size());
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
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
                g2d.setColor(Color.WHITE);
                for (int i = 0; i < 100; i++) {
                    int x = (int)(Math.random() * 600);
                    int y = (int)(Math.random() * 400);
                    g2d.fillOval(x, y, 2, 2);
                }
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
    
    private void displayCurrentArtwork() {
        if (artworks.size() > 0 && currentIndex >= 0) {
            ArtworkWithId current = artworks.get(currentIndex);
            titleLabel.setText("Title: " + current.getTitle() + " (ID: " + current.getId() + ")");
            artistLabel.setText("Artist: " + current.getArtist());
            descriptionLabel.setText(current.getDescription());
            
            zoomLevel = 1.0;
            artCanvas.repaint();
            
            prevButton.setEnabled(currentIndex > 0);
            nextButton.setEnabled(currentIndex < artworks.size() - 1);
            deleteButton.setEnabled(true);
            if (artworkList != null && currentIndex < artworkList.getItemCount()) {
                artworkList.select(currentIndex);
            }
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
        new VirtualGalleryDB();
    }
}
