package chat_group;

import java.io.IOException;

public class Client {
	public static void main(String[] args) throws IOException
	{
		
		Chat c = new Chat("Iteration");
		Chat c2 = new Chat("Not Iteration");
		Thread t1 = new Thread(c);
		Thread t2 = new Thread(c2);
		
		
		t1.start();t2.start();
	}
}
