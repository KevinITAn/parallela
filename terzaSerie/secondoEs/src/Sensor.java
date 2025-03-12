class Sensor implements Runnable {
    private final int threshold;//limite condiviso

    public Sensor(final int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void run() {
        System.out.println("Sensor[" + threshold + "]: start monitoring!");

        while (!SensorSystemExplicit.resetIfAbove(threshold)) {//resetta se sopra
            /* Busy wait */
        }

        System.out.println("Sensor[" + threshold + "]: threshold passed!");
    }
}