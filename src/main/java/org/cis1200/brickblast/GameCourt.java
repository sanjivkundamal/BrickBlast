package org.cis1200.brickblast;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.LinkedList;
import java.util.Random;

/**
 * GameCourt
 *
 * This class holds the primary game logic for how different objects interact
 * with one another. Take time to understand how the timer interacts with the
 * different methods and how it repaints the GUI on every tick().
 */
public class GameCourt extends JPanel {

    // the state of the game logic
    // makes the array of bricks
    private Brick[][] bricks = new Brick[3][10];

    private Paddle paddle; // the paddle, keyboard control
    private Ball ball; // the ball, bounces

    private boolean playing = false; // whether the game is running
    private final JLabel status; // Current status text, i.e. "Running..."

    // Game constants
    public static final int COURT_WIDTH = 300;
    public static final int COURT_HEIGHT = 300;
    public static final int PADDLE_VELOCITY = 4;

    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 35;

    private LinkedList<Powerup> powerups = new LinkedList<>();
    private Random random = new Random();

    private LinkedList<Powerup.Type> powerupQueue = new LinkedList<>();
    private Powerup.Type currentActive = null;
    private int activeTimer = 0;
    private int paddleVelocity = PADDLE_VELOCITY;

    public GameCourt(JLabel status) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // The timer is an object which triggers an action periodically with the
        // given INTERVAL. We register an ActionListener with this timer, whose
        // actionPerformed() method is called each time the timer triggers. We
        // define a helper method called tick() that actually does everything
        // that should be done in a single time step.
        Timer timer = new Timer(INTERVAL, e -> tick());
        timer.start(); // MAKE SURE TO START THE TIMER!

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        // This key listener allows the square to move as long as an arrow key
        // is pressed, by changing the square's velocity accordingly. (The tick
        // method below actually moves the square.)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    paddle.setVx(-paddleVelocity);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    paddle.setVx(paddleVelocity);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (currentActive == null && !powerupQueue.isEmpty()) {
                        activatePowerup(powerupQueue.removeFirst());
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                paddle.setVx(0);
                paddle.setVy(0);
            }

            // public void keyTyped(KeyEvent e) {}
        });

        this.status = status;
    }

    /**
     * (Re-)set the game to its initial state.
     */
    public void reset() {
        for (int i = 0; i < bricks.length; i++) {
            for (int j = 0; j < bricks[0].length; j++) {
                Color brickColor = new Color(0, 0, 0);
                if ((i + j) % 2 == 0) {
                    brickColor = Color.CYAN;
                } else {
                    brickColor = Color.BLUE;
                }
                bricks[i][j] = new Brick(COURT_WIDTH, COURT_HEIGHT, brickColor, j * 30, i * 15, 15);
            }
        }

        // Randomly select 2 bricks to be exploding bricks
        for (int count = 0; count < 2; count++) {
            int randomRow = random.nextInt(bricks.length);
            int randomCol = random.nextInt(bricks[0].length);
            bricks[randomRow][randomCol] = new ExplodingBrick(
                    COURT_WIDTH, COURT_HEIGHT, randomCol * 30, randomRow * 15, 15
            );
        }

        paddle = new Paddle(COURT_WIDTH, COURT_HEIGHT, Color.BLACK);

        ball = new Ball(COURT_WIDTH, COURT_HEIGHT, Color.YELLOW);

        playing = true;
        status.setText("Running...");

        powerups = new LinkedList<>();

        powerupQueue = new LinkedList<>();
        currentActive = null;
        activeTimer = 0;
        paddleVelocity = PADDLE_VELOCITY;

        ball.setColor(Color.YELLOW);
        paddle.setColor(Color.BLACK);

        // Make sure that this component has the keyboard focus
        requestFocusInWindow();
    }

    /**
     * This method is called every time the timer defined in the constructor
     * triggers.
     */
    void tick() {
        if (playing) {
            // Update timers
            if (activeTimer > 0) {
                activeTimer--;
                if (activeTimer == 0) {
                    deactivatePowerup();
                }
            }

            // advance the square and snitch in their current direction.
            paddle.move();
            ball.move();

            // Move powerups
            for (Powerup p : powerups) {
                p.move();
            }

            // make the snitch bounce off walls...
            ball.bounce(ball.hitWall());
            // ...and the bricks
            for (int i = 0; i < bricks.length; i++) {
                for (int j = 0; j < bricks[0].length; j++) {
                    if (bricks[i][j] != null) {
                        ball.bounce(ball.hitObj(bricks[i][j]));
                        if (ball.intersects(
                                new Brick(
                                        COURT_WIDTH, COURT_HEIGHT, Color.BLACK, j * 30 - 2, i * 15,
                                        18
                                )
                        )) {
                            // Check if exploding brick
                            if (bricks[i][j] instanceof ExplodingBrick) {
                                explode(i, j);
                            } else {
                                // Spawn powerup randomly
                                if (random.nextInt(10) == 0) { // 10% chance
                                    Powerup.Type type = Powerup.Type.values()[random
                                            .nextInt(Powerup.Type.values().length)];
                                    powerups.add(
                                            new Powerup(
                                                    COURT_WIDTH, COURT_HEIGHT, type,
                                                    bricks[i][j].getPx(), bricks[i][j].getPy()
                                            )
                                    );
                                }
                            }
                            bricks[i][j] = null;
                        }
                    }
                }
            }
            // ball.bounce(ball.hitObj(brick));
            // ...and ball bounces off paddle
            ball.bounce(ball.hitObj(paddle));

            // Check powerup collection
            for (int k = 0; k < powerups.size(); k++) {
                Powerup p = powerups.get(k);
                if (p.intersects(paddle)) {
                    powerupQueue.add(p.getType());
                    powerups.remove(k);
                    k--;
                } else if (p.getPy() > COURT_HEIGHT) {
                    powerups.remove(k);
                    k--;
                }
            }

            // check for the game end conditions
            if (ball.hitBottomWall()) {
                playing = false;
                status.setText("You lose!");
            }
            Boolean isBricks = false;
            for (int i = 0; i < bricks.length; i++) {
                for (int j = 0; j < bricks[0].length; j++) {
                    Boolean notNull = (bricks[i][j] != null);
                    isBricks = notNull || isBricks;
                }
            }
            if (!isBricks) {
                playing = false;
                status.setText("You Win!");
            }

            // Update status
            if (playing) {
                status.setText(currentActive != null ? currentActive.toString() : "Running...");
            }

            // update the display
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paddle.draw(g);
        for (int i = 0; i < bricks.length; i++) {
            for (int j = 0; j < bricks[0].length; j++) {
                if (bricks[i][j] != null) {
                    bricks[i][j].draw(g);
                }

            }
        }
        ball.draw(g);
        for (Powerup p : powerups) {
            p.draw(g);
        }
        g.setColor(Color.BLACK);
        g.drawString("Powerup Queue: " + powerupQueue.toString(), 10, COURT_HEIGHT - 15);
        if (currentActive != null) {
            g.drawString(
                    "Active: " + currentActive + " (" + (activeTimer / 10) + "s)", 10,
                    COURT_HEIGHT - 30
            );
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }

    /**
     * Explodes the surrounding bricks when an ExplodingBrick is hit.
     */
    private void explode(int row, int col) {
        for (int i = Math.max(0, row - 1); i <= Math.min(bricks.length - 1, row + 1); i++) {
            for (int j = Math.max(0, col - 1); j <= Math.min(bricks[0].length - 1, col + 1); j++) {
                if (bricks[i][j] != null) {
                    bricks[i][j] = null;
                }
            }
        }
    }

    /**
     * Activates a powerup from the queue.
     */
    private void activatePowerup(Powerup.Type type) {
        switch (type) {
            case SLOW_BALL:
                currentActive = type;
                activeTimer = 300; // 10 seconds
                ball.setVx(ball.getVx() / 2);
                ball.setVy(ball.getVy() / 2);
                ball.setColor(Color.GREEN);
                break;
            case FAST_PADDLE:
                currentActive = type;
                activeTimer = 300; // 10 seconds
                paddleVelocity = 8;
                paddle.setColor(Color.RED);
                break;
            case TRANSFORM_BRICK:
                // Find a random brick to transform (not already exploding)
                LinkedList<int[]> available = new LinkedList<>();
                for (int i = 0; i < bricks.length; i++) {
                    for (int j = 0; j < bricks[0].length; j++) {
                        if (bricks[i][j] != null && !(bricks[i][j] instanceof ExplodingBrick)) {
                            available.add(new int[] { i, j });
                        }
                    }
                }
                if (!available.isEmpty()) {
                    int[] pos = available.get(random.nextInt(available.size()));
                    bricks[pos[0]][pos[1]] = new ExplodingBrick(
                            COURT_WIDTH, COURT_HEIGHT, pos[1] * 30, pos[0] * 15, 15
                    );
                }
                // Transform brick powerup is immediate, no timer
                break;
            default:
                break;

        }
    }

    /**
     * Deactivates the current powerup.
     */
    private void deactivatePowerup() {
        switch (currentActive) {
            case SLOW_BALL:
                ball.setVx(ball.getVx() * 2);
                ball.setVy(ball.getVy() * 2);
                ball.setColor(Color.YELLOW);
                break;
            case FAST_PADDLE:
                paddleVelocity = PADDLE_VELOCITY;
                paddle.setColor(Color.BLACK);
                break;
            case TRANSFORM_BRICK:
                // Transform brick powerup doesn't have a timer, so nothing to deactivate
                break;
            default:
                break;
        }
        currentActive = null;
    }

    /**
     * Saves the current brick grid to a .txt file.
     */
    public void save(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (int i = 0; i < bricks.length; i++) {
                for (int j = 0; j < bricks[0].length; j++) {
                    if (bricks[i][j] == null) {
                        writer.print(" ");
                    } else if (bricks[i][j] instanceof ExplodingBrick) {
                        writer.print("E");
                    } else {
                        writer.print("B");
                    }
                }
                writer.println();
            }
        }
        // Ensure focus remains on the court for paddle movement
        requestFocusInWindow();
    }

    /**
     * Loads the brick grid from a .txt file.
     */
    public void load(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null && i < bricks.length) {
                for (int j = 0; j < line.length() && j < bricks[0].length; j++) {
                    char c = line.charAt(j);
                    if (c == ' ') {
                        bricks[i][j] = null;
                    } else if (c == 'E') {
                        bricks[i][j] = new ExplodingBrick(
                                COURT_WIDTH, COURT_HEIGHT, j * 30, i * 15, 15
                        );
                    } else if (c == 'B') {
                        Color color = (i + j) % 2 == 0 ? Color.CYAN : Color.BLUE;
                        bricks[i][j] = new Brick(
                                COURT_WIDTH, COURT_HEIGHT, color, j * 30, i * 15, 15
                        );
                    }
                }
                i++;
            }
        }
        // Reset other state
        powerups.clear();
        powerupQueue.clear();
        currentActive = null;
        activeTimer = 0;
        paddleVelocity = PADDLE_VELOCITY;
        ball.setColor(Color.YELLOW);
        paddle.setColor(Color.BLACK);
        playing = true;
        status.setText("Running...");
        requestFocusInWindow();
    }

    // Getters for testing
    public Brick[][] getBricks() {
        return bricks;
    }

    public Ball getBall() {
        return ball;
    }
}
