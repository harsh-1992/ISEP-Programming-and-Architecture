package merkle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public abstract class TreeBuilder {
	
	public static MerkleNode getRoot(File file) throws NoSuchAlgorithmException, FileNotFoundException, UnsupportedEncodingException
	{
		return getSubTree(file, Integer.MAX_VALUE);
	}
	
	public static MerkleNode getSubTree(File file, int max) throws NoSuchAlgorithmException, FileNotFoundException, UnsupportedEncodingException
	{
		Queue<MerkleNode> layer = generateLeaves(file, max);
		if(max == Integer.MAX_VALUE) { seeLayer(layer); }
		while(layer.size() > 1)
		{
			layer = computeNextLayer(layer);
			if(max == Integer.MAX_VALUE) { seeLayer(layer); }
		}
		
		// When the algorithm ends, parents contains only 1 MerkleNode, it is the root.
		MerkleNode root = layer.poll();
		return root;
	}
	
	private static Queue<MerkleNode> computeNextLayer(Queue<MerkleNode> layer) throws NoSuchAlgorithmException
	{
		Queue<MerkleNode> parents = new LinkedList<MerkleNode>();
		@SuppressWarnings("unused")
		int initialSize = layer.size();
		
		while(!layer.isEmpty())
		{
			if(layer.size() > 1)
			{
				// Popping the leaves 2 by 2
				MerkleNode n1 = layer.poll();
				MerkleNode n2 = layer.poll();
				
				parents.add(new MerkleTreeNode(n1, n2));
			}
			else
			{
				// Last element: duplicate it				
				MerkleNode poll = layer.poll();
				MerkleTreeNode n = new MerkleTreeNode(poll, poll);
				parents.add(n);
			}
		}
//		System.out.println(String.format("    >>> Merging nodes 2 by 2: started with %s nodes, ended with %s nodes", initialSize, parents.size()));
//		Utils.sep();
		
		return parents;
	}
	
	private static Queue<MerkleNode> generateLeaves(File file, int max) throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException
	{
		Scanner myReader = new Scanner(file);
		Queue<MerkleNode> leaves = new LinkedList<MerkleNode>();
		int cpt = 1;
		while (myReader.hasNextLine()) {
			if(cpt <= max)
			{
				String data = myReader.nextLine();
				
	//			System.out.println(String.format("Reading line %s: %s", cpt, data));
				leaves.add(new MerkleTreeLeaf(cpt++, data));
			}
			else
			{
				break;
			}
		}
		myReader.close();
//		Utils.sep();
		return leaves;
	}
	
	private static void seeLayer(Queue<MerkleNode> layer)
	{
		System.out.println("================");
		for(MerkleNode n : layer)
		{
			Utils.dispNode(n);
		}
		System.out.println("================");
	}

}
