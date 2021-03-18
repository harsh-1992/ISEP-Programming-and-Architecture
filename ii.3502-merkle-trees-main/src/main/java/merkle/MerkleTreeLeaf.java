package merkle;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MerkleTreeLeaf extends MerkleNode{	
	public MerkleTreeLeaf(int index, String log) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		this.start_index = index;
		this.end_index = index;
		
		MessageDigest digest = MessageDigest.getInstance(Utils.SHA256);
		
		byte[] hashedIndex = log.getBytes(Utils.UTF8);

		// Prepending 0x00 to the hash
		hashedIndex = Utils.prependByte((byte)0x00, hashedIndex);
		
		
		this.myHash = digest.digest(hashedIndex);
		
		
	}

}
