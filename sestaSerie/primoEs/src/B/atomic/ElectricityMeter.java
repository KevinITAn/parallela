package B.atomic;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ElectricityMeter {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int count = 0;
    private int watts = 0;

    // Simulate the consumption of electricity
    public void consume(int wattHours) {
        lock.writeLock().lock();
        try {
            watts += wattHours;
            count += 1;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getWatts() {
        lock.readLock().lock();
        try {
            return watts;
        } finally {
            lock.readLock().unlock();
        }

    }

    public int getCount() {
        lock.readLock().lock();
        try {
            return count;
        } finally {
            lock.readLock().unlock();
        }
    }

    public float getAverageConsumption() {
        lock.readLock().lock();
        try {
            return count == 0 ? 0 : (float) watts / (float) count;
        } finally {
            lock.readLock().unlock();
        }
    }
}
