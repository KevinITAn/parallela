package B.volatilee;

public class EnergyConsumptionSimulation {
    //non basta rende aggiornata la referenza ma non previene problemi di uso prima di un safe-costructor
    static volatile ElectricityMeter electricityMeter = null;

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
