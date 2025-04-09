import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Broker implements Runnable{

    private double capital=100000;

    @Override
    public void run() {
        ThreadLocalRandom random=ThreadLocalRandom.current();

        while(!Thread.interrupted()){
            Map<String,Double> stocks= StockMarket.getStocksBaseValue();
            List<String> keys = new ArrayList<>(stocks.keySet());
            String randomKey = keys.get(random.nextInt(keys.size()));

            if(stocks.get(randomKey)>capital){
                System.out.println("i don't have enough capital");
                continue;
            }
            capital-=stocks.get(randomKey);
            System.out.println("I buy");

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("Stock marker closing, exiting...");
                return;
            }
        }
    }
}
