import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;



public class Main {
    private static void cpuIntensiveOps() {
        // Timeout for CPU-intensive operation set to 3 seconds
        final long timeout = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3);
        final Random r = new Random();
        long count = 0;
        // Loop until timeout
        while (System.currentTimeMillis() < timeout) {
            // CPU intensive operations
            count += r.nextLong(100);
            if (count > 100_000_000) {
                count = 0;
            }
        }
    }

    public static void main(String[] args) {
        // Create and start CPU-saturating threads
        final List<Thread> cpuSaturatingThreads = IntStream.range(1, 3 + Runtime.getRuntime().availableProcessors())
                .mapToObj(i -> new Thread(Main::cpuIntensiveOps))
                .peek(Thread::start)
                .toList();

        // Create two Worker threads
        Thread thread1 = new Thread(new Worker(), "Thread 1");
        Thread thread2 = new Thread(new Worker(), "Thread 2");

        //set priority PART A

        thread2.setPriority(10);
        thread1.setPriority(1);

        // Start Worker threads
        thread1.start();
        thread2.start();

        // Wait for Worker threads to terminate
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Wait for CPU-saturating threads to terminate
        for (Thread thread : cpuSaturatingThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
