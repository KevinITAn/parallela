
public class Fibonacci {
    public static void createFibonacci() {
        long fibo1 = 1, fibo2 = 1;
        for (int i = 3; i <= 90; i++) {
            /* Compute fibonacci number */
            long fibonacci = fibo1 + fibo2;

            /* Print result  */
            System.out.printf("Main : fibonacci(%d)=%d %n", i, fibonacci);

            /* Update state for next fibonacci number */
            fibo1 = fibo2;
            fibo2 = fibonacci;
        }
    }
}
