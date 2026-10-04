package org.cis1200.brickblast;

// imports necessary libraries for Java swing

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class RunBrickBlast implements Runnable {
    public void run() {
        // NOTE : recall that the 'final' keyword notes immutability even for
        // local variables.

        // Top-level frame in which game components live.
        // Be sure to change "TOP LEVEL FRAME" to the name of your game
        final JFrame frame = new JFrame("TOP LEVEL FRAME");
        frame.setLocation(300, 300);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Running...");
        status_panel.add(status);

        // Main playing area
        final GameCourt court = new GameCourt(status);
        frame.add(court, BorderLayout.CENTER);

        // Reset button
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        // Note here that when we add an action listener to the reset button, we
        // define it as an anonymous inner class that is an instance of
        // ActionListener with its actionPerformed() method overridden. When the
        // button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        reset.addActionListener(e -> court.reset());
        control_panel.add(reset);

        final JButton save = new JButton("Save");
        save.addActionListener(e -> {
            try {
                court.save("brickblast_save.txt");
                status.setText("Saved!");
            } catch (IOException ex) {
                status.setText("Save failed!");
            }
        });
        control_panel.add(save);

        final JButton load = new JButton("Load");
        load.addActionListener(e -> {
            try {
                court.load("brickblast_save.txt");
                status.setText("Loaded!");
            } catch (IOException ex) {
                status.setText("Load failed!");
            }
        });
        control_panel.add(load);

        final JButton instructions = new JButton("Instructions");
        instructions.addActionListener(e -> {
            String message = "Welcome to BrickBlast!\n\n" +
                    "This is a classic Brickbreaker game where you control a paddle to " +
                    "bounce a ball and destroy all the bricks to win.\n\n"
                    +
                    "How to Play:\n" +
                    "- Use the LEFT and RIGHT arrow keys to move the paddle.\n" +
                    "- The ball will bounce off the walls, paddle, and bricks.\n" +
                    "- Destroy all bricks to win. If the ball hits the bottom, you lose.\n\n" +
                    "Cool Features:\n" +
                    "- Exploding Bricks: Some bricks explode and destroy " +
                    "surrounding bricks when hit.\n"
                    +
                    "- Powerups: Collect falling powerups for effects like " +
                    "slowing the ball (green), " +
                    "transforming a brick to exploding (red), or speeding " +
                    "up your paddle (orange).\n"
                    +
                    "- Save/Load: Save your progress to a file and load it later.\n\n" +
                    "Enjoy the game!";
            JOptionPane.showMessageDialog(
                    frame, message, "Instructions", JOptionPane.INFORMATION_MESSAGE
            );
        });
        control_panel.add(instructions);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start game
        court.reset();
    }
}