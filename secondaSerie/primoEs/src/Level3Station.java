import org.isin.railwayquest.client.RailwayQuest;
import org.isin.railwayquest.client.Train;

import java.util.concurrent.locks.ReentrantLock;

public class Level3Station extends RailwayQuest {
    ReentrantLock lock=new ReentrantLock();
    private class GreenTrain extends Train implements Runnable {
        @Override
        public void run() {
            for (int lap = 1; lap <= 10; lap++) {
                move();
                lock.lock();
                    try{
                    move(); // Move to junction
                    enterStation(); // Move from junction to station
                    passengerExchange();
                    leaveStation(); // Move from station to junction
                    move(); // Move from junction to track
                    // Move back to spawn point
                }finally {
                    lock.unlock();
                }
                move();
                move();
                move();
                move();
                move();
                move();
                move();
                move();
                move();
            }
        }
    }

    private class RedTrain extends Train implements Runnable {
        @Override
        public void run() {
            for (int lap = 1; lap <= 10; lap++) {
                move();
                lock.lock();
                try{
                    move(); // Move to junction
                    enterStation(); // Move from junction to station
                    passengerExchange();
                    leaveStation(); // Move from station to junction
                    move(); // Move from junction to track
                    // Move back to spawn point
                }finally {
                    lock.unlock();
                }
                move();
                move();
                move();
                move();
                move();
                move();
                move();
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
        new Level3Station().execute("LEVL-T9Z8-HJAI-R1UX-DY9J");
    }
}
