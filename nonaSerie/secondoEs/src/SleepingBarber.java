import java.util.concurrent.*;
import java.util.Random;

public class SleepingBarber {

    // Coda condivisa per i clienti in attesa
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);
    private final Random random = ThreadLocalRandom.current();

    public static void main(String[] args) {
        SleepingBarber shop = new SleepingBarber();
        shop.start();
    }

    public void start() {

        // Barbiere
        new Thread(() -> {
            while (true) {
                try {
                    //tempo per controllare
                    Thread.sleep(random.nextInt(51) + 50);

                    // Il barbiere preleva un cliente dalla coda (attende se la coda è vuota)
                    String customer = queue.take();
                    System.out.println("Barber is cutting hair for " + customer);

                    //taglio capelli
                    Thread.sleep(random.nextInt(501) + 500);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            int customerNumber = 1;
            while (true) {
                try {
                    // arriva cliente
                    Thread.sleep(random.nextInt(251) + 450);

                    // tempo attesa prima di stanza attesa
                    Thread.sleep(random.nextInt(81) + 80);

                    String customer = "Customer " + customerNumber++;
                    // Se la coda è piena, il cliente se ne va, altrimenti entra in sala d'attesa
                    if (!queue.offer(customer)) {
                        System.out.println(customer + " leaves because no chairs are available.");
                    } else {
                        System.out.println(customer + " is waiting.");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
