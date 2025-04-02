package A;

import java.util.concurrent.ThreadLocalRandom;

class HouseHold implements Runnable {
    @Override
    public void run() {
        System.out.println("HouseHold: waiting installation");
        //controllo se è stato pubblicato
        EnergyConsumptionSimulation.lock.lock();
        try {
            while (EnergyConsumptionSimulation.electricityMeter == null) {
                EnergyConsumptionSimulation.condition.await();  // Aspetta finché il contatore non è disponibile
            }
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        } finally {
            EnergyConsumptionSimulation.lock.unlock();
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
