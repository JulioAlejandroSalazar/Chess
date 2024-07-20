package main;

import javax.swing.JFrame;

public class Main {
	
	static MainPanel mainPanel = new MainPanel();

	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setTitle("Chess");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		frame.add(mainPanel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		mainPanel.launchGame();
		
	}

}
