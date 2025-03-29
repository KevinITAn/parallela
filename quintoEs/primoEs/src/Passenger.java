import java.util.Random;
import java.util.concurrent.TimeUnit;

class Passenger implements Runnable {
    private final int id;
    private final Flight flight;

    public Passenger(int id, Flight flight) {
        this.id = id;
        this.flight = flight;
    }

    @Override
    public void run() {
        Random random = new Random();
        int numTickets = 0;

// This value might be outdated, but the only effect is one extra cycle, so it's a minor issue.
// I could use while (true) { ... if (flight.getNumSeatsAvailable() == 0) break; }, but I don't like that solution. :D
        while (flight.getNumSeatsAvailable() > 0) {

            final int row = random.nextInt(flight.getNumRows());
            final int seat = random.nextInt(flight.getNumSeatsPerRow());

            if(!flight.bookSeat(row,seat))
                continue;
            //book ok!
           numTickets++;
            if (numTickets % 4 == 0) {
                System.out.println(flight);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(25);
            } catch (InterruptedException e) {
                System.err.printf("Passenger%d interrupted. Terminating%n", id);
                break;
            }
        }
        System.out.printf("Passenger%d booked %d tickets %n", id, numTickets);
    }
}
