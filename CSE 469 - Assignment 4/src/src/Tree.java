package src;

import java.util.ArrayList;

// Borrowed the structure from a StackOverflow question:
// http://stackoverflow.com/questions/3522454/java-tree-data-structure

public class Tree<String> 
{
	private Node<String> root;

	public Tree(String rootData)
	{
		root = new Node<String>();
		root.data = rootData;
		root.children = new ArrayList<Node<String>>();
		
	}
	
	public static class Node<String>
	{
		private String data;
		private Node<String> parent;
		private ArrayList<Node<String>> children;
		
		public Node()
		{
			children = null;
		}
		
		public String getData() { return data;}
		public Node<String> getParent() { return parent;}
		public ArrayList<Node<String>>getChildren() { return children;}
		
		public void putData(String data) { this.data = data;}
		public void putParent(Node<String> parent) { this.parent = parent;}
		public void putChildren(ArrayList<Node<String>> children) { this.children = children;}
	}
}
