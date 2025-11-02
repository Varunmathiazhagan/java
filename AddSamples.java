import java.awt.*;
import java.awt.image.BufferedImage;

public class AddSamples {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        db.initializeDatabase();

        BufferedImage[] samples = new BufferedImage[5];
        for (int i = 0; i < 5; i++) samples[i] = createSampleImage(i+1);

        String[] titles = {"Abstract Sunset","Geometric Harmony","Ocean Dreams","Digital Forest","Cosmic Journey"};
        String[] artists = {"Digital Artist 1","Digital Artist 2","Digital Artist 3","Digital Artist 4","Digital Artist 5"};
        String[] desc = {
            "A beautiful abstract representation of sunset colors",
            "Modern geometric patterns in vibrant colors",
            "Serene ocean waves with gradient blues",
            "Abstract forest with digital textures",
            "Space-inspired digital artwork"
        };

        int added = 0;
        for (int i = 0; i < 5; i++) {
            int id = db.insertArtwork(titles[i], artists[i], desc[i], samples[i]);
            if (id > 0) {
                System.out.println("Inserted sample id=" + id + " title='" + titles[i] + "'");
                added++;
            } else {
                System.err.println("Failed to insert sample " + titles[i]);
            }
        }
        System.out.println("Added " + added + " sample artworks.");
        db.close();
    }

    private static BufferedImage createSampleImage(int type) {
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
}
