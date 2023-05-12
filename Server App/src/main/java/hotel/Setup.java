package hotel;

import java.util.Random;

/**
 * This class is used to create and setup the default rooms and hotels.
 */
public class Setup
{
    private static final Random random = new Random(System.currentTimeMillis()); // Used to create random numbers

    private static final String[] descriptions =
    {
        "Includes free Wi-Fi services and free parking services ",
        "Includes breakfast, free Wi-Fi services and free parking services ",
        "Includes free Wi-Fi services, free parking services and balcony with sea view ",
        "Includes breakfast, free Wi-Fi services, free parking services and balcony with sea view ",
        "Includes breakfast, free Wi-Fi services, free parking services and pool access",
        "Includes free Wi-Fi services, free parking services and pool access"
    }; // Descriptions for rooms

    /**
     * This method creates the default hotels and rooms.
     * Note: the hotels have random characteristics.
     */
    protected static void createDefaultHotels()
    {
        int numberOfRooms = 5; // Each hotel has only 5 rooms

        Hotel hotel = new Hotel("Hotel Galini", "Mykonos", 5); // Create a new hotel
        for(int i = 0; i < numberOfRooms; ++i)
        {
            int numberOfBeds = Math.abs(random.nextInt() % 3) + 1; // Create a random number of beds between 1 and 3
            int price = Math.abs(random.nextInt() % 301) + 100; // Create a random price between $100 and $400
            int description = Math.abs(random.nextInt()) % Setup.descriptions.length; // Choose a random index for the room description
            hotel.getRooms().add(new Room( Setup.descriptions[description], price, numberOfBeds)); // Add a new room to the rooms in the hotel
        }
        Hotel.hotels.add(hotel); // Add the hotel to the list of hotels

        // Repeat the process for as many hotels as you want to create

        hotel = new Hotel("Spring Resort", "Santorini", 4);
        for(int i = 0; i < numberOfRooms; ++i)
        {
            int numberOfBeds = Math.abs(random.nextInt() % 3) + 1;
            int price = Math.abs(random.nextInt() % 131) + 50;
            int description = Math.abs(random.nextInt()) % Setup.descriptions.length;
            hotel.getRooms().add(new Room( Setup.descriptions[description], price, numberOfBeds));
        }
        Hotel.hotels.add(hotel);

        hotel = new Hotel("Olympian Bay", "Parga", 4);
        for(int i = 0; i < numberOfRooms; ++i)
        {
            int numberOfBeds = Math.abs(random.nextInt() % 3) + 1;
            int price = Math.abs(random.nextInt() % 151) + 50;
            int description = Math.abs(random.nextInt()) % Setup.descriptions.length;
            hotel.getRooms().add(new Room( Setup.descriptions[description], price, numberOfBeds));
        }
        Hotel.hotels.add(hotel);

        hotel = new Hotel("Summer View Hotel", "Thessaloniki", 5);
        for(int i = 0; i < numberOfRooms; ++i)
        {
            int numberOfBeds = Math.abs(random.nextInt() % 3) + 1;
            int price = Math.abs(random.nextInt() % 301) + 60;
            int description = Math.abs(random.nextInt()) % Setup.descriptions.length;
            hotel.getRooms().add(new Room( Setup.descriptions[description], price, numberOfBeds));
        }
        Hotel.hotels.add(hotel);

        hotel = new Hotel("Hotel Zeus", "Athens", 3);
        for(int i = 0; i < numberOfRooms; ++i)
        {
            int numberOfBeds = Math.abs(random.nextInt() % 3) + 1;
            int price = Math.abs(random.nextInt() % 81) + 30;
            int description = Math.abs(random.nextInt()) % Setup.descriptions.length;
            hotel.getRooms().add(new Room( Setup.descriptions[description], price, numberOfBeds));
        }
        Hotel.hotels.add(hotel);

        Hotel.saveRooms(); // After all the rooms and hotels have been created and added, save the result to the data file.
    }
}
