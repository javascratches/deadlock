package pl.javascratches.deadlock;

public class Account {

    private double balance;

    public Account(double openingBalance) {
        this.balance = openingBalance;
    }

    public synchronized boolean withdraw(final int amount) {
        if (this.balance >= amount) {
            this.balance = this.balance - amount;
            return true;
        }
        return false;
    }

    public synchronized void deposit(final int amount) {
        this.balance = this.balance + amount;
    }

    public synchronized double getBalance() {
        return this.balance;
    }

    public synchronized boolean transferTo(Account other, int amount) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException __) {
        }
        if (this.balance >= amount) {
            this.balance = this.balance - amount;
            other.deposit(amount);
            return true;
        }
        return false;
    }
}

