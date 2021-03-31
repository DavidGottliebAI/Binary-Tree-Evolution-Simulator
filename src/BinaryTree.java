import java.util.ArrayList;
import java.util.Random;


public class BinaryTree implements Comparable<BinaryTree> {
	static class TreeNode {
	    String data;
	    TreeNode left, right;    
	    TreeNode(String value) {
	    this.data = value;
	    left = right = null;
	    }
	}
	TreeNode root;
	private TreeNode left;
	private TreeNode right;
	private int data;
	private ArrayList<TreeNode> selectedNodes = new ArrayList<TreeNode>();
//	private int valueBoundsLower;
//	private int valueBoundsUpper;
//	private int sizeMultiplier = 1;
	public double fitness;
//	private String targetTree = "x*10";
	private TreePopulation treePopulation;

	public BinaryTree(TreePopulation treePopulation) {
		this.treePopulation = treePopulation;
	}

	public void printTree() {
		printTree(root);
	}  
	
	public void printTree(TreeNode node) {
		if(node == null) {
			return;
		}
		
		printTree(node.left);
		System.out.print(node.data + " ");
		printTree(node.right);
	}
	
	public double evaluator(TreeNode node, double x) {
		
		try {
			if(node.left == null && node.right == null) { // check for terminal nodes
				// if the terminal node data is not a number (a symbol), then we need to return x or y
				try {
					return Double.parseDouble(node.data);
				} catch(NumberFormatException e) {
					if(node.data.equals("X")) {
						return x;
					}
				}
			}
		} catch(NullPointerException e) {
			return 0;
		}
		
		double leftCalc = this.evaluator(node.left, x);
		double rightCalc = this.evaluator(node.right, x);
		
		if (node.data.equals("+")) { // evaluate operations
			return leftCalc + rightCalc;
		} else if(node.data.equals("-")) {
			return leftCalc - rightCalc;
		} else if(node.data.equals("*")) {
			return leftCalc * rightCalc;
		} else if(node.data.equals("cos")) {
			return Math.cos(rightCalc + leftCalc);
		} else if(node.data.equals("sin")) {
			return Math.sin(rightCalc + leftCalc);
		} else if(node.data.equals("tan")) {
			return Math.tan(rightCalc + leftCalc);
		} else if(node.data.equals("^")) {
			return Math.pow(rightCalc, leftCalc);
		} else if(node.data.equals("/")) {
			return rightCalc / leftCalc;
		} else if(node.data.equals("sqrt")) {
			return Math.sqrt(rightCalc + leftCalc);
		}
		return 0;
	}
	
	public BinaryTree deepCopy(TreeNode givenNode) {
		
		BinaryTree newBinaryTree = new BinaryTree(this.treePopulation);
		TreeNode newNode = new TreeNode(givenNode.data);
		
		deepCopy(givenNode, newNode);
		newBinaryTree.root = newNode;
		newBinaryTree.left = newNode.left;
		newBinaryTree.right = newNode.right;
		
		newBinaryTree.updateFitness();
		return newBinaryTree;
	}
	
	private void deepCopy(TreeNode oldNode, TreeNode newNode) {
		if(oldNode == null) {
			return;
		}
		if(oldNode.left != null) {
			TreeNode newleftNode = new TreeNode(oldNode.left.data);
			newNode.left = newleftNode;
			deepCopy(oldNode.left, newNode.left);
		}
		if(oldNode.right != null) {
			TreeNode newRightNode = new TreeNode(oldNode.right.data);
			newNode.right = newRightNode;
			deepCopy(oldNode.right, newNode.right);
		}
	}

	public void selectRandomNodes(TreeNode node, int mutatePercentage, BinaryTree bt) {
		Random random = new Random(); 
		if(random.nextInt(100) + 1 <= mutatePercentage) {
			bt.addSelectedNodes(node);
		}
		try {
			if(node.left == null && node.right == null) { // chance to add nodes (additional mutation operation)
				int chanceForNewNode = random.nextInt(100);
				String[] functions = {"+", "-", "*", "cos", "sin", "tan", "sqrt", "/", "^"};
//				String terminalValue = Math.round((this.valueBoundsLower + (((this.valueBoundsUpper - this.valueBoundsLower) + 1) * Math.random())) * 10.0) / 10.0 + "";
				String terminalValue = Math.round((this.treePopulation.valueBoundsLower + (((this.treePopulation.valueBoundsUpper - this.treePopulation.valueBoundsLower) + 1) * Math.random())) * 10.0) / 10.0 + "";
				if(chanceForNewNode == 0) {
					node.data = functions[random.nextInt(functions.length)]+ "";
					node.left = new TreeNode(terminalValue);
					node.right = new TreeNode(terminalValue);
				}
				return;
			} else {
				selectRandomNodes(node.left, mutatePercentage, bt);
				selectRandomNodes(node.right, mutatePercentage, bt);
			}
		} catch(NullPointerException e) {
			return;
		}
	}
	
	public void updateFitness() {
		int populationFitness = 0;
		for(int i = -50; i < 50; i++) {
			String expression = convertToString(this.treePopulation.targetTree, i);
			populationFitness += Math.pow(this.evaluator(this.root, i) - eval(expression), 2);
		}
		populationFitness += this.countTotalNodes(this.root) * this.treePopulation.sizeMultiplier;
		this.setFitness(Math.abs(populationFitness));
	}
	
	public String convertToString(String targetString, int num) {
		String str = num + "";
		String newStr = targetString.replaceAll("x", str);
		return newStr;
	}

	public void mutateNodes(TreeNode node, double mutationRate, BinaryTree bt) {
		Random random = new Random();
		double newTerminal = 0;
		String[] functions = {"+", "-", "*", "cos", "sin", "tan", "sqrt", "/", "^"};
		int mutatePercentage = (int) Math.ceil(mutationRate / 100.0 * 100.0);
		selectRandomNodes(node, mutatePercentage, bt);
		for(int i = 0; i < getSelectedNodes().size() - 1; i++) {
			try {
				if(!getSelectedNodes().get(i).data.equals("X") && !getSelectedNodes().get(i).data.equals("Y")) {
					Double.parseDouble((getSelectedNodes().get(i).data));
				}
				newTerminal = Math.round((this.treePopulation.valueBoundsLower + (((this.treePopulation.valueBoundsUpper - this.treePopulation.valueBoundsLower) + 1) * Math.random())) * 10.0) / 10.0; // from -50 to 50
				getSelectedNodes().get(i).data = newTerminal + "";
				newTerminal = random.nextInt(5); // 10% chance X or Y
				if(newTerminal == 0) {
					getSelectedNodes().get(i).data = "X";
				}
			} catch(NumberFormatException e) {
//				if(getSelectedNodes().get(i).data.equals("cos") || node.data.equals("sin")) { // check to see if cos/sin
//					newTerminal = random.nextInt(2) + functions.length - 2;
//				} else {
//					newTerminal = random.nextInt(functions.length);
//				}
				getSelectedNodes().get(i).data = functions[(int) newTerminal] + ""; // change parent node (might not work)
			}
		}
		selectedNodes.clear();
			
	}
	
	public TreeNode selectOneRandomNode(TreeNode node) {
		Random random = new Random();
		if(node.left == null && node.right == null) {
			return node;
		}
		if(node.left != null) {
			if(random.nextInt(this.countTotalNodes(this.left)) == 0) {
				return node.left;
			}
			selectOneRandomNode(node.left);
		} else if(node.right != null) {
			if(random.nextInt(this.countTotalNodes(this.right)) == 0) {
				return node.right;
			}
			selectOneRandomNode(node.right);
		}
		return node;
	}
	
	public void crossover(BinaryTree otherTree) {
		Random random = new Random();
		if(random.nextBoolean()) {
			TreeNode otherRight = otherTree.deepCopy(selectOneRandomNode(otherTree.root)).root.right;
			TreeNode thisRight = this.deepCopy(selectOneRandomNode(root)).root.right;
			otherTree.root.right = thisRight;
			this.root.right = otherRight;
		}
	}
	
	public BinaryTree createTree(int n) {
		
		BinaryTree newBinaryTree = new BinaryTree(this.treePopulation);
		String[] functions = {"+", "-", "*", "cos", "sin", "tan", "sqrt", "/", "^"};
	    Random random = new Random();
		TreeNode newNode = new TreeNode(functions[random.nextInt(functions.length)] + "");
		
		create(n, newNode.data, newNode);
		newBinaryTree.root = newNode;
		newBinaryTree.left = newNode.left;
		newBinaryTree.right = newNode.right;
		
		return newBinaryTree;
	}

	public void create(int n, String data, TreeNode newNode) {
		// make sure single nodes have cos/sin as parent node
		Random random = new Random();
		String[] functions = {"+", "-", "*", "cos", "sin", "tan", "sqrt", "/", "^"};
		String terminalValue = Math.round((this.treePopulation.valueBoundsLower + (((this.treePopulation.valueBoundsUpper - this.treePopulation.valueBoundsLower) + 1) * Math.random())) * 10.0) / 10.0 + "";
		if(n == 0) {
			newNode.data = functions[random.nextInt(functions.length)] + "";
			TreeNode nextNode1 = new TreeNode(terminalValue);
			newNode.left = nextNode1;
			TreeNode nextNode2 = new TreeNode(terminalValue);
			newNode.right = nextNode2;
			if(true) {
				if(random.nextBoolean()) {
					TreeNode nextNode3 = new TreeNode("X");
					newNode.data = functions[random.nextInt(functions.length)] + "";
					newNode.left = nextNode3;
				} else {
					TreeNode nextNode4 = new TreeNode("X");
					newNode.data = functions[random.nextInt(functions.length)] + "";
					newNode.right = nextNode4;
				}
			}
			return;
		}
		
		TreeNode nextNode1 = new TreeNode(functions[random.nextInt(functions.length)]+ "");
		newNode.left = nextNode1;
		TreeNode nextNode2 = new TreeNode(functions[random.nextInt(functions.length)]+ "");
		newNode.right = nextNode2;
		
		int remainingNodes = random.nextInt(n);
		double chanceToBranch = random.nextInt(10);
		
		if(chanceToBranch <= 8) {
			create(remainingNodes, functions[random.nextInt(functions.length)]+ "", newNode.left);
			create(n - remainingNodes - 1, functions[random.nextInt(functions.length)]+ "", newNode.right);
		} else {
			TreeNode finalNode1 = new TreeNode(terminalValue);
			newNode.left = finalNode1;
			TreeNode finalNode2 = new TreeNode(terminalValue);
			newNode.right = finalNode2;
			if(random.nextInt(5) == 0) {
				if(random.nextBoolean()) {
					TreeNode finalNode3 = new TreeNode("X");
					newNode.left = finalNode3;
				} else {
					TreeNode finalNode4 = new TreeNode("X");
					newNode.right = finalNode4;
				}
			}
			return;
		}
	}
	
	public int countTotalNodes(TreeNode node) {
		return countTerminalNodes(node) * 2 + 1;
	}
	
	public int countTerminalNodes(TreeNode node) {
		if(node == null) {
			return 0;
		}
		if(node.left != null && node.left != null) {
			return 1 + countTerminalNodes(node.right) + countTerminalNodes(node.left);
		} else if(node.right != null) {
			return 1 + countTerminalNodes(node.right);
		} else if(node.left != null) {
			return 1 + countTerminalNodes(node.left);
		}
		return 0;
	}
	
	public static double eval(final String str) { // taken from https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor
	        // factor = `+` factor | `-` factor | `(` expression `)`
	        //        | number | functionName factor | factor `^` factor

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

	            return x;
	        }
	    }.parse();
	}
	
	@Override
	public int compareTo(BinaryTree otherTree) {
		return (int) Math.ceil((otherTree.fitness - this.fitness));
	}

	private void addSelectedNodes(TreeNode node) {
		selectedNodes.add(node);
	}

	public ArrayList<TreeNode> getSelectedNodes() {
		return selectedNodes;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public double getFitness() {
		return this.fitness;
	}
	
//	public void setValueBounds(int lowerValueBounds, int upperValueBounds) {
//		this.valueBoundsLower = lowerValueBounds;
//		this.valueBoundsUpper = upperValueBounds;
//	}
	
//	public void setSizeMultiplier(int sizeMultiplier) {
//		this.sizeMultiplier = sizeMultiplier;
//	}

//	public void setTargetTree(String targetTree) {
//		this.targetTree = targetTree;
//	}
	
}