import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class BinaryTreeVisualizer{
	
	public TreePopulation treePopulation;
	public JFrame frameMain;
	private JFrame frameVisual;
	private JPanel adminPanel;
	private JTextField mutateField;
	private JTextField populationField;
	private JTextField generationsField;
	private JTextField sizeMultiplierField;
	private JTextField valueBoundsUpperField;
	private JTextField valueBoundsLowerField;
	private JTextField targetTreeField;
	private JButton startButton;
	private JButton resetButton;
	private int mutationRate = 10;
	private int numberGenerations = 50;
	private int populationSize = 1000;
	private int sizeMultiplier = 5;
	private int valueBoundsLower;
	private int valueBoundsUpper;
	private String targetTree = "";
	public boolean evolutionRunning = false;
	private int DELAY = 50;
	private BestBinaryTree bestTree;
	

	public BinaryTreeVisualizer() {
		
		this.treePopulation = new TreePopulation();
		this.bestTree = new BestBinaryTree(this.treePopulation);
		this.frameMain = new JFrame();
		this.frameVisual = new JFrame();
		this.frameMain.setTitle("Select Some Parameters!");
		this.frameVisual.setTitle("Best Binary Tree!");
		this.adminPanel = new JPanel();

		this.startButton = new JButton("Ready");
		this.startButton.addActionListener(new startListener(this, this.startButton));
		this.resetButton = new JButton("Reset");
		this.resetButton.addActionListener(new resetListener(this, this.resetButton));

		JLabel mutateLabel = new JLabel("Mutation Rate");
		this.mutateField = new JTextField("10");
		mutateField.setPreferredSize(new Dimension(30, 20));
		JLabel generationsLabel = new JLabel("Number of Generation");
		this.generationsField = new JTextField("50");
		generationsField.setPreferredSize(new Dimension(30, 20));
		JLabel populationLabel = new JLabel("Population Size");
		this.populationField = new JTextField("1000");
		populationField.setPreferredSize(new Dimension(40, 20));
		JLabel sizeMultiplierLabel = new JLabel("Size Multiplier");
		this.sizeMultiplierField = new JTextField("5");
		sizeMultiplierField.setPreferredSize(new Dimension(30, 20));
		JLabel valueBoundslabel = new JLabel("Value Bounds");
		this.valueBoundsLowerField = new JTextField("-20");
		this.valueBoundsUpperField = new JTextField("20");
		JLabel targetTreeLabel = new JLabel("Target Tree");
		this.targetTreeField = new JTextField("x*10");
		targetTreeField.setPreferredSize(new Dimension(100, 20));

		this.adminPanel.add(mutateLabel);
		this.adminPanel.add(mutateField);
		this.adminPanel.add(generationsLabel);
		this.adminPanel.add(generationsField);
		this.adminPanel.add(populationLabel);
		this.adminPanel.add(populationField);
		this.adminPanel.add(sizeMultiplierLabel);
		this.adminPanel.add(sizeMultiplierField);
		this.adminPanel.add(valueBoundslabel);
		this.adminPanel.add(valueBoundsLowerField);
		this.adminPanel.add(valueBoundsUpperField);
		this.adminPanel.add(targetTreeLabel);
		this.adminPanel.add(targetTreeField);
		this.adminPanel.add(startButton);
		this.adminPanel.add(resetButton);
		this.frameMain.add(adminPanel, BorderLayout.SOUTH);
		this.frameVisual.add(this.bestTree);
		
		Timer t = new Timer(DELAY , new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (evolutionRunning) {
					bestTree.setBestTree(treePopulation.binaryTrees.get(treePopulation.binaryTrees.size() - 1));
					treePopulation.evolutionLoop();
					System.out.println("Best of Generation: " + treePopulation.numLoops);
					treePopulation.binaryTrees.get(treePopulation.binaryTrees.size()-1).printTree();
					System.out.println("");
					frameVisual.repaint();
					if(treePopulation.numLoops >= numberGenerations) {
						evolutionRunning = false;
					}
				}
				if(treePopulation.numLoops >= numberGenerations) {
					instantReset();
				}
			}
		});

		t.start();
		
		this.frameVisual.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frameVisual.setSize(1800, 1000);
		this.frameVisual.setLocation(10, 10); // might want to play with later
		this.frameVisual.setVisible(true);

		this.frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frameMain.setSize(1100, 100);
		this.frameMain.setLocation(10, 10); // might want to play with later
		this.frameMain.setVisible(true);
	}

	public int getTextFieldNumber(JTextField textField) { // may need refactoring
		String text = textField.getText();
		int textFieldNumber = Integer.parseInt(text);
		return textFieldNumber;
	}
	
	public void resetAll() {
		this.startButton.setText("Ready");
		this.treePopulation.reset();
		this.bestTree.reset();
	}
	
	public void instantReset() {
		this.startButton.setText("Ready");
		this.treePopulation.reset();
	}
	
	public void setMutationRate() {
		this.mutationRate = getTextFieldNumber(mutateField);
		this.treePopulation.setMutationRate(this.mutationRate);
	}

	public void setPopulationSize() {
		this.populationSize = getTextFieldNumber(populationField);
		this.treePopulation.setPopulationSize(this.populationSize);
	}

	public void setGenerations() {
		this.numberGenerations = getTextFieldNumber(generationsField);
		this.treePopulation.setGenerations(this.numberGenerations);
	}

	public void setSizeMultiplier() {
		this.sizeMultiplier = getTextFieldNumber(sizeMultiplierField);
		this.treePopulation.setSizeMultiplier(this.sizeMultiplier);
	}

	public void setValueBounds() {
		this.valueBoundsLower = getTextFieldNumber(valueBoundsLowerField);
		this.valueBoundsUpper = getTextFieldNumber(valueBoundsUpperField);
		this.treePopulation.setValueBounds(this.valueBoundsLower, this.valueBoundsUpper);
	}
	
	public void setTargetTree() {
		this.targetTree = targetTreeField.getText();
		this.treePopulation.setTargetTree(this.targetTree);
	}

	public void setEvolutionRunning(boolean b) {
		this.evolutionRunning = b;
	}

	
}
