class Worker implements Runnable {
    public void run() {
        for (int i = 0; i < 50; i++) {
            System.out.println(Thread.currentThread().getName() + " running");
            //PART B
            Thread.yield();
        }
    }
}