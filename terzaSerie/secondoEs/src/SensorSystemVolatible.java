import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SensorSystemVolatible {
    private static volatile int amount = 0;//condiviso e modificabile

    //race conditional readmodify-write
    static int incrementAndGet(final int step) {
        amount += step;
        return amount;
    }

    //race conditional check-then-act
    static boolean resetIfAbove(final int threshold) {
        if (amount > threshold) {
            amount = 0;
            return true;
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