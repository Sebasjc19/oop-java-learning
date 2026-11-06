package io.github.sebasjc19.a_encapsulation;

public class Example_a {

    static void main() {

        AccountBank account1 = new AccountBank("Sebasjc19", 10000);
        System.out.println("Account owner: " + account1.owner + "\n" +
                "balance: " + account1.balance);

        /*
            Case #1
         */
        try {
            account1.withdraw(20000);
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
        }

        /*
            Case #2
         */
        try {
            account1.deposit(-100);
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
        }
    }

    static class AccountBank {
        private String owner;
        private double balance;

        public AccountBank(String owner, double balance) {
            if (owner == null | owner.isBlank()) {
                throw new IllegalArgumentException("The owner is needed");
            }
            if (balance < 0) {
                throw new IllegalArgumentException("Balance can not be negative");
            }
            this.owner = owner;
            this.balance = balance;
        }

        public void deposit(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount can not be negative");
            }
            this.balance += amount;
        }

        public void withdraw(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount can not be negative");
            }
            if (balance < amount) {
                throw new IllegalArgumentException("Insufficient balance");
            }
            this.balance -= amount;
        }

        public String getOwner() {
            return this.owner;
        }

        public double getBalance() {
            return this.balance;
        }
    }
}

