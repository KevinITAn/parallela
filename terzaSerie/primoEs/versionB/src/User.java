import java.util.concurrent.ThreadLocalRandom;

class User implements Runnable {
    private final int ID;
    private final PublicRestrooms restrooms;
    private int usageCount;
    private int occupiedCount;

    public User(PublicRestrooms restrooms, int id) {
        this.restrooms = restrooms;
        this.ID = id;
        this.usageCount = 0;
        this.occupiedCount = 0;
    }

    @Override
    public void run() {
        System.out.println(this + " starting");
        for (int i = 0; i < 250; i++) {
            if (restrooms.enter()) {
                usageCount++;
            } else {
                occupiedCount++;
            }

            // Simulate time before going the restroom again
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(1, 5));
            } catch (InterruptedException e) {
                System.err.println("Execution interrupted, terminating.");
                break;
            }
        }
        System.out.println(this + " finished");
    }

    public int getUsageCount() {
        return usageCount;
    }

    public int getOccupiedCount() {
        return occupiedCount;
    }

    @Override
    public String toString() {
        return "User" + ID;
    }
}
