import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Cannonball {
    private int x, y;
    private double dx, dy;
    private int speed = 5;
    private boolean active = true;
    
    private static Image ballImage;

    static {
        try {
            ballImage = ImageIO.read(new File("assets/cannonball/fired_cannonball.png"));
        } catch (IOException e) {
            System.out.println("Cannonball Image Error: " + e.getMessage());
        }
    }

    public Cannonball(int startX, int startY, int targetX, int targetY) {
        this.x = startX;
        this.y = startY;

        double angle = Math.atan2(targetY - y, targetX - x);
        this.dx = Math.cos(angle) * speed;
        this.dy = Math.sin(angle) * speed;
    }

    public void update() {
        x += dx;
        y += dy;
        
        //deactivate once off-screen (adjust pa itu)
        if (x < -100 || x > 1500 || y < -100 || y > 1500) {
            active = false;
        }
    }

    public void draw(Graphics g, Component obs) {
        if (ballImage != null) {
            //center the ball on the x,y coordinate
            g.drawImage(ballImage, x - 12, y - 12, 24, 24, obs);
        }
    }

    public boolean isActive() { return active; }
}