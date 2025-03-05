public class TestVolatile extends Thread {
    private boolean keepRunning = true;
    @Override
    public void run() {
        long count = 0;
        while (keepRunning) {
            count++;
        }
        System.out.println("Thread terminated." + count);
    }
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Program started.");
        TestVolatile t = new TestVolatile();
        t.start();
        Thread.sleep(1000);
        t.keepRunning = false;
        System.out.println("keepRunning set to false.");
    }
}