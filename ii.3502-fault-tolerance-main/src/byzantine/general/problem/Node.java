package byzantine.general.problem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Node
{
	private String name;
	private int value;
	private boolean faulty;
	
	/**
	 * Compares the Vi-s of every node.
	 * 3 different situations are possible:
	 * - At least one Vi of j is different -->
 	 * A node of the same hierarchy is faulty. Applying majority algorithm. 
	 * - All the Vi of j are equals, but values inside the vector are different -->
	 * Sending node is faulty. Applying a set algorithm. In our case, consensus is value 999
	 * - All the Vi of j are equals, and all values inside the vector are equal -->
	 * No node is faulty. Consensus is easily reached (majority --> Unanimity)
	 * 
	 * Example: comparing vectors V0 of Node 1, 2, 3:
	 * 1:[0,X,0] != 2:[0,0,0] != 3:[0,Y,0] --> Majority of [0,X,0] = 0
	 * 
	 * Example of consensus vector: (Consensus vector of Node 1. Node 2 is faulty. V[i] = i.)
	 * [ 0:0, 1:1, 2:999, 3:3 ]
	 */
	private Map<String, Integer> consensus; 
	
	/**
	 * Should contain vectors for all the vectors
	 * Example of matrix: (Matrix of Node 1. Node 2 is faulty. V[i] = i.)
	 * 
	 *     [ // Vector for : [Sent From : Value Sent, ...]
	 * 		 0 : [ 0:0, 2:X, 3:0 ],
	 * 		 2 : [ 0:A, 2:B, 3:C ],
	 * 		 3 : [ 0:3, 2:Y, 3:3 ]  ] 
	 */
	private Map<String, Map<String, Integer>> matrix;
	
	
	public Node(String name, int v, boolean f)
	{
		this.name = name;
		this.value = v;
		this.faulty = f;
		this.matrix = new HashMap<String, Map<String, Integer>>();
		this.consensus = new HashMap<String, Integer>();
	}
	
	
	public String getName() { return this.name; }
	public int getValue()
	{
		if(this.faulty)
		{
			Random r = new Random();
			return r.nextInt(Main.upperBound);
		}
		else
		{
			return this.value;			
		}
	}
	public int getAlgValue()
	{
		if(faulty)
		{
			return Main.algValue;
		}
		else
		{
			return getValue();
		}
	}
	public boolean isFaulty() { return this.faulty; }
	public Map<String, Integer> getConsensus() { return this.consensus; }
	public Map<String, Map<String, Integer>> getMatrix() { return this.matrix; }
		
	public void sendAsGeneral(List<Node> otherNodes)
	{
		for(Node n : otherNodes)
		{
			if(!n.getName().equals(this.name))
			{
				Map<String, Integer> vector_n = new HashMap<String, Integer>();
//				vector_n.put(this.name, getValue());
				vector_n.put(n.getName(), getValue());
				n.getMatrix().put(this.name, vector_n);
			}
		}
	}
	
	public void sendAsLieutenant(List<Node> otherLieutenants)
	{
		// get value for n
		int valueForNodeN;
		for(String key : this.matrix.keySet())
		{						
			if(this.faulty){
				valueForNodeN = getValue(); // Faulty nodes send random informations no matter what
			}
			else{
				valueForNodeN = this.matrix.get(key).get(this.name);
			}
			for(Node n : otherLieutenants)
			{
				if(!n.getName().equals(this.name) && n.getMatrix().containsKey(key))
				{
					n.getMatrix().get(key).put(this.name, valueForNodeN);
				}
			}
		}
	}
	
	public Map<String, Integer> computeConsensus(List<Node> otherNodes)
	{
		// Example of matrix :
		
		 /*     [ // Vector for : [Sent From : Value Sent, ...]
		    	 * 		 0 : [ 0:0, 2:X, 3:0 ],
		    	 * 		 2 : [ 0:A, 2:B, 3:C ],
		    	 * 		 3 : [ 0:3, 2:Y, 3:3 ]  ] 
    	 */
		
		
		for(Entry<String, Map<String, Integer>> vector : this.matrix.entrySet())
		{
			// Vector V0 of N1:
			// 0 : [ 0:0, 2:X, 3:0 ]
			
			String i = vector.getKey(); // i = 0
			// Now we can compare vector associated with i
			List<Map<String, Integer>> vectors_i = new ArrayList<Map<String,Integer>>();
			
			// Get the different vectors Vi
			for(Node n : otherNodes)
			{
				if(n.getMatrix().containsKey(i)) // for example, N0's matrix contains no 0 vector
				{
					// Vector V0 of N2: [0,0,0]
					Map<String, Integer> n_vector = n.getMatrix().get(i);
					vectors_i.add(n_vector);
				}
			}
			// Now vectors_i contains all the vectors for i
			// [ [0,X,0], [0,0,0], [0,Y,0] ]
			// We can see which case it is
			
			// Comparing the vectors to see if all vectors are equal
			boolean areVectorsEquals = true;
			for(int v_i=0; v_i<vectors_i.size(); v_i++)
			{
				for(int v_j=v_i; v_j<vectors_i.size(); v_j++)
				{
					if(!vectors_i.get(v_i).equals(vectors_i.get(v_j)))
					{
						areVectorsEquals = false;
					}
				}
			}
			List<Integer> values = new ArrayList<Integer>(vector.getValue().values());
			if(areVectorsEquals)
			{
				if(Collections.frequency(
						values,
						values.get(0))
						== values.size())
				{
					consensus.put(vector.getKey(), values.get(0));
				}
				else
				{
					consensus.put(vector.getKey(), Main.algValue);
				}
			}
			else
			{
				// Vectors unequal, use majority
				int consensus_i = 0;
				int max = 0;
				for(Entry<String, Integer> occ : vector.getValue().entrySet())
				{
					if(Collections.frequency(vector.getValue().values(), occ.getValue()) > max)
					{
						max = Collections.frequency(vector.getValue().values(), occ.getValue());
						consensus_i = occ.getValue();
					}
					else if(Collections.frequency(vector.getValue().values(), occ.getValue()) == max)
					{
						if(occ.getValue() != consensus_i)
						{
							consensus_i = Main.algValue;
							break;
						}
					}
				}
				consensus.put(vector.getKey(), consensus_i);
			}
		}
		consensus.put(this.name, this.getAlgValue());
		return consensus;
	}
	
	
	public void dispMatrix()
	{
		System.out.println("Node " + getName() + ": ");
		for(Entry<String, Map<String, Integer>> entry : this.matrix.entrySet())
		{
			System.out.println(entry);
		}
	}
	
		
	@Override
	public String toString()
	{
		return String.format("%s:%s", this.name, this.value);
	}

}
