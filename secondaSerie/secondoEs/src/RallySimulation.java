import java.util.ArrayList;
import java.util.List;


public class RallySimulation {
    private static final int NUM_CARS = 10;
    volatile static int startSignal = 0; //visibility problem

    public static void main(String[] args) {
        List<Thread> allThreads = new ArrayList<>();
        for (int i = 1; i <= NUM_CARS; i++) {
            allThreads.add(new Thread(new RallyCar(i)));
        }

        // Start all rally car threads
        allThreads.forEach(Thread::start);

        // Simulate marshal signaling the start of the race for each car
        for (int i = 1; i <= NUM_CARS; i++) {
            try {
                Thread.sleep(1000); // Wait for 1 second between signaling each car
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Marshal: Car #%d can start now!%n", i);
            startSignal = i; // Signal the car to start by updating startSignal
        }

        // Wait for all threads to finish
        try {
            for (Thread thread : allThreads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Rally simulation terminated");
    }
}
