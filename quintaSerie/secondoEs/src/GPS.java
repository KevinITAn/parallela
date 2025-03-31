import java.util.concurrent.ThreadLocalRandom;

class GPS implements Runnable {
    @Override
    public void run() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // Update curLocation with new coordinate
        double lat = random.nextDouble(-90.0, +90.0);
        double lon = random.nextDouble(-180.0, +180.0);

        while (!GPSSimulation.completed) {
            GPSSimulation.lock.lock();
            try {
                GPSSimulation.curLocation = new Coordinate(lat,lon);
            } finally {
                GPSSimulation.lock.unlock();
            }

            try {
                Thread.sleep(random.nextLong(1,4));
            } catch (final InterruptedException e) {
                System.err.println("GPS thread interrupted.");
                Thread.currentThread().interrupt();
            }
        }
    }
}
