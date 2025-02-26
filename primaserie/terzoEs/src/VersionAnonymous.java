public class VersionAnonymous {
    public static void main(String[] args) {

        System.out.println("anonymous version");

        //create threads

        Thread[] anonymousThread=new Thread[5];

        for(int i=0;i<5;i++){
            anonymousThread[i]=new Thread(new Runnable() {
                @Override
                public void run() {
                    Fibonacci.createFibonacci();
                }
            });
        }

        //startThreads

        for(Thread nowThread:anonymousThread)
            nowThread.start();

        //wait thread

        for (Thread nowThread : anonymousThread) {
            try {
                nowThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All thread are finisched");

    }
}
