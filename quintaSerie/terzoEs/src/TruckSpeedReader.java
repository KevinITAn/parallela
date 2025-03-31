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

            // Update local speed if needed
            if (lastObservedSpeed != TruckSpeedMonitor.getSharedSpeed()) {
                System.out.println("Reader" + readerId + ": updating last observed speed: " + lastObservedSpeed + " km/h to " + TruckSpeedMonitor.getSharedSpeed() + " km/h");
                lastObservedSpeed = TruckSpeedMonitor.getSharedSpeed();
            } else {
                System.out.println("Reader" + readerId + ": (Last observed speed: " + lastObservedSpeed + " km/h vs shared speed: " + TruckSpeedMonitor.getSharedSpeed() + " km/h");
            }

        }
    }
}
