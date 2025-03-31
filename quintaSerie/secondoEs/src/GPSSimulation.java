import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GPSSimulation {
    static boolean completed = false;
    static Coordinate curLocation = null;
    static Lock lock = new ReentrantLock();

    public static void main(final String[] args) {
        // Create and start GPS thread
        final Thread gpsThread = new Thread(new GPS());
        gpsThread.start();

        System.out.println("Simulation started");
        Coordinate prevLocation = null;

        // Wait until location changes
        do {
            lock.lock();
            try {
                prevLocation = curLocation;
            } finally {
                lock.unlock();
            }
        } while (prevLocation == null);

        System.out.println("Initial position received");

        // Request 100 position updates
        for (int i = 0; i < 100; i++) {
            Coordinate lastLocation;
            do {
                lock.lock();
                try {
                    lastLocation = curLocation;
                } finally {
                    lock.unlock();
                }
            } while (lastLocation == prevLocation);

            // Write distance between firstLocation and secondLocation position
            System.out.println("Distance from " + prevLocation + " to "
                    + lastLocation + " is "
                    + prevLocation.distance(lastLocation));

            prevLocation = lastLocation;
        }

        completed = true;

        // Stop GPS thread and wait until it finishes
        try {
            gpsThread.join();
        } catch (final InterruptedException e) {
            System.err.println("Interrupted while waiting for GPS thread");
        }
        System.out.println("Simulation completed");
    }
}
