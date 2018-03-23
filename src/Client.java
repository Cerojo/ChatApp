import java.net.Socket;
//This class is used to create a client thread
//A client is initialised here.

public class Client {
	private static boolean open = true; //Variable is always true

	public static void main(String[] args) {
		startClient(args); //Call start Client
	}

	private static void startClient(String args[]){
		if(args.length == 1){ //Arguement if is provided for a port number
			try {
				int port = Integer.parseInt(args[0]); //Convert string port to integer value
				Socket socket = new Socket("localhost", port); //Create a socket

				if (socket != null) { //if and while socket exists, get it on.
					while (open) {
					}
					socket.close(); //Close socket
					System.exit(0); //Exit app
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		else { //When arguement is not provided use a default of 2000
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