package hotel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used to represent a hotel. It contains the name of the hotel, the location of the hotel,
 * the rating of the hotel, as well as a list of rooms. It also contains functions to operate on hotels.
 */
public class Hotel
{
	protected static final List<Hotel> hotels = new ArrayList<>(); // List of all the hotels in the server
	protected static final String savePath = System.getProperty("user.home") + "\\Server App"; // The directory in which the data file will be saved

	private final String name;
	private final String location;
	private final int ratings;
	private final List<Room> rooms;

	/**
	 * This method initializes, creates and assigns the list of hotels in the server.
	 */
	protected static void init()
	{
		boolean newHotels = false;
		try
		{
			File directory = new File(Hotel.savePath); // The data directory
			File hotelsFile = new File(Hotel.savePath + "\\hotels.txt"); // The data file
			if (!directory.exists() || !directory.isDirectory()) // If the data directory doesn't exist or it is not a directory
			{
				directory.mkdir(); // Create the data directory
				System.out.println("Created The Home Directory At: " + directory.getAbsolutePath());
			}

			if(!(hotelsFile.exists() && hotelsFile.isFile())) // If the data file doesn't exist or it is not a file
			{
				hotelsFile.createNewFile(); // Create the data file
				System.out.println("Created The Data File At: " + hotelsFile.getAbsolutePath());
				newHotels = true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(newHotels)
			Setup.createDefaultHotels(); // Create the default hotels
		else
			Hotel.loadRooms(); // Load the hotels from the data file
	}


	protected Hotel(String name, String location, int ratings)
	{
		this.name = name;
		this.ratings = ratings;
		this.location = location;
		this.rooms = new ArrayList<>();			
	}

	/**
	 * This method converts this hotel object into a user-friendly format.
	 * @return the String representation of this hotel.
	 */
	@Override
	public String toString()
	{
		return "Hotel \n{ " +
				"\n\tName: " + this.name +
				"\n\tLocation: " + this.location +
				"\n\tRatings: " + this.ratings +
				"\n}";
	}

	/**
	 * This method returns the name of the hotel.
	 * @return the name of the hotel.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * This method returns the list of all the hotels in the server.
	 * @return the list of all the hotels.
	 */
	public static List<Hotel> getHotels()
	{
		return Hotel.hotels;
	}

	/**
	 * This method returns the hotel at the provided index.
	 * @param index the index of the hotel being requested.
	 * @return the hotel requested.
	 */
	public static Hotel getHotel(int index)
	{
		return Hotel.hotels.get(index);
	}

	/**
	 * This method returns the list of all the rooms in this hotel.
	 * @return the list of rooms.
	 */
	public synchronized List<Room> getRooms()
	{
		return this.rooms;
	}

    /**
     * This method is used to get the rooms booked by a particular customer.
     * @param customerName the name of the customer who made the booking.
     * @return the list of rooms the customer has booked.
     */
	public synchronized List<Room> getRoomsBooked(String customerName)
    {
        return this.rooms.stream().filter(room -> room.details.customerName.equalsIgnoreCase(customerName)).collect(Collectors.toList());
    }

	/**
	 * This method returns the list of empty rooms in this hotel.
	 * @return the list of rooms that have not been booked.
	 */
	public synchronized List<Room> getEmptyRooms()
	{
		return this.rooms.stream().filter(room -> !room.isBooked()).collect(Collectors.toList());
	}

	/**
	 * This method returns the list of booked rooms in this hotel.
	 * @return the list of rooms that have been booked.
	 */
	public synchronized List<Room> getBookedRooms()
	{
		return this.rooms.stream().filter(Room::isBooked).collect(Collectors.toList());
	}

	/**
	 * This method is used to get the room at the provided index.
	 * This method only returns if the name provided is equal to the name of the customer who booked it.
	 * @param name the name of the customer trying to get the room.
	 * @param index the index of the room being accessed.
	 * @return the room requested.
	 * @throws Exception if the index is out of range or if the name provided was not the same as the customer who booked it.
	 */
	public synchronized Room get(String name, int index) throws Exception
	{
		Room room = room(index);
		if(room.details.customerName.equalsIgnoreCase(name))
			return room;
		throw new Exception("No booking was made by " + name + " for room " + (index + 1));
	}

	/**
	 * This method returns a room in this hotel at the index provided.
	 * It throws an exception if the index provided is illegal.
	 * @param roomIndex the index of the room requested.
	 * @return the room requested.
	 * @throws Exception if the index provided is out of range.
	 */
	public synchronized Room room(int roomIndex) throws Exception
	{
		if(roomIndex < 0)
			throw new Exception("Invalid Room Number");
		return this.rooms.get(roomIndex);
	}

	/**
	 * This method is used to book a room.
	 * It accepts the name and email of the customer, the duration of stay and the room being booked.
	 * @param customerName the name of the customer booking the room.
	 * @param customerEmail the email of the customer booking the room.
	 * @param date the starting date of the booking.
	 * @param duration the duration of the booking in days.
	 * @param room the room being booked.
	 * @throws Exception if the room has already been booked by another customer.
	 */
	public synchronized void book(String customerName, String customerEmail, String date, int duration, Room room) throws Exception
	{
		room.book(customerName, duration);
	}

	/**
	 * This method is used to delete the booking of a room.
	 * It accepts the name of the customer and the room under consideration.
	 * @param customerName the name of the customer who booked the room.
	 * @param room the room that was booked the customer.
	 * @throws Exception if the room was not booked by the name provided.
	 */
	public synchronized void deleteBooking(String customerName, Room room) throws Exception
	{
		if(!room.details.customerName.equalsIgnoreCase(customerName))
			throw new Exception("No booking was made by " + customerName);
		room.free();
	}

	/**
	 * This method returns the rooms whose prices are lower than or equal to the price provided.
	 * @param price the price of a room.
	 * @return the list of rooms with that price.
	 */
	public synchronized List<Room> searchByPrice(int price)
	{
		return this.rooms.stream().filter(room -> room.price <= price).collect(Collectors.toList());
	}

	/**
	 * This method returns the rooms whose number of beds are the same as the number provided.
	 * @param numberOfBeds the number of beds in a room.
	 * @return the list of rooms with that number of beds.
	 */
	public synchronized List<Room> searchByBeds(int numberOfBeds)
	{
		return this.rooms.stream().filter(room -> room.numberOfBeds == numberOfBeds).collect(Collectors.toList());
	}

	/**
	 * This method saves all the hotels in the server to the data file.
	 * It iterates over each hotel and saves it before finally saving all to the data file.
	 */
	public static void saveRooms()
	{
		try
		{
			PrintWriter writer = new PrintWriter(Hotel.savePath + "\\hotels.txt"); // Create a new printWriter to write the data to a system file
			StringBuilder data = new StringBuilder(); // Create a new StringBuilder. It is going to be used to store all the data of a hotel.
			for(Hotel hotel : Hotel.hotels) // Save each hotel in the server
				saveHotel(data, hotel);
			writer.print(data); // Write the hotel data to the file
			writer.close(); // Close the writer
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}

	/**
	 * This method is used to save a single hotel.
	 * @param mainFile the builder to which the data of the hotel will be attached.
	 * @param hotel the hotel being processed.
	 */
	private static void saveHotel(StringBuilder mainFile, Hotel hotel)
	{
	    // Save the properties of the hotel to the data builder
		mainFile.append("Hotel:").append("\r\n\t")
				.append("name: ").append(hotel.name).append("\r\n\t")
				.append("location: ").append(hotel.location).append("\r\n\t")
				.append("ratings: ").append(hotel.ratings).append("\r\n\t")
				.append("rooms:").append("\r\n\t\t");
		for(Room room : hotel.rooms)
			saveRoom(mainFile, room);
		mainFile.append("\r\n");
	}

    /**
     * This method saves a room.
     * @param mainFile the builder to which the room data is saved.
     * @param room the room to be saved.
     */
	private synchronized static void saveRoom(StringBuilder mainFile, Room room)
	{
	    // Save the properties of the room to the data builder
		mainFile.append("Room:").append("\r\n\t\t\t")
				.append("description: ").append(room.description).append("\r\n\t\t\t")
				.append("price: ").append(room.price).append("\r\n\t\t\t")
				.append("beds: ").append(room.numberOfBeds).append("\r\n\t\t\t")
				.append("details: ")
                .append("{ ")
				//.append(" customer: ").append(room.details.customerName)     // No personal info shared.
                .append(" duration: ").append(room.details.DurationInDays)
                .append(" days from timestamp: ").append(room.details.timeStamp).append(" }");
		mainFile.append("\r\n\t\t");
	}

    /**
     * This method is used to load hotels from the data file.
     */
	public static void loadRooms()
    {
        try
        {
            List<String> data = Files.readAllLines(Paths.get(Hotel.savePath + "\\hotels.txt")); // Read all the lines in the data file
            int currentHotel = 1;
            while(currentHotel < data.size()) // While there are still more hotels to processed
				currentHotel = loadHotel(data, currentHotel); // Load the hotel
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
            System.exit(-1); // Shutdown the server because the rooms cannot be loaded
        }
    }

    /**
     * This method loads a single hotel from the main data file.
     * @param data the main data file which contains the hotels.
     * @param currentIndex the index of the current hotel.
     * @return the index of the next hotel to be processed.
     */
	private static int loadHotel(List<String> data, int currentIndex)
    {
        List<String> hotelFile = new ArrayList<>();
        for(int i = currentIndex; i < data.size(); ++i) // For each line in the data list
		{
			String line = data.get(i);
			++currentIndex;

			if(line.startsWith("Hotel:")) // If the line contains the data of another hotel, break out.
				break;

			hotelFile.add(line);
		}

        // Extract the details of the hotel
		String line = hotelFile.get(0);
        String hotelName = line.substring(line.indexOf("name: ") + 6);
        line = hotelFile.get(1);
        String hotelLocation = line.substring(line.indexOf("location: ") + 10);
        line = hotelFile.get(2);
        int hotelRatings = Integer.parseInt(line.substring(line.indexOf("ratings: ") + 9));

        Hotel hotel = new Hotel(hotelName, hotelLocation, hotelRatings); // Create a new hotel

        for(int i = 5; i < hotelFile.size(); i += 5)
			loadRoom(hotel, hotelFile, i); // Load the rooms in the hotel

        Hotel.hotels.add(hotel); // Add the newly loaded hotel to the main list of hotels
		return currentIndex;
    }

    /**
     * This method loads a single room from the data file.
     * @param hotel the hotel being processed.
     * @param hotelFile the data file of the hotel.
     * @param start the starting index for the room properties in the data file.
     */
    private static void loadRoom(Hotel hotel, List<String> hotelFile, int start)
	{
	    // Extract the details of the room
		String line = hotelFile.get(start++);
		String roomDescription = line.substring(line.indexOf("description: ") + 13);
		line = hotelFile.get(start++);
		int roomPrice = Integer.parseInt(line.substring(line.indexOf("price: ") + 7));
		line = hotelFile.get(start++);
		int beds = Integer.parseInt(line.substring(line.indexOf("beds: ") + 6));
		String details = hotelFile.get(start);

		// Extract its booking details
		int startCust = details.indexOf("customer: ");
		int startDur = details.indexOf("duration: ");
		int startTime = details.indexOf("days from timestamp: ");
		int end = details.indexOf(" }");

		String customer = details.substring(startCust + 10, startDur - 1);
		int duration = Integer.parseInt(details.substring(startDur + 10, startTime - 1));
		long timestamp = Long.parseLong(details.substring(startTime + 21, end));

		// Create the room and add it to the current hotel being processed
		Room room = new Room(roomDescription, roomPrice, beds);
		room.setDetails(new BookingDetails(customer, duration, timestamp));
		hotel.rooms.add(room);
	}

}