package B.volatilee;

import java.util.concurrent.ThreadLocalRandom;

class HouseHold implements Runnable {
    @Override
    public void run() {
        System.out.println("HouseHold: waiting installation");
        //controllo se è stato pubblicato
        //rendendo anche volatible una soluzione corretta
        while (true) {
            if (EnergyConsumptionSimulation.electricityMeter != null) {
                break;
            }
        }

        System.out.println("HouseHold: starting consumption");
        while (!Thread.currentThread().isInterrupted()) {
            final int wattHours = ThreadLocalRandom.current().nextInt(1, 10);
            EnergyConsumptionSimulation.electricityMeter.consume(wattHours);
            try {
                Thread.sleep(15);
            } catch (final InterruptedException e) {
                System.err.println("HouseHold interrupted.");
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("HouseHold: terminating");
    }
}
