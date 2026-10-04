package org.cis1200.brickblast;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BrickBlastTest {

    @Test
    public void testSaveCreatesFile() throws IOException {
        JLabel status = new JLabel();
        GameCourt court = new GameCourt(status);
        court.reset();

        String filename = "test_save.txt";
        court.save(filename);

        File file = new File(filename);
        assertTrue(file.exists(), "Save file should be created");
        file.delete(); // Clean up
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        JLabel status = new JLabel();
        GameCourt court = new GameCourt(status);
        court.reset();

        // Modify a brick to exploding
        court.getBricks()[0][0] = new ExplodingBrick(
                GameCourt.COURT_WIDTH, GameCourt.COURT_HEIGHT, 0, 0, 15
        );

        String filename = "test_save_load.txt";
        court.save(filename);

        // Reset and load
        court.reset();
        court.load(filename);

        // Check if the brick is loaded as ExplodingBrick
        assertTrue(
                court.getBricks()[0][0] instanceof ExplodingBrick,
                "Loaded brick should be ExplodingBrick"
        );

        new File(filename).delete(); // Clean up
    }

    @Test
    public void testResetInitializesBricks() {
        JLabel status = new JLabel();
        GameCourt court = new GameCourt(status);
        court.reset();

        Brick[][] bricks = court.getBricks();
        assertNotNull(bricks, "Bricks should be initialized");
        assertEquals(3, bricks.length, "Should have 3 rows");
        assertEquals(10, bricks[0].length, "Should have 10 columns");
        assertNotNull(bricks[0][0], "First brick should not be null");
    }

    @Test
    public void testPowerupTypes() {
        assertEquals(3, Powerup.Type.values().length, "Should have 3 powerup types");
        assertEquals(
                Powerup.Type.SLOW_BALL, Powerup.Type.values()[0], "First type should be SLOW_BALL"
        );
        assertEquals(
                Powerup.Type.FAST_PADDLE, Powerup.Type.values()[1],
                "Second type should be FAST_PADDLE"
        );
        assertEquals(
                Powerup.Type.TRANSFORM_BRICK, Powerup.Type.values()[2],
                "Third type should be TRANSFORM_BRICK"
        );
    }

    @Test
    public void testBallInitialization() {
        JLabel status = new JLabel();
        GameCourt court = new GameCourt(status);
        court.reset();

        Ball ball = court.getBall();
        assertNotNull(ball, "Ball should be initialized");
        assertEquals(-2, ball.getVx(), "Ball initial x velocity");
        assertEquals(-3, ball.getVy(), "Ball initial y velocity");
    }
}
