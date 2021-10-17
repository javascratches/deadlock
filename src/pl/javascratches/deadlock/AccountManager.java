package pl.javascratches.deadlock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AccountManager {
    private ConcurrentHashMap<Integer, Account> accounts = new ConcurrentHashMap<>();
    private volatile boolean shutdown = false;
    private BlockingQueue<TransferTask> pending = new LinkedBlockingQueue<>();
    private BlockingQueue<TransferTask> forDeposit = new LinkedBlockingQueue<>();
    private BlockingQueue<TransferTask> failed = new LinkedBlockingQueue<>();
    private Thread withdrawals;
    private Thread deposits;

    public Account createAccount(int balance) {
        var account = new Account(balance);
        accounts.put(account.getId(), account);
        return account;
    }

    public void submit(TransferTask transfer) {
        try {
            pending.put(transfer);
        } catch (InterruptedException e) {
            System.err.println("Pending queue error");
            try {
                failed.put(transfer);
            } catch (InterruptedException ex) {
                System.err.println("Failed queue error");
            }
        }
    }

    public void init() {
        Runnable withdraw = () -> {
            while (!shutdown) {
                try {
                    var task = pending.poll(1, TimeUnit.SECONDS);
                    if (task != null) {
                        var sender = task.sender();
                        if (sender.withdraw(task.amount())) {
                            forDeposit.put(task);
                        } else {
                            failed.put(task);
                        }
                    }
                } catch (InterruptedException e) {
                    System.err.println("Withdraw error");
                }
            }
        };

        Runnable deposit = () -> {
            while (!shutdown) {
                TransferTask task;
                try {
                    task = forDeposit.poll(1, TimeUnit.SECONDS);
                    if (task != null) {
                        var receiver = task.receiver();
                        receiver.deposit(task.amount());
                    }
                } catch (InterruptedException e) {
                    System.err.println("Deposit error");
                }
            }

        };

        init(withdraw, deposit);
    }

    private void init(Runnable withdraw, Runnable deposit) {
        withdrawals = new Thread(withdraw);
        deposits = new Thread(deposit);
        withdrawals.start();
        deposits.start();
    }

    public void await() {
        try {
            withdrawals.join();
            deposits.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void shutdown() {
        shutdown = true;
    }

}
