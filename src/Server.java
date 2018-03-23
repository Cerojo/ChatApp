import java.net.Socket;
import java.net.ServerSocket;
//This class is used to create and initiate the server class

public class Server {
	private static ServerSocket server; //Server variable
	private static Socket client; //Socket variable
	private static final UserLogic[] threads = new UserLogic[10]; //Clients threads will be sorted here. 10 Maximum client threads

	public static void main(String[] args) {
		startServer(args); //Start the server method
	}

	private static void startServer(String[] args){
		int port;

		try {
			if (args.length == 1) { //Check if port is provided
				port = Integer.parseInt(args[0]); //Extract the port
			}
			else {
				port = 2000; //Use default port of 2000 if not provided
			}

			server = new ServerSocket(port); //Open server

			while (true) { //Keep code using
				client = server.accept(); //Accept a client if trying to connect to server
				for (int i = 0; i < threads.length; i++) { //Go through each thread
					if (threads[i] == null) { //When thread space is variable
						(threads[i] = new UserLogic(client, threads)).start(); //Create thread client
						break;
					}
				}
			 }
		} catch (Exception e) {
			System.out.println(e);
		}
    }
}