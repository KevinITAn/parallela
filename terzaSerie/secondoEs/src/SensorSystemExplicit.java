import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;


public class SensorSystemExplicit {
    private static int amount = 0;//condiviso e modificabile
    static final ReentrantLock lock=new ReentrantLock();//blocca la classe

    //race conditional readmodify-write
    static int incrementAndGet(final int step) {
        lock.lock();
        try{
            amount += step;
            return amount;
        }finally {
            lock.unlock();
        }

    }

    //race conditional check-then-act
    static boolean resetIfAbove(final int threshold) {

        lock.lock();
        try{
            if (amount > threshold) {
                amount = 0;
                return true;
            }
        }finally {
            lock.unlock();
        }
        return false;
    }

    public static void main(final String[] args) {
        final List<Thread> threads = new ArrayList<>();

        // Create threads and sensors
        for (int i = 1; i <= 10; i++) {
            final int sensorThreshold = (i * 10);
            threads.add(new Thread(new Sensor(sensorThreshold)));
        }

        // start all threads
        threads.forEach(Thread::start);

        final Random random = new Random();
        while (true) {
            final int increment = random.nextInt(9);
            final int newAmount = incrementAndGet(increment);
            System.out.println("Actuator: shared state incremented to " + newAmount);
            if (newAmount > 120) {
                break;
            }
            try {
                Thread.sleep(random.nextInt(9));
            } catch (final InterruptedException e) {
                System.err.println("Execution interrupted.");
            }
        }

        for (final Thread t : threads) {
            try {
                t.join();
            } catch (final InterruptedException e) {
                System.err.println("Execution interrupted.");
            }
        }
    }
}