package echo.algorithm;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

public class Node {
	private int id;
	private Map<Integer, Integer> vector;
	
	private Queue<Entry<Integer, Integer>> messageStack;
	private int leaderId;
	
	
	public Node(int id)
	{
		this.id = id;
		this.leaderId = id;
		this.vector = new HashMap<Integer, Integer>();
		this.messageStack = new LinkedList<Map.Entry<Integer,Integer>>();
//		this.vector.put(id, id);
	}
	
	public void initRounds(List<Node> nodes)
	{
//		System.out.println("Node " + this.id + "'s initialization phase");
		for(Node n : nodes)
		{
			if(n.getId() != this.id)
			{
				n.addMessageToStack(this.id, this.leaderId);
			}
		}
	}
	
	public void computeRound(List<Node> nodes)
	{
//		System.out.println("Node " + this.id + " has "+ messageStack.size() + " messages on stack");

		// Read messages
		while(messageStack.size() > 0)
		{
//			Entry<Integer, Integer> message = messageStack.pop();
			Entry<Integer, Integer> message = messageStack.poll();
//			System.out.println("    Message from node " + message.getKey() + " : " + message.getValue() + ". Comparing (message:" + message.getValue() + ") to (this:" + this.leaderId + ").");
			if(message.getValue() > this.leaderId)
			{
//				System.out.println("    (message:" + message.getValue() + ") > (this:" + this.leaderId +")");
				// Do nothing: Vj > Vi
			}
			else if(message.getValue() == this.leaderId)
			{
//				System.out.println("    (message:" + message.getValue() + ") = (this:" + this.leaderId +")");
				// Check if value is in vector
				if(this.vector.containsKey(message.getKey()) && this.vector.get(message.getKey()) == message.getValue())
				{
					// Stop the algorithm
//					System.out.println("        Received echo");
				}
				else
				{
					// Li = Lj
					this.leaderId = message.getValue();
					// Vi[j] = Lj
					this.vector.put(message.getKey(), message.getValue()); // update value
					// Send back
					nodes.get(nodes.indexOf(new Node(message.getKey()))).addMessageToStack(this.id, message.getValue());
				}
				
			}
			else
			{
//				System.out.println("    (message:" + message.getValue() + ") < (this:" + this.leaderId +")");
				// Set leaderId as Vj: Li = Lj
				this.leaderId = message.getValue(); 
				// Add value to vector: Vi[j] = Lj
				this.vector.put(message.getKey(), message.getValue()); // update value
				// Send to the rest of the nodes, excluding ourselves and the sender
				for(Node n : nodes)
				{
					if(n.getId() != this.id && n.getId() != message.getKey())
					{
						n.addMessageToStack(this.id, message.getValue());
					}
				}
			}
		}
		
		
	}

	private void addMessageToStack(int senderId, int value) {
//		this.messageStack.push(new AbstractMap.SimpleEntry<Integer, Integer>(senderId, value));	
		this.messageStack.add(new AbstractMap.SimpleEntry<Integer, Integer>(senderId, value));	
//		System.out.println("        Sending message to node " + this.getId() + ", value " + value + " was sent (#" + Main.messageCount +")");
		Main.messageCount += 1;
	}

	public int getId() {
		return id;
	}
	
	public int getLeaderId() {
		return leaderId;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Map<Integer, Integer> getVector() {
		return vector;
	}


	public void setVector(Map<Integer, Integer> vector) {
		this.vector = vector;
	}


	public Queue<Entry<Integer, Integer>> getMessageStack() {
		return messageStack;
	}


	public void setMessageStack(Queue<Entry<Integer, Integer>> messageStack) {
		this.messageStack = messageStack;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Node))
        {
			return false;
        }
		else
		{
			return (this.id == ((Node)o).getId());
		}
	}
	
	

}
