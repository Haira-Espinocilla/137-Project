import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnemyShip {
    private int x, y;
    private int speed = 2;

    private Image[] frames1 = new Image[8];
    private Image[] frames2 = new Image[8];

    private int frameCounter = 0;
    private int transitionCounter = 0;
    
    private List<Cannonball> cannonballs = new ArrayList<>();
    private int shootTimer = 0;
    private final int SHOOT_DELAY = 120;

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
                frames1[i] = ImageIO.read(new File("assets/enemyship/" + paths[i*2]));
                frames2[i] = ImageIO.read(new File("assets/enemyship/" + paths[i*2+1]));
            }
        } catch (IOException e) {
            System.out.println("Enemy Image Error: " + e.getMessage());
        }
    }

    public void update(int playerX, int playerY) {
        determineTargetDirection(playerX, playerY);

        if (currentDirection != targetDirection) {
            if (transitionCounter <= 0) {
                updateRotation();
                transitionCounter = 10;
            } else {
                transitionCounter--;
            }
        }

        if (currentDirection == targetDirection) {
            if (!isSideways(playerX, playerY)) {
                moveForward();
                shootTimer = SHOOT_DELAY; 
            } else {
                //if in position, check timer
                if (shootTimer >= SHOOT_DELAY) {
                    shoot(playerX, playerY);
                    shootTimer = 0; //reset timer 
                }
                shootTimer++;
            }
        }

        //update active cannonballs, remove is inactive
        for (int i = cannonballs.size() - 1; i >= 0; i--) {
            Cannonball b = cannonballs.get(i);
            b.update();
            if (!b.isActive()) {
                cannonballs.remove(i);
            }
        }

        frameCounter = (frameCounter + 1) % 80;
    }
    
    private boolean isSideways(int playerX, int playerY) {
        int tolerance = 10; //wiggle room
        
        //if facing horizontal, look at alignment on the X axis
        if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
            return Math.abs(x - playerX) < tolerance;
        } 
        //if facing vertical, look at alignment on the Y axis
        else if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
            return Math.abs(y - playerY) < tolerance;
        }
        
        return false;
    }

    //sideways targeting
    private void determineTargetDirection(int playerX, int playerY) {
        double dx = playerX - x;
        double dy = playerY - y;

        Direction facing;

        //find where the player is
        if (Math.abs(dx) > Math.abs(dy)) {
            facing = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        } else {
            facing = (dy > 0) ? Direction.DOWN : Direction.UP;
        }

        //broadside
        if (facing == Direction.UP || facing == Direction.DOWN) {
            targetDirection = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        } else {
            targetDirection = (dy > 0) ? Direction.DOWN : Direction.UP;
        }
    }

    private void updateRotation() {
        int current = currentDirection.ordinal();
        int target = targetDirection.ordinal();

        int diff = (target - current + 8) % 8;

        if (diff <= 4) {
            current = (current + 1) % 8;
        } else {
            current = (current - 1 + 8) % 8;
        }

        currentDirection = Direction.values()[current];
    }

    private void moveForward() {
        switch (currentDirection) {
            case UP:    
            	y -= speed; 
            	break;
            case DOWN:  
            	y += speed; 
            	break;
            case LEFT:  
            	x -= speed; 
            	break;
            case RIGHT: 
            	x += speed; 
            	break;
        }
    }
    
    //subject to change: this function depends on the size of the ship (192x128)
    private void shoot(int playerX, int playerY) {
    	//center of enemy ship
        int enemyCenterX = this.x + 96; // 192 / 2
        int enemyCenterY = this.y + 64; // 128 / 2

        //center of player ship
        int playerCenterX = playerX + 96; 
        int playerCenterY = playerY + 64;

        cannonballs.add(new Cannonball(enemyCenterX, enemyCenterY, playerCenterX, playerCenterY));
    }

    public void draw(Graphics g, Component obs) {
        int idx = currentDirection.ordinal();
        Image frame = (frameCounter < 40) ? frames1[idx] : frames2[idx];

        if (frame != null) {
            g.drawImage(frame, x, y, 192, 128, obs);
        }

        for (Cannonball b : cannonballs) {
            b.draw(g, obs);
        }
    }
}