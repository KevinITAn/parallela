import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SalesEmployee implements Runnable {
    private static final Lock lock = new ReentrantLock();
    private static int salesCounter = 0;

    // REMARK: do not edit the following variables
    final static AtomicInteger ranking = new AtomicInteger(1);
    final private int id;

    SalesEmployee(final int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("SalesEmployee" + id + ": starting");
        final Random random = new Random();

        boolean isRunning = true;
        while (isRunning) {
            lock.lock();
            try {
                if (salesCounter > 1_000_000) {
                    salesCounter = 0;
                    isRunning = false;
                } else {
                    salesCounter = salesCounter + 1 + random.nextInt(5);
                }
            } finally {
                lock.unlock();
            }
        }
        // Remark: do not edit - just for visualization purposes
        System.out.println("SalesEmployee" + id + ": finished. Rank: " + ranking.getAndIncrement());
    }
}

public class SalesRanking {
    public static void main(final String[] args) {
        final List<Thread> allThread = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            allThread.add(new Thread(new SalesEmployee(i)));
        }

        allThread.forEach(Thread::start);

        for (final Thread thread : allThread) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                System.err.println("Error while joining threads");
            }
        }
    }
}
