import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

public class BestBinaryTree extends JComponent {
	
	private int centerX = 850; // 900 (half frame length) - radius / 2
	private int centerY = 150;
	private int radius = 100;
	private int scaling = 300;
	private boolean update = true;
	private BinaryTree bt;
	
	public BestBinaryTree(TreePopulation treePopulation) {
		this.bt = new BinaryTree(treePopulation);
	}

	public void setBestTree(BinaryTree bestTree) {
		this.bt =  bestTree.deepCopy(bestTree.root);
		this.update = true;
	}
	
	public void reset() { // reset binary tree
		this.bt.root = null;
		this.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setStroke(new BasicStroke(10));
		g2.setFont(g2.getFont().deriveFont(50.0f));
		
		if(update) {
			if(this.bt.root != null) {
				g2.draw(new Ellipse2D.Double(centerX - radius, centerY - radius, 2.0 * radius, 2.0 * radius)); // root  
				g2.drawString(this.bt.root.data + "", centerX - 50, centerY + 25);
				if(this.bt.root.left != null) {
					paintComponentLeftNodes(g2, this.bt.root.left, centerX, centerY + radius, 0);	
				} 
				if(this.bt.root.right != null) {
					paintComponentRightNodes(g2, this.bt.root.right, centerX, centerY + radius, 0);
				}
			}
		}
		this.update = false;
	}

	protected void paintComponentLeftNodes(Graphics g, BinaryTree.TreeNode node, int previousX, int previousY, int depth) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		int newLineX = previousX - 400 + scaling * depth;
		int newLineY = previousY + 400 - radius - scaling;
		int newNodeX = previousX - radius - 400 + scaling * depth;
		int newNodeY = previousY - 2 * radius + 600 - radius - scaling;
		int newStringX = previousX - 50 - 400 + scaling * depth;
		int newStringY = previousY - radius -70 + 600 - scaling;
		
		g2.drawLine(previousX, previousY, newLineX, newLineY);
		g2.draw(new Ellipse2D.Double(newNodeX, newNodeY, 2.0 * radius, 2.0 * radius));
		g2.drawString(node.data + "", newStringX, newStringY);
		
		if(node.left != null) {
			paintComponentLeftNodes(g2, node.left, newLineX, newLineY + 2 * radius, depth + 1);
		} 
		if(node.right != null) {
			paintComponentRightNodes(g2, node.right, newLineX, newLineY + 2 * radius , depth + 1);
		}
		return;
	}
	
	private void paintComponentRightNodes(Graphics2D g, BinaryTree.TreeNode node, int previousX, int previousY, int depth) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		int newLineX = previousX + 400 - scaling * depth;
		int newLineY = previousY + 400 - radius - scaling;
		int newNodeX = previousX - radius + 400 - scaling * depth;
		int newNodeY = previousY - 2 * radius + 600 - radius - scaling;
		int newStringX = previousX - 50 + 400 - scaling * depth;
		int newStringY = previousY - radius -70 + 600 - scaling;
		
		g2.drawLine(previousX, previousY, newLineX, newLineY);
		g2.draw(new Ellipse2D.Double(newNodeX, newNodeY, 2.0 * radius, 2.0 * radius));
		g2.drawString(node.data + "", newStringX, newStringY);
		
		if(node.left != null) {
			paintComponentLeftNodes(g2, node.left, newLineX, newLineY + 2 * radius, depth + 1);
		} 
		if(node.right != null) {
			paintComponentRightNodes(g2, node.right, newLineX, newLineY + 2 * radius, depth + 1);
		}
		return;
	}
}
