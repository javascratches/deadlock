package pl.javascratches.deadlock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private static AtomicInteger idGenerator = new AtomicInteger(1);
    private final int id;
    private double balance;
    private final Lock lock = new ReentrantLock();

    public Account(double openingBalance) {
        this.balance = openingBalance;
        this.id = idGenerator.getAndDecrement();
    }

    public boolean withdraw(final int amount) {
        lock.lock();
        try {
            if (this.balance >= amount) {
                this.balance = this.balance - amount;
                return true;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    public void deposit(final int amount) {
        lock.lock();
        try {
            this.balance = this.balance + amount;
        } finally {
            lock.unlock();
        }
    }

    public double getBalance() {
        lock.lock();
        try {
            return this.balance;
        } finally {
            lock.unlock();
        }
    }

    public int getId() {
        return id;
    }
}
