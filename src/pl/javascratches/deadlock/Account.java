package pl.javascratches.deadlock;

public class Account {
    private static int nextId = 0;

    private final int accountId;
    private double balance;

    public Account(double openingBalance) {
        this.balance = openingBalance;
        this.accountId = getAndIncrementId();
    }

    private synchronized int getAndIncrementId() {
        nextId = nextId + 1;
        return nextId;
    }

    public synchronized void deposit(final int amount) {
        this.balance = this.balance + amount;
    }

    public synchronized double getBalance() {
        return this.balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public boolean transferTo(Account other, int amount) {
        if (this.accountId == other.getAccountId()) {
            return false;
        }

        if (this.accountId < other.accountId) {
            synchronized (this) {
                if (balance >= amount) {
                    balance = balance - amount;
                    synchronized (other) {
                        other.deposit(amount);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            synchronized (other) {
                synchronized (this) {
                    if (this.balance >= amount) {
                        this.balance = this.balance - amount;
                        other.deposit(amount);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
    }
}
