package org.cis1200.brickblast;

import java.awt.*;

/**
 * A powerup that falls down and can be collected by the paddle.
 */
public class Powerup extends GameObj {
    public enum Type {
        SLOW_BALL, FAST_PADDLE, TRANSFORM_BRICK
    }

    private final Type type;
    private final Color color;

    public static final int SIZE = 10;
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 2; // Falling down

    public Powerup(int courtWidth, int courtHeight, Type type, int posX, int posY) {
        super(INIT_VEL_X, INIT_VEL_Y, posX, posY, SIZE, SIZE, courtWidth, courtHeight);
        this.type = type;
        switch (type) {
            case SLOW_BALL:
                this.color = Color.GREEN;
                break;
            case TRANSFORM_BRICK:
                this.color = Color.RED;
                break;
            case FAST_PADDLE:
                this.color = Color.ORANGE;
                break;
            default:
                this.color = Color.WHITE;
        }
    }

    public Type getType() {
        return type;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillRect(this.getPx(), this.getPy(), this.getWidth(), this.getHeight());
    }
}
