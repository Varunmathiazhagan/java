import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Swing-based VirtualGallery that integrates with DatabaseManager.
 */
public class VirtualGalleryDBSwing extends JFrame {
    private DatabaseManager dbManager;
    private ArrayList<ArtworkWithId> artworks;
    private int currentIndex = -1;

    private ArtPanel artPanel;
    private DefaultListModel<String> listModel;
    private JList<String> artworkJList;
    private JLabel titleLabel, artistLabel, descLabel, statusLabel;
    private JButton prevButton, nextButton, loadSamplesBtn, addBtn, editBtn, deleteBtn, refreshBtn, clearSamplesBtn, clearAllBtn;
    private double zoomLevel = 1.0;

    public VirtualGalleryDBSwing() {
        super("Virtual Art Gallery - Database (Swing)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 760);
        setLocationRelativeTo(null);

        dbManager = new DatabaseManager();
        dbManager.initializeDatabase();

        buildUI();
        loadArtworksFromDatabase();
        if (artworks.size() > 0) currentIndex = 0;
        displayCurrentArtwork();
    }

    private void buildUI() {
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 246, 250));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(22, 29, 35));
        header.setPreferredSize(new Dimension(0, 68));
        JLabel title = new JLabel("Virtual Art Gallery — Database Edition");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(8, 16, 8, 16));
        header.add(title, BorderLayout.WEST);
        getContentPane().add(header, BorderLayout.NORTH);

        // Left list panel
        listModel = new DefaultListModel<>();
        artworkJList = new JList<>(listModel);
        artworkJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        artworkJList.addListSelectionListener(e -> {
            int sel = artworkJList.getSelectedIndex();
            if (!e.getValueIsAdjusting() && sel >= 0 && sel < artworks.size()) {
                currentIndex = sel;
                displayCurrentArtwork();
            }
        });
        JScrollPane listScroll = new JScrollPane(artworkJList);
        listScroll.setPreferredSize(new Dimension(240, 0));
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(new Color(235, 235, 235));
        left.add(new JLabel("  Artworks"), BorderLayout.NORTH);
        left.add(listScroll, BorderLayout.CENTER);
        getContentPane().add(left, BorderLayout.WEST);

        // Center art panel
        artPanel = new ArtPanel();
        artPanel.setBackground(new Color(24, 28, 32));
        getContentPane().add(artPanel, BorderLayout.CENTER);

        // Right admin panel
        JPanel admin = new JPanel();
        admin.setLayout(new BoxLayout(admin, BoxLayout.Y_AXIS));
        admin.setBorder(new EmptyBorder(10, 10, 10, 10));
        admin.setPreferredSize(new Dimension(260, 0));

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(0, 120, 0));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        admin.add(statusLabel);
        admin.add(Box.createVerticalStrut(8));

        // Helper to style buttons for visibility and consistent layout
        java.util.function.Consumer<JButton> styleBtn = b -> {
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            b.setOpaque(true);
            b.setBackground(new Color(60, 70, 80));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setContentAreaFilled(true);
            b.setBorderPainted(false);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            b.setMargin(new Insets(6, 12, 6, 12));
        };

        loadSamplesBtn = new JButton("Load Samples");
        styleBtn.accept(loadSamplesBtn);
        loadSamplesBtn.addActionListener(e -> loadSampleArtworks());
        admin.add(loadSamplesBtn);
        admin.add(Box.createVerticalStrut(6));

        addBtn = new JButton("Add Artwork...");
        styleBtn.accept(addBtn);
        addBtn.addActionListener(e -> showAddDialog());
        admin.add(addBtn);
        admin.add(Box.createVerticalStrut(6));

        editBtn = new JButton("Edit Current");
        styleBtn.accept(editBtn);
        editBtn.addActionListener(e -> showEditDialog());
        admin.add(editBtn);
        admin.add(Box.createVerticalStrut(6));

        deleteBtn = new JButton("Delete Current");
        styleBtn.accept(deleteBtn);
        deleteBtn.setBackground(new Color(180, 40, 40));
        deleteBtn.addActionListener(e -> deleteCurrentArtwork());
        admin.add(deleteBtn);
        admin.add(Box.createVerticalStrut(12));

        refreshBtn = new JButton("Refresh");
        styleBtn.accept(refreshBtn);
        refreshBtn.addActionListener(e -> refreshGallery());
        admin.add(refreshBtn);
        admin.add(Box.createVerticalStrut(6));

        clearSamplesBtn = new JButton("Clear Samples");
        styleBtn.accept(clearSamplesBtn);
        clearSamplesBtn.addActionListener(e -> confirmAndClearSamples());
        admin.add(clearSamplesBtn);
        admin.add(Box.createVerticalStrut(6));

        clearAllBtn = new JButton("Clear All");
        styleBtn.accept(clearAllBtn);
        clearAllBtn.setBackground(new Color(160, 20, 20));
        clearAllBtn.addActionListener(e -> confirmAndClearAll());
        admin.add(clearAllBtn);

        admin.add(Box.createVerticalGlue());

        // Info at bottom of right panel
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(8, 8, 8, 8));
        titleLabel = new JLabel("");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        artistLabel = new JLabel("");
        descLabel = new JLabel("");
        info.add(titleLabel);
        info.add(artistLabel);
        info.add(descLabel);
        admin.add(info);

        getContentPane().add(admin, BorderLayout.EAST);

        // Bottom controls
        JPanel controls = new JPanel();
        prevButton = new JButton("◄ Previous");
        prevButton.addActionListener(e -> previousArtwork());
        nextButton = new JButton("Next ►");
        nextButton.addActionListener(e -> nextArtwork());
        JButton zoomIn = new JButton("Zoom +");
        zoomIn.addActionListener(e -> { zoomLevel = Math.min(3.0, zoomLevel + 0.15); artPanel.repaint(); });
        JButton zoomOut = new JButton("Zoom -");
        zoomOut.addActionListener(e -> { zoomLevel = Math.max(0.4, zoomLevel - 0.15); artPanel.repaint(); });
        controls.add(prevButton);
        controls.add(zoomOut);
        controls.add(zoomIn);
        controls.add(nextButton);
        getContentPane().add(controls, BorderLayout.SOUTH);

        // Key bindings
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        JRootPane root = getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "prev");
        root.getActionMap().put("prev", new AbstractAction() { public void actionPerformed(ActionEvent e) { previousArtwork(); } });
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "next");
        root.getActionMap().put("next", new AbstractAction() { public void actionPerformed(ActionEvent e) { nextArtwork(); } });
    }

    private void loadArtworksFromDatabase() {
        artworks = dbManager.getAllArtworks();
        populateArtworkList();
        if (artworks.size() > 0 && currentIndex < 0) currentIndex = 0;
        statusLabel.setText("Total artworks: " + artworks.size());
    }

    private void populateArtworkList() {
        listModel.clear();
        if (artworks == null) return;
        for (ArtworkWithId a : artworks) {
            listModel.addElement("#" + a.getId() + " - " + a.getTitle());
        }
        if (currentIndex >= 0 && currentIndex < listModel.size()) artworkJList.setSelectedIndex(currentIndex);
    }

    private void displayCurrentArtwork() {
        if (artworks == null || artworks.size() == 0 || currentIndex < 0) {
            titleLabel.setText(""); artistLabel.setText(""); descLabel.setText("");
            artPanel.repaint();
            updateButtons();
            return;
        }
        ArtworkWithId cur = artworks.get(currentIndex);
        titleLabel.setText(cur.getTitle() + " (ID: " + cur.getId() + ")");
        artistLabel.setText("by " + cur.getArtist());
        descLabel.setText(cur.getDescription());
        if (currentIndex < listModel.size()) artworkJList.setSelectedIndex(currentIndex);
        zoomLevel = 1.0;
        artPanel.repaint();
        updateButtons();
    }

    private void updateButtons() {
        prevButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(artworks != null && currentIndex < artworks.size() - 1);
        deleteBtn.setEnabled(artworks != null && currentIndex >= 0);
        editBtn.setEnabled(artworks != null && currentIndex >= 0);
    }

    private void previousArtwork() { if (currentIndex > 0) { currentIndex--; displayCurrentArtwork(); } }
    private void nextArtwork() { if (artworks != null && currentIndex < artworks.size() - 1) { currentIndex++; displayCurrentArtwork(); } }

    private void refreshGallery() { loadArtworksFromDatabase(); if (artworks.size() == 0) currentIndex = -1; displayCurrentArtwork(); }

    private void loadSampleArtworks() {
        Artwork[] samples = {
            new Artwork("Abstract Sunset", "Digital Artist 1", "A beautiful abstract representation of sunset colors", createSampleImage(1)),
            new Artwork("Geometric Harmony", "Digital Artist 2", "Modern geometric patterns in vibrant colors", createSampleImage(2)),
            new Artwork("Ocean Dreams", "Digital Artist 3", "Serene ocean waves with gradient blues", createSampleImage(3)),
            new Artwork("Digital Forest", "Digital Artist 4", "Abstract forest with digital textures", createSampleImage(4)),
            new Artwork("Cosmic Journey", "Digital Artist 5", "Space-inspired digital artwork", createSampleImage(5))
        };
        int count = 0;
        for (Artwork a : samples) {
            int id = dbManager.insertArtwork(a.getTitle(), a.getArtist(), a.getDescription(), a.getImage());
            if (id > 0) count++;
        }
        statusLabel.setText("Loaded " + count + " sample artworks into database");
        refreshGallery();
    }

    private BufferedImage createSampleImage(int type) {
        // reuse a compact generator similar to VirtualGallery
        BufferedImage img = new BufferedImage(800, 520, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        switch (type) {
            case 1:
                g2d.setPaint(new GradientPaint(0,0,new Color(255,140,0),0,520,new Color(255,69,0)));
                g2d.fillRect(0,0,800,520);
                g2d.setColor(new Color(255,215,0,160)); for (int i=0;i<10;i++) g2d.fillOval(60+i*60,140+i*24,120,120);
                break;
            case 2:
                g2d.setColor(new Color(40,44,52)); g2d.fillRect(0,0,800,520);
                Color[] colors = {Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.GREEN};
                for (int i=0;i<4;i++) { g2d.setColor(new Color(colors[i].getRed(),colors[i].getGreen(),colors[i].getBlue(),160)); g2d.fillPolygon(new int[]{120+i*140,260+i*140,190+i*140},new int[]{420,420,140},3); }
                break;
            case 3:
                g2d.setPaint(new GradientPaint(0,0,new Color(0,191,255),0,520,new Color(0,0,139)));
                g2d.fillRect(0,0,800,520); g2d.setColor(new Color(255,255,255,100)); for (int i=0;i<6;i++) g2d.fillArc(0,60+i*90,800,80,0,180);
                break;
            case 4:
                g2d.setPaint(new GradientPaint(0,0,new Color(34,139,34),0,520,new Color(0,100,0)));
                g2d.fillRect(0,0,800,520); g2d.setColor(new Color(139,69,19)); for (int i=0;i<9;i++) { g2d.fillRect(60+i*80,260,30,320); g2d.setColor(new Color(0,128,0,180)); g2d.fillOval(50+i*80,200,70,90); g2d.setColor(new Color(139,69,19)); }
                break;
            default:
                g2d.setColor(Color.BLACK); g2d.fillRect(0,0,800,520); g2d.setColor(Color.WHITE); for (int i=0;i<160;i++) g2d.fillOval((int)(Math.random()*800),(int)(Math.random()*520),2,2);
                break;
        }
        g2d.dispose();
        return img;
    }

    private void showAddDialog() {
        JTextField title = new JTextField();
        JTextField artist = new JTextField();
        JTextArea desc = new JTextArea(4, 20);
        JButton choose = new JButton("Choose Image...");
        final BufferedImage[] selected = new BufferedImage[1];
        choose.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int res = fc.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try { selected[0] = javax.imageio.ImageIO.read(f); } catch (Exception ex) { selected[0] = null; }
            }
        });
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel fields = new JPanel(new GridLayout(0,1,4,4));
        fields.add(new JLabel("Title:")); fields.add(title);
        fields.add(new JLabel("Artist:")); fields.add(artist);
        fields.add(new JLabel("Description:")); fields.add(new JScrollPane(desc));
        fields.add(choose);
        p.add(fields, BorderLayout.CENTER);
        int ok = JOptionPane.showConfirmDialog(this, p, "Add Artwork", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            String t = title.getText().trim(); String a = artist.getText().trim(); String d = desc.getText().trim();
            if (t.isEmpty() || a.isEmpty()) { JOptionPane.showMessageDialog(this, "Title and Artist required"); return; }
            BufferedImage img = selected[0] != null ? selected[0] : createSampleImage(1);
            int id = dbManager.insertArtwork(t, a, d, img);
            if (id > 0) { statusLabel.setText("Artwork added (ID: " + id + ")"); refreshGallery(); } else statusLabel.setText("Failed to add artwork");
        }
    }

    private void showEditDialog() {
        if (artworks == null || currentIndex < 0) return;
        ArtworkWithId cur = artworks.get(currentIndex);
        JTextField title = new JTextField(cur.getTitle());
        JTextField artist = new JTextField(cur.getArtist());
        JTextArea desc = new JTextArea(cur.getDescription(), 4, 20);
        JButton choose = new JButton("Change Image...");
        final BufferedImage[] selected = new BufferedImage[1];
        choose.addActionListener(e -> { JFileChooser fc = new JFileChooser(); if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { try { selected[0] = javax.imageio.ImageIO.read(fc.getSelectedFile()); } catch (Exception ex) { selected[0] = null; } } });
        JPanel p = new JPanel(new BorderLayout(6,6)); JPanel fields = new JPanel(new GridLayout(0,1,4,4));
        fields.add(new JLabel("Title:")); fields.add(title); fields.add(new JLabel("Artist:")); fields.add(artist); fields.add(new JLabel("Description:")); fields.add(new JScrollPane(desc)); fields.add(choose); p.add(fields, BorderLayout.CENTER);
        int ok = JOptionPane.showConfirmDialog(this, p, "Edit Artwork (ID: " + cur.getId() + ")", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            String t = title.getText().trim(); String a = artist.getText().trim(); String d = desc.getText().trim();
            if (t.isEmpty() || a.isEmpty()) { JOptionPane.showMessageDialog(this, "Title and Artist required"); return; }
            BufferedImage img = selected[0] != null ? selected[0] : cur.getImage();
            boolean okUp = dbManager.updateArtwork(cur.getId(), t, a, d, img);
            statusLabel.setText(okUp ? "Artwork updated" : "Failed to update artwork");
            refreshGallery();
        }
    }

    private void deleteCurrentArtwork() {
        if (artworks == null || currentIndex < 0) return;
        ArtworkWithId cur = artworks.get(currentIndex);
        int res = JOptionPane.showConfirmDialog(this, "Delete '" + cur.getTitle() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            boolean ok = dbManager.deleteArtwork(cur.getId());
            statusLabel.setText(ok ? "Artwork deleted" : "Failed to delete artwork");
            refreshGallery();
        }
    }

    private void confirmAndClearSamples() {
        int res = JOptionPane.showConfirmDialog(this, "Remove all sample artworks (Digital Artist %)?", "Clear Samples", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) { boolean ok = dbManager.deleteSampleArtworks(); statusLabel.setText(ok ? "Sample artworks removed" : "Failed to remove samples"); refreshGallery(); }
    }

    private void confirmAndClearAll() {
        int res = JOptionPane.showConfirmDialog(this, "This will delete ALL artworks. Continue?", "Clear All Artworks", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) { boolean ok = dbManager.deleteAllArtworks(); statusLabel.setText(ok ? "All artworks removed" : "Failed to clear all artworks"); refreshGallery(); }
    }

    private class ArtPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(24, 28, 32)); g2.fillRect(0,0,getWidth(),getHeight());
            if (artworks == null || artworks.size() == 0 || currentIndex < 0) {
                g2.setColor(new Color(200,200,200)); g2.setFont(new Font("Segoe UI", Font.BOLD, 20)); String msg = "No Artworks Available"; int sw = g2.getFontMetrics().stringWidth(msg); g2.drawString(msg, (getWidth()-sw)/2, getHeight()/2); g2.dispose(); return;
            }
            ArtworkWithId cur = artworks.get(currentIndex);
            BufferedImage img = cur.getImage(); if (img == null) { g2.dispose(); return; }
            int iw = (int)(img.getWidth()*zoomLevel), ih = (int)(img.getHeight()*zoomLevel);
            int x = (getWidth()-iw)/2, y = (getHeight()-ih)/2;
            g2.drawImage(img, x, y, iw, ih, null);
            g2.setColor(new Color(255,255,255,200)); g2.drawRect(x-4,y-4,iw+8,ih+8);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new VirtualGalleryDBSwing().setVisible(true));
    }
}
