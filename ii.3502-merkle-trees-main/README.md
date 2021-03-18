# ii.3502-merkle-trees
The main goal of this lab session is to implement:
- A log file parser: each line in the file contains a log, that should be hashed and processed in the Merkle tree. Hashing the log line gives multiple advantages: this way, the content of the line can be simplified, is more easily comparable, and ensures homogeneity of the logs format - assuming all the lines are hashed using the same hashing format.
- A Merkle tree builder: using the hashed log lines, we build the Merkle tree. Each hashed log line represents a leaf in the Merkle tree, and the tree is computed until a single node, the root, can be returned.

For example, a Merkle tree based on hashes of leaves `[H1, H2, H3, H4, H5]` is equivalent to the tree based on hashes `[H1, H2, H3, H4, H5, H5, H5, H5]` as the `H5` leaf will be duplicated to be combined into `H5..5`, and as `H5..5` itself will be duplicated to be combined into `H5..5`.

- Merkle tree consistency proofing methods: using Consistency Proof and Audit Proof (Path), the final goal of the lab is to prove that the Merkle tree can ensure that no data has been tampered with - or prove that it has been, and should not be trusted!

Jar files have been compiled using maven. Jar files are located at `./ii.3502-merkle-trees/target/`. You can rebuild the project using `mvn package`.

## Log file parser
To represent the log we need to hash and whose integrity we have to verify, we can simply use `.txt` files. In those files, each line should represent a different log. It does not matter how long a log line is, as it will be considered as a string value.

When the algorithm requires a log file, it should be attached using its absolute path. For example, when setting up the RMI Log Server, instructions state:
```
1  - Type "set <Address>:<Port>" to use the address and port to set the Log server. Not entering any arguments will use default configuration ('localhost:12345')
2  - Type "log <path>" to specify the path to the logFile.
3  - Run Auditor.jar, using 'java -jar auditor.jar'
```

The second instruction in particular can be executed by running, for example: `log C:\Users\User\distributed_systems\merkle-trees\log_file.txt`

## Merkle tree builder
The merkle tree is built using queues (first in first out collections): the first queue contains the hashed leaves. Then a second queue is constituted: for each pair of leaves, a node hashing each leaf of the pair is added to the stack until the leaves queue is empty. When all the leaves have been processed, it is repeated on the second layer of merkle nodes, resulting in another layer.

The algorithm stops when the stack contains exactly one node, which should be the root.

```
            ---- 1..8 ----             | Root Layer    (4th queue)
           /              \            |           ===            
       1..4                5..8        | Node Layer #2 (3rd queue)
      /    \              /    \       |           ===            
  1..2      3..4      5..6      7..8   | Node Layer #1 (2nd queue)
 /    \    /    \    /    \    /    \  |           ===            
1      2  3      4  5      6  7      8 | Leaf Layer    (1st queue)
```

This example presents the case with a tree containing 8 leafs. As we can see, the 8 leaves are added to a first queue. After they have been added, leaves are popped by pairs to form nodes which are added to a second queue. After all the leaves have been processed, the nodes of the second queue are popped by pairs to form nodes added to a third queue. Finally, the two nodes of the 3rd queue are merged to form the root.

Following a remark of M. Lucas on Teams, for the cases where <img src="https://render.githubusercontent.com/render/math?math=log_2(N) \notin \mathbb{N}"> where N is the number of leaves, I chose to duplicate the last node when it was single. The following example illustrates this case. The duplicated nodes are noted with parenthesis:

```
            ---- 1..5 ----            |
           /              \           |
       1..4                5..5       | The third queue contains 2 elements, so the nodes can be merged by pairs. No duplication required.
      /    \              /    \      |
  1..2      3..4      5..5     (5..5) | The second queue contains 3 elements. Nodes (1..2) and (3..4) can be merged into node (1..4), but node (5..5) requires another duplicate.
 /    \    /    \    /    \           |
1      2  3      4  5     (5)         | The leaf layer contains 5 elements. Pairs (1,2) and (3,4) are popped to be merged, but leaf (5) is alone and requires a duplicate.
```

## Proofs
### Proof by path
Computing the path of a leaf consists in recomputing the hash of the root using the hash of a local instance of the leaf.

To do so, we naturally need to go through the computing of every node whose hash "contains" the hash of the leaf we are analyzing. For example, if we analyze the hash H3 using its path, we will need to compute node (3..4), node (1..4), and node (1..8).

The path contains the hashes of the nodes required to compute the path from the leaf to the root. Following our example, starting from H3:
- We need H4 to compute H3..4 using H3
- We need H1..2 to compute H1..4 using H3..4
- We need H5..8 to compute H1..8 using H1..4

### Consistency Proof
The consistency proof of a tree allows the user to compare the given tree to another one, and determine whether or not the two trees are versions of each other - older or newer.

To determine whether or not the trees are versions of a same tree, we require an array of hashes. The array of hashes should contain all the nodes of the older version of the tree, and the new nodes.

For example, let's compare those trees:
```
            === TREE 1 ===                           === TREE 2 ===
            ---- 1..8 ----             |             ---- 1..6 ----            
           /              \            |            /              \           
       1..4                5..8        |        1..4                5..6       
      /    \              /    \       |       /    \              /    \      
  1..2      3..4      5..6      7..8   |   1..2      3..4      5..6     (5..6)  
 /    \    /    \    /    \    /    \  |  /    \    /    \    /    \     
1      2  3      4  5      6  7      8 | 1      2  3      4  5      6  
```
- The tree on the left is the most recent version (with nodes 1 to 8), and the one on the right is the older version (with nodes 1 to 6).  
- Looking at the older tree, we are looking for the highest-level node(s) that are common to both Tree 1 and Tree 2.
  - Tree 2's highest node is H1..5.
  - It is not present in the Tree 1.
  - The highest-level nodes in Tree 2 that are present in Tree 1 are: [H1..4, H5..6]. We add those to the array.
- Looking at the newer tree, we look for the highest-level node(s) that are necessary to compute the root H1..8.
  - We already have H5..6, so we will require H7..8 to compute H5..8.
  - We will then be able to compute H5..8, and we already have H1..4, so we will be able to compute the root H1..8
  - We add the node H7..8 to the array.
  
In the end, the array contains [1..4, 5..6, 7..8].
