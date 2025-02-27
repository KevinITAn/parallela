import org.isin.railwayquest.client.Train;

public class CommonTrain extends Train implements Runnable{

    @Override
    public void run() {
        for(int i=0;i<10;i++)
            for(int j=0;j<16;j++)
                this.move();
    }

}
