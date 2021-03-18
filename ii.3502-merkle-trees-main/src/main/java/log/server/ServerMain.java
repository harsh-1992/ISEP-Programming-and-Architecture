package log.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

import merkle.Utils;

public class ServerMain {

	private static String fullAddress = "localhost:12345";
	private static File file = new File(Utils.RESOURCES + "logTest.txt");
	
	public static void main(String[] args)
			throws NoSuchAlgorithmException, IOException {
		System.out.println("=== Instructions ===" + '\n'
				+ "1  - Type \"set <Address>:<Port>\" to use the address and port to set the Log server. Not entering any arguments will use default configuration ('localhost:12345')"
				+ '\n' + "2  - Type \"log <path>\" to specify the path to the logFile." + '\n'
				+ "3  - Run Auditor.jar, using 'java -jar auditor.jar'" + '\n' + " *********************** " + '\n'
				+ "Enter 'exit' or 'q' to close without running a server");
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

	public static void execute(String[] args) throws RemoteException, MalformedURLException,
			FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		if (args.length == 2 && args[0].equals("set")) {
			fullAddress = args[1];
		}
		if (args.length > 1 && args[0].equals("log")) {
			String[] path = new String[args.length - 1];
			for (int i = 0; i < args.length - 1; i++) {
				path[i] = args[i + 1];
			}
			String spath = String.join(" ", path);
			file = new File(spath);
			LogServer server = new LogServer(file);
			java.rmi.registry.LocateRegistry.createRegistry(Integer.valueOf(fullAddress.split(":")[1]));
			Naming.rebind("rmi://" + fullAddress + "/" + LogServer.LOOKUP_NAME, server);
		}
	}
}
