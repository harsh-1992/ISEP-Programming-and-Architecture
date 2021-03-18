package chat_group;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer implements Runnable
{
	// FIND PROCESS' PID: netstat -a -o –n
	
	private static Vector<BufferedWriter> clients = new Vector<BufferedWriter>();
	
	private Socket connectionSocket;
	public ChatServer(Socket socket)
	{
		try
		{
			System.out.println("New Client connected to the chat server. Welcome!");
			this.connectionSocket = socket;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("Chat server running");
		ServerSocket mysocket = null;
		try {
			mysocket = new ServerSocket(5555);
			while(true)
			{
				Socket clientSocket = mysocket.accept();
				ChatServer s = new ChatServer(clientSocket);
				Thread serverThread = new Thread(s);
				serverThread.start();
				
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}

	@Override
	public void run() {
		try
		{
	        BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
            
	        clients.add(writer);
	        
	        while(true)
            {
                String chatLine = reader.readLine().trim();
                System.out.println("Received: " + chatLine);
                
                System.out.println("I got " + clients.size() + " clients.");
                for(BufferedWriter bw : clients){
                    try
                    {
                         bw.write(chatLine + "\r\n");
                         bw.flush();
                    }
                    catch(Exception e){ e.printStackTrace(); }
                 }
            }
		}
		catch(Exception e){e.printStackTrace();}
		
	}
	
	
	

}
