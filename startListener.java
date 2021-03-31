import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class startListener implements ActionListener {
	
	private BinaryTreeVisualizer binaryTreeVisualizer;
	private JButton startButton;

	public startListener(BinaryTreeVisualizer binaryTreeVisualizer, JButton startButton) {
		this.binaryTreeVisualizer = binaryTreeVisualizer;
		this.startButton = startButton;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.binaryTreeVisualizer.setMutationRate();
		this.binaryTreeVisualizer.setPopulationSize();
		this.binaryTreeVisualizer.setGenerations();
		this.binaryTreeVisualizer.setSizeMultiplier();
		this.binaryTreeVisualizer.setValueBounds();
		this.binaryTreeVisualizer.setTargetTree();
		if(this.startButton.getText().equals("Ready")) {
			this.startButton.setText("Start");
		} else if(this.startButton.getText().equals("Start") && !this.binaryTreeVisualizer.evolutionRunning) {
			this.startButton.setText("Stop");
			this.binaryTreeVisualizer.setEvolutionRunning(true);
		} else if(this.startButton.getText().equals("Stop") && this.binaryTreeVisualizer.evolutionRunning) {
			this.startButton.setText("Start");
			this.binaryTreeVisualizer.setEvolutionRunning(false);
		} else {
			this.startButton.setText("Please Reset");
			this.binaryTreeVisualizer.setEvolutionRunning(false);
		}
	}

}
