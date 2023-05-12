import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * This class represents a client.
 * A client is an object that can interact with the hotel server and perform useful operations.
 */
public class Main
{	
	private static PrintWriter out; // For sending messages to the server
	private static BufferedReader in; // For receiving messages from the server
	private static Scanner scanner; // For receiving input from the keyboard

	/**
	 * This method is used to connect a client to the server.
	 * @param url the address to connect to.
	 * @param port the port to connect on.
	 */
	public static void connect(String url, int port)
	{
		try(Socket socket = new Socket(url, port)) // Try to create a new Socket
		{
			out = new PrintWriter(socket.getOutputStream()); // Create the printWriter from the outputStream of the socket
			in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Create the BufferedReader from the inputStream of the socket
			scanner = new Scanner(System.in); // Create the scanner from the main System input: the keyboard

			do
			{
				try
				{
					if(socket.isClosed()) // If the socket has been disconnected from the server, break out of the loop.
						break;

					char[] input = new char[8192];
					in.read(input); // Read the messages from the server

					String line = new String(input);
					int lastChar = line.indexOf(0); // Get the last visible character
					int index = line.indexOf("Booking Receipt"); // Determine if a room was booked
					line = line.substring(0, lastChar); // Trim the message to remove the excess whitespace
					if(line.equalsIgnoreCase("Disconnected")) // If the client has been disconnected from the server
						break;
					else if(index > 0) // If a room was just booked
					{
						PrintWriter writer = new PrintWriter(System.getProperty("user.home") + "\\booking.txt"); // Create a new printWriter to store the receipt
						writer.print(line.substring(index));
						writer.flush();
						writer.close();
						System.out.println(line.substring(0, index));
						System.out.println("The booking receipt has been downloaded to " + System.getProperty("user.home") + " and saved as \"booking.txt\"");
					}
					else
						System.out.println(line); // Print out the message to the screen

					line = scanner.nextLine(); // Read input from the keyboard
					out.println(line); // Send it to the server
					out.flush(); // Ensure that the message is sent to the server
				}
				catch (Exception e)
				{
					break; // An error occurred somewhere, disconnect from the server.
				}
			}
			while(true);
			System.out.println("Disconnected from the server");
		}
		catch (Exception e)
		{
			System.out.println("Unable to connect to the server. Either a wrong URL and/or port number was passed or the server is down.");
			e.printStackTrace();
		}

	}
	
	public static void main(String ... args) throws Exception
	{
		if(args.length != 2) // If the URL and port are not passed when calling this class, an exception is thrown.
			throw new Exception("Expected 2 arguments, found " + args.length + ". Proper Usage: java Main {url} {port}");
		connect(args[0], Integer.parseInt(args[1])); // Connect this client to the server
	}	
}

