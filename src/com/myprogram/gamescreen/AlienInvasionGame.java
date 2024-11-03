/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.myprogram.gamescreen;

/**
 *
 * @author Pang
 */
import com.myprogram.Menu.MenuScreen;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class AlienInvasionGame extends JPanel implements ActionListener {

    private Timer timer;
    private int playerX = 250;  // Player starting X position
    private int playerY = 500;  // Player fixed Y position
    private int health = 3;     // Player health
    private int score = 0;
    private int currentLevel = 1;  // Track the current level
    private final int maxLevel = 4; // Max level set to 4
    private List<Rectangle> aliens;
    private BasicBlocks blocks;
    private List<MachineGun> bullets;  // List to store player's bullets
    private List<AlienBullet> alienBullets;  // List to store alien bullets
    private Image playerImage;
    private Image alienImage;
    private int alienSpeedX = 1;   // Horizontal speed of aliens
    private int alienSpeedY = 10;  // Vertical drop when they hit the edge
    private boolean moveRight = true;  // Track direction of movement
    private boolean gameOver = false; // Track game over state
    private boolean levelComplete = false;
    private boolean allLevelsComplete = false; // Track if all levels are complete
    private Random random = new Random();
    private boolean aliensMoving = false;
    private boolean gameComplete = false;
    private final double[] alienSpeedMultiplier = {1.8, 2.2, 2.5, 3.0};
    private int level4TimeLimit = 30; // Time limit for Level 4 in seconds
    private int level4RemainingTime;  // Remaining time for Level 4
    private boolean level4TimerActive = false;
    private int finalTimeRemaining = 0;
    private Timer countdownTimer;

    public AlienInvasionGame() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(600, 600));

        playerImage = new ImageIcon(getClass().getResource("/com/myprogram/images/Player (1).png")).getImage();
        alienImage = new ImageIcon(getClass().getResource("/com/myprogram/images/Invaders (2).png")).getImage();
        aliens = createAliens();
        blocks = new BasicBlocks();
        bullets = new ArrayList<>();
        alienBullets = new ArrayList<>();
        timer = new Timer(16, this);  // Game loop, roughly 60 FPS
        timer.start();
        // Key bindings for movement and shooting
        InputMap im = getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "shoot");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "shoot");  // Bind up arrow key to shoot
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0), "returnToMenu");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "restartGame");

        am.put("returnToMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToMenu(); // Implement the method to go back to the main menu
            }
        });
        am.put("restartGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame(); // Implement the method to restart the game
            }
        });
        am.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playerX > 0) {
                    playerX -= 15;
                }
            }
        });
        am.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playerX < 550) {
                    playerX += 15;
                }
            }
        });
        am.put("shoot", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shootBullet();
            }
        });
        setFocusable(true);
        requestFocusInWindow();

        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentLevel == 4 && level4RemainingTime > 0) {
                    level4RemainingTime--; // Decrement the remaining time
                    if (level4RemainingTime <= 0) {
                        gameOver = true; // Set game over if time runs out
                        countdownTimer.stop(); // Stop the countdown timer
                    }
                    repaint(); // Refresh the display to show the new time
                }
            }
        });
    }

    private void returnToMenu() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (currentFrame != null) {
            currentFrame.dispose(); // Close the game window
        }
        new MenuScreen();
    }

    private void restartGame() {
        allLevelsComplete = false;
        currentLevel = 1;
        score = 0;
        health = 3;
        aliens.clear();
        bullets.clear();
        alienBullets.clear();

        aliens = createAliens();
        blocks.reset();
        gameOver = false;
        level4RemainingTime = level4TimeLimit;
        level4TimerActive = false;
        repaint();
    }

    private List<Rectangle> createAliens() {
        List<Rectangle> alienList = new ArrayList<>();
        int alienWidth = 30;
        int alienHeight = 30;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 8; col++) {
                alienList.add(new Rectangle(50 + col * (alienWidth + 10), 50 + row * (alienHeight + 10), alienWidth, alienHeight)); // Adjusted spacing
            }
        }
        return alienList;
    }

    private void shootBullet() {
        bullets.add(new MachineGun(playerX + 18, playerY, 5, 10));  // Create a bullet and add to the list
    }

    private void shootAlienBullet(Rectangle alien) {
        alienBullets.add(new AlienBullet(alien.x + 15, alien.y + 40, 5, 10)); // Bullet position based on alien position
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the score and health
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 20, 30);
        g.setColor(Color.RED);
        g.drawString("Health: " + health, 20, 50);
        g.setColor(Color.CYAN);
        g.drawString("Level: " + currentLevel, 20, 70);  // Display current level

        g.drawImage(playerImage, playerX, playerY, this);

        if (currentLevel == 4) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Time Remaining: " + (int) level4RemainingTime + "s", 20, 90);  // Display remaining time
        }

        // Draw aliens
        for (Rectangle alien : aliens) {
            g.drawImage(alienImage, alien.x, alien.y, alien.width, alien.height, this);
        }

        // Draw player's bullets
        for (MachineGun bullet : bullets) {
            bullet.draw((Graphics2D) g);
        }
        // Draw alien bullets
        for (AlienBullet alienBullet : alienBullets) {
            alienBullet.draw((Graphics2D) g);
        }

        // Draw blocks (shields)
        blocks.draw((Graphics2D) g);  // Call BasicBlocks draw method

        // Draw Game Over message
        if (gameOver) {
            g.setColor(Color.RED); // Change color to red
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over", 200, 300);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.RED);
            g.drawString("Press M to return to menu", 180, 350);
            g.drawString("Press R to restart", 180, 380);
        }

        if (allLevelsComplete) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("YOUR LEVEL COMPLETE", 100, 300);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Press M to return to menu", 150, 350);
            g.drawString("Press R to restart", 150, 380);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver || gameComplete) {
            countdownTimer.stop();
            return; 
        }
        if (currentLevel == 4) {
            if (!countdownTimer.isRunning()) {
                level4RemainingTime = level4TimeLimit; 
                countdownTimer.start(); 
            }
        }
        // Move aliens
        boolean hitEdge = false;
        int rowBulletCount = 0;
        for (Rectangle alien : aliens) {
            if (moveRight && alien.x + alien.width >= getWidth()) {
                hitEdge = true;
                break;
            } else if (!moveRight && alien.x <= 0) {
                hitEdge = true;
                break;
            }
        }

        if (hitEdge) {
            moveRight = !moveRight;  // Reverse direction
            rowBulletCount = 0;
            for (Rectangle alien : aliens) {
                alien.y += alienSpeedY;  // Move down
                if (random.nextInt(10) < 6 && rowBulletCount < 6) {  // 30% chance for each alien to shoot
                    shootAlienBullet(alien);  // Shoot alien bullet
                    rowBulletCount++;
                }
            }
        }

        // Access alien speed multiplier safely
        int currentLevelIndex = currentLevel - 1; // Get the current level index (0-based)
        if (currentLevelIndex >= 0 && currentLevelIndex < alienSpeedMultiplier.length) {
            int speedMultiplier = (int) alienSpeedMultiplier[currentLevelIndex];
            for (Rectangle alien : aliens) {
                if (moveRight) {
                    alien.x += alienSpeedX * speedMultiplier; // Move right
                } else {
                    alien.x -= alienSpeedX * speedMultiplier; // Move left
                }
            }
        }
        // Update player's bullets
        for (int i = 0; i < bullets.size(); i++) {
            MachineGun bullet = bullets.get(i);
            bullet.update(1.0, blocks);

            if (!bullet.isAlive()) {
                bullets.remove(i);
                i--;
                continue;
            }
            for (int j = 0; j < aliens.size(); j++) {
                if (bullet.collisionRect(aliens.get(j))) {
                    aliens.remove(j);
                    bullet.setAlive(false);
                    score += 10;
                    break;
                }
            }
        }
        
        if (aliens.isEmpty()) {
            if (countdownTimer.isRunning()) {
                finalTimeRemaining = level4RemainingTime;  // Store the remaining time
                countdownTimer.stop(); 
            }
        if (currentLevel < maxLevel) {
            currentLevel++;
            bullets.clear();
            aliens = createAliens();
            blocks.reset();
            
        }else {
                allLevelsComplete = true;
                currentLevel = maxLevel;
            }
        }
        // Update alien bullets
        for (int i = 0; i < alienBullets.size(); i++) {
            AlienBullet alienBullet = alienBullets.get(i);
            alienBullet.update(blocks);
            // Check for collision with player
            if (alienBullet.collisionRect(new Rectangle(playerX, playerY, 40, 40))) {
                health--;
                alienBullets.remove(i);
                i--;
                if (health <= 0) {
                    gameOver = true; 
                    if (countdownTimer.isRunning()) {
                        finalTimeRemaining = level4RemainingTime; // Store remaining time at game over
                    }
                    countdownTimer.stop();
                }
            } else if (!alienBullet.isAlive()) {
                alienBullets.remove(i); // Remove bullet if it's no longer active
                i--; 
            }
        }
        repaint();
        
        if (health <= 0) {
            gameOver = true;
            if (countdownTimer.isRunning()) {
                finalTimeRemaining = level4RemainingTime;  // Store remaining time at game over
            }
            countdownTimer.stop(); 
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Alien Invasion");
        AlienInvasionGame game = new AlienInvasionGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void startGame() {
        //resetGame(); 
        currentLevel = 1; 
        gameOver = false; 
        gameComplete = false; 
        score = 0; 
        bullets.clear(); 
        aliens = createAliens(); 
        blocks.reset(); 
        timer.start(); 
        repaint(); 

    }

    private void resetGame() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static class MachineGun {

        private Rectangle bullet;
        private final double speed = 2.5d;
        private boolean isAlive = true; // Flag to check if bullet is active

        public MachineGun(double xPos, double yPos, int width, int height) {
            bullet = new Rectangle((int) xPos, (int) yPos, width, height);
        }

        public void draw(Graphics2D g) {
            if (bullet == null) {
                return;
            }

            g.setColor(Color.YELLOW);
            g.fill(bullet);
        }

        public void update(double delta, BasicBlocks blocks) {
            if (bullet == null || !isAlive) {
                return;
            }

            bullet.y -= (delta * speed); // Move bullet upward
            wallCollide(blocks); // Check for collision with blocks
            isOutOfBounds(); // Check if the bullet is out of bounds
        }

        public boolean collisionRect(Rectangle rect) {
            if (bullet == null) {
                return false;
            }

            if (bullet.intersects(rect)) {
                this.isAlive = false; // Deactivate the bullet on collision
                return true;
            }

            return false;
        }

        public boolean isAlive() {
            return isAlive;
        }

        public void setAlive(boolean isAlive) {
            this.isAlive = isAlive;
        }

        public boolean isOffScreen() {
            return bullet.y < 0; // Bullet is off-screen if it goes above the top
        }

        protected void wallCollide(BasicBlocks blocks) {
            for (int i = 0; i < blocks.wall.size(); i++) {
                if (bullet.intersects(blocks.wall.get(i))) {
                    blocks.wall.remove(i); // Remove block upon collision
                    isAlive = false; // Deactivate the bullet
                    return;
                }
            }
        }

        protected void isOutOfBounds() {
            if (bullet.y < 0) {
                isAlive = false; // Deactivate if out of screen bounds
            }
        }
    }

    public static class AlienBullet {

        private Rectangle bullet;
        private final double speed = 2.5d;
        private boolean isAlive = true; // Flag to check if bullet is active

        public AlienBullet(double xPos, double yPos, int width, int height) {
            bullet = new Rectangle((int) xPos, (int) yPos, width, height);
        }

        public void draw(Graphics2D g) {
            if (bullet == null) {
                return;
            }

            g.setColor(Color.RED); // Set bullet color to red
            g.fill(bullet);
        }

        public void update(BasicBlocks blocks) {
            if (bullet == null || !isAlive) {
                return;
            }

            bullet.y += (speed); // Move bullet downward
            wallCollide(blocks); // Check for collision with blocks
            isOutOfBounds(); // Check if the bullet is out of bounds
        }

        public boolean collisionRect(Rectangle rect) {
            if (bullet == null) {
                return false;
            }

            if (bullet.intersects(rect)) {
                this.isAlive = false; // Deactivate the bullet on collision
                return true;
            }

            return false;
        }

        public boolean isAlive() {
            return isAlive;
        }

        public void setAlive(boolean isAlive) {
            this.isAlive = isAlive;
        }

        public boolean isOffScreen() {
            return bullet.y > 600; // Bullet is off-screen if it goes below the bottom
        }

        protected void wallCollide(BasicBlocks blocks) {
            for (int i = 0; i < blocks.wall.size(); i++) {
                if (bullet.intersects(blocks.wall.get(i))) {
                    blocks.wall.remove(i); // Remove block upon collision
                    isAlive = false; // Deactivate the bullet
                    return;
                }
            }
        }

        protected void isOutOfBounds() {
            if (bullet.y > 600) {
                isAlive = false; // Deactivate if out of screen bounds
            }
        }
    }

// BasicBlocks class
    public static class BasicBlocks {

        public ArrayList<Rectangle> wall;

        public BasicBlocks() {
            wall = new ArrayList<>(); // Initialize the wall
            initializeBlocks(); // Populate the blocks on creation
        }

        private void initializeBlocks() {
            // Create multiple blocks at specified positions
            basicBlocks(75, 450);
            basicBlocks(275, 450);
            basicBlocks(475, 450);
            basicBlocks(675, 450);
        }

        public void draw(Graphics2D g) {
            g.setColor(Color.GREEN); // Set block color
            for (Rectangle block : wall) {
                g.fill(block); // Draw each block
            }
        }

        public void basicBlocks(int xPos, int yPos) {
            int wallWidth = 3;
            int x = 0;
            int y = 0;

            for (int i = 0; i < 13; i++) {
                if ((14 + (i * 2) + wallWidth < 22 + wallWidth)) {
                    row(14 + (i * 2) + wallWidth, xPos - (i * 3), yPos + (i * 3));
                    x = (i * 3) + 3;
                } else {
                    row(22 + wallWidth, xPos - x, yPos + (i * 3));
                    y = (i * 3);
                }
            }

            // Left side.
            for (int i = 0; i < 5; i++) {
                row(8 + wallWidth - i, xPos - x, (yPos + y) + (i * 3));
            }

            // Right side.
            for (int i = 0; i < 5; i++) {
                row(8 + wallWidth - i, (xPos - x + (14 * 3)) + (i * 3), (yPos + y) + (i * 3));
            }
        }

        public void row(int rows, int xPos, int yPos) {
            for (int i = 0; i < rows; i++) {
                Rectangle brick = new Rectangle(xPos + (i * 3), yPos, 3, 3); // Create new brick
                wall.add(brick); // Add brick to the wall
            }
        }

        public void reset() {
            wall.clear(); // Clear existing blocks
            initializeBlocks(); // Recreate blocks
        }

        public ArrayList<Rectangle> getBlocks() {
            return wall; // Return current blocks (if needed)
        }

        public boolean isEmpty() {
            return wall.isEmpty(); // Check if there are any blocks left
        }
    }
}
