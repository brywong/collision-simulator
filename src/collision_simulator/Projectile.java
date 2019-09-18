package collision_simulator;

import java.awt.*;
import java.awt.geom.*;

public class Projectile {
	
	private float mass;
	private float velocity;
	private float location;
	private int projID;
	private int yPos;
	
	public Projectile(float m, float v, float l, int ID, int y) {
		mass = m;
		velocity = v;
		location = l;
		projID = ID;
		yPos = y;
	}
	
	public float mass() {
		return mass;
	}
	
	public float velocity() {
		return velocity;
	}
	
	public float location() {
		return location;
	}
	
	public void changeVelocity(float v) {
		velocity = v;
	}
	
	public void changeLocation(float l) {
		location = l;
	}
	
	public void drawProjectile(Graphics2D g2) {
		g2.setFont(new Font("Roboto", Font.BOLD, 50));
    	g2.setPaint(Color.BLACK);
    	g2.setStroke(new BasicStroke(2));
	    if (projID == 1) {
	    	g2.draw(new Ellipse2D.Double(location, yPos, 100, 100));
	    	// Calculations used to adjust (x,y) position for "1" to center text inside of projectile
	    	int length1 = (int) g2.getFontMetrics().getStringBounds("1", g2).getWidth() / 2;
	    	int height1 = (int) g2.getFontMetrics().getHeight() / -2 + (int) g2.getFontMetrics().getAscent();
	    	g2.drawString("1", location + 50 - length1, yPos + 50 + height1);
	    }
		if (projID == 2) {
			g2.draw(new Ellipse2D.Double(location + 100, yPos, 100, 100));
			// Calculations used to adjust (x,y) position for "2" to center text inside of projectile
	    	int length2 = (int) g2.getFontMetrics().getStringBounds("2", g2).getWidth() / 2;
	    	int height2 = (int) g2.getFontMetrics().getHeight() / -2 + (int) g2.getFontMetrics().getAscent();
			g2.drawString("2", location + 150 - length2, yPos + 50 + height2);
		}
	}
}
