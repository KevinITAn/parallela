// Represents a rally car participating in the race
class RallyCar implements Runnable {
    final int carNumber;

    RallyCar(int carNumber) {
        this.carNumber = carNumber;
    }

    @Override
    public void run() {
        System.out.printf("RallyCar #%d waiting for start signal...%n", carNumber);
        while (RallySimulation.startSignal != carNumber) {
            // Busy wait until start signal matches this car's number
        }
        System.out.printf("RallyCar #%d starting.%n", carNumber);
        final long duration = 500 + (long) (Math.random() * 1000);
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("RallyCar #%d finished the race.%n", carNumber);
    }
}