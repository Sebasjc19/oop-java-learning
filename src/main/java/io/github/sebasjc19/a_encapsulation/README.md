# Encapsulation

**English** | [Español](README.es.md)

## What is it?

**Encapsulation** is one of the pillars of Object-Oriented Programming. It means:

1. **Hiding the internal state** of an object (its fields), making it inaccessible from the outside.
2. **Exposing only controlled operations** (methods) that let others interact with that state safely.

The key idea: **an object is responsible for keeping its own state valid**. No outside code should be able to leave it in an inconsistent state.

In Java this is achieved mainly through access modifiers (`private`, `protected`, `public`, package-private) and methods that validate before modifying.

## Why does it matter?

- **Protects invariants**: rules that must always hold (e.g. "the balance is never negative").
- **Centralizes validation**: rules live in one place instead of being repeated (or forgotten) everywhere the class is used.
- **Lets you change the implementation** without breaking callers: as long as the public interface stays the same, the internals can change freely.
- **Makes reasoning and debugging easier**: if state only changes through a few methods, you know exactly where to look when something goes wrong.
- **Makes code easier to test**: each rule can be verified with a test against a specific method.

## How NOT to do it

```java
public class BankAccount {
    public String owner;
    public double balance;
}
```

```java
BankAccount account = new BankAccount();
account.owner = "";              // An account with no owner?
account.balance = -500;          // Negative balance?
account.balance -= 1_000_000;    // Withdrawal without checking funds?
account = new BankAccount();     // owner and balance left as null / 0
```

Problems:

- **`public` fields**: anyone can read and modify the state without any control.
- **No validation**: the object can end up in invalid states (no owner, negative balance).
- **Scattered rules**: every place that withdraws money must remember to check the balance. It only takes one to forget.
- **`double` for money**: causes rounding errors (`0.1 + 0.2 != 0.3`).

Another common trap is thinking it's enough to mark fields `private` and generate *getters and setters* for everything:

```java
public class BankAccount {
    private double balance;

    public void setBalance(double balance) {
        this.balance = balance;  // Still accepts any value
    }
}
```

This is **not real encapsulation**: the field is private, but the setter leaves the door just as wide open. Encapsulation isn't "hiding the field", it's **controlling how it changes**.

## How to do it right

The [`BankAccount`](BankAccount.java) class in this package applies encapsulation correctly:

```java
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

    private static void validateAmount(BigDecimal amount) { ... }

    public String getOwner() { return this.owner; }
    public BigDecimal getBalance() { return this.balance; }
}
```

What it does well:

| Decision | Why |
|---|---|
| `private` fields | Nothing outside the class can modify `owner` or `balance` directly. |
| `owner` is `final` | The owner is assigned once and never changes. |
| Validation in the constructor | It's **impossible** to create an invalid account: the object is born in a correct state. |
| No `setBalance` | The balance only changes through business-meaningful operations: `deposit` and `withdraw`. |
| `withdraw` checks funds | The "can't withdraw more than you have" rule lives in one place and can't be bypassed. |
| `validateAmount` is `private` | It's an internal detail; it reuses validation without exposing it. |
| Read-only getters | State can be inspected but not altered. Since `BigDecimal` and `String` are immutable, returning them directly is safe. |
| `BigDecimal` instead of `double` | Exact precision for monetary values. |

Usage:

```java
BankAccount account = new BankAccount("Sebas", new BigDecimal("100"));
account.deposit(new BigDecimal("50"));    // balance = 150
account.withdraw(new BigDecimal("30"));   // balance = 120

account.withdraw(new BigDecimal("500"));  // RuntimeException: Insufficient balance
account.deposit(new BigDecimal("-10"));   // RuntimeException: Amount must be greater than zero
new BankAccount("", BigDecimal.TEN);      // RuntimeException: The owner is needed
// account.balance = ...                  // Doesn't compile: balance is private
```