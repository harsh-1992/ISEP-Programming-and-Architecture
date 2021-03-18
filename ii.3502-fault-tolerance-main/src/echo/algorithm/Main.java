package echo.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;


public class Main {
	public static int messageCount;
	public static int roundCount;
	
	public static List<Integer> listMessageCounts = new ArrayList<Integer>();
	public static List<Integer> listRoundCounts = new ArrayList<Integer>();
	
	
	public static void main(String[] args)
	{
//		specialSim(true, 10); // best case: nodes ordered ascending
//		specialSim(false, 1000); // worst case: nodes ordered descending²
		
		int numSim = 5000;
		for(int i = 0; i<numSim; i++)
		{
			simulation(10);
		}
		
		System.out.println(" ============================== ");
		System.out.println("Min number of messages: " + Collections.min(listMessageCounts));
		System.out.println("Average number of messages: " + (listMessageCounts.stream().mapToDouble(a -> a).sum())/numSim);
		System.out.println("Max number of messages: " + Collections.max(listMessageCounts));
		System.out.println(" -- ");
		System.out.println("Min number of messages: " + Collections.min(listRoundCounts));
		System.out.println("Average number of messages: " + (listRoundCounts.stream().mapToDouble(a -> a).sum())/numSim);
		System.out.println("Max number of messages: " + Collections.max(listRoundCounts));
	}
	
	private static void displaySystemState(List<Node> network)
	{
		for(Node n : network)
		{
			System.out.println(String.format("Node %s >> vector: %s, currently selected leader: %s", n.getId(), n.getVector(), n.getLeaderId()));
		}
	}
	
	private static void computeRound(List<Node> network)
	{
		for(Node n : network)
		{
			n.computeRound(network);
		}
		System.out.println('\n');
	}
	
	private static void initSystem(List<Node> network)
	{
		for(Node n : network)
		{
			n.initRounds(network);
		}
	}
	
	private static List<Node> generateRandomNodes(int size)
	{
		List<Node> nodes = new ArrayList<Node>();
		for(int i=0; i<size; i++)
		{
			nodes.add(new Node(i));
		}
		Collections.shuffle(nodes);
		return nodes;
	}
	
	private static void simulation(int numberOfNodes)
	{
		messageCount = 0;
		roundCount = 1; // Init @ one because 1st round is the initialization
		List<Node> network = generateRandomNodes(numberOfNodes);
		initSystem(network);
		displaySystemState(network);

		
		// While every node hasn't elected a leader, run the algorithm
		boolean stop = false;
		while(!stop)
		{
			computeRound(network);
			roundCount += 1;
			stop = true;
			for(Node n : network)
			{
				if(n.getMessageStack().size() > 0)
				{
					stop = false;
				}
			}
			displaySystemState(network);
		}
		computeRound(network); // Not necessary: just there to show that there is no message on stacks left
		
		System.out.println("=== TOTAL NUMBER OF MESSAGES SENT : " + messageCount + " ===");
		System.out.println("=== TOTAL NUMBER OF ROUNDS SPENT  : " + roundCount + " ===");
		listMessageCounts.add(messageCount);
		listRoundCounts.add(roundCount);
	}
	
	public static void specialSim(boolean best, int numberOfNodes)
	{
		messageCount = 0;
		roundCount = 1; // Init @ one because 1st round is the initialization
		List<Node> network = new ArrayList<Node>();
		
		if(best)
		{
			for(int i = 0; i<numberOfNodes; i++)
			{
				network.add(new Node(i));
			}			
		}
		else
		{
			for(int i = numberOfNodes-1; i>=0; i--)
			{
				network.add(new Node(i));
			}
		}
		initSystem(network);
		displaySystemState(network);
		
		// While every node hasn't elected a leader, run the algorithm
		boolean stop = false;
		while(!stop)
		{
			computeRound(network);
			roundCount += 1;
			stop = true;
			for(Node n : network)
			{
				if(n.getMessageStack().size() > 0)
				{
					stop = false;
				}
			}
			displaySystemState(network);
		}
		computeRound(network); // Not necessary: just there to show that there is no message on stacks left
		
		System.out.println("=== TOTAL NUMBER OF MESSAGES SENT : " + messageCount + " ===");
		System.out.println("=== TOTAL NUMBER OF ROUNDS SPENT  : " + roundCount + " ===");
	}
	
	

}
