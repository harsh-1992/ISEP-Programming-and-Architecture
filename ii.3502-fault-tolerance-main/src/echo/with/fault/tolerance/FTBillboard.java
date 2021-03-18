package echo.with.fault.tolerance;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FTBillboard extends Remote,Billboard {
	
	static String LOOKUP_NAME = "FTBillboard";
	
	/**
	 * Returns the name of the current leader for this instance
	 * @return String containing address:port of the leader's registry
	 * @throws RemoteException
	 */
	String getLeader() throws RemoteException;

	/**
	 * List of address:port of neighbors of this node.
	 * If this node is leader, it will return the list of replicas.
	 * @return
	 * @throws RemoteException
	 */
	List<String> getNeighbors() throws RemoteException;

	/**
	 * Registers a replica to a leader.
	 * If the leader does not answer this request in a timely fashion,
	 * a new leader is chosen as the first element of the replica list after
	 * removal of the leader.
	 * @param server address:port of the caller
	 * @param replica callback
	 * @throws RemoteException
	 */
	void registerReplica(String server, FTBillboard replica) throws RemoteException;
}