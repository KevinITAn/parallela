import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingSimulation {
    private static final int NUM_CUSTOMER=100;
    private static final int NUM_ROOMS = 10000;
    private static final int THRESHOLD = 2500;
    private final Map<Integer, Customer> bookings = new ConcurrentHashMap<>();
    private final AtomicInteger numOfBookings = new AtomicInteger(0);

    private class Customer implements Runnable {
        final int id;

        public Customer(int id) {
            this.id = id;
        }

        public void run() {
            boolean bookRandomRoom = numOfBookings.incrementAndGet() < THRESHOLD;
            int roomNumber;
            if (bookRandomRoom) {
                boolean didBookRoom;
                do {
                    roomNumber = ThreadLocalRandom.current().nextInt(NUM_ROOMS);
                    didBookRoom = bookings.putIfAbsent(roomNumber, this) == null;
                } while (!didBookRoom);
            } else {
                for (roomNumber = 0; roomNumber < NUM_ROOMS; roomNumber++) {
                    boolean didBookRoom = bookings.putIfAbsent(roomNumber, this) == null;
                    if (didBookRoom)
                        break;
                }
            }
            // Waiting for booking confirmation
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.err.println("Customer " + id + " was interrupted while waiting for booking confirmation.");
                return;
            }
            System.out.println("Customer " + id + " got booking confirmation for room number " + roomNumber + " booked randomly: " + bookRandomRoom);
        }
    }

    long simulate(boolean executor, boolean virtual) {
        return (executor) ? simulateExecutors(virtual) : simulateThreads(virtual);
    }

    private long simulateThreads(boolean virtual) {
        long startTime = System.currentTimeMillis();
        List<Thread> threadList=new ArrayList<>();

        if(virtual){
            for(int i=0;i<NUM_CUSTOMER;i++){
                threadList.add(Thread.ofVirtual().start(new Customer(i)));
            }
        }else{
            for(int i=0;i<NUM_CUSTOMER;i++){
                Thread tmpThread=new Thread(new Customer(i));
                tmpThread.start();
                threadList.add(tmpThread);

            }
        }

        try{
            for (Thread thread : threadList) {
                thread.join();
            }
        }catch (InterruptedException e){
            System.err.println(e.getMessage());
        }


        return System.currentTimeMillis() - startTime;
    }

    private long simulateExecutors(boolean virtual) {
        long startTime = System.currentTimeMillis();


        if(virtual){
            ExecutorService executor=Executors.newVirtualThreadPerTaskExecutor();
            for(int i=0;i<NUM_CUSTOMER;i++)
                executor.submit(new Customer(i));
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate in time");
                }
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for executor to terminate");
            }

        }else {
            ExecutorService executor=Executors.newWorkStealingPool();
            for(int i=0;i<NUM_CUSTOMER;i++)
                executor.submit(new Customer(i));
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate in time");
                }
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for executor to terminate");
            }

        }

        return System.currentTimeMillis() - startTime;
    }

    enum SimulationType {
        VIRTUAL_THREADS(true, false),
        PLATFORM_THREADS(false, false),
        VIRTUAL_EXECUTOR(true, true),
        PLATFORM_EXECUTOR(false, true);

        SimulationType(boolean virtual, boolean executor) {
            this.virtual = virtual;
            this.executor = executor;
        }

        private boolean virtual;
        private boolean executor;
    }

    public static void main(String[] args) {
        final int numSteps = 5;
        Map<SimulationType, List<Long>> simulationTimes = new TreeMap<>();
        for (SimulationType type : SimulationType.values()) {
            simulationTimes.put(type, new ArrayList<>());
            for (int step = 1; step <= numSteps; step++) {
                System.out.printf("Running %s [%2d/%2d]%n", type.name(), step, numSteps);
                final long simulationTime = new BookingSimulation().simulate(type.executor, type.virtual);
                simulationTimes.get(type).add(simulationTime);
            }
        }
        simulationTimes.forEach((type, times) -> System.out.printf("%20s: %8.1f ms%n", type.name(), times.stream().mapToLong(Long::longValue).average().getAsDouble()));
    }
}
