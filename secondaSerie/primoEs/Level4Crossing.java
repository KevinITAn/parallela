package cap02_ThreadSafety_Part1_Abate.es1;

import org.isin.railwayquest.client.RailwayQuest;
import org.isin.railwayquest.client.Train;

import java.util.concurrent.locks.ReentrantLock;

public class Level4Crossing extends RailwayQuest {
    private ReentrantLock lock = new ReentrantLock();

    private void cross(Train train){
        lock.lock();
        try{
            train.enterCrossing();
            train.traverseCrossing();
            train.leaveCrossing();
        }finally {
            lock.unlock();
        }

    }

    private class GreenTrain extends Train implements Runnable {

        @Override
        public void run() {
            for (int lap = 1; lap <= 7; lap++) {
                move();
                move();

                cross(this);

                for (int i = 0; i < 11; i++) {
                    move();
                }

                cross(this);

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

                cross(this);

                for(int i = 0; i < 3; i++){
                    move();
                }

                cross(this);

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
        new Level4Crossing().execute("");
    }
}
