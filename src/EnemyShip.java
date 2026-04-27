import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class EnemyShip {
    private int x, y;
    private int speed = 2;
    
    private Image[] frames1 = new Image[8];
    private Image[] frames2 = new Image[8];
    private int frameCounter = 0;
    private int transitionCounter = 0;

    public enum Direction {
        UP, 
        UP_RIGHT, 
        RIGHT, 
        DOWN_RIGHT, 
        DOWN, 
        DOWN_LEFT, 
        LEFT, 
        UP_LEFT
    }

    private Direction currentDirection = Direction.UP;
    private Direction targetDirection = Direction.UP;

    public EnemyShip(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        loadImages();
    }

    private void loadImages() {
        try {
            // Reusing your path logic
            String[] paths = {
                "up/enemyship1_1_1.png", 
                "up/enemyship2_1_1.png",
                "up-right/enemyship1_1_0.png", 
                "up-right/enemyship2_1_0.png",
                "right/enemyship1_0_3.png", 
                "right/enemyship2_0_3.png",
                "down-right/enemyship1_0_2.png", 
                "down-right/enemyship2_0_2.png",
                "down/enemyship1_0_1.png", 
                "down/enemyship2_0_1.png",
                "down-left/enemyship1_0_0.png", 
                "down-left/enemyship2_0_0.png",
                "left/enemyship1_1_3.png", 
                "left/enemyship2_1_3.png",
                "up-left/enemyship1_1_2.png", 
                "up-left/enemyship2_1_2.png"
            };

            for (int i = 0; i < 8; i++) {
                // Adjust paths to your enemy asset folder
                frames1[i] = ImageIO.read(new File("assets/enemyship/" + paths[i*2]));
                frames2[i] = ImageIO.read(new File("assets/enemyship/" + paths[i*2+1]));
            }
        } catch (IOException e) {
            System.out.println("Enemy Image Error: " + e.getMessage());
        }
    }

    public void update(int playerX, int playerY) {
        // 1. AI Logic: Determine which direction the player is in
    	double distance = Math.sqrt(Math.pow(playerX - x, 2) + Math.pow(playerY - y, 2));
    	if (distance > 50) { // Only recalculate direction if the player isn't right on top of us
    	    determineTargetDirection(playerX, playerY);
    	}
        determineTargetDirection(playerX, playerY);

        // 2. Rotation Logic
        if (transitionCounter > 0) {
            transitionCounter--;
        } else if (currentDirection != targetDirection) {
            updateRotation();
            // Increasing this makes the "staying in the diagonal state" last longer
            transitionCounter = 40; 
        }

        // 3. Movement Logic: Only moves if transitionCounter is 0 
        // AND it's not a diagonal direction.
        if (transitionCounter == 0) {
            moveForward();
        }
        
        // 4. Animation logic
        frameCounter = (frameCounter + 1) % 80;
    }

    private void determineTargetDirection(int playerX, int playerY) {
        // Calculate the angle between enemy and player
        double angle = Math.toDegrees(Math.atan2(playerY - y, playerX - x));
        if (angle < 0) angle += 360;

        // Map angle to your 8 directions
        if (angle >= 337.5 || angle < 22.5) targetDirection = Direction.RIGHT;
        else if (angle >= 22.5 && angle < 67.5) targetDirection = Direction.DOWN_RIGHT;
        else if (angle >= 67.5 && angle < 112.5) targetDirection = Direction.DOWN;
        else if (angle >= 112.5 && angle < 157.5) targetDirection = Direction.DOWN_LEFT;
        else if (angle >= 157.5 && angle < 202.5) targetDirection = Direction.LEFT;
        else if (angle >= 202.5 && angle < 247.5) targetDirection = Direction.UP_LEFT;
        else if (angle >= 247.5 && angle < 292.5) targetDirection = Direction.UP;
        else if (angle >= 292.5 && angle < 337.5) targetDirection = Direction.UP_RIGHT;
    }

    private void updateRotation() {
        int current = currentDirection.ordinal();
        int target = targetDirection.ordinal();
        int diff = (target - current + 8) % 8;

        if (diff <= 4) current = (current + 1) % 8;
        else current = (current - 1 + 8) % 8;

        currentDirection = Direction.values()[current];
    }

    
    private void moveForward() {
        switch (currentDirection) {
            case UP:    y -= speed; break;
            case DOWN:  y += speed; break;
            case LEFT:  x -= speed; break;
            case RIGHT: x += speed; break;
            
            // Diagonals are now "Idle" states. 
            // The ship still FACES these ways, but x and y don't change.
            case UP_RIGHT: 
            case DOWN_RIGHT: 
            case UP_LEFT: 
            case DOWN_LEFT:
                break; 
        }
    }

    public void draw(Graphics g, Component obs) {
        int directionIndex = currentDirection.ordinal();
        Image currentFrame = (frameCounter < 40) ? frames1[directionIndex] : frames2[directionIndex];
        if (currentFrame != null) {
            g.drawImage(currentFrame, x, y, 192, 128, obs); 
        }
    }
}