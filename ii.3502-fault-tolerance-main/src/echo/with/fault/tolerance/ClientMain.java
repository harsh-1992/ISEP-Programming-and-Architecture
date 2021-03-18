package echo.with.fault.tolerance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ClientMain {
	
	private static List<String> neighbors = new ArrayList<String>();
	private static int failureCount = 0;
	private static int FAILURE_CAP = 3;
	
	public static void main(String[] args) throws NotBoundException, IOException
	{
		System.out.println("=== Instructions ===" +'\n' +
				"- Type \"<Address>:<Port>\" to get the message on this server (i.e: localhost:1099)" +'\n' +
				"- Type \"<Address>:<Port> <Message>\" to submit a message on this server (i.e: localhost:1099 A_message_here)"+ '\n' +
				" *********************** ");
		boolean shouldExit = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		while(!shouldExit)
		{
			System.out.print(">");
			String message = br.readLine().trim();
			if(message.equals("exit"))
			{
				shouldExit = true;
				break;
			}
			else
			{
				execute(message.split(" "));		
			}
		}			
	}
	
	public static void execute(String[] args) throws MalformedURLException, RemoteException, NotBoundException
	{
		try
		{
			FTBillboard obj = (FTBillboard) Naming.lookup("rmi://" + args[0] + "/" + FTBillboard.LOOKUP_NAME);
			neighbors = obj.getNeighbors();
			if(args.length == 1)
			{
				fetchMessage(obj);
			}
			
			if(args.length == 2)
			{
				obj.setMessage(args[1]);
			}
		}
		catch(java.rmi.ConnectException e)
		{
			failureCount++;
			System.out.println("Failed connecting to " + args[0] + " (failure:" + failureCount + "/"+ FAILURE_CAP + ")");
			
			if(failureCount < FAILURE_CAP)
			{
				execute(args);	
			}
			else
			{
				failureCount = 0;
				if(neighbors.size() != 0 && neighbors != null)
				{
					String newLeader = neighbors.get(0);
					System.out.println("Executing on " + newLeader);
					if(args.length == 1)
					{
						execute(new String[] {newLeader});
					}
					else if(args.length == 2)
					{
						execute(new String[] {newLeader, args[1]});
					}
				}
				else
				{
					System.out.println("No backup server has been found.");
				}
			}
		}
	}
	
	public static void fetchMessage(FTBillboard server) throws RemoteException
	{
//		System.out.println("Neighbors: " + server.getNeighbors());
		System.out.println("Message:   " + server.getMessage());
	}
}
