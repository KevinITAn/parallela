class TruckSpeedReader implements Runnable {
    private final int readerId;
    private int lastObservedSpeed;

    public TruckSpeedReader(final int readerId) {
        this.readerId = readerId;
        this.lastObservedSpeed = -1;
    }

    @Override
    public void run() {
        while (TruckSpeedMonitor.isMonitoring.get()) {
            // Acquire lock to access shared state
            TruckSpeedMonitor.lock.lock();
            try {
                // Update local speed if needed
                if (lastObservedSpeed != TruckSpeedMonitor.sharedSpeed) {
                    System.out.println("Reader" + readerId + ": updating last observed speed: " + lastObservedSpeed + " km/h to " + TruckSpeedMonitor.sharedSpeed + " km/h");
                    lastObservedSpeed = TruckSpeedMonitor.sharedSpeed;
                } else {
                    System.out.println("Reader" + readerId + ": (Last observed speed: " + lastObservedSpeed + " km/h vs shared speed: " + TruckSpeedMonitor.sharedSpeed + " km/h");
                }
            } finally {
                // Release lock
                TruckSpeedMonitor.lock.unlock();
            }
        }
    }
}
