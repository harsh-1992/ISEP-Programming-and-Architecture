package chat_group;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Chat implements Runnable
{
	private JTextField txt_chatbox;
	private JTextArea lbl_chat;
	private JButton btn_send;
	private String username;
	private BufferedWriter writer = null;
	private BufferedReader reader = null;
	private Socket socketClient;
	
	public Chat(String username)
	{
		

		this.username = username;
		init();
		setSocket();
		
		
		if(writer != null)
		{
			setListener(writer);
		}
        
	}
	
	private void setSocket() {
		try {
			socketClient = new Socket("localhost", 5555);
			writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
		}
		catch (IOException e) { e.printStackTrace(); }
		
		
	}

	private void setListener(Writer writer)
	{
		btn_send.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent ev)
            {
                String s = username + " said: "+ txt_chatbox.getText();                    
                try
                {
                	// Send to server
                    writer.write(s  +"\r\n");
                    writer.flush(); 
                    
                    // Reset txtbox
                    txt_chatbox.setText("");
                }
                catch(Exception e) { e.printStackTrace(); }
            }
        });
	}
	
	private void init()
	{
		JFrame frame = new JFrame("Chat - " + username);
		frame.setSize(500,500);
        
        JPanel p1=new JPanel();
        p1.setLayout(new BorderLayout());
            
        JPanel p2=new JPanel();
        p2.setLayout(new BorderLayout());        
        
        txt_chatbox=new JTextField();
        p1.add(txt_chatbox, BorderLayout.CENTER);
        
        btn_send=new JButton("Send");
        p1.add(btn_send, BorderLayout.EAST); 
        
        lbl_chat=new JTextArea();
        p2.add(lbl_chat, BorderLayout.CENTER);
        
        p2.add(p1, BorderLayout.SOUTH);
        
        frame.setContentPane(p2);
        frame.setVisible(true);  
	}

	@Override
	public void run()
	{
		try
		{
            String serverMsg;
            while((serverMsg = reader.readLine()) != null)
            {
                System.out.println("from server: " + serverMsg);
                lbl_chat.append(serverMsg+"\n");
            }
		}
		catch(Exception e){ e.printStackTrace(); }
	}

}
