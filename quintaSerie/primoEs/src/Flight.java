import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

class Flight {

    private final ReentrantLock lock=new ReentrantLock();
    private final String flightNumber;
    private final int numRows;
    private final int numSeatsPerRow;
    private int numSeatsAvailable;
    public final boolean[][] seats;

    public Flight(String flightNumber, int numRows, int numSeatsPerRow) {
        this.flightNumber = flightNumber;
        this.numRows = numRows;
        this.numSeatsPerRow = numSeatsPerRow;
        this.numSeatsAvailable = numRows * numSeatsPerRow;
        this.seats = new boolean[numRows][numSeatsPerRow];
    }

    public String getFlightNumber() {
            return flightNumber;
    }

    public int getNumRows() {
            return numRows;
    }

    public int getNumSeatsPerRow() {
            return numSeatsPerRow;
    }



    public boolean[][] getSeats() {
            return Arrays.copyOf(seats,seats.length);
    }

    public int getNumSeatsAvailable() {
        lock.lock();
        try {
            return numSeatsAvailable;
        } finally {
            lock.unlock();
        }
    }

    public boolean bookSeat(int row,int seat){
        lock.lock();
        try{
            if (!seats[row][seat]) {
                seats[row][seat] = true;
                numSeatsAvailable--;
                return true;
            }
            return false;
        }finally {
            lock.unlock();
        }
    }

    public String toString() {
        return String.format("Flight %s (%d seats available)", flightNumber, numSeatsAvailable);
    }
}
