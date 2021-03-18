package simple_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {

	public static void main(String[] args)
	{
		try{
			Socket socketClient = new Socket("localhost",5555);
			System.out.println("Client: "+"Connection Established");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			
			BufferedWriter writer= new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
			String serverMsg;
			writer.write("8\r\n");
			writer.write("10\r\n");
			writer.flush();
			
			
//			System.out.println("count: " + reader.lines().count());
			while((serverMsg = reader.readLine()) != null)
			{
				System.out.println(serverMsg);
			}
			
//			socketClient.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}
