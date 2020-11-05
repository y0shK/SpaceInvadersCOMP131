package proj2;
import java.awt.Color;
import java.awt.Graphics;

public class Person extends GraphicsObject {
	private double size;
	private Color color;
	public Person(double x, double y, double size, Color color) {
		super(x, y);
		this.size = size;
		this.color = color;
	}
	
	public void draw(Graphics g) {
		g.setColor(this.color);
		g.fillOval((int) this.x, (int) this.y, (int) this.size, (int) this.size);
		
	}


}
