package cap02_ThreadSafety_Part1_Abate.es1;

import org.isin.railwayquest.client.RailwayQuest;
import org.isin.railwayquest.client.Train;

public class Level4CrossingV2 extends RailwayQuest {
    private Object lockA = new Object();
    private Object lockB = new Object();


    private class GreenTrain extends Train implements Runnable {

        @Override
        public void run() {
            for (int lap = 1; lap <= 7; lap++) {
                move();
                move();

                //cross A
                synchronized (lockA) {
                    enterCrossing();
                    traverseCrossing();
                    leaveCrossing();
                }

                for (int i = 0; i < 11; i++) {
                    move();
                }

                //cross B
                synchronized (lockB) {
                    enterCrossing();
                    traverseCrossing();
                    leaveCrossing();
                }

                move();

            }
        }
    }

    private class RedTrain extends Train implements Runnable {
        @Override
        public void run() {
            for (int lap = 1; lap <= 10; lap++) {

                for(int i = 0; i < 6; i++){
                    move();
                }

                //cross A
                synchronized (lockA) {
                    enterCrossing();
                    traverseCrossing();
                    leaveCrossing();
                }

                for(int i = 0; i < 3; i++){
                    move();
                }

                //cross B
                synchronized (lockB) {
                    enterCrossing();
                    traverseCrossing();
                    leaveCrossing();
                }

                for(int i = 0; i < 5; i++){
                    move();
                }

            }
        }
    }

    @Override
    protected void execute(String token) {
        startAttempt(token);

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
            System.err.println("Interruption occurred.");
        }
        terminateAttempt();
    }

    public static void main(String[] args) {
        new Level4CrossingV2().execute("");
    }
}
