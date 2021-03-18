package echo.with.fault.tolerance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerMain {
	public static void main(String[] args) throws NotBoundException, IOException
	{
		createServer(args);	
	}	
	
	public static void createServer(String[] args) throws RemoteException, MalformedURLException, NotBoundException
	{
		FTBillboardServer server = new FTBillboardServer();
		if(args.length > 0)
		{
			// args[0] = localhost:1099
			String address = args[0];
			String ip = address.split(":")[0];
			int port = Integer.valueOf(address.split(":")[1]);

			java.rmi.registry.LocateRegistry.createRegistry(port);
			Naming.rebind("rmi://" + address + "/" + FTBillboard.LOOKUP_NAME, server);
			System.out.println("Server bound in registry on port " + port);
			
			if(args.length == 2)
			{
				FTBillboard obj = (FTBillboard) Naming.lookup("rmi://" + args[1] + "/" + FTBillboard.LOOKUP_NAME);
				for(String neighbor : obj.getNeighbors())
				{
					FTBillboard FTNeighbor;
					try {
						FTNeighbor = (FTBillboard) Naming.lookup("rmi://" + neighbor + "/" + FTBillboard.LOOKUP_NAME);
						FTNeighbor.registerReplica(address, server);
						
						System.out.println("Registered Replica " + address + " on Server " + neighbor);
						
					} catch (MalformedURLException | RemoteException | NotBoundException e) {
						e.printStackTrace();
					}
				}
				obj.registerReplica(address, server);
				System.out.println("Registered Replica " + address + " on Server " + args[1]);
				
				
			}
		}
		else
		{
			// Sad faces
		}
	}
}
