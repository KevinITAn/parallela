package B;

import org.isin.railwayquest.client.Crane;
import org.isin.railwayquest.client.RailwayQuest;
import org.isin.railwayquest.client.Train;

public class Level6WoodWorkerB extends RailwayQuest {
    private Crane crane;
    private Boolean redAtCrane = false;
    private Boolean greenAtCrane = false;
    private Object sync = new Object();

    private class RedTrain extends Train implements Runnable {
        @Override
        public void run() {
            for (int lap = 1; lap <= 10; lap++) {
                // Go to deposit
                for (int i = 0; i < 6; i++)
                    move();

                logInfo("Arrived at the deposit");
                load();
                // Go to Crane
                for (int i = 0; i < 4; i++)
                    move();

                logInfo("Arrived at the crane");
                synchronized (sync) {
                    redAtCrane = true;
                    if (greenAtCrane) {
                        crane.transfer();
                        sync.notify();
                    } else {
                        while (!greenAtCrane) {
                            try {
                                sync.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    greenAtCrane = false;
                }
                // Go to starting point
                for (int i = 0; i < 4; i++)
                    move();
            }
        }
    }

    private class GreenTrain extends Train implements Runnable {
        @Override
        public void run() {
            // Go to Crane
            for (int i = 0; i < 10; i++)
                move();

            for (int lap = 1; lap <= 10; lap++) {
                logInfo("Arrived at the crane");

                synchronized (sync) {
                    greenAtCrane = true;
                    if (redAtCrane) {
                        crane.transfer();
                        sync.notify();
                    } else {
                        while (!redAtCrane) {
                            try {
                                sync.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    redAtCrane = false;
                }

                // Go to starting point
                for (int i = 0; i < 4; i++)
                    move();

                // Go to Factory
                for (int i = 0; i < 6; i++)
                    move();

                logInfo("Arrived at the factory");
                unload();

                if (lap == 10) {
                    break;
                }
                // Go to Crane
                for (int i = 0; i < 4; i++)
                    move();
            }
        }
    }

    @Override
    protected void execute(String token) {
        startAttempt(token);

        // Acquire crane
        crane = getCrane(Crane.Label.A);

        RedTrain redTrain = new RedTrain();
        GreenTrain greenTrain = new GreenTrain();

        register(redTrain, Train.Color.Red);
        register(greenTrain, Train.Color.Green);

        Thread redTrainThread = new Thread(redTrain);
        Thread greenTrainThread = new Thread(greenTrain);

        redTrainThread.start();
        greenTrainThread.start();

        try {
            redTrainThread.join();
            greenTrainThread.join();
        } catch (InterruptedException e) {
            System.err.println("Interruption occurred. Terminating execution");
        }
        terminateAttempt();
    }

    public static void main(String[] args) {
        new Level6WoodWorkerB().execute("LEVL-2LNZ-4M6P-OA20-R3B2");
    }
}
