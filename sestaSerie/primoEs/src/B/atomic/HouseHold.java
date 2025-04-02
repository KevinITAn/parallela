package B.atomic;

import java.util.concurrent.ThreadLocalRandom;

class HouseHold implements Runnable {
    @Override
    public void run() {
        System.out.println("HouseHold: waiting installation");

        while (EnergyConsumptionSimulation.electricityMeter.get() == null) {
            Thread.yield();//per rallentare
        }

        System.out.println("HouseHold: starting consumption");
        while (!Thread.currentThread().isInterrupted()) {
            final int wattHours = ThreadLocalRandom.current().nextInt(1, 10);
            EnergyConsumptionSimulation.electricityMeter.get().consume(wattHours);//get -> già sicuro di not null
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
