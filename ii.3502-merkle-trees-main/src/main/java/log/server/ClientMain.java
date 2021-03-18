package log.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import merkle.Utils;

public class ClientMain {

	private static LogServerInterface server;

	public static void main(String[] args) throws NotBoundException, IOException, NoSuchAlgorithmException {
		System.out.println("=== Instructions ===" + '\n' + "1  - Type \"<Address>:<Port>\" to connect to the server"
				+ '\n'
				+ "2a - Type \"genPath <index> <log>\"  where index is an integer and log is the content of the log line to check if the content of log line index has been tampered with"
				+ '\n'
				+ "2b - Type \"genProof <index>\" where index is an integer to generate the proof that subtree going from 1 to the leaf of requested index is indeed a past version of the complete tree"
				+ '\n' + " *********************** " + '\n' +
				"Enter 'exit' or 'q' to close this executable");
		boolean shouldExit = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (!shouldExit) {
			System.out.print(">");
			String message = br.readLine().trim();
			if (message.equals("exit") || message.equals("q")) {
				shouldExit = true;
				break;
			} else {
				execute(message.split(" "));
			}
		}
	}

	public static void execute(String[] args) throws MalformedURLException, RemoteException, NotBoundException,
			UnsupportedEncodingException, NoSuchAlgorithmException {
//		System.out.println(Arrays.toString(args));
		if (args.length == 1 && args[0].contains(":")) {
			// Connect
			server = (LogServerInterface) Naming.lookup("rmi://" + args[0] + "/" + LogServer.LOOKUP_NAME);
			System.out.println("Connected to " + args[0]);
		} else if (args.length > 1 && server != null) {
			if (args[0].equals("getRoot")) {
				byte[] rootHash = server.getRootHash();
				int logSize = server.getLogSize();

				System.out.println(String.format("The log has %s lines. The final hash of this log file is: %s", logSize, Utils.dispBytes(rootHash)));
			} else if (args[0].equals("genPath")) {
				int pathToGet = Integer.valueOf(args[1]);
				System.out.println("    >>> genPath(" + pathToGet + ")");
				List<byte[]> path = server.genPath(pathToGet);

				// Computing proof by path from given arguments (index & log content)
				MessageDigest digest = MessageDigest.getInstance(Utils.SHA256);
				// hash of given leaf (given by args)
				byte[] proofByPath = digest.digest(Utils.prependByte((byte) 0x00, args[2].getBytes(Utils.UTF8)));

				System.out.println(String.format("Node %s according to Auditor: %s", pathToGet, Utils.dispBytes(proofByPath)));

				// Computing root using genPath(args[1]) and proofByPath

				// locate m on 1...n
				int pos = 0;
				for (int i = 1; i < path.size() + 1; i++) {
					if (pathToGet <= Math.pow(2, i)) {
						pos = i - 1;
						break;
					}
				}
//				System.out.println(String.format("Position of the coupled leaf in the array 'path': %s", pos));

				// Merge with path[pos]
				System.out.println("-- Merging with the leaf --");
				if (pathToGet % 2 == 0) {
					// Step 1: concat the byte with the precedent byte
					proofByPath = Utils.concatBytes(path.get(pos), proofByPath);
				} else {
					// Step 1: concat the byte with the precedent byte
					proofByPath = Utils.concatBytes(proofByPath, path.get(pos));
				}
				// Step 2: Prepend byte 0x01
				proofByPath = Utils.prependByte((byte) 0x01, proofByPath);
				proofByPath = digest.digest(proofByPath);
//				System.out.println("Finished merging. Result:");
				System.out.println(Utils.dispBytes(proofByPath));
				Utils.sep();

				if (pos > 0) {
					for (int i = pos - 1; i > -1; i--) {
//						System.out.println(String.format("Merging with the hash in position #%s of array 'path'", i));
						// Step 1: concat the byte with the precedent byte
						proofByPath = Utils.concatBytes(path.get(i), proofByPath);
						// Step 2: Prepend byte 0x01
						proofByPath = Utils.prependByte((byte) 0x01, proofByPath);
						proofByPath = digest.digest(proofByPath);
//						System.out.println("Finished merging. Result:");
//						System.out.println(Utils.dispBytes(proofByPath));
					}
				}
				for (int i = pos + 1; i < path.size(); i++) {
//					System.out.println(String.format("Merging with the hash in position #%s of array 'path'", i));
					// Step 1: concat the byte with the precedent byte
					proofByPath = Utils.concatBytes(proofByPath, path.get(i));
					// Step 2: Prepend byte 0x01
					proofByPath = Utils.prependByte((byte) 0x01, proofByPath);
					proofByPath = digest.digest(proofByPath);
//					System.out.println("Finished merging. Result:");
//					Utils.dispBytes(proofByPath);
				}

				// Now proofByPath is equal to the root hash
				// Compare with the root hash:
				System.out.println(String.format("No data loss, as the hashcode is identical: %s",
						Arrays.equals(server.getRootHash(), proofByPath)));
			} else if (args[0].equals("genProof")) {
				int proofOfSubTree = Integer.valueOf(args[1]);
				System.out.println("    >>> genProof(" + proofOfSubTree + ")");
				List<byte[]> proof = server.genProof(proofOfSubTree);
				for (byte[] hash : proof) {
					System.out.println(Arrays.toString(hash));
				}
			}
		}

	}

}
