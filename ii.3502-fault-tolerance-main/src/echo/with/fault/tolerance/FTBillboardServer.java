package echo.with.fault.tolerance;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class FTBillboardServer extends UnicastRemoteObject implements FTBillboard {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Object msgLock = new Object();
	private String message;
	private String FTLeader;
	
	private List<String> neighbors;
	
	protected FTBillboardServer() throws RemoteException {
		super();
		neighbors = new ArrayList<String>();
	}

	@Override
	public String getMessage() throws RemoteException {
		String out;
		synchronized (msgLock) {
			out = message;
		}
		return out;
	}

	@Override
	public void setMessage(String message) throws RemoteException {
		synchronized (msgLock) {
			this.message = message;
			for(String neighbor : neighbors)
			{
				FTBillboard FTNeighbor;
				try {
					FTNeighbor = (FTBillboard) Naming.lookup("rmi://" + neighbor + "/" + FTBillboard.LOOKUP_NAME);
					FTNeighbor.setMessage(message);
				} catch (MalformedURLException | RemoteException | NotBoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getLeader() throws RemoteException {
		return this.FTLeader;
	}

	@Override
	public List<String> getNeighbors() throws RemoteException {
		return neighbors;
	}
	
	public void setLeader(String FTLeader)
	{
		this.FTLeader = FTLeader;
	}

	@Override
	public void registerReplica(String server, FTBillboard replica) throws RemoteException {
		if(neighbors != null)
		{
			if(!neighbors.contains(server))
			{
				// Propagating information
//				for(String neighbor : neighbors)
//				{
//					FTBillboard FTNeighbor;
//					try {
//						FTNeighbor = (FTBillboard) Naming.lookup("rmi://" + neighbor + "/" + FTBillboard.LOOKUP_NAME);
//						FTNeighbor.registerReplica(server, replica);
//						
//						System.out.println("Registered Replica " + server + " on Server " + neighbor);
//						
//					} catch (MalformedURLException | RemoteException | NotBoundException e) {
//						e.printStackTrace();
//					}
//				}
				neighbors.add(server);	
			}
		}
		else
		{
			System.out.println("Wrong server initialization. Please restart server.");
		}
	}
	
	
//	public void log(String log)
//	{
//		System.out.println(log);
//	}

}
