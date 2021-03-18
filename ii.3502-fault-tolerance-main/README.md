# ii.3502-fault-tolerance
## Part 2: Theorical exercises
### 2.1: SPoF
A SPoF (Single Point of Failure), in a system, is a single element whose disfunctionment drags along the entire system to failure.

As a good distributed system architecture should be designed to be fully available and fault tolerant, SPoF are problematic to distributed systems because the more of them there is, the less likely it is for the system to run continuously and be available at all times.

### 2.2: Byzantine Agreement
#### Question 1: Execute this algorithm for 3 non-faulty nodes, with Vi = i. Write down the values of the vectors at each step. Did the nodes reach an agreeement ?

I executed the algorithm with 3 non-faulty nodes, and Vi = i.

```
== Part 1 & 2: Sending and Collecting values ==
Node 0:     Node 1:     Node 2:       
1={0=1}     0={1=0}     0={2=0}
2={0=2}     2={1=2}     1={2=1}
```

The syntax I used to display the values in the matrix is: `{Node I={Node J=Value of Node I according to Node J}}`. For example: `{1={0=1, 2=1}, 2={0=2, 1=2}}` is read as follows: `Node 1 has a value 1 according to Node 0 and a value 1 according to Node 2. Node 2 has a value 2 according to Node 0 and a value 2 according to Node 1`.  
As we can see, all nodes sent their value to all the other nodes. As expected, as Vi = i, we every node receives [0, 1, 2].

```
== Part 3: Sending Vectors ==
Node 0:           Node 1:           Node 2:
1={0=1, 2=1}      0={1=0, 2=0}      0={1=0, 2=0}
2={0=2, 1=2}      2={0=2, 1=2}      1={0=1, 2=1}
```

In this part, each node sends its vector V to each other node. As each node receives the vectors from other nodes, each vector is added to a matrix containing all the vectors. The matrix size is N-1xN-1, where N is the number of nodes.  
As expected, as none of the nodes are faulty:
- Values of Node I is i: for example, value of Node 0 is 0 
- All the reported (shared) values are identical: for example, value for Node 0 according to both Node 1 and Node 2 is 0

```
== Part 4: Reaching Consensus ==
0 reached this conclusion: {0=0, 1=1, 2=2}
1 reached this conclusion: {0=0, 1=1, 2=2}
2 reached this conclusion: {0=0, 1=1, 2=2}
Reached consensus among the nodes!
```

After sharing values inside the vectors to all the other nodes (Step 3), each node has a matrix containing all the vectors. It can then get a result vector Vr such that the i-th entry of the vector is the value most represented in the matrix containing the vectors of every node, computed at the previous step.  
In our case, as all values for a Node i is the same in a vector, the majority chosen is actually an unanimity.

If all the consensus vectors are equals, a consensus is reached for all the nodes.

>Because there was no conflictual situation, where multiple maxima are found, a consensus is found between the 3 nodes..

It is the main goal of Byzantine algorithm: the value is not as important as the fact that every single node agrees on a final value. In the example with Byzantine generals, whether to attack or retreat, in a node network, the value of every node.

#### Question 2: Try to execute the same algorithm with k = 1. Did the nodes reach an agreement ? How many nodes are required to reach an agreement in that case ? How many nodes are required to tolerate k faulty nodes ?

When trying to compute the algorithm using n=3 and k=1, results can be inconsistent, as Node 2 can send different values to different nodes. If it sends the same value to all (or a majority) of the nodes, the run will not be problematic. Here are the results for a troublesome run. The faulty node is Node 2.
```
== Part 1 & 2: Sending and Collecting values ==
Node 0:     Node 1:     Node 2:       
1={0=1}     0={1=0}     0={2=0}
2={0=1}     2={1=2}     1={2=1}
```

As we can see here, values for Node 2 according to Nodes 0 and 1 are different. Likewise, values sent by Node 2 for values of Nodes 0 and 1 are inconsistent. It is even more obvious in Part 3, where we can see the values side by side:

```
== Part 3: Sending Vectors ==
Node 0:           Node 1:           Node 2: 
1={0=1, 2=2}      0={1=0, 2=2}      0={1=0, 2=0}
2={0=1, 1=2}      2={0=1, 1=2}      1={0=1, 2=1}
```

```
== Part 4: Reaching Consensus ==
0 reached this conclusion: {0=0, 1=999, 2=999}
1 reached this conclusion: {0=999, 1=1, 2=999}
2 reached this conclusion: {0=0, 1=1, 2=999}
Did NOT reach consensus among the nodes!
```

As we can see, no majority could be computed in Nodes 0 and 1 (except for their own value). The default response set by the algorithm in case of conflict is 999.  
In the end, for the consensus vectors, we can see that each vector is different -- or at least one of them differs from the others. This means a consensus has not been reached, and the system is failing.

>While, in a `k=1, n=3` situation, we can reach consensus, it is not assured, and thus can translate into a faulty situation where the system is compromised.

> To reach an agreement in any situation for `k=1`, we need at least 4 nodes in the network (`k=1, n=4`)

> To reach an agreement for a situation where there are k faulty nodes for a total of n nodes (`network(k, n)`) the following relation is required:
<img src="https://render.githubusercontent.com/render/math?math=n \geq 2k%2B1">




### 2.3: Leader Election with ECHO algorithm
#### Defining the environment
In the case where:
- Nodes are identified with an unique integer called ID
- The leader is the node with the lowest ID
- Each node is linked to all the other nodes (complete graph)
- Nodes receive messages and treat them in order of receiving (FIFO style) 

For our examples, we will consider a network composed of 10 nodes, with IDs going from 0 to 9.

#### Best case
The best case is when the nodes are ordered in an ascendant manner.

For example, with a network composed of 10 nodes, IDs going from 0 to 9, the best case scenario is when the node #0 sends the messages first, then node #1, then node #i until node #9.  
This gives us 234 messages sent over 3 rounds. As expected, the elected leader is node #0 for every node.

#### Worst case
The worst case is when the nodes are ordered in an descendant manner.

For example, with a network composed of 10 nodes, IDs going from 0 to 9, the best case scenario is when the node #9 sends the messages first, then node #8, then node #i until node #0.  
This gives us 522 messages sent over 3 rounds. As expected, the elected leader is node #0 for every node.

Simulating a few worst-case situations with an increasing number of nodes in the network, we get these results:
```
Number of nodes	| Number of messages sent
             10	| 522
             20	| 4142
             30	| 13862
             40	| 32682
             50 | 63602
             60	| 109622
             70	| 173742
             80	| 258962
             90	| 368282
            100	| 504702
```
This leads us to an approximation a
<img src="https://render.githubusercontent.com/render/math?math=\frac{N^{3}}{2}">
messages sent in the worst-case scenario, where N is the number of nodes in the complete graph.

## Part 3: Pratical Leader Election with Java RMI
This practical lab can be split in two parts:
- A tutorial that introduces the `Billboard` interface
- The implementation of the Fault Tolerant `Billboard`

### Billboard
In this interface, users can send and read messages on the billboard using methods defined as `getMessage()` and `setMessage(...)`.  
The class implementation brings more features : `getMessage()` and `setMessage(...)` use a lock system. The lock brings safety, as the data can only be accessed by the Server when it is free. It means that two instances of servers cannot access the data at the same time, and subsequently, data will always be updated for the second access request.

This strategy of lock/release is often used when threads are involved, as well as in protocols such as Modbus.

### Fault Tolerant Billboard
Now the goal is to implement a Fault Tolerant duplication feature. Instead of running the service on a single server, it is ran on multiple ones.  
Running a service on multiple redundant has multiple advantages:
- When an instance of server is down or the requests fail, one way or another, another instance of server can replace it and respond to the client
- When there is a surge of client requests, the load can be divided to relieve each instance

The leader election could be implemented to elect the main server with a more complex algorithm: in our case, we elected the first server as the leader, and the next node in the list of redundant servers will be elected as the new leader if the current leader does not answer anymore.


### Command: `run.bat` (in bin)
Running the .bat file will launch several things:
- Servers instances, on `localhost` on ports `1099`, `1250`, and `12345`. Servers on ports `1250` and `12345` are set as redancies of server on port `1099`.
- A client instance, connecting to `localhost`, on port `1099`

To instanciate a server as leader, we give one argument: the IP address and the port (i.e: `localhost:1099`). To instanciate a server as a redundancy, we give two arguments: the IP address and the port of the redundancy and those of the leader (i.e: `localhost:1250 localhost:1099`).

To instanciate a client instance, we give one argument: the IP address and the port of the Server (i.e `localhost:1099`).

The client has different methods to call:
- To get the message posted on the billboard: `<address>:<port>` (i.e: `localhost:1099` will get the message posted on the board, and the request will be sent to server `localhost:1099`).
- To post a message on the billboard: `<address>:<port> <message>` (i.e: `localhost:1099 new_message` will post `new_message` on the board, on server `localhost:1099`).

In our tests, we can simulate a server failing by closing a command window. For example, we can follow this sequence of actions:
- Compile and build the project
- Launching `run.bat`. The elected server is the server located on port `1099`.
- Selecting the client window, and getting the message (using `localhost:1099`). The message should be `null`, as no one posted a message yet.
- Post a message on the billboard: for example, post `new_message` (using `localhost:1099 new_message`). There should be no return.
- Read the message again. You can read the message on the server located on port `1099` or any other redundancy server, as the message should have been spread in the network.
- Close server `localhost:1099` by exiting the window, or running `exit`.
- Read the message on server `localhost:1099`. There should be errors, as the server is unreacheable. The client will try several times to reach the server (exact number can be defined in the code, it is currently set to 3).
- After all the tries have returned failure, a new server will be elected. Server on port `1250` should be elected, as it was the first redundancy server added to the list. Server 1250 will now handle the requests. 
