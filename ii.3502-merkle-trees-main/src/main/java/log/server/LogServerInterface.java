package log.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import merkle.MerkleTreeNode;

public interface LogServerInterface extends Remote {
	
	static String LOOKUP_NAME = "LogServer";
	
	void 			appendLog(String logLine) 			throws RemoteException;
	byte[] 			getRootHash() 						throws RemoteException;
	int				getLogSize()						throws RemoteException;
	
	List<byte[]>	genPath(int m)						throws RemoteException;
	List<byte[]>	genProof(MerkleTreeNode subTree)	throws RemoteException;
	List<byte[]>	genProof(int m)						throws RemoteException;
}
