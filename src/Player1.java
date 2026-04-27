import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Player1 extends JPanel {
    private Image backgroundImage;
    
    private Image[] frames1 = new Image[8];
    private Image[] frames2 = new Image[8];
    
    private int shipX = 300;
    private int shipY = 400; 
    private int frameCounter = 0;
    private int transitionCounter = 0;
    
    enum Direction {
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

    public Player1() {
        backgroundImage = Toolkit.getDefaultToolkit().createImage("assets/bg/bg_sea(2).gif");
        loadImages();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                Direction newTarget = null;

                // determine target direction
                if (keyCode == KeyEvent.VK_UP) {
                	newTarget = Direction.UP; 
                }
                else if (keyCode == KeyEvent.VK_DOWN) {
                	newTarget = Direction.DOWN; 
                }
                else if (keyCode == KeyEvent.VK_LEFT) {
                	newTarget = Direction.LEFT;
                }
                else if (keyCode == KeyEvent.VK_RIGHT) {
                	newTarget = Direction.RIGHT;
                }

                if (newTarget != null) {
                    // if already facing target direction, just move
                    if (currentDirection == newTarget && targetDirection == newTarget) {
                        if (newTarget == Direction.UP) {
                        	shipY -= 15; 
                        }
                        else if (newTarget == Direction.DOWN) {
                        	shipY += 15;
                        }
                        else if (newTarget == Direction.LEFT) {
                        	shipX -= 15;
                        }
                        else if (newTarget == Direction.RIGHT) {
                        	shipX += 15;
                        }
                    } else {
                        // if not facing target direction, face target direction
                        targetDirection = newTarget;
                        if (transitionCounter <= 0) {
                            transitionCounter = 10; 
                        }
                    }
                }
            }
        });
    }

    private void loadImages() {
        try {
            // asset paths
            String[] paths = {
                "up/ship_1_1.png", 
                "up/ship2_1_1.png",
                "up-right/ship_1_0.png", 
                "up-right/ship2_1_0.png",
                "right/ship_0_3.png", 
                "right/ship2_0_3.png",
                "down-right/ship_0_2.png", 
                "down-right/ship2_0_2.png",
                "down/ship_0_1.png", 
                "down/ship2_0_1.png",
                "down-left/ship_0_0.png", 
                "down-left/ship2_0_0.png",
                "left/ship_1_3.png", 
                "left/ship2_1_3.png",
                "up-left/ship_1_2.png", 
                "up-left/ship2_1_2.png"
            };

            for (int i = 0; i < 8; i++) {
                frames1[i] = ImageIO.read(new File("assets/main_player/" + paths[i*2]));
                frames2[i] = ImageIO.read(new File("assets/main_player/" + paths[i*2+1]));
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateRotation() {
        if (currentDirection == targetDirection) return;

        int current = currentDirection.ordinal();
        int target = targetDirection.ordinal();

        // shortest distance around circle
        int diff = (target - current + 8) % 8;

        if (diff <= 4) {
            current = (current + 1) % 8; // clockwise
        } else {
            current = (current - 1 + 8) % 8; // counter-clockwise
        }

        currentDirection = Direction.values()[current];
        
        // if still not facing target direction, keep going
        if (currentDirection != targetDirection) {
            transitionCounter = 10;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        
        // animation timing
        frameCounter = (frameCounter + 1) % 80;
        
        // rotation timing
        if (transitionCounter > 0) {
            transitionCounter--;
            if (transitionCounter == 0) {
                updateRotation();
            }
        }

        // image selection
        int directionIndex = currentDirection.ordinal();
        Image currentFrame = (frameCounter < 40) ? frames1[directionIndex] : frames2[directionIndex];

        if (currentFrame != null) {
            g.drawImage(currentFrame, shipX, shipY, 240, 180, this);
        }
        
    }

}