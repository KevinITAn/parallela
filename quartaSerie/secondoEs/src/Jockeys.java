import java.util.concurrent.ThreadLocalRandom;

public class Jockeys implements Runnable{



    private final int num;

    public Jockeys(int num) {
        this.num = num;
    }

    @Override
    public void run() {
        ThreadLocalRandom random= ThreadLocalRandom.current();
        int msSleep=random.nextInt(501)+1000;

        try {
            Thread.sleep(msSleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Jockey"+num+": reached starting line");

        PalioSiena.lock.lock();
        try {
            PalioSiena.numJockeysArrive++;
            // Se questo è l'ultimo thread, risveglia tutti
            if (PalioSiena.numJockeysArrive == PalioSiena.NUM_JOCKEYS) {
                PalioSiena.condition.signalAll();
            }
        } finally {
            PalioSiena.lock.unlock();
        }


        //finish but it's wait until last thread
        PalioSiena.lock.lock();
        try {
            while(PalioSiena.NUM_JOCKEYS!=PalioSiena.numJockeysArrive){
                try {
                    long startTime = System.nanoTime();
                    PalioSiena.condition.await();
                    long elapsedTime = System.nanoTime() - startTime; // Tempo trascorso in attesa
                    System.out.println("Jockey"+num+": waited"+ (elapsedTime / 1_000_000) + " ms");
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }
        } finally {
            PalioSiena.lock.unlock();
        }

    }
}
