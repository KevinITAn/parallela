import org.isin.railwayquest.client.RailwayQuest;
import org.isin.railwayquest.client.Train;

public class Main extends RailwayQuest {
    @Override
    protected void execute(String token) {
        // Start the attempt using the token to identify
        startAttempt(token);

        // Create a train instance
        Train redTrain = new Train();
        Train greenTrain=new Train();

        // Register and assign train
        register(redTrain, Train.Color.Red);
        register(greenTrain,Train.Color.Green);

        //create thread

        Thread redThread=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10;i++)
                    for(int j=0;j<14;j++)
                        redTrain.move();
            }
        });

        Thread greeThread=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10;i++)
                    for(int j=0;j<12;j++)
                        greenTrain.move();
            }
        });

        // scheduling of thread
        redThread.start();
        greeThread.start();

        //mainThread wait for 2 also thread

        try {
            greeThread.join();
            redThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Terminate the attempt
        terminateAttempt();
    }

    public static void main(String[] args) {
        new Main().execute("LEVL-Q8D3-9TXB-5XXD-5D0M");
    }
}