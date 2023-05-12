package hotel;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * This class is used to represent the server.
 * It takes care of connecting to individual clients as well as attending to their requests.
 */
public class Server 
{

	/**
	 * This method starts the server.
	 */
	public static void start()
	{
		Hotel.init(); // Initialize the hotels

		ServerSocket serverSocket = null; // Declare a new serverSocket

		try
		{
			serverSocket = new ServerSocket(2807, 0, InetAddress.getByName("127.0.0.1")); // Create the serverSocket and listen on the specified address and port number
			System.out.println("Server has started!");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1); // Close the server and exit the program if the serverSocket was not created
		}

		while(true) // While the server is up and running
		{
			Socket client; // Declare a client socket
			try
			{
				client = serverSocket.accept(); // Assign the client to the incoming socket received by the serverSocket
				new Thread(new Connection(client)).start(); // Wrap the socket into a connection and into a thread and start the thread
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.exit(1); // Close the server if the server was unable to receive any connection and exit the program
			}
		}

	}

}