package A;

class ElectricCompany implements Runnable {
    @Override
    public void run() {
        System.out.println("ElectricCompany: installing electricity meter");
        try {
            Thread.sleep(2000);
        } catch (final InterruptedException e) {
            System.err.println("ElectricCompany interrupted.");
            return;
        }

        EnergyConsumptionSimulation.lock.lock();
        try{
                EnergyConsumptionSimulation.electricityMeter = new ElectricityMeter();
            //sveglia tutti i thread
            EnergyConsumptionSimulation.condition.signalAll();
        }finally {
            EnergyConsumptionSimulation.lock.unlock();
        }




        System.out.println("ElectricCompany: electricity meter installed. Starting consumption monitoring");

        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            System.err.println("ElectricCompany interrupted.");
            return;
        }

        while (!Thread.currentThread().isInterrupted()) {
            final float averageConsumption = EnergyConsumptionSimulation.electricityMeter.getAverageConsumption();
            System.out.println("ElectricCompany: averageConsumption: " + averageConsumption + " kWH");

            try {
                Thread.sleep(250);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("ElectricCompany interrupted.");
            }
        }
        System.out.println("ElectricCompany: terminating");
    }
}
