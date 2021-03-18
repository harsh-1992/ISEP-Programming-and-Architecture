package merkle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Main {

	public static void main(String[] args) throws NoSuchAlgorithmException, FileNotFoundException, UnsupportedEncodingException{
		if(args.length > 0)
		{
			File file = new File(String.join(" ", args));
			// Here, the Stack leaves contains the hash of all the leaves. We compute the Merkle tree and get the root:
			MerkleNode root = TreeBuilder.getRoot(file);
			
			System.out.println(Utils.dispBytes(root.getMyHash()));
		}
	}
}
