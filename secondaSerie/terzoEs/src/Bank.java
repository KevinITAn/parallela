import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class Bank {
    private static double withdrawMoney=0;

    public static void main(String[] args) {
        List<Thread> threadList=new ArrayList<>();
        //create 5 users
        for(int i=0;i<5;i++){
            threadList.add(new Thread(new User()));
        }
        //run user action
        for(Thread tmp:threadList)
            tmp.run();
        //wait all user
        for(Thread tmp:threadList){
            try {
                tmp.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("All user finish!");

    }

    //race problem
    //explicit locks

    public static void registerWithdrawMoney(double amount){
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try{
            withdrawMoney+=amount;
        }finally {
            lock.unlock();
        }

    }


    //synchronized block
    /*
    public static void registerWithdrawMoney(double amount){
        synchronized (Bank.class){
            withdrawMoney+=amount;
        }

    }

    //synchronized methods
    /*
    public synchronized static void registerWithdrawMoney(double amount){
            withdrawMoney+=amount;
    }
*/

}
