package io.github.sebasjc19.a_encapsulation;

import java.math.BigDecimal;

class BankAccount {
    private final String owner;
    private BigDecimal balance;

    public BankAccount(String owner, BigDecimal balance) {
        if (owner == null || owner.isBlank()) {
            throw new RuntimeException("The owner is needed");
        }
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Balance not valid");
        }
        this.owner = owner;
        this.balance = balance;
    }

    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validateAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new RuntimeException("Amount must not be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
    }

    public String getOwner() {
        return this.owner;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }
}


