package pl.javascratches.deadlock;

import java.util.concurrent.TimeUnit;

public class Main {
    private static final int MAX_TRANSFERS = 10_000;

    public static void main(String[] args) throws InterruptedException {
        var manager = new AccountManager();
        manager.init();

        var accountA = manager.createAccount(10_000);
        var accountB = manager.createAccount(20_000);

        Thread threadA = new Thread(() -> {
            for (int i = 0; i < MAX_TRANSFERS; i = i + 1) {
                var transfer = new TransferTask(accountA, accountB, 1);
                manager.submit(transfer);
            }
        });

        Thread threadB = new Thread(() -> {
            for (int i = 0; i < MAX_TRANSFERS; i = i + 1) {
                var transfer = new TransferTask(accountB, accountA, 1);
                manager.submit(transfer);
            }
        });

        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();

        TimeUnit.SECONDS.sleep(3);
        manager.shutdown();
        manager.await();

        System.out.println(accountA.getBalance());
        System.out.println(accountB.getBalance());
    }
}
