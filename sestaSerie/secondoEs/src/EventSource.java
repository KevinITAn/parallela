import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class EventSource implements Runnable {
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, EventListener> allListeners = new HashMap<>();

    @Override
    public void run() {
        for (long i = 0; i < 30_000_000; i++) {
            // Create a new event
            final Event e = new Event(i);

            // avoid concurrent access to the map
            lock.lock();
            try {
                // Handle the event for each eventListener that has registered to this EventSource
                for (final int id : allListeners.keySet()) {
                    final EventListener listener = allListeners.get(id);
                    listener.onEvent(id, e);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void registerListener(final int id, final EventListener listener) {
        // avoid concurrent access to the map
        lock.lock();
        try {
            allListeners.put(id, listener);
        } finally {
            lock.unlock();
        }
    }
}
