import java.util.ArrayList;
import java.util.List;

public class RestRoomsExercise {
    public static void main(final String[] args) {
        PublicRestrooms restRoom = new PublicRestrooms(2);

        List<Thread> allPersons = new ArrayList<>();
        List<User> allUsers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            final User user = new User(restRoom, i);
            allUsers.add(user);
            allPersons.add(new Thread(user));
            System.out.println("Created: " + user);
        }

        allPersons.forEach(Thread::start);

        // Wait for all threads to terminate
        for (Thread thread : allPersons) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                System.err.println("Execution interrupted.");
            }
        }

        int userTotalUsageCount = 0;
        int userTotalOccupiedCount = 0;
        for (User user : allUsers) {
            userTotalUsageCount += user.getUsageCount();
            userTotalOccupiedCount += user.getOccupiedCount();
            System.out.println(user + ": usages: " + user.getUsageCount() + " occupied: " + user.getOccupiedCount());
        }

        System.out.println("Usage recap");
        System.out.println("Total user usage count: " + userTotalUsageCount);
        System.out.println("Total restroom usage count: " + restRoom.getUsageCount());

        System.out.println("Occupancy recap");
        System.out.println("Total user occupied count: " + userTotalOccupiedCount);
        System.out.println("Total restroom occupied count: " + restRoom.getOccupiedCount());
    }
}
