import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TreePopulation {

	private int mutationRate = 10;
	private int generations = 50;
	private int populationSize = 1000;
	public int sizeMultiplier = 5;
	public int numLoops = 0;
	public ArrayList<BinaryTree> binaryTrees = new ArrayList<BinaryTree>();
	private ArrayList<Double> bestFitnesses = new ArrayList<Double>();
	public String targetTree = "";
	public int valueBoundsLower;
	public int valueBoundsUpper;
	private BestBinaryTree bbt = new BestBinaryTree(this);

	public TreePopulation() {
		Random random = new Random();
		for (int i = 0; i < populationSize; i++) {
			BinaryTree bt = new BinaryTree(this);
			int treeSize = random.nextInt(3);
			bt = bt.createTree(treeSize);
			binaryTrees.add(bt);
		}
	}

	public void evolutionLoop() {
		if(this.numLoops >= generations) {
			return;
		}
		this.numLoops += 1;
		updateFitnessScores();
		Collections.sort(binaryTrees);
		select();
		repopulate();
		crossover();
		mutate();
	}

	private void crossover() {
		for (int i = 0; i < populationSize; i += 2) {
			if (binaryTrees.get(i).root != null && binaryTrees.get(i + 1) != null) {
				binaryTrees.get(i).crossover(binaryTrees.get(i + 1));
			}
		}
	}
	
	public void reset() {
		binaryTrees.clear();
		this.numLoops = 0;
		Random random = new Random();
		for (int i = 0; i < populationSize; i++) {
			BinaryTree bt = new BinaryTree(this);
			int treeSize = random.nextInt(3);
			bt = bt.createTree(treeSize);
			binaryTrees.add(bt);
		}
	}

	private void repopulate() {
		ArrayList<BinaryTree> repopulatedBinaryTrees = new ArrayList<BinaryTree>();
		int index = binaryTrees.size() - 1;
		while (repopulatedBinaryTrees.size() < binaryTrees.size() * 2) { // * 2 because binaryTrees has been truncated
			if (index < (binaryTrees.size() - 1) / 2) {
				index = 0;
			}
			BinaryTree newBinaryTree = binaryTrees.get(index).deepCopy(binaryTrees.get(index).root);
			repopulatedBinaryTrees.add(newBinaryTree);
			index--;
		}
		binaryTrees.clear();
		for(int i = 0; i < repopulatedBinaryTrees.size(); i++) {
			binaryTrees.add(repopulatedBinaryTrees.get(i));
		}
//		while (binaryTrees.size() < populationSize) { // * 2 because binaryTrees has been truncated
//			if (index > populationSize / 2) {
//				index = 0;
//			}
//			BinaryTree newBinaryTree = binaryTrees.get(index).deepCopy(binaryTrees.get(index).root);
//			binaryTrees.add(newBinaryTree);
//			index++;
//		}
	}

	public void updateFitnessScores() {
		for (int j = 0; j < binaryTrees.size(); j++) {

			int populationFitness = 0;
			for (int i = -50; i < 51; i++) {
//				String expression = targetTree.replace('x', (char) (i+'0'));
				String expression = convertToString(targetTree, i);
//				System.out.println(expression);
				populationFitness += Math
						.pow(binaryTrees.get(j).evaluator(binaryTrees.get(j).root, i) - eval(expression), 2);
			}
			double averageNodes = 0;
			for (int i = 0; i < binaryTrees.size(); i++) {
				averageNodes += binaryTrees.get(i).countTotalNodes(binaryTrees.get(i).root);
			}
			averageNodes /= binaryTrees.size();
			populationFitness += averageNodes * sizeMultiplier;
			binaryTrees.get(j).setFitness(populationFitness);
		}
	}
	
	public String convertToString(String targetString, int num) {
		String str = num + "";
		String newStr = targetString.replaceAll("x", str);
		return newStr;
	}

	public void select() {
		for (int i = 0; i < populationSize / 2; i++) {
			binaryTrees.remove(0); // since sorted from largest to smallest
		}
	}

	public void mutate() {
		for (int i = 0; i < populationSize; i++) {
			binaryTrees.get(i).mutateNodes(binaryTrees.get(i).root, mutationRate, binaryTrees.get(i));
		}
	}

	public void setMutationRate(int mutationRate) {
		this.mutationRate = mutationRate;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public void setGenerations(int numberGenerations) {
		this.generations = numberGenerations;
	}

	public void setSizeMultiplier(int sizeMultiplier) {
		this.sizeMultiplier = sizeMultiplier;
//		for(int i = 0; i < binaryTrees.size(); i++) {
//			binaryTrees.get(i).setSizeMultiplier(sizeMultiplier);
//		}
	}

	public void setValueBounds(int lowerValueBounds, int upperValueBounds) {
		this.valueBoundsLower = lowerValueBounds;
		this.valueBoundsUpper = upperValueBounds;
//		for(int i = 0; i < binaryTrees.size(); i++) {
//			binaryTrees.get(i).setValueBounds(lowerValueBounds, upperValueBounds);
//		}
	}
	
	public void setTargetTree(String targetTree) {
		this.targetTree = targetTree;
//		for(int i = 0; i < binaryTrees.size(); i++) {
//			binaryTrees.get(i).setTargetTree(targetTree);
//		}
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

}
