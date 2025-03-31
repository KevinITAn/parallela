import java.util.ArrayList;
import java.util.List;

public class FlightBooking {
    public static void main(String[] args) {
        Flight flight = new Flight("AB1234", 30, 6);
        System.out.println(flight);

        List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < 15; i++)
            allThreads.add(new Thread(new Passenger(i, flight)));

        allThreads.forEach(Thread::start);

        for (Thread thread : allThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for thread to terminate");
            }
        }
    }
}
