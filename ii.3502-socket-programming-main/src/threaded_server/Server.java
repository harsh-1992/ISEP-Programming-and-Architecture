package threaded_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable
{	
	private Socket connectionSocket;
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("Threaded server running");
		ServerSocket mysocket = null;
		try {
			mysocket = new ServerSocket(5555);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		while(true)
		{
			Socket clientSocket = mysocket.accept();
			Server s = new Server(clientSocket);
			Thread serverThread = new Thread(s);
			serverThread.start();
		}
		
	}
	
	public Server(Socket socket)
	{
		try
		{
			this.connectionSocket = socket;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{		
		try
		{
			BufferedReader reader =
			new BufferedReader(new
			InputStreamReader(connectionSocket.getInputStream()));
			BufferedWriter writer=
			new BufferedWriter(new
			OutputStreamWriter(connectionSocket.getOutputStream()));
	
			writer.write("*** Welcome to the Calculation Server (Addition Only)***\r\n");
			writer.write("*** Please type in the first number and press Enter : \n");
			writer.flush();
			String data1 = reader.readLine().trim();
	
			writer.write("*** Please type in the second number and press Enter :\n");
			writer.flush();
			String data2 = reader.readLine().trim();
	
			int num1=Integer.parseInt(data1);
			int num2=Integer.parseInt(data2);
	
			int result=num1+num2;
			System.out.println("Addition operation done " );
	
			writer.write("\r\n=== Result is : "+result);
			writer.flush();
			connectionSocket.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}



}
