import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MonteCarloMain {

    private static final int NUM_THREADS = 5 ;
    private static final long MAX_RUNTIME_MS = 20_000;
    private static final double TARGET_PRECISION = 1e-6;

    static AtomicInteger cntInside = new AtomicInteger(0);
    static AtomicInteger cntPoint = new AtomicInteger(0);

    public static void main(String[] args) {
        // create thread
        List<Thread> arrThread = new ArrayList<>();
        for (int i = 0; i < NUM_THREADS; i++)
            arrThread.add(new Thread(new MonteCarloWorker()));
        // start
        for (Thread workerTmp : arrThread)
            workerTmp.start();

        long startTime = System.currentTimeMillis();

        // continues check
        while (true) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // calcolo
            double piEstimate = 4.0 * cntInside.get() / cntPoint.get();
            double precision = Math.abs(Math.PI - piEstimate);

            // check se devo finire
            if (precision < TARGET_PRECISION) {
                System.out.println("Precision of 6 digits reached!");
                break;
            }

            if (System.currentTimeMillis() - startTime > MAX_RUNTIME_MS) {
                System.out.println("Time limit of 20s reached!");
                break;
            }
        }
        // Interrupt all worker threads
        for (Thread worker : arrThread)
            worker.interrupt();

        System.out.println("Final π Approximation: " + (4.0 * cntInside.get() / cntPoint.get()));

    }
}