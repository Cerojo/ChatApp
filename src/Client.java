import java.net.Socket;

public class Client {
	private static boolean open = true;

	public static void main(String[] args) {
		startClient(args);
	}

	private static void startClient(String args[]){
		if(args.length == 1){
			try {
				int port = Integer.parseInt(args[0]);
				Socket socket = new Socket("localhost", port);

				if (socket != null) {
					while (open) {
					}
					socket.close();
					System.exit(0);
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		else {
			try {
				int port = 2000;
				Socket socket = new Socket("localhost", port);

				if (socket != null) {
					while (open) {
					}
					socket.close();
					System.exit(0);
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
}