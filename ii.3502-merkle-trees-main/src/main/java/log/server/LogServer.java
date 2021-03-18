package log.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import merkle.MerkleNode;
import merkle.MerkleTreeLeaf;
import merkle.MerkleTreeNode;
import merkle.TreeBuilder;

public class LogServer extends UnicastRemoteObject implements LogServerInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MerkleNode root;
	private File file;
	private int logSize;
	
	protected LogServer(File file) throws RemoteException, FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		super();
		this.root = TreeBuilder.getRoot(file);
		this.logSize = this.root.getEndIndex();
		this.file = file;
	}

	public void appendLog(String logLine) {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(this.file, true));
			output.newLine();
			output.append(logLine);
			output.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] getRootHash() throws RemoteException {
		if(root != null)
		{
			return root.getMyHash();
		}
		return null;
	}

	public List<byte[]> genPath(int m) throws RemoteException {
		List<byte[]> path = new ArrayList<byte[]>();
		if(root != null && m <= root.getEndIndex())
		{
			recursiveGetPath(m, (MerkleTreeNode)this.root, path);
		}
		return path;
	}
	
	private List<byte[]> findNode(MerkleTreeNode node, int lastKnownIndex, List<byte[]> hashes)
	{
		/**
		 *            5            8
		 * -----------[------------]-----------
		 *  <ZONE 1>  ^  <ZONE 3>  ^  <ZONE 5>
		 *            2            4
		 */
		
		System.out.println("LAST KNOWN INDEX: " + lastKnownIndex);
		System.out.println("NODE'S INDEXES  : " + node.getStartIndex() + " - " + node.getEndIndex());
		
		// ZONE 1: means that the node is new compared to the old Merkle Tree
		if(node.getStartIndex() > lastKnownIndex)
		{
			hashes.add(node.getMyHash());
		}
		// ZONE 2: means that the lower boundary leaf is the last memory in the logs
		else if(node.getStartIndex() == lastKnownIndex)
		{
//			hashes.add(node.getMyHash());
			// Add the other part
			hashes.add(node.getChild2().getMyHash());

			// Get the leaves
			while(! (node.getChild1() instanceof MerkleTreeLeaf))
			{
				node = (MerkleTreeNode) node.getChild1();
			}
			hashes.add(node.getChild1().getMyHash());
			hashes.add(node.getChild2().getMyHash());
		}
		// ZONE 3: in between. Needs to search for the boundary inside the node
		else if(node.getStartIndex() < lastKnownIndex && node.getEndIndex() > lastKnownIndex)
		{
			// split and recursion
			for(MerkleNode n : Arrays.asList(node.getChild1(), node.getChild2()))
			{
				hashes = findNode((MerkleTreeNode)n, lastKnownIndex, hashes);
			}
		}
		// CASE 4: upper boundary is the last memory 
		else if(node.getEndIndex() == lastKnownIndex)
		{
			hashes.add(node.getMyHash());
		}
		// CASE 5: this node has already been registered
		else 
		{
			hashes.add(node.getMyHash());
		}
		
		return hashes;
	}

	public List<byte[]> genProof(MerkleTreeNode oldTree) throws RemoteException {
		List<byte[]> newTreeHashes = new ArrayList<byte[]>();
		// Last known index:
		int lastKnownIndex = oldTree.getEndIndex();
		
		// We split old tree until we find the last known index
		newTreeHashes = findNode((MerkleTreeNode) this.root, lastKnownIndex, newTreeHashes);
		return newTreeHashes;
	}
	
	
	

	private List<byte[]> recursiveGetPath(int m, MerkleNode n, List<byte[]> path)
	{
		// i.e: root = node 1...8
		// 		then, child1 = node 1...4 and child2 = node 5...8
		if(n instanceof MerkleTreeLeaf)
		{
			if(m == n.getStartIndex())
			{
				// Don't add, it's the leaf!
			}
			else
			{
				path.add(n.getMyHash());
			}
			return path;
		}
		else
		{
			for(MerkleNode child : Arrays.asList(((MerkleTreeNode)n).getChild1(),((MerkleTreeNode)n).getChild2()))
			{
				// i.e: m = 4. child = child1 = N1...4. 4 >= 1 && 4 <= 4
				if(m >= child.getStartIndex() && m <= child.getEndIndex())
				{
					path = recursiveGetPath(m, child, path);
				}
				else
				{
					path.add(child.getMyHash());
				}
			}
			return path;
		}
	}
	
	public MerkleNode getRoot() {
		return root;
	}

	public void setRoot(MerkleNode root) {
		this.root = root;
	}

	public List<byte[]> genProof(int proofOfSubTree) throws RemoteException {
		MerkleNode root_old;
		try {
			root_old = TreeBuilder.getSubTree(file, proofOfSubTree);
			return genProof((MerkleTreeNode)root_old);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getLogSize() throws RemoteException {
		return this.logSize;
	}
}
