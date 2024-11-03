package com.myprogram.Menu;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Pang
 */
import com.myprogram.gamescreen.AlienInvasionGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MenuScreen extends JFrame {

    public MenuScreen() {

        setTitle("Alien Invasion - Menu");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("ALIEN INVASION", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.GREEN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel startLabel = new JLabel("START or PRESS ENTER", JLabel.CENTER);
        startLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        startLabel.setForeground(Color.RED);
        startLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add labels to the panel
        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(startLabel);
        panel.add(Box.createVerticalGlue());

        // Button to Start Game
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.PLAIN, 18));
        startButton.setFocusPainted(false);
        startButton.setBackground(Color.RED);
        startButton.setForeground(Color.WHITE);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        panel.add(startButton);

        // Add a KeyListener to detect the "Enter" key press
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startGame();
                }
            }
        });

        setFocusable(true); // Important for KeyListener to work

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void startGame() {
        this.setVisible(false);  // Hide the menu screen

        // Create a new JFrame for the AlienInvasionGame
        JFrame gameFrame = new JFrame("Alien Invasion");
        AlienInvasionGame gamePanel = new AlienInvasionGame(); // Create the game panel
        gameFrame.add(gamePanel); // Add the game panel to the frame
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(800, 600);  // Set size for the game screen
        gameFrame.setVisible(true);   // Show the game frame

        // Add a key listener to detect 'M' press for back to menu
        gameFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_M) { // Assuming 'M' to go back to menu
                    gameFrame.dispose(); // Close the game window
                    setVisible(true); // Show the menu screen again
                }
            }
        });

        gamePanel.startGame(); // Start the game panel (AlienInvasionGame)
    }

    public static void main(String[] args) {
        MenuScreen menu = new MenuScreen();  // Show the menu screen first
    }
}
