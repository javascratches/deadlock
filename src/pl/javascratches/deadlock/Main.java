package pl.javascratches.deadlock;

public class Main {
    private static final int MAX_TRANSFERS = 100;

    public static void main(String[] args) throws InterruptedException {
        Account accountA = new Account(10_000);
        Account accountB = new Account(10_000);

        Thread threadA = new Thread(() -> {
            for (int i = 0; i < MAX_TRANSFERS; i = i + 1) {
                boolean ok = accountA.transferTo(accountB, 1);
                if (!ok) {
                    System.out.println("Thread A failed at " + i);
                }
            }
        });

        Thread threadB = new Thread(() -> {
            for (int i = 0; i < MAX_TRANSFERS; i = i + 1) {
                boolean ok = accountB.transferTo(accountA, 1);
                if (!ok) {
                    System.out.println("Thread B failed at " + i);
                }
            }
        });

        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();

        System.out.println(accountA.getBalance());
        System.out.println(accountB.getBalance());
    }
}
