import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class resetListener implements ActionListener {
	
	private BinaryTreeVisualizer binaryTreeVisualizer;
	private JButton resetButton;

	public resetListener(BinaryTreeVisualizer binaryTreeVisualizerComponent, JButton resetButton) {
		this.binaryTreeVisualizer = binaryTreeVisualizerComponent;
		this.resetButton = resetButton;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.binaryTreeVisualizer.setEvolutionRunning(false);
		this.binaryTreeVisualizer.resetAll();
	}

}
