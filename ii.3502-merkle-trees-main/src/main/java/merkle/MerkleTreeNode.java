package merkle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Define a Java class that represents a Merkle Trees. Each tree object represents one node in the tree. It
 * has fields containing the hash of the current tree, pointers to the left and right subtrees, and two fields
 * denoting the beginning and ending index of the range of the log they cover (e.g., h5..8 covers records 5
 * to 8)
 * */

public class MerkleTreeNode extends MerkleNode{
	
	private MerkleNode child1;
	private MerkleNode child2;
	
	
	public MerkleTreeNode(MerkleNode n1, MerkleNode n2) throws NoSuchAlgorithmException
	{
		this.child1 = n1;
		this.child2 = n2;
		
		this.start_index = Math.min(n1.getStartIndex(), n2.getStartIndex());
		this.end_index = Math.max(n1.getEndIndex(), n2.getEndIndex());
		
		// Compute myHash:
		// Step 1: Concatenate hashes of the two children
		this.myHash = Utils.concatBytes(n1.getMyHash(), n2.getMyHash());
		// Step 2: Prepend byte 0x01
		this.myHash = Utils.prependByte((byte)0x01, this.myHash);
		
		// Hash the result
		MessageDigest digest = MessageDigest.getInstance(Utils.SHA256);		
		myHash = digest.digest(this.myHash);
	}



	public MerkleNode getChild1() {
		return this.child1;
	}
	
	public MerkleNode getChild2() {
		return this.child2;
	}
}
