import java.awt.Color;
import java.awt.Graphics;

public class PlayerLaser extends GraphicsObject {
    private double size;
    private Color color;

    public PlayerLaser(double x, double y, double size, Color color) {
        super(x, y);
        this.size = size;
        this.color = color;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillRect((int) this.x, (int) this.y, (int) this.size, (int) this.size);
    }

    public void initiateLaserMovement() {
        this.speed_y += 5;
    }
}
