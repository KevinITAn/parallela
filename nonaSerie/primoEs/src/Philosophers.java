import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

enum Status {
    THINKING, HUNGRY, EATING
}

class Fork {
    private boolean taken = false;

    public synchronized boolean take() {
        if (taken) return false;
        taken = true;
        return true;
    }

    public synchronized void release() {
        taken = false;
    }
}

class Philosopher implements Runnable {
    private final int id;
    private int eatCount = 0;

    public Philosopher(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                think();
                pickUpForks();
                eat();
                putDownForks();
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println(this + " ate " + eatCount + " times");
    }

    private void think() throws InterruptedException {
        System.out.println(this + " is thinking");
        Thread.sleep(Philosophers.UNIT_OF_TIME * ThreadLocalRandom.current().nextInt(6));
    }

    private void eat() throws InterruptedException {
        System.out.println(this + " is eating");
        Thread.sleep(Philosophers.UNIT_OF_TIME);
        eatCount++;
    }

    private void pickUpForks() throws InterruptedException {
        int left = id;
        int right = (id + 1) % Philosophers.NUM_PHILOSOPHERS;
        //rendo atomica operazione di prendere entrambe le forchette!
        Philosophers.lock.lock();
        try {
            Philosophers.status[id] = Status.HUNGRY;
            while (!canEat(id)) {
                Philosophers.conditions[id].await();//nel caso che i due vicini stanno mangiando
            }//sono sicuro che i 2 vicini non mangiano quindi prendo le fork
            Philosophers.status[id] = Status.EATING;
            Philosophers.forks[left].take();
            Philosophers.forks[right].take();
        } finally {
            Philosophers.lock.unlock();
        }
    }

    //rilascia forks e notifica a destra e sinistra
    private void putDownForks() {
        int left = id;
        int right = (id + 1) % Philosophers.NUM_PHILOSOPHERS;

        Philosophers.lock.lock();
        try {
            Philosophers.forks[left].release();
            Philosophers.forks[right].release();
            Philosophers.status[id] = Status.THINKING;

            // Notifica i vicini se possono mangiare ora
            test((id + 4) % Philosophers.NUM_PHILOSOPHERS); // sinistra
            test((id + 1) % Philosophers.NUM_PHILOSOPHERS); // destra
        } finally {
            Philosophers.lock.unlock();
        }
    }

    //Un filosofo può mangiare solo se i due filosofi adiacenti non stanno mangiando.
    private boolean canEat(int i) {
        int left = (i + Philosophers.NUM_PHILOSOPHERS - 1) % Philosophers.NUM_PHILOSOPHERS;
        int right = (i + 1) % Philosophers.NUM_PHILOSOPHERS;
        return Philosophers.status[left] != Status.EATING &&
                Philosophers.status[right] != Status.EATING;
    }

    private void test(int i) {
        if (Philosophers.status[i] == Status.HUNGRY && canEat(i)) {
            Philosophers.status[i] = Status.EATING;
            Philosophers.forks[i].take();
            Philosophers.forks[(i + 1) % Philosophers.NUM_PHILOSOPHERS].take();
            Philosophers.conditions[i].signal();
        }
    }

    @Override
    public String toString() {
        return "Philosopher " + id;
    }
}

public class Philosophers {
    public static final int NUM_PHILOSOPHERS = 5;
    public static final int UNIT_OF_TIME = 50;
    public static final int RUNNING_TIME = 60_000;

    public static final Fork[] forks = new Fork[NUM_PHILOSOPHERS];
    public static final ReentrantLock lock = new ReentrantLock();
    public static final Condition[] conditions = new Condition[NUM_PHILOSOPHERS];
    public static final Status[] status = new Status[NUM_PHILOSOPHERS];

    static {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new Fork();
            status[i] = Status.THINKING;
            conditions[i] = lock.newCondition();
        }
    }

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            threads.add(new Thread(new Philosopher(i)));
        }

        threads.forEach(Thread::start);

        try {
            Thread.sleep(RUNNING_TIME);
        } catch (InterruptedException e) {
            System.out.println("Simulation interrupted.");
        }

        threads.forEach(Thread::interrupt);
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Error joining thread.");
            }
        }
    }
}
