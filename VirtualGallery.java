import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Modernized Swing-based VirtualGallery (keeps existing Artwork model)
 */
public class VirtualGallery extends JFrame {
    private ArrayList<Artwork> artworks;
    private int currentIndex = 0;
    private ArtPanel artPanel;
    private JPanel infoPanel;
    private JLabel titleLabel;
    private JLabel artistLabel;
    private JLabel descriptionLabel;
    private JButton prevButton, nextButton, zoomInButton, zoomOutButton;
    private double zoomLevel = 1.0;

    public VirtualGallery() {
        super("Virtual Art Gallery");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize data
        initializeArtworks();

        // Build UI
        buildUI();

        setSize(1100, 720);
        setLocationRelativeTo(null);
        setVisible(true);

        displayCurrentArtwork();
    }

    private void buildUI() {
        getContentPane().setBackground(new Color(245, 246, 250));
        setLayout(new BorderLayout(12, 12));

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(22, 29, 35));
        header.setPreferredSize(new Dimension(0, 72));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel h1 = new JLabel("Virtual Art Gallery");
        h1.setForeground(new Color(235, 239, 242));
        h1.setFont(new Font("Segoe UI", Font.BOLD, 22));
        h1.setAlignmentX(CENTER_ALIGNMENT);
        JLabel sub = new JLabel("A minimal, elegant viewing experience");
        sub.setForeground(new Color(170, 180, 190));
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setAlignmentX(CENTER_ALIGNMENT);
        header.add(Box.createVerticalGlue());
        header.add(h1);
        header.add(sub);
        header.add(Box.createVerticalGlue());
        add(header, BorderLayout.NORTH);

        // Center art panel
        artPanel = new ArtPanel();
        artPanel.setBackground(new Color(24, 28, 32));
        add(artPanel, BorderLayout.CENTER);

        // Info + controls at bottom
        JPanel bottom = new JPanel();
        bottom.setLayout(new BorderLayout(10, 10));
        bottom.setOpaque(false);

        createInfoPanel();
        bottom.add(infoPanel, BorderLayout.CENTER);
        createControlPanel();
        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.add(prevButton);
        controls.add(zoomOutButton);
        controls.add(zoomInButton);
        controls.add(nextButton);
        bottom.add(controls, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);

        // Key bindings
        setupKeyBindings();
    }

    private void createInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setOpaque(true);
        infoPanel.setBackground(new Color(250, 250, 252));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(20, 40, 60));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        artistLabel = new JLabel("");
        artistLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        artistLabel.setForeground(new Color(90, 100, 110));
        artistLabel.setAlignmentX(LEFT_ALIGNMENT);

        descriptionLabel = new JLabel("");
        descriptionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        descriptionLabel.setForeground(new Color(120, 125, 130));
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);

        infoPanel.add(titleLabel);
        infoPanel.add(artistLabel);
        infoPanel.add(descriptionLabel);
    }

    private void createControlPanel() {
        prevButton = new JButton("◄");
        prevButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        prevButton.setBackground(new Color(40, 50, 60));
        prevButton.setForeground(new Color(235, 239, 242));
        prevButton.setFocusPainted(false);
        prevButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        prevButton.addActionListener(e -> previousArtwork());

        nextButton = new JButton("►");
        nextButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nextButton.setBackground(new Color(40, 50, 60));
        nextButton.setForeground(new Color(235, 239, 242));
        nextButton.setFocusPainted(false);
        nextButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        nextButton.addActionListener(e -> nextArtwork());

        zoomInButton = new JButton("＋");
        zoomInButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        zoomInButton.setBackground(new Color(200, 205, 210));
        zoomInButton.setFocusPainted(false);
        zoomInButton.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        zoomInButton.addActionListener(e -> zoomIn());

        zoomOutButton = new JButton("－");
        zoomOutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        zoomOutButton.setBackground(new Color(200, 205, 210));
        zoomOutButton.setFocusPainted(false);
        zoomOutButton.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        zoomOutButton.addActionListener(e -> zoomOut());
    }

    private void setupKeyBindings() {
        // Use root pane input map for simple navigation
        getRootPane().getInputMap().put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "prev");
        getRootPane().getActionMap().put("prev", new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) { previousArtwork(); }
        });
        getRootPane().getInputMap().put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "next");
        getRootPane().getActionMap().put("next", new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) { nextArtwork(); }
        });
        getRootPane().getInputMap().put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "zoomIn");
        getRootPane().getActionMap().put("zoomIn", new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) { zoomIn(); }
        });
        getRootPane().getInputMap().put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), "zoomIn");
        getRootPane().getInputMap().put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "zoomOut");
        getRootPane().getActionMap().put("zoomOut", new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) { zoomOut(); }
        });
    }

    private void initializeArtworks() {
        artworks = new ArrayList<>();
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
        // Reuse the same visuals from the older implementation
        BufferedImage img = new BufferedImage(800, 520, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (type) {
            case 1:
                java.awt.GradientPaint gradient1 = new java.awt.GradientPaint(0, 0, new Color(255, 140, 0), 0, 520, new Color(255, 69, 0));
                g2d.setPaint(gradient1);
                g2d.fillRect(0, 0, 800, 520);
                g2d.setColor(new Color(255, 215, 0, 160));
                for (int i = 0; i < 10; i++) g2d.fillOval(60 + i * 60, 140 + i * 24, 120, 120);
                break;
            case 2:
                g2d.setColor(new Color(40, 44, 52));
                g2d.fillRect(0, 0, 800, 520);
                Color[] colors = {Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.GREEN};
                for (int i = 0; i < 4; i++) {
                    g2d.setColor(new Color(colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), 160));
                    g2d.fillPolygon(new int[]{120 + i * 140, 260 + i * 140, 190 + i * 140}, new int[]{420, 420, 140}, 3);
                }
                break;
            case 3:
                java.awt.GradientPaint gradient2 = new java.awt.GradientPaint(0, 0, new Color(0, 191, 255), 0, 520, new Color(0, 0, 139));
                g2d.setPaint(gradient2);
                g2d.fillRect(0, 0, 800, 520);
                g2d.setColor(new Color(255, 255, 255, 100));
                for (int i = 0; i < 6; i++) g2d.fillArc(0, 60 + i * 90, 800, 80, 0, 180);
                break;
            case 4:
                java.awt.GradientPaint gradient3 = new java.awt.GradientPaint(0, 0, new Color(34, 139, 34), 0, 520, new Color(0, 100, 0));
                g2d.setPaint(gradient3);
                g2d.fillRect(0, 0, 800, 520);
                g2d.setColor(new Color(139, 69, 19));
                for (int i = 0; i < 9; i++) {
                    g2d.fillRect(60 + i * 80, 260, 30, 320);
                    g2d.setColor(new Color(0, 128, 0, 180));
                    g2d.fillOval(50 + i * 80, 200, 70, 90);
                    g2d.setColor(new Color(139, 69, 19));
                }
                break;
            default:
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, 800, 520);
                g2d.setColor(Color.WHITE);
                for (int i = 0; i < 160; i++) g2d.fillOval((int) (Math.random() * 800), (int) (Math.random() * 520), 2, 2);
                break;
        }

        g2d.dispose();
        return img;
    }

    private void displayCurrentArtwork() {
        if (artworks.size() == 0) return;
        Artwork current = artworks.get(currentIndex);
        titleLabel.setText(current.getTitle());
        artistLabel.setText("by " + current.getArtist());
        descriptionLabel.setText(current.getDescription());
        zoomLevel = 1.0;
        updateButtons();
        artPanel.repaint();
    }

    private void updateButtons() {
        prevButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(currentIndex < artworks.size() - 1);
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
            zoomLevel += 0.15;
            artPanel.repaint();
        }
    }

    private void zoomOut() {
        if (zoomLevel > 0.4) {
            zoomLevel -= 0.15;
            artPanel.repaint();
        }
    }

    private class ArtPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setColor(new Color(24, 28, 32));
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (artworks.size() == 0) {
                g2.setColor(new Color(200, 200, 200));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                String msg = "No Artworks Available";
                int sw = g2.getFontMetrics().stringWidth(msg);
                g2.drawString(msg, (getWidth() - sw) / 2, getHeight() / 2);
                g2.dispose();
                return;
            }

            Artwork current = artworks.get(currentIndex);
            BufferedImage img = current.getImage();
            if (img == null) return;

            int iw = (int) (img.getWidth() * zoomLevel);
            int ih = (int) (img.getHeight() * zoomLevel);

            int x = (getWidth() - iw) / 2;
            int y = (getHeight() - ih) / 2;

            g2.drawImage(img, x, y, iw, ih, null);

            // white frame
            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawRect(x - 4, y - 4, iw + 8, ih + 8);

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new VirtualGallery());
    }
}
