package hotel;

import java.util.Date;

/**
 * This class is used to represent the details of the booking of a room.
 */
public class BookingDetails
{
    public String customerName; // The name of the customer who booked the room
    public int DurationInDays; // The duration, in days, for which the room will be booked
    public long timeStamp; // The current time the room was booked

    public BookingDetails(String customerName, int DurationInDays, long timeStamp)
    {
        this.customerName = customerName;
        this.DurationInDays = DurationInDays;
        this.timeStamp = timeStamp;
    }

    public BookingDetails()
    {
        this.customerName = "";
        this.DurationInDays = 0;
        this.timeStamp = 0;
    }

    /**
     * This method is used to check if the room containing these details is already booked.
     * @return the booking status of the room.
     */
    public boolean isBooked()
    {
        return !this.customerName.isEmpty() && this.DurationInDays > 0 && this.timeStamp > 0;
    }

    /**
     * This method clears the booking of the room that contains these details.
     */
    public void clear()
    {
        this.customerName = "";
        this.DurationInDays = 0;
        this.timeStamp = 0;
    }

    /**
     * This method converts this details object into a user-friendly format.
     * @return the String representation of these details.
     */
    @Override
    public String toString()
    {
       return "Customer Name ("  + this.customerName
               + ") Duration In Days (" + this.DurationInDays
               + ") Date Booked (" + new Date(this.timeStamp) + ")";
    }
}
