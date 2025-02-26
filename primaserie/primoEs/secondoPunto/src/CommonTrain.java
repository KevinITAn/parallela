import org.isin.railwayquest.client.Train;

public class CommonTrain extends Train implements Runnable{

    Train rscTrain;

    public CommonTrain(Train rscTrain) {
        if(rscTrain==null){
            System.err.println("ERRORE!Treno non passato correttamente!");
         return;
        }
        this.rscTrain = rscTrain;
    }

    @Override
    public void run() {
        for(int i=0;i<10;i++)
            for(int j=0;j<16;j++)
                rscTrain.move();
    }
}
