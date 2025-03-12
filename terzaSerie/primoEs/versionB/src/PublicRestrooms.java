import java.util.concurrent.ThreadLocalRandom;

class PublicRestrooms {
    public final Object objLock=new Object();
    private int usageCount;
    private int occupiedCount;
    private final int numAvailable;
    private int numOccupied;

    public PublicRestrooms(int numRestrooms) {
        this.numAvailable = numRestrooms;
        this.numOccupied = 0;
        this.usageCount = 0;
        this.occupiedCount = 0;
    }

    public boolean enter() {
        // Verify if free restrooms are available

        synchronized (objLock) {
            if (numOccupied < numAvailable) {
                // Available restroom found: occupy
                numOccupied++;
                usageCount++;
            } else {
                // All restrooms occupied!
                occupiedCount++;
                return false;
            }
        }


        // use restroom: no protection required
        useRestroom();

        // leave restroom

        synchronized (objLock) {
            numOccupied--;
        }
        return true;
    }

    private void useRestroom() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(5, 15));
        } catch (InterruptedException e) {
            System.err.println("Execution interrupted.");
        }
    }

    public int getUsageCount() {
        return usageCount;
    }

    public int getOccupiedCount() {
        return occupiedCount;
    }
}
