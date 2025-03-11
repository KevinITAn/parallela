class Sensor implements Runnable {
    private final int threshold;

    public Sensor(final int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void run() {
        System.out.println("Sensor[" + threshold + "]: start monitoring!");

        while (!SensorSystem.resetIfAbove(threshold)) {
            /* Busy wait */
        }

        System.out.println("Sensor[" + threshold + "]: threshold passed!");
    }
}