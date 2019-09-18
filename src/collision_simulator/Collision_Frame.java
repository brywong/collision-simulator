package collision_simulator;

import javax.swing.JFrame;

public class Collision_Frame extends JFrame{
	
	public Collision_Frame() {
		// Define title of JFrame
		super("Collision Simulator");
		
		// Add JPanel (in Collision_Panel) to JFrame
		this.add(new Collision_Panel());
		
		// Make sure program ends when window is closed
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		
		// Allow JFrame to display all objects in the JPanel
		pack();
	}
	
}
