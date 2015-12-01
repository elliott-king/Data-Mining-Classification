package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import src.Tree.Node;

public class HW4_Code {

	public enum Dataset {
		CAR			("car.csv","car_processed.csv", 4,
				new String[] {"Number of Passengers","Trunk Size","Safety"}),
		GOLF		("golf.csv","golf_processed.csv", 5,
				new String[] {"Outlook","Temperature","Humidity","Windy"}),
		NONE 		(null,null,0,null);
		
		private final String filename;
		private final String processed_filename;
		private final int num_cols;
		private final String[] col_headings;
		
		Dataset(String filename, String processed_filename, int num_cols, String[] col_headings)
		{
			this.filename = filename;
			this.processed_filename = processed_filename;
			this.num_cols = num_cols;
			this.col_headings = col_headings;
		}
		
		private String filename() { return filename;}
		@SuppressWarnings("unused")
		private String processed_filename() { return processed_filename;}
		private int num_cols() { return num_cols;}
		private String[] col_headings() { return col_headings;}
	}
	
	static Dataset chosen_dataset;
	
	public static void main(String[] args) {

		
		ArrayList<ArrayList<String>> Data;
		chosen_dataset = Dataset.NONE;
		@SuppressWarnings("unused")
		Tree.Node<String> Decision_Tree;
		
		// Ask user for desired dataset: 
		Scanner user_in = new Scanner(System.in);
		while(chosen_dataset == Dataset.NONE)
		{
			String input;
			System.out.println("Please choose either the car dataset or the golf dataset: ");
			
			input = user_in.nextLine();
			input = input.replaceAll("[^a-zA-Z ]", "").toLowerCase();
			input = input.replaceAll("\n", "");
			input = input.replaceAll(" ", "");
			
			switch(input)
			{
			case "car": chosen_dataset = Dataset.CAR; break;
			case "golf": chosen_dataset = Dataset.GOLF; break;
			default: chosen_dataset = Dataset.NONE; break;
			}
			
			if (chosen_dataset == Dataset.NONE) { System.out.println("Sorry, that is not one of the options.");}
		} // end while
		
		user_in.close();
		
		// The import_data function is copied from HW3.
		/* This is a 2D matrix that corresponds to the layout of the csv files.
		 * Within the overall Arraylist, there is an ArrayList for each column.
		 * In each inside ArrayList is stored the values, in order, for that column.
		 */
		Data = import_data(chosen_dataset);
		ArrayList<Integer> ignore = new ArrayList<Integer>();
		ignore.add(Data.size()-1);
		Decision_Tree = Decision_Tree_Algorithm(Data, ignore);
		Print_Decision_Tree(Decision_Tree, "a");
	}

	private static Tree.Node<String> Decision_Tree_Algorithm(ArrayList<ArrayList<String>> Datastuff, ArrayList<Integer> ignoreIn)
	{
		ArrayList<ArrayList<String>> Data = Datastuff;
		ArrayList<Integer> ignore = ignoreIn;
		Tree.Node<String> node = new Tree.Node<String>();
		ArrayList<String> classes = getIndependentStrings(Data.get(Data.size()-1));
		
		// Check if all records belong to the same class:
		if(classes.size() <= 1) 
		{ 
			node.putData(classes.get(0));
			return node;
		}
		if(ignore.size() == Data.size()) 
		{
			System.out.println("Weird shit");
			node.putData(classes.get(0));
			return node;
		}
		
		// Otherwise, get column to isolate:
		int gini_column = Calculate_Maximum_GINI(Data,ignore);
		ignore.add(gini_column);
		ArrayList<String> attributes = getIndependentStrings(Data.get(gini_column));
		
		// Assign a new AL to store the modified datasets for each attribute
		ArrayList<ArrayList<ArrayList<String>>> set_of_datasets = new ArrayList<ArrayList<ArrayList<String>>>();
		for(int j = 0; j < attributes.size(); j++)
		{
			set_of_datasets.add(new ArrayList<ArrayList<String>>());
			for(int i = 0; i < Data.size(); i++)
			{
				set_of_datasets.get(j).add(new ArrayList<String>());
			}
		}
		while(Data.get(0).size() > 0)
		{
			// System.out.println();
			String temp = Data.get(gini_column).get(0);
			for(int i = 0; i < attributes.size(); i++)
			{
				if(temp.equals(attributes.get(i)))
				{
					for(int j = 0; j < Data.size(); j++)
					{
						// System.out.print(Data.get(j).get(0) + "\t\t\t");
						set_of_datasets.get(i).get(j).add(Data.get(j).get(0));
						Data.get(j).remove(0);
					}
					// System.out.println(); //TODO
					break;
				}
			}
		}
				
		// Now make current node named for the attribute we are isolating:
		node.putData(chosen_dataset.col_headings()[gini_column]);
		System.out.println(node.getData());		// TODO
		
		// Now create a list of the children of this node, one for each version of the attribute:
		ArrayList<Tree.Node<String>> children = new ArrayList<Tree.Node<String>>();
		for(int i = 0; i < attributes.size(); i++)
		{
			children.add(new Tree.Node<String>());
			children.get(i).putData(attributes.get(i));
			// System.out.println(attributes.get(i)); //TODO
			
			// This makes a grandchild, which is what we call the function on recursively:
			ArrayList<Tree.Node<String>> temp = new ArrayList<Tree.Node<String>>();
			temp.add(Decision_Tree_Algorithm(set_of_datasets.get(i),ignore));
			children.get(i).putChildren(temp);
		}
		node.putChildren(children);
		return node;
	}
	
	private static int Calculate_Maximum_GINI(ArrayList<ArrayList<String>> Data, ArrayList<Integer> ignore)
	{
		double max = 0;
		int col = 0;
		
		// Iterate through columns:
		for(int i = 0; i < Data.size(); i++)
		{
			if(!ignore.contains(i))
			{
				double gini = Calculate_GINI(Data.get(i),Data.get(Data.size()-1));
				if(gini > max)
				{
					max = gini;
					col = i;
				}
			}
		}
		System.out.println(max); //TODO
		return col;
	}
	
	private static double Calculate_GINI(ArrayList<String> column, ArrayList<String> classification)
	{
		ArrayList<String> values = getIndependentStrings(column);
		ArrayList<String> classes = getIndependentStrings(classification);
		int[] totals = new int[values.size()];
		int[][] class_individuals = new int[values.size()][classes.size()];
		int sum = column.size();
		double gini = 0;
		
		for (int i = 0; i < column.size(); i++)
		{
			totals[values.indexOf(column.get(i))]++;
			class_individuals[values.indexOf(column.get(i))][classes.indexOf(classification.get(i))]++;
			
		}
		
		for (int i = 0; i < totals.length; i++)
		{
			double temp = 0;
			for(int j = 0; j < class_individuals[0].length; j++)
			{
				temp = temp + Math.pow(((double)class_individuals[i][j])/(double)totals[i], 2);
			}
			temp = 1 - temp;
			gini = gini + ((double)totals[i])/((double)sum)*temp;
		}
		
		return gini;
	}
	

	private static void Print_Decision_Tree(Node<String> Decision_Tree, String append) {
		System.out.println(Decision_Tree.getData() + "\t" + append);
		if(Decision_Tree.getChildren() != null)
		{
			for(char i = 'a'; i < 'a' + Decision_Tree.getChildren().size(); i++)
			{
				String temp = append + (i);
				Print_Decision_Tree(Decision_Tree.getChildren().get(i-'a'),temp);
			}
		}
		
	}
	
	private static ArrayList<String> getIndependentStrings(ArrayList<String> column)
	{
		ArrayList<String> finals = new ArrayList<String>();
		
		for(int i = 0; i < column.size(); i++)
		{
			String stringAtI = column.get(i);
			if(!finals.contains(stringAtI)) { finals.add(stringAtI);}
		}
		return finals;
	}
	
	// The following function is (mostly) copied and pasted from HW3:
	private static ArrayList<ArrayList<String>> import_data(Dataset chosen_set)
	{
		ArrayList<ArrayList<String>> imported_data = new ArrayList<ArrayList<String>>();
		
		Scanner in_from_file = null;
		try
		{
			in_from_file = new Scanner (new File(chosen_set.filename()));
		} catch (FileNotFoundException e) {System.out.print("File not found");}
		in_from_file.useDelimiter(",");
		
		// We will try and dynamically allocate a new 2D dynamic matrix:
		for(int i = 0; i < chosen_set.num_cols(); i++)
		{
			imported_data.add(new ArrayList<String>());
		}
		
		while(in_from_file.hasNext())
		{
			String nextLine = in_from_file.nextLine();
			String[] charSplit = nextLine.split(",");
			
			for(int i = 0; i < chosen_set.num_cols(); i++)
			{
				imported_data.get(i).add(charSplit[i]);
			}
		}
		in_from_file.close();
		return imported_data;
	}
}
