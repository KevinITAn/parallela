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
        Train blueTrain=new Train();
        Train yelllowTrain=new Train();

        // Register and assign train
        register(redTrain, Train.Color.Red);
        register(greenTrain,Train.Color.Green);
        register(blueTrain,Train.Color.Blue);
        register(yelllowTrain,Train.Color.Yellow);

        //create thread

        Thread redThread=new Thread(new CommonTrain(redTrain));
        Thread greenThread=new Thread(new CommonTrain(greenTrain));

        Thread blueThread=new Thread(() -> {
            for(int i=0;i<27;i++)
                for(int j=0;j<6;j++)
                    blueTrain.move();
        });

        Thread yellowThread=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<22;i++)
                    for(int j=0;j<8;j++)
                        yelllowTrain.move();
            }
        });

        // scheduling of thread
        redThread.start();
        greenThread.start();
        yellowThread.start();
        blueThread.start();

        //mainThread wait for 2 also thread

        try {
            greenThread.join();
            redThread.join();
            yellowThread.join();
            blueThread.join();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }

        // Terminate the attempt
        terminateAttempt();
    }

    public static void main(String[] args) {
        new Main().execute("");
    }
}