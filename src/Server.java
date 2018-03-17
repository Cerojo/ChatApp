import java.net.Socket;
import java.net.ServerSocket;

public class Server {
	private static ServerSocket server;
	private static Socket client;
	private static final UserLogic[] threads = new UserLogic[10];

	public static void main(String[] args) {
		startServer(args);
	}

	private static void startServer(String[] args){
		int port;

		try {
			if (args.length == 1) {
				port = Integer.parseInt(args[0]);
			}
			else {
				port = 2000;
			}

			server = new ServerSocket(port);

			while (true) {
				client = server.accept();
				for (int i = 0; i < threads.length; i++) {
					if (threads[i] == null) {
						(threads[i] = new UserLogic(client, threads)).start();
						break;
					}
				}
			 }
		} catch (Exception e) {
			System.out.println(e);
		}
    }
}