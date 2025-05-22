import java.util.*;
import java.util.concurrent.*;

public class PrimeChecker {
    private static void sequential(long minValue, long maxValue) {
        long initialTime=System.currentTimeMillis();
        Set<Long> primes = new TreeSet<>();
        for (long i = minValue; i < maxValue; i++) {
            if (isPrime(i))
                primes.add(i);
        }
        System.out.println("Sequential time:"+(System.currentTimeMillis()-initialTime));
        System.out.println(primes);
    }

    private static void threads(long minValue, long maxValue) {
        long initialTime=System.currentTimeMillis();
        List<Thread> threadList=new ArrayList<>();
        Set<Long> primes = new ConcurrentSkipListSet<>();//per gestire concorrenza accesso dati
        for(long i=minValue;i<maxValue;i++){
            final long l=i;
            Thread threadNow=new Thread(()->{
                if(isPrime(l))
                    primes.add(l);
            });
            threadList.add(threadNow);
            threadNow.start();
        }

        for(Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        System.out.println("Threads: " + (System.currentTimeMillis()-initialTime));
        System.out.println(primes);
    }

    private static void executor1(long minValue, long maxValue) {
        long initialTime=System.currentTimeMillis();
        Set<Long> primes = new ConcurrentSkipListSet<>();//gestisce accessi concorrenti
        ExecutorService executor= Executors.newWorkStealingPool(7);

        for(long i=minValue;i<maxValue;i++){
            final long finalI = i;
            Runnable runnable= new Runnable() {
                @Override
                public void run() {
                        if(isPrime(finalI))
                            primes.add(finalI);
                }
            };

            executor.execute(runnable);
        }
        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.err.println("execute for 1 minutes!!");
        }

        System.out.println("Executor 1: " + (System.currentTimeMillis()-initialTime));
        System.out.println(primes);
    }

    private static void executor2(long minValue, long maxValue) {
        long initialTime = System.currentTimeMillis();
        Set<Long> primes = new TreeSet<>(); // usato solo dal main thread
        ExecutorService executor = Executors.newWorkStealingPool(7);
        CompletionService<Optional<Long>> completionService = new ExecutorCompletionService<>(executor);
        //task
        for (long i = minValue; i < maxValue; i++) {
            final long finalI = i;
            completionService.submit(() -> {
                if (isPrime(finalI))
                    return Optional.of(finalI);
                return Optional.empty();
            });
        }
        executor.shutdown();

        // Recupera i risultati non appena sono disponibili
        for (long i = minValue; i < maxValue; i++) {
            try {
                Future<Optional<Long>> future = completionService.take(); // blocca finché non c'è un risultato
                Optional<Long> result = future.get();
                result.ifPresent(primes::add);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Executor 2 : " + (System.currentTimeMillis() - initialTime));
        System.out.println(primes);
    }


    public static void main(String[] args) {
        long minValue = 1_000_000_000_000L;
        long maxValue = 1_000_000_001_000L;
        sequential(minValue, maxValue);
        threads(minValue, maxValue);
        executor1(minValue, maxValue);
        executor2(minValue, maxValue);
    }

    private static boolean isPrime(long n) {
        if (n <= 1)
            return false;
        if (n == 2)
            return true;
        if (n % 2 == 0)
            return false;
        for (long i = 3; i * i <= n; i += 2) {
            if (n % i == 0)
                return false;
        }
        return true;
    }
}
