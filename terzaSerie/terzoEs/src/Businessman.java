import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class Businessman {

    // Shared phone numbers of businessman
    public static int home;
    public static int office;
    public static int mobile;
    public static int emergency;
    public static int version;

    public final static ReentrantLock lock=new ReentrantLock();

    private static int getNewPhoneNumber() {
        // simulate some time-consuming task to obtain a new number from operator
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 250));
        } catch (InterruptedException e) {
            System.err.println("Execution interrupted.");
        }
        // generate new phone number
        return ThreadLocalRandom.current().nextInt(1000000, 10000000);
    }

    public static void main(String[] args) {
        // Create initial version of phoneNumbers
        Businessman.home = getNewPhoneNumber();
        Businessman.office = getNewPhoneNumber();
        Businessman.mobile = getNewPhoneNumber();
        Businessman.emergency = getNewPhoneNumber();
        Businessman.version = 0;

        // Create contacts, threads and start threads
        List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            allThreads.add(new Thread(new Contract(i)));
        allThreads.forEach(Thread::start);

        // Start simulation
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.err.println("Execution interrupted.");
                break;
            }


            lock.lock();
            try {
                System.out.println("I moved! Getting new phone numbers");
                Businessman.home = getNewPhoneNumber();
                Businessman.office = getNewPhoneNumber();
                Businessman.mobile = getNewPhoneNumber();
                Businessman.emergency = getNewPhoneNumber();
            } finally {
                lock.unlock();
            }

            System.out.println("new numbers are: new Phonenumbers [home=" + Businessman.home + ", office=" + Businessman.office + ", mobile=" + Businessman.mobile + ", emergency=" + Businessman.emergency + "]");
            Businessman.version++;
        }
        // Used to terminate
        Businessman.version = -1;
        for (Thread thread : allThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Execution interrupted.");
            }
        }
        System.out.println("Completed");
    }
}
