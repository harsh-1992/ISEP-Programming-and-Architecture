package byzantine.general.problem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main {
	
	public static int upperBound;
	public static int algValue;

	public static void main(String[] args) {		
		List<Node> network = new ArrayList<Node>(
				Arrays.asList(
						new Node("0", 0, false),
						new Node("1", 1, false),
						new Node("2", 2, true)//,
//						new Node("3", 3, false)
						));
		
		algValue = 999; // If a node is identified as faulty, this value will be attributed to the node
		upperBound = network.size(); // Gives an upper bound to random generation for faulty nodes
		
		System.out.println("== Part 1 & 2: Sending and Collecting values ==");
		for(Node n : network)
		{
			n.sendAsGeneral(network);
			// The general/lieutenant syntax refers to this good explanation:
			// https://www.youtube.com/watch?v=_e4wNoTV3Gw
		}		
		dispNetworkMatrices(network);
		System.out.println("");
		
		System.out.println("== Part 3: Sending Vectors ==");
		for(Node n : network)
		{
			n.sendAsLieutenant(network);
		}
		dispNetworkMatrices(network);
		System.out.println("");
		
		
		System.out.println("== Part 4: Reaching Consensus ==");
		List<Map<String, Integer>> consensuses = new ArrayList<Map<String,Integer>>();
		for(Node n : network)
		{
			Map<String, Integer> cons_n = n.computeConsensus(network);
			consensuses.add(cons_n);
			System.out.println(n.getName() + " reached this conclusion: " + cons_n);
		}
		// If all consensuses are equals
		if(Collections.frequency(consensuses, consensuses.get(0)) == consensuses.size())
		{
			System.out.println("Reached consensus among the nodes!");
		}
		else
		{
			System.out.println("Did NOT reach consensus among the nodes!");
		}
		
		System.out.println("");
		
	}
	
	private static void dispNetworkMatrices(List<Node> network)
	{
		for(Node n : network)
		{
			n.dispMatrix();
		}
	}
}
