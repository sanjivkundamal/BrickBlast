package org.cis1200.brickblast;


import java.awt.*;


/**
 * A game object displayed using an image.
 *
 * Note that the image is read from the file when the object is constructed, and
 * that all objects created by this constructor share the same image data (i.e.
 * img is static). This is important for efficiency: your program will go very
 * slowly if you try to create a new BufferedImage every time the draw method is
 * invoked.
 */
public class Brick extends GameObj {
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 0;
    private final Color color;

    public Brick(int courtWidth, int courtHeight, Color color, int posX, int posY, int size) {
        super(INIT_VEL_X, INIT_VEL_Y, posX, posY, 2 * size, size, courtWidth, courtHeight);
        this.color = color;

    }

    @Override
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillRect(this.getPx(), this.getPy(), this.getWidth(), this.getHeight());
    }

    public Color getColor() {
        return this.color;
    }
}

/**
 * An exploding brick that destroys surrounding bricks when hit.
 */
class ExplodingBrick extends Brick {
    public ExplodingBrick(int courtWidth, int courtHeight, int posX, int posY, int size) {
        super(courtWidth, courtHeight, Color.RED, posX, posY, size);
    }

    // The explosion logic will be handled in GameCourt when this brick is hit
}
