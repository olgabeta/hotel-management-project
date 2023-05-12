package hotel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.util.List;
import java.util.regex.*;
import java.text.SimpleDateFormat;

/**
 * This class represents a connection to the server and is used to interact with the server.
 */
public class Connection implements Runnable
{
    private final Socket socket; // The socket connecting the client to the server
    private final PrintWriter writer; // For sending server responses to the client
    private final BufferedReader reader; // For receiving client responses
    private int page = 0; // The current page displayed by the server
    private int currentHotel; // The hotel that was chosen

    public Connection(Socket socket) throws Exception
    {
        this.socket = socket;
        this.writer = new PrintWriter(socket.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * This method is the landing page of the server. This is displayed when a client connects to the server. It displays a list of available hotels.
     */
    private void home()
    {
        this.writer.println("..........*W*E*L*C*O*M*E*.........");
        this.writer.println("Thank you for choosing CEO-Tourism.gr for your stay!");
        this.writer.println("..................................");
        this.writer.println("Please select a hotel.");
        this.writer.println("Note: You can always enter 0 to quit.");
        this.writer.println();
        // Display all the hotels in the server
        for(int i = 0; i < Hotel.hotels.size(); ++i)
            this.writer.println((i + 1) + ": " + Hotel.hotels.get(i) + '\n');
        this.writer.print("Select Hotel: ");
        this.writer.flush(); // Send this page to the client
    }

    /**
     * This method is the welcome page of each individual hotel in the server.
     */
    private void welcome()
    {
        // Print out the functionalities of that hotel
        this.writer.println("......" + Hotel.getHotel(this.currentHotel).getName() + "......");
        this.writer.println("Note: You can always enter 0 to go back to the previous menu or to cancel a booking.");
        this.writer.println();
        this.writer.println("1: Book New Room");
        this.writer.println("2: Display Booked Rooms");
        this.writer.println("3: Display Empty Rooms");
        this.writer.println("4: View All Rooms");
        this.writer.println("5: Clear Customer Booking");
        this.writer.println("6: Search For Rooms");
        this.writer.println("0: Quit");
        this.writer.print("Select Option: ");
        this.writer.flush();
    }

    /**
     * This method shows the list of rooms which have been booked in the hotel.
     */
    private void bookedRooms()
    {
        List<Room> rooms = Hotel.getHotel(this.currentHotel).getBookedRooms(); // Get the list of rooms which have been booked in this hotel
        this.writer.println("...Booked Rooms...");
        if(rooms.isEmpty()) // If there are no booked rooms
            this.writer.println("There Are No Booked Rooms.");
        else
        {
            // Display the rooms
            for(int x = 0; x < rooms.size(); ++x)
                this.writer.println((x + 1) + ": " + rooms.get(x) + '\n');
        }

        this.writer.print("Enter 0 to go back: ");
        this.writer.flush();
    }

    /**
     * This method is used to book a room in the hotel.
     */
    private void bookRoom() {
        List<Room> rooms = Hotel.getHotel(this.currentHotel).getEmptyRooms(); // Get the list of rooms in the hotel which have not been booked
        if(rooms.isEmpty())
        {
            this.writer.println("Sorry, There Are No Rooms Available.");
            this.writer.flush();
            return;
        }
        else
        {
            for(int x = 0; x < rooms.size(); ++x)
                this.writer.println((x + 1) + ": " + rooms.get(x) + '\n');
        }
        this.writer.print("Select Room: "); // Prompt the user to select a room
        this.writer.flush();
        int response = Integer.parseInt(read()); // Read the input from the client
        try
        {
            if(response > 0) // If the client's response is valid
            {
                Room room = rooms.get(response - 1); // Get the room about to be booked
                this.writer.print("Enter Customer Full Name: ");
                this.writer.flush();
                String name = read(); // Get the name of the customer

                this.writer.print("Enter Contact Information (Email Address): ");
                this.writer.flush();
                String email = read(); // Get the email of the customer
                String regex = "^(.+)@(.+)$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(email);
                while(!matcher.matches()){ // Check if input matches the regular expression for email validation
                    this.writer.println("Please provide a valid email address: ");
                    this.writer.flush();
                    email = read();
                    matcher = pattern.matcher(email);
                }

                this.writer.print("Enter Booking Start Date: ");
                this.writer.flush();
                String date = read(); // Get the starting date of the booking
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                format.setLenient(true);
                try {
                   format.parse(date);
                } catch (ParseException e) {
                    this.writer.print("Invalid date format. Automatically setting today as the booking start date.\n");
                    this.writer.flush();
                } // Ensure that date format is according to the desired pattern

                this.writer.print("Enter Duration in Days: ");
                this.writer.flush();
                int duration = Integer.parseInt(read()); // Get the duration of stay

                Hotel.getHotel(this.currentHotel).book(name, email, date, duration, room); // Book the room
                this.writer.println("Success: Room " + response + " has been booked by " + name + " (" + email + ")");
                this.writer.println("Booking Receipt: " + room); // Send the booking receipt to the client
                Hotel.saveRooms(); // Because there has been a change, save the hotels to the data file.
            }
        }
        catch (Exception e)
        {
            this.writer.println("Error: " + e.getMessage());
        }
        this.writer.flush();
    }


    /**
     * This method is used to remove the booking of a client.
     */
    private void removeBooking()
    {
        this.writer.println("...Remove Customer Booking...");
        this.writer.print("Enter Customer Name: ");
        this.writer.flush();
        String name = read(); // Get the name of the client
        try
        {
            List<Room> rooms = Hotel.getHotel(this.currentHotel).getRoomsBooked(name); // Get the list of rooms that have already been booked by this customer
            if(rooms.isEmpty()) // If the customer has not booked any rooms
            {
                this.writer.println("Sorry, no rooms have been booked by " + name);
                this.writer.print("Enter 0 to go back: ");
                this.writer.flush();
            }
            else
            {
                // Display all the rooms
                for(int x = 0; x < rooms.size(); ++x)
                    this.writer.println((x + 1) + ": " + rooms.get(x) + '\n');

                this.writer.println("Please indicate the room you wish to clear.");
                this.writer.print("Select Room: "); // Prompt the client to select the room
                this.writer.flush();

                int response = Integer.parseInt(read()); // Get the response of the client
                if(response > 0 && response <= rooms.size()) // If the response of the client is valid
                {
                    Room room = rooms.get(response - 1); // Get the indicated room
                    Hotel.getHotel(this.currentHotel).deleteBooking(name, room); // Delete the booking of the room
                    this.writer.println("Success: The booking of " + name + " for room " + response + " has been removed!");
                    this.writer.print("Enter 0 to go back: ");
                    this.writer.flush();
                    Hotel.saveRooms(); // Because there has been a change, save the room.
                }
                else
                    {
                    this.writer.println("Sorry, Wrong Room Number.");
                    this.writer.print("Enter 0 to go back: ");
                    this.writer.flush();
                    }
            }
        }
        catch(Exception e)
        {
            this.writer.println("Error: " + e.getMessage());
            this.writer.flush();
        }
    }

    /**
     * This method displays all the rooms in the hotel.
     */
    private void rooms()
    {
        List<Room> rooms = Hotel.getHotel(this.currentHotel).getRooms(); // Get all the rooms in the hotel
        this.writer.println("...All Rooms...");
        if(rooms.isEmpty()) // If there are no rooms
            this.writer.println("Sorry, There Are No Rooms Available.");
        else
        {
            // Display the rooms
            for(int x = 0; x < rooms.size(); ++x)
                this.writer.println((x + 1) + ": " + rooms.get(x) + '\n');
        }

        this.writer.print("Enter 0 to go back: ");
        this.writer.flush();
    }

    /**
     * This method is used to display the rooms which have not been booked.
     */
    private void emptyRooms()
    {
        List<Room> rooms = Hotel.getHotel(this.currentHotel).getEmptyRooms(); // Get the rooms which have not been booked in the hotel
        this.writer.println("...Empty Rooms...");
        if(rooms.isEmpty())
            this.writer.println("Sorry, There Are No Rooms Available.");
        else
        {
            for(int x = 0; x < rooms.size(); ++x)
                this.writer.println((x + 1) + ": " + rooms.get(x) + '\n');
        }

        this.writer.print("Enter 0 to go back: ");
        this.writer.flush();
    }

    /**
     * This method is used to search for rooms based on a particular parameter, ether by price or by number of beds.
     */
    private void search()
    {
        this.writer.println("1: Search For Rooms Based On Price");
        this.writer.println("2: Search For Rooms Based On Number Of Beds");
        this.writer.print("Input Command: ");
        this.writer.flush();
        int response = Integer.parseInt(read()); // Get the response of the client

        List<Room> rooms = null;
        if(response == 1) // If search by price was selected
        {
            this.writer.println("Enter Max Price: ");
            this.writer.flush();
            int price = Integer.parseInt(read()); // Get the price from the client
            rooms = Hotel.getHotel(this.currentHotel).searchByPrice(price); // Get the rooms in the hotel that have that price or lower
        }
        else if(response == 2) // If search by beds was selected
        {
            this.writer.println("Enter Number Of Beds: ");
            this.writer.flush();
            int beds = Integer.parseInt(read()); // Get the number of beds from the client
            rooms = Hotel.getHotel(this.currentHotel).searchByBeds(beds); // Get the rooms in the hotel that have that number of beds
        }

        if(rooms == null || rooms.isEmpty()) // If there are no rooms or a wrong response was received from the client
            this.writer.println("Sorry, There Are No Rooms Available.");
        else
        {
            //Display the rooms
            for(Room room : rooms)
                this.writer.println(room);
        }
        this.writer.print("Enter 0 to go back: ");
        this.writer.flush();
    }

    /**
     * This method is the main route of the server. The appropriate page to be displayed is decided here.
     */
    private void show()
    {
        if(this.page == 0)
            welcome();
        if(this.page == 1)
            bookRoom();
        else if(this.page == 2)
            bookedRooms();
        else if (this.page == 3)
            emptyRooms();
        else if(this.page == 4)
            rooms();
        else if(this.page == 5)
            removeBooking();
        else if(this.page == 6)
            search();
    }

    /**
     * This method is used to read the input from the client.
     * @return the input of the client.
     */
    private String read()
    {
        try
        {
            String line = this.reader.readLine(); // Read a line from the client
            if(line != null) // As long as the line is valid
                return line;
        }
        catch (IOException ignored)
        {

        }
        return "-1"; // If anything else happens, always return "-1".
    }

    /**
     * This method is the main heart of the server-client connection. The lifetime of this method is the same as the connection between the server and client.
     * This method will only return once the client disconnects or if the server is shutdown.
     */
    @Override
    public void run()
    {
        String address = socket.getInetAddress().toString(); // Get the address of the connecting client
        System.out.println("Connected to Client " + address);

        home(); // Display the landing page
        int response = Integer.parseInt(read()); // Get the response from the client

        if(response > 0 && response <= Hotel.hotels.size()) // If the response is valid
        {
            this.currentHotel = response - 1; // Determine the hotel selected
            while(true)
            {
                show(); // Show the route
                int input = Integer.parseInt(read()); // Read input from the client
                if(input != -1)
                {
                    if(this.page == 0 && input == 0) // If this is the welcome page of a hotel and a client wishes to leave, disconnect.
                        break;
                    this.page = input;
                }
            }
        }

        try
        {
            this.writer.print("Disconnected");
            this.writer.flush();
            this.socket.close(); // Close the socket between the client and the server
            System.out.println("Client " + address + " has been disconnected!");
        }
        catch(Exception ignored)
        {

        }
    }
}
