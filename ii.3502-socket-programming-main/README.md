# ii.3502-socket-programming
## Introduction
For more convenience, instead of creating multiple projects for each part, I chose to create packages for each part:
- *Part I: Simple server using TCP sockets* is associated to *simple_server*
- *Part II: Threaded Server using TCP Socket* is associated to *threaded_server*
- *Part III: Group chat system using low-level sockets* is associated to *chat_group*

Project specifications: Eclipse version 4.10.0 and JDK 13.0.1

## Testing
On Windows, to find the PID of an application, run the following command line on a terminal: `netstat -a -o –n`. Our applications should run on localhost, port 5555. Finding the PID allows you to force exit on the application using the specified port and address. This is necessary to re-run a new instance of server if the previous one is not closed.

All three parts of this TP can be run directly. All executable jars are located in the ./jars folder. You can also test the parts one by one by opening the project on an IDE and running each class separately.

## Part I: Simple server using TCP sockets
As an introduction to TCP sockets, we create a simple server-client application to add numbers. The code is already given, although the Client part is missing a part.

We connect ourselves to the port 5555. This is specified on the programmatical creation of the ServerSocket java object. The while loop runs the following parts until the socket is closed:
- Accepting connections from Clients
- Reading 2 numbers
- Computing the two read numbers: summing them and writing them to the Client socket

We can simulate a Client Socket using PuTTY. We can note the following limitations:
- Only one Client can connect to our server: running two instances of PuTTY simultaneously does not work. Only one Client can access the server, and the second one has to wait until the first is done with using the server's service.
- Writing anything that cannot be interpreted as an integer will break the server. This, of course, could be easily fixed, but it can simulate a breach or potentially faulty system. In case multiple clients try to access the server, if the server is broken this way by one of the first clients, the following clients will not be able to access the services.

The following specifications are used by PuTTY:
- Host name: localhost or 127.0.0.1
- Port: 5555
- Connection type: Raw
- Close window on exit: Never

The programmatical implementation of a Client Socket is very similar to the implementation of a Server Socket: we create the Socket object, giving *localhost* and *5555* as parameters (address and port of the server socket). Opening a buffered writer and reader allows us to give parameters to the server (integers to sum) and read the server's response (the sum of integers).  
We simulate the user choice of integers using *writer.write("8\r\n")*. By flushing the instruction, it is sent to the server.  
After writing the numbers, we can read the server's response indefinitely using a while loop to display the answers.

## Part II: Threaded Servever using TCP Socket
To fix some issues we faced in the first implementation, we can implement multiple threading in our server.

To implement our Multi-threading server, we can re-use the code of the simple server. However, the class will now *implements Runnable*.  
When implenting the Runnable interface, we have to override the interface methods, namely the *run()* method. The run method should contain the service that should be run by each server:
- A buffered writer and reader to read parameters of client and write back responses
- The service itself (summing the integer parameters given by the client)

After setting up the Server Socket itself, the *main* method will create a new instance of thread for each client connection received. This is done by accepting all sockets in a while loop, then opening a new instance of Server, and running it on a new Thread instance (with the runnable server as parameter).

Implementing this multi threaded server has multiple advantages:
- Multiple Clients can connect simultaneously on the server: this can be simulated by opening two (or more) instances of PuTTY. As we can see, instead of having the second PuTTY instance displaying nothing until the first client is done using the server, we now have the proper server messages displaying. We can enter a number in the first client instance, the input two numbers on our second client, and come back to the first client and finish using the server's services.
- Writing a string to the server will still produce a broken answer, but only for the faulty Client request: instead of breaking the server itself, only the thread assigned to the Client is broken. This means other clients will not be affected and will still obtain their expected response. This is a first measure of security for the users.


## Part III: Group chat system using low-level sockets
In this part, we create a basic chat system:
- Clients can connect on the server to send string messages to other clients connected on the same server
- Server reads the messages sent by a client and sends it to all the connected client (including the sender)
- Clients read the server message and display it on their GUI

### Chat Server (chat_group.ChatServer.java)
This part is easily done and follows the previous explaination. Using our Threaded server developed in Part II, we create a multi-threaded server.

The *run()* method contains the main service provided to each client:
- We add the buffered writer to a static list of buffered writers, that represent the list of connected clients. Read the remarks at the end of *Chat Server* for more details.
- We open a buffered writer and reader to read the incoming messages and dispatch them to all the connected clients
- When a message is received, we write the contents to all the clients connected on the server

The *main* method creates threads for each connection request received - as explained in Part II.

Remarks:
- Instead of adding buffered writers to represent the clients, we could have stored the clients' socket. However, in the for loop iterating through the clients, we would have had to create new BufferedWriter for each client, which would have been a waste of performance
- We use a static collection of clients so that it is shared accross different threads

### Chat Client (chat_group.Chat.java)
We start by implementing the GUI of the chat client. As it is not the subject of the TP, I'll skip most explainations, however:
- I used swing to implement the GUI elements
- The *init()* method instanciates the GUI, but the buttons will do nothing (yet)
- The *setListener(writer)* method requires a Writer, as it needs to send the message to the Server. This is why we need to set the connection between client and server before setting the button listener

Like the Server, we can implement the Chat client class as Runnable. This allows us to create different instances of clients which will send and read messages to the Server.  
The implementation of the different instances of chat clients is done in *chat_group.Client*. In this class, we simply create two chat clients (with different names: iteration and not iteration) and connect them to the server.
