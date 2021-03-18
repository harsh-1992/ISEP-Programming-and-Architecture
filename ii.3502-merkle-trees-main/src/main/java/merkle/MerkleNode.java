package merkle;


public abstract class MerkleNode {

	protected byte[] myHash;
	protected int start_index;
	protected int end_index;
	
	
	
	
	public byte[] getMyHash() {
		return myHash;
	}



	public void setMyHash(byte[] myHash) {
		this.myHash = myHash;
	}
	
	public void setStartIndex(int index)
	{
		this.start_index = index;
	}
	public int getStartIndex()
	{
		return this.start_index;
	}
	
	public void setEndIndex(int index)
	{
		this.end_index = index;
	}
	public int getEndIndex()
	{
		return this.end_index;
	}
	
}
