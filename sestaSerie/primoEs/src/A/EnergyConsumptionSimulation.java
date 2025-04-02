package A;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class EnergyConsumptionSimulation {
    static ElectricityMeter electricityMeter = null;

    static final ReentrantLock lock=new ReentrantLock();
    static final Condition condition= lock.newCondition();

    public static void main(final String[] args) {
        final Thread householdThread = new Thread(new HouseHold());
        final Thread electricCompanyThread = new Thread(new ElectricCompany());

        householdThread.start();
        electricCompanyThread.start();

        try {
            // Simulate for 5 seconds
            Thread.sleep(5000);

            // Interrupt threads
            electricCompanyThread.interrupt();
            householdThread.interrupt();

            // Wait for threads to terminate
            electricCompanyThread.join();
            householdThread.join();
        } catch (final InterruptedException e) {
            System.err.println("Main interrupted.");
        }
        System.out.println(String.format("Main final results: total watts=%d consumption events=%d average consumption=%.2f"
                , electricityMeter.getWatts()
                , electricityMeter.getCount()
                , electricityMeter.getAverageConsumption())
        );
    }
}
