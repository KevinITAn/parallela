import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Random;

public class SleepingBarber {

    private static final int CHAIRS = 3;
    private final BlockingQueue<Customer> waitingRoom = new ArrayBlockingQueue<>(CHAIRS);
    private final AtomicBoolean barberSleeping = new AtomicBoolean(true);
    private final Random rand = ThreadLocalRandom.current();

    public static void main(String[] args) {
        SleepingBarber shop = new SleepingBarber();
        shop.start();
    }

    public void start() {
        Thread barberThread = new Thread(new Barber(), "Barber");
        barberThread.start();

        Thread customerGenerator = new Thread(() -> {
            int customerId = 1;
            while (true) {
                try {
                    Thread.sleep(rand.nextInt(450, 700));
                    Customer customer = new Customer(customerId++);
                    Thread t = new Thread(customer, "Customer-" + customer.id);
                    t.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        customerGenerator.start();
    }

    class Barber implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    // Controlla la waiting room ogni 50–100ms
                    Thread.sleep(rand.nextInt(50, 100));

                    Customer customer = waitingRoom.poll();
                    if (customer != null) {
                        barberSleeping.set(false);
                        System.out.println("Barber is cutting hair of Customer " + customer.id);
                        Thread.sleep(rand.nextInt(500, 1000)); // taglio capelli
                        System.out.println("Barber finished with Customer " + customer.id);
                    } else {
                        // Nessun cliente → dorme
                        if (!barberSleeping.get()) {
                            System.out.println("Barber is sleeping...");
                        }
                        barberSleeping.set(true);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Customer implements Runnable {
        private final Random rand = ThreadLocalRandom.current();
        private final int id;

        Customer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(rand.nextInt(80, 160)); // tempo per raggiungere la waiting room
                if (waitingRoom.offer(this)) {
                    System.out.println("Customer " + id + " is waiting.");
                } else {
                    // sala piena → cliente se ne va
                    System.out.println("Customer " + id + " left (no available chair).");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
