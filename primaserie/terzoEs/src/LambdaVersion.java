public class LambdaVersion {
    public static void main(String[] args) {

        System.out.println("Lambda version");

        //create threads

        Thread[] classThread=new Thread[5];

        for(int i=0;i<5;i++) {
            classThread[i] = new Thread(() -> {
                long fibo1 = 1, fibo2 = 1;
                for (int k = 3; k <= 90; k++) {
                    /* Compute fibonacci number */
                    long fibonacci = fibo1 + fibo2;

                    /* Print result  */
                    System.out.printf("Main : fibonacci(%d)=%d %n", k, fibonacci);

                    /* Update state for next fibonacci number */
                    fibo1 = fibo2;
                    fibo2 = fibonacci;
                }
                ;
            });
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
