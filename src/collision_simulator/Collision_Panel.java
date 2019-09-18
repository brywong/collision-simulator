package collision_simulator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.text.DecimalFormat;

public class Collision_Panel extends JPanel{

	Projectile proj1;
	Projectile proj2;

	Timer timer;

	public static final int roomLength = 500; // define bounds for position
		
	public static final int yPos = 250; // y-pos for floor of projectiles
	
	public static final Dimension DIM = new Dimension(roomLength + 200, yPos + 200); // define window

	public static final int FPS = 40; // establish how often the timer in setUpTimer() function repeats

	float offset = 0; // cycles timer needs to go through before collision occurs
	
	boolean firstRun = true; // establish initial offset + distance values
	
	int prevCode; // used to determine type of collision in order to set new velocities
	
    private static DecimalFormat df = new DecimalFormat("0.00"); // rounding of displayed values to 2 decimal places

	public Collision_Panel() {
		this.setPreferredSize(DIM);
		boolean definedVar = false;
		// Define projectiles via JTextFields
		while(!definedVar) {
			definedVar = defineProjectiles();
		}
		setUpTimer();
	}

	private boolean defineProjectiles() {
		JTextField massOne = new JTextField(2);
		JTextField velOne = new JTextField(2);
		JTextField locOne = new JTextField(2);
		JTextField massTwo = new JTextField(2);
		JTextField velTwo = new JTextField(2);
		JTextField locTwo = new JTextField(2);

		JPanel display = new JPanel();
		display.setLayout(new BoxLayout(display, BoxLayout.X_AXIS));

		JPanel projOne = new JPanel();
		JPanel projTwo = new JPanel();
		projOne.setLayout(new BoxLayout(projOne, BoxLayout.Y_AXIS));
		projTwo.setLayout(new BoxLayout(projTwo, BoxLayout.Y_AXIS));

		projOne.add(new JLabel("Mass of Projectile 1 (kg):"));
		projOne.add(massOne);
		projOne.add(new JLabel("Velocity of Projectile 1 (m/s):"));
		projOne.add(velOne);
		projOne.add(new JLabel("Location of Projectile 1 (m):"));
		projOne.add(new JLabel("(Left-most Projectile || Max Value: 500)"));
		projOne.add(locOne);

		display.add(projOne);

		projTwo.add(new JLabel("Mass of Projectile 2 (kg):"));
		projTwo.add(massTwo);
		projTwo.add(new JLabel("Velocity of Projectile 2 (m/s):"));
		projTwo.add(velTwo);
		projTwo.add(new JLabel("Location of Projectile 2 (m):"));
		projTwo.add(new JLabel("(Right-most Projectile || Max Value: 500)"));
		projTwo.add(locTwo);

		display.add(projTwo);

		int result = JOptionPane.showConfirmDialog(null, display, 
				"Please Enter Projectile Information", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try {
				proj1 = new Projectile(Float.parseFloat(massOne.getText()), Float.parseFloat(velOne.getText()),
						Float.parseFloat(locOne.getText()), 1, yPos);
				proj2 = new Projectile(Float.parseFloat(massTwo.getText()), Float.parseFloat(velTwo.getText()),
						Float.parseFloat(locTwo.getText()), 2, yPos);
				return true;
			}
			catch (Exception e) {
				return false;
			}
		}
		else {
			// close simulation + window somehow (TBD)
			return false;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		// Draw projectiles on panel
		if (proj1 != null && proj2 != null) {
			proj1.drawProjectile(g2);
			proj2.drawProjectile(g2);
		}
		
		g2.setFont(new Font("Arial", Font.BOLD, 30));
		g2.drawString("Projectile 1:", 50, 50);
		g2.drawString("Projectile 2:", 300, 50);
		
		g2.setFont(new Font("Arial", 0, 20));
		g2.drawString("Mass: " + df.format(proj1.mass()) + " kg", 50, 80);
		g2.drawString("Position: " + df.format(proj1.location()) + " m", 50, 110);
		g2.drawString("Velocity: " + df.format(proj1.velocity()) + " m/s", 50, 140);
		
		g2.drawString("Mass: " + df.format(proj2.mass()) + " kg", 300, 80);
		g2.drawString("Position: " + df.format(proj2.location()) + " m", 300, 110);
		g2.drawString("Velocity: " + df.format(proj2.velocity()) + " m/s", 300, 140);
		
		g2.setFont(new Font("Arial", Font.ITALIC, 15));
		g2.drawString("Scale: 1 meter (m) = 1 pixel (px)", 50, 225);
		
		g2.setStroke(new BasicStroke(1));
		g2.drawLine(0, yPos + 100, roomLength + 200, yPos + 100);
		
		g2.setFont(new Font("Montserrat", Font.BOLD, 20));
		int textLength = (int) g2.getFontMetrics().getStringBounds("By Bryce Wong", g2).getWidth();
		g2.drawString("By Bryce Wong", roomLength + 190 - textLength, yPos + 190);
	}

	private void setUpTimer() {
		// 1 sec / FPS = # milliseconds/frame
		timer = new Timer(1000/FPS, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (offset <= 0 || firstRun) {
					if (firstRun) {
						firstRun = false;
					}
					else {
						if (prevCode == 0) {
							float new_v1 = newVelocity(proj1, proj2);
							float new_v2 = newVelocity(proj2, proj1);
							proj1.changeVelocity(new_v1);
							proj2.changeVelocity(new_v2);
							System.out.println(new_v1 + " " + new_v2);
						}
						else if (prevCode == 1) {
							proj1.changeVelocity(-proj1.velocity());
						}
						else if (prevCode == 2) {
							proj2.changeVelocity(-proj2.velocity());
						}
						else {
							proj1.changeVelocity(-proj1.velocity());
							proj2.changeVelocity(-proj2.velocity());
						}
					}
					// Calculate projected distance traveled by each projectile
					ArrayList<Float> distanceTravelled = calculateDistance();
					// Check if distances are valid (no collision possible or hit wall before collision occurs)
					int errorCode = checkDistanceValues(distanceTravelled);
					if (errorCode == 0) {
						// Plug values and change velocities
						System.out.println("Projectiles will Collide");
						offset = distanceTravelled.get(0) / proj1.velocity() * FPS;
					}
					// Calculate distance to wall
					else {
						System.out.println("No Collision");
						if (errorCode == 1) {
							offset = proj1.location() / Math.abs(proj1.velocity()) * FPS;
						}
						else {
							offset = (500 - proj2.location()) / Math.abs(proj2.velocity()) * FPS;
						}
					}
					prevCode = errorCode;
					System.out.println(errorCode + " " + distanceTravelled);
				}
				offset--;
				proj1.changeLocation(proj1.location() + proj1.velocity() / FPS);
				proj2.changeLocation(proj2.location() + proj2.velocity() / FPS);
				// Change display with new projectile locations
				repaint();
			}
		});
		timer.start();
	}

	private ArrayList<Float> calculateDistance() {
		ArrayList<Float> distanceValues = new ArrayList<Float>();
		float p1Vel = proj1.velocity();
		float p2Vel = proj2.velocity();
		float distanceBetween = Math.abs((proj2.location() - proj1.location()));
		// Collision won't occur because projectiles never meet (same direction, but one faster than other)
		if (p2Vel > p1Vel) {
			return distanceValues;
		}
		// proj1 o--> <--o proj2 collision
		if (p1Vel > 0 && p2Vel < 0) {
			float velocityRatio = Math.abs(p1Vel / p2Vel);
			float x1 = (velocityRatio * distanceBetween) / (1 + velocityRatio);
			float x2 = x1 - distanceBetween;
			distanceValues.add(x1);
			distanceValues.add(x2);
		}
		// proj1 o---> proj2 o-> collision
		else if (p1Vel > 0 && p2Vel >= 0 && p1Vel != p2Vel) {
			float baseDistance = p2Vel * distanceBetween / (p1Vel - p2Vel);
			distanceValues.add(baseDistance + distanceBetween);
			distanceValues.add(baseDistance);
		}
		// <-o proj1 <---o proj2 collision
		else if (p1Vel <= 0 && p2Vel < 0 && p1Vel != p2Vel) {
			float baseDistance = Math.abs(-p1Vel * distanceBetween / (-p2Vel + p1Vel));
			distanceValues.add(-baseDistance);
			distanceValues.add(-baseDistance - distanceBetween);
		}
		return distanceValues;
	}

	private int checkDistanceValues(ArrayList<Float> a) {
		float p1Vel = proj1.velocity();
		float p2Vel = proj2.velocity();
		// If projectiles cannot collide
		if (a.size() == 0 || (a.get(0) == 0 && a.get(1) == 0)) {
			// <--o proj1 proj2 o--> collision
			if ( (p1Vel < 0 && p2Vel > 0) ) {
				float t1 = proj1.location() / Math.abs(p1Vel);
				float t2 = ((float) roomLength - proj2.location()) / Math.abs(p2Vel);
				// proj1 will hit left wall first
				if (t1 < t2) {
					return 1;
				}
				// proj2 will hit right wall first
				else if (t2 < t1){
					return 2;
				}
				// Both projectiles will hit the wall at the same time (special case)
				else {
					return 3;
				}
			}
			else {
				// <---o proj1 <-o proj2 collision (proj1 hits left wall first)
				if (p1Vel < 0) {
					return 1;
				}
				// proj1 o--> proj2 o--> collision (proj2 hits right wall first)
				else {
					return 2;
				}
			}
		}
		// Projectiles do collide (theoretically)
		else {
			// Check for potential collision occuring past walls
			float d1 = a.get(0);
			float d2 = a.get(1);
			// proj1 collides with left wall before collision occurs
			if (proj1.velocity() < 0 && Math.abs(d1) > proj1.location()) {
				return 1;
			}
			// proj2 collides with right wall before collision occurs
			else if (proj2.velocity() > 0 && (d2 + proj2.location()) > 500){
				return 2;
			}
			else {
				return 0;
			}
		}
	}

	private float newVelocity(Projectile p1, Projectile p2) {
		float m1 = p1.mass();
		float m2 = p2.mass();
		float v1 = p1.velocity();
		float v2 = p2.velocity();
		float newVelocity = (2 * m2 * v2 + (m1 - m2) * v1) / (m1 + m2);
		return newVelocity;
	}

}
