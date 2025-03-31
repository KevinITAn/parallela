import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class TruckSpeedMonitor {
    final static AtomicBoolean isMonitoring = new AtomicBoolean(true);

    final static ReentrantLock lock = new ReentrantLock();
    static int sharedSpeed = 0;

    public static void main(final String[] args) {
        final ArrayList<Thread> allReaderThreads = new ArrayList<>();
        final Random random = new Random();

        // Create reader threads
        for (int i = 0; i < 10; i++) {
            allReaderThreads.add(new Thread(new TruckSpeedReader(i)));
        }

        // Start all reader threads
        for (final Thread readerThread : allReaderThreads) {
            readerThread.start();
        }

        // Simulate speed updates for 1000 iterations
        for (int i = 0; i < 100; i++) {
            // Acquire lock to update shared speed
            lock.lock();
            try {
                sharedSpeed = random.nextInt(100); // Simulating speed in km/h
            } finally {
                // Release lock
                lock.unlock();
            }
            // Wait 1 ms between updates
            try {
                Thread.sleep(1);
            } catch (final InterruptedException e) {
                System.err.println("Interrupted while waiting between updates.");
                break;
            }
        }

        // Set monitoring flag to false to terminate reader threads
        isMonitoring.set(false);

        // Wait for all reader threads to complete
        for (final Thread readerThread : allReaderThreads)
            try {
                readerThread.join();
            } catch (final InterruptedException e) {
                System.err.println("Interrupted while waiting for reader threads to finish.");
            }

        System.out.println("Speed monitoring terminated.");
    }
}
