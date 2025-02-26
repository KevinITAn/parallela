public class VersionClass implements Runnable {
    @Override
    public void run() {
        Fibonacci.createFibonacci();
    }
}

class versionClassMain{
    public static void main(String[] args) {

        System.out.println("Class version");

        //create threads

        Thread[] classThread=new Thread[5];

        for(int i=0;i<5;i++){
            classThread[i]=new Thread(new VersionClass());
        }

        //startThreads

        for(Thread nowThread:classThread)
            nowThread.start();

        //wait thread

        for (Thread nowThread : classThread) {
            try {
                nowThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All thread are finisched");

    }
}
