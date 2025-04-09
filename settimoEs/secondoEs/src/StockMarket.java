import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class StockMarket {
    private static final Map<String,Double> stocksBaseValue=new HashMap<>();
    private static final ReentrantLock lock=new ReentrantLock();



    public static void main(String[] args) {
        initMap();
        List<Thread> brokers=new ArrayList<>();
        for(int i=0;i<15;i++)
            brokers.add(new Thread(new Broker()));


        for(Thread t : brokers)
            t.start();

        //start update
        for(int i=0;i<1000;i++){
            updateValue();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //interrupt all thread
        for(Thread t : brokers)
            t.interrupt();

        System.out.println("Stocks market is closed for today!");

    }

    private static void initMap(){
        stocksBaseValue.put("LDO.MI",40.2);
        stocksBaseValue.put("MSFT",100.0);
        stocksBaseValue.put("NVDA",100.0);
        stocksBaseValue.put("GOOGL",100.0);
        stocksBaseValue.put("AMZN",100.0);
        stocksBaseValue.put("META",100.0);
        stocksBaseValue.put("WMT",100.0);
        stocksBaseValue.put("V",100.0);
        stocksBaseValue.put("MA",100.0);
        stocksBaseValue.put("JPM",100.0);

    }

    //modifica valore della mappa tra -25% e 25%
    private static void updateValue(){
        ThreadLocalRandom random=ThreadLocalRandom.current();
        lock.lock();
        try {
            stocksBaseValue.replaceAll((k, v) -> v + (v * random.nextDouble(-0.25, 0.25)));
        } finally {
            lock.unlock();
        }

    }
    //cosi non devo usare i lock e rendo impossibile a broker aggiungere o eliminare robe
    public static Map<String,Double> getStocksBaseValue() {
        lock.lock();
        try {
            return Collections.unmodifiableMap(new HashMap<>(stocksBaseValue));
        } finally {
            lock.unlock();
        }
    }

}
