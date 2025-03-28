import java.util.concurrent.ThreadLocalRandom;

public class MonteCarloWorker implements Runnable{
    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){

            // performed by compute threads
            ThreadLocalRandom random= ThreadLocalRandom.current();
            double x=random.nextDouble(2);//0-1(2 escluso)
            double y= random.nextDouble(2);

            //increment cntPoint
            MonteCarloMain.cntPoint.incrementAndGet();

            //checkAndIncrement
            if(x*x + y*y <= 1)
                MonteCarloMain.cntInside.incrementAndGet();

        }
    }
}
