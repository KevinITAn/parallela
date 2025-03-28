import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PalioSiena {

    static final int NUM_JOCKEYS=10;
    static int numJockeysArrive=0;

     static final Lock lock=new ReentrantLock();
     static Condition condition=lock.newCondition();

    public static void main(String[] args) {
        //create all jockeys
        List<Thread> threadList=new ArrayList<>();

        for(int i=0;i<NUM_JOCKEYS;i++)
            threadList.add(new Thread(new Jockeys(i)));

        //start thread
        for(Thread tmp:threadList)
            tmp.start();

        //wait until all thread finish
        for(Thread tmpThread : threadList){
            try {
                tmpThread.join();
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }



    }
}
