import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EventSimulation {
    public static void main(final String[] args) {
        final EventSource eventSource = new EventSource();
        final Thread eventSourceThread = new Thread(eventSource);

        // Start eventSource thread
        eventSourceThread.start();

        // Create and register listeners to eventSource
        final List<EventListener> allListeners = new ArrayList<>();
        for (int i = 1; i <= 20; i++)
            allListeners.add(new EventListener(i, eventSource));

        // Wait for thread to terminate
        try {
            eventSourceThread.join();
        } catch (final InterruptedException e) {
            System.err.println("Interrupted while waiting for the event source thread to terminate.");
        }
    }
}
