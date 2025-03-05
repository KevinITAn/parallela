import java.util.Random;
import java.util.UUID;

public class User implements Runnable{
    private final UUID uuid = UUID.randomUUID();
    private final Random generator=new Random();
    private double balance=1000;

    @Override
    public void run() {
        double amount = generator.nextInt(46) + 5;
        int sleepTime=generator.nextInt(15)+5;

        while(amount>0){
            //sleep time
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //business logic
            if(amount>balance){
                System.out.println(uuid+": I was able to withdraw only" + balance+" instead of "+amount);
                amount=balance;
            }else
                System.out.println(uuid + ": I’ve withdrawn" + amount + "from the account. New\n" + "balance" + balance);

            balance = withdraw(amount);
            Bank.registerWithdrawMoney(amount);

        }


    }

    private double withdraw(double amount){
        return balance-amount;
    }
}
