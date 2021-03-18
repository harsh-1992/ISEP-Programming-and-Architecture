# ii3502.Peer-to-Peer storage
## Exercise 1: Routing
### 1. Build a possible routing table for node 22. Indicate only the intervals covered by each entry.
Finger table for N22, m=6, and network of the 15 following nodes: [N1, N7, N12, N18, N22, N23, N42, N44, N49, N54, N55, 60, N62, N63]

| <img src="https://render.githubusercontent.com/render/math?math=N22%2B 2^i, i \in [0, m-1]"> | Registered node | Covered range |
| :-: | :-: | :-: |
| N22+1 | N23 | [55:63] \union [0:23] |
| N22+2 | N42 | [24:42] |
| N22+4 | N42 | [24:42] |
| N22+8 | N42 | [24:42] |
| N22+16 | N42 | [24:42] |
| N22+32 | N54 | [43:54] |

### 2. Consider that node 22 wants to access an object with id 0. Which peer will it contact first?
N22 will contact the node registered in its finger table that is the closest to <img src="https://render.githubusercontent.com/render/math?math=0 \equiv K (mod\ i^{m})">, while being inferior or equal.

In our case, <img src="https://render.githubusercontent.com/render/math?math=0 \equiv 64 (mod 64)">, so we are looking for the registered node which identifier is the closest to 64 while remaining inferior or equal to 64. The selected node is N54.

### 3. State a possible node sequence until the node responsible for key 0 is reached.
We already stated that N54 will be the first selected node going from N22 to D0.

Once we get to N54, we will repeat the algorithm until we reach D0. We need the finger table of N54. Here is a partial finger table; I only display the relevant values:

| <img src="https://render.githubusercontent.com/render/math?math=N54%2B 2^i, i \in [0, m-1]"> | Registered node |
| :-: | :-: |
| N54+1 | N55 |
| N54+2 | N56 |
| N54+4 | N60 |
| N54+8 | N62 |
| N54+16 | N7 |

In our case, we'll try to reach N62 (<img src="https://render.githubusercontent.com/render/math?math=62 \leq 64">). Likewise, since <img src="https://render.githubusercontent.com/render/math?math=62 \neq 0">, we repeat the algorithm:

| <img src="https://render.githubusercontent.com/render/math?math=N62%2B 2^i, i \in [0, m-1]"> | Registered node |
| :-: | :-: |
| N62+1 | N63 |
| N62+2 | N1 |

We can see that the closest node to D0 is N1. It should be the node covering the interval that contains D0.

The final path is [N22 -> N54 -> N62 -> N1]

## Exercise 2: Storage architecture analysis
### 1. If we consider that failures can occur, explain, which vulnerability the existence of F can introduce.
If failures can occur, the existance of a single coordinator site F can introduce a SPoF.

If multiple coordinator sites <img src="https://render.githubusercontent.com/render/math?math=F_i"> exist, there could be concurrent access issues.

### 2. Is F providing better availability to system? Why?
Having a single coordinator site provides a better availability, as it allows the implementation of duplication of the rings.

Without a Coordinator site, if an issue arises in a network where a client tries to reach data, there will be no back-up.

With a Coordinator site, if an issue arises in the first network, the coordinator can redirect the request on the second network.

### 3. We add a mirroring node to F (i.e., full F replica) called F', F' can also be called by clients to send requests. We do not explain how F' works, it just exists. Does this addition enhances the system fault tolerance or availability? How?
The addition of a mirror F' increases the availability in that F is no longer a SPoF (clients can still access the network through the mirror F').

### 4. Which mechanism can you propose so that F and F' store the same metadatas? Precise if your solution leads to immediately identical metadatas, or to eventually identical metadatas.
We want F and F' to contain the same metadatas, so we are trying to implement Strong or Weak consistency.

We could implement Merkle Tree on the data to ensure that the hashes of the roots of each network is identical after every update of data in the network. 

