package merkle;

import java.util.Arrays;

public class Utils {
	
	public static byte[] prependByte(byte b, byte[] array)
	{
		byte[] totalHash = new byte[array.length + 1];
		totalHash[0] = b;
		for(int i=1; i<totalHash.length; i++)
		{
			totalHash[i] = array[i-1];
		}
		
		return totalHash;
	}
	
	public static byte[] concatBytes(byte[] a1, byte[]a2) {
		int a1Len = a1.length;
		int a2Len = a2.length;

		byte[] c = new byte[a1Len + a2Len];
		System.arraycopy(a1, 0, c, 0, a1Len);
		System.arraycopy(a2, 0, c, a1Len, a2Len);

	    return c;
	}
	
	public static String dispBytes(byte[] a)
	{
		return ("Hash: " + Arrays.toString(a));
	}
	
	public static void dispNode(MerkleNode n)
	{
		System.out.println(String.format("Node %s...%s | %s", n.getStartIndex(), n.getEndIndex(), dispBytes(n.getMyHash())));
	}
	
	public static String SHA256 = "SHA-256";
	public static String UTF8 = "UTF-8";
	public static String RESOURCES = "src\\main\\resources\\";
	
	public static void sep() {
		System.out.println("-------------------------");
		
	}

}
