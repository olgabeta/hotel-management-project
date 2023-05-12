package hotel;

/**
 * This class is used to represent a room in a hotel.
 * It contains the description of the room, its price, the number of beds it contains and its booking details.
 */
public class Room
{
	public String description;
	public int price;
	public int numberOfBeds;
	public BookingDetails details;
	
	public Room(String description, int price, int numberOfBeds)
	{
		this.description = description;
		this.price = price;
		this.numberOfBeds = numberOfBeds;
		this.details = new BookingDetails();
	}

	/**
	 * This method is used to set the booking details of a room.
	 * @param details the details to be set.
	 */
	public void setDetails(BookingDetails details)
	{
		this.details = details;
	}

	/**
	 * This method is used to book a room.
	 * @param customerName the name of the customer booking the room.
	 * @param duration the duration, in days, the customer is booking the room for.
	 * @throws Exception if the room has already been booked by another customer.
	 */
	public void book(String customerName, int duration) throws Exception
	{
		if(!this.details.isBooked()) // If the room has not been booked, create new booking details.
		{
			this.details.customerName = customerName;
			this.details.DurationInDays = duration;
			this.details.timeStamp = System.currentTimeMillis();
		}
		else
			throw new Exception("Room has already been booked by a customer!");
	}

	/**
	 * This method checks if this room has been booked.
	 * @return the booking status of the room.
	 */
	public boolean isBooked()
	{
		return this.details.isBooked();
	}

	/**
	 * This method removes the booking of a room.
	 */
	public void free()
	{
		this.details.clear();
	}

	/**
	 * This method converts this room object into a user-friendly format.
	 * @return the String representation of this room.
	 */
	public String toString()
	{
		return "Room \n{ "
		+ "\n\tDescription: \"" + this.description + "\""
		+ "\n\tNumber Of Beds: " + this.numberOfBeds
		+ "\n\tPrice: $" + this.price
		+ "\n\tDetails: " + (this.details.isBooked() ? this.details : "Not Booked") + "\n}";
	}
}