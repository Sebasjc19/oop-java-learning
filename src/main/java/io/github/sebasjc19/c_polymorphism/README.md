# Polymorphism

**English** | [Español](README.es.md)

## What is it?

**Polymorphism** ("many forms") is one of the pillars of Object-Oriented Programming. It lets **the same message** (a method call) produce **different behavior** depending on the **actual type** of the object that receives it.

The key idea: **client code works with the parent type and doesn't need to know which subclass it's dealing with**. Whoever calls `payment.process(amount)` doesn't know whether it's paying by card or by PayPal; each object responds in its own way.

Java has two forms of polymorphism:

- **Compile-time (overloading)**: several methods with the same name and different parameters. The compiler picks which one to use based on the arguments.
- **Runtime (overriding)**: a subclass redefines a parent method and the JVM decides **at runtime** which version to call. This is the "real" OOP polymorphism and the one this package covers.

Concepts involved:

- **Upcasting**: assigning a subclass object to a parent-type reference (`Payment payment = new CardPayment();`). It's automatic and always safe.
- **Dynamic dispatch**: when an overridden method is called, the version of the **actual object** runs, not the one of the variable's type.
- **Downcasting**: converting a parent-type reference back to the child type (`(CardPayment) payment`) to reach methods specific to the subclass. It's explicit and can fail.
- **`instanceof`**: checks an object's actual type before downcasting.

> This package uses inheritance between concrete classes. `abstract` classes and `interface`s also enable polymorphism, but they're covered in the **abstraction** topic.

## Why does it matter?

- **Generic client code**: a single method (`checkout`) works with any kind of payment, present or future.
- **Removes type conditionals**: no need for `if/else` chains or `switch` statements asking "what type are you?".
- **Makes extension easy**: adding `CryptoPayment` only requires a new subclass; `checkout` stays untouched (*open/closed* principle).
- **Each class owns its behavior**: the logic for paying by card lives in `CardPayment`, not scattered across the code that uses it.
- **Heterogeneous collections**: a `List<Payment>` can hold payments of any type and treat them all the same way.

## How NOT to do it

**1. Asking for the type instead of delegating**

```java
public static String checkout(Object payment, BigDecimal total) {
    if (payment instanceof CardPayment) {
        return "Card: " + total;
    } else if (payment instanceof PaypalPayment) {
        return "Paypal: " + total;
    } else if (payment instanceof CryptoPayment) { // Every new type must be remembered here
        return "Crypto: " + total;
    }
    throw new RuntimeException("Unknown payment");
}
```

```java
public class Payment {
    private String type; // "CARD", "PAYPAL"...

    public String process(BigDecimal amount) {
        switch (type) {
            case "CARD":   return "Card: " + amount;
            case "PAYPAL": return "Paypal: " + amount;
            default:       throw new RuntimeException("Unknown type");
        }
    }
}
```

Problems:

- **Every new type forces changes to existing code**: `checkout` (or the `switch`) grows with each payment method and gets repeated wherever types need to be told apart.
- **Easy to miss a case**: if a type is added and one of the `if` chains isn't updated, the error shows up at runtime.
- **Logic in the wrong place**: each payment's behavior lives outside its own class.
- **Types as `String`**: a typo (`"CRAD"`) compiles just fine.

**2. A base class that only throws**

```java
public class Payment {
    public String process(BigDecimal amount) {
        throw new RuntimeException("Method not valid");
    }
}

new Payment().process(BigDecimal.TEN); // Compiles, but fails at runtime
```

Problems:

- **The error arrives late**: the compiler lets you create a `Payment` and call `process`, but the program fails at runtime.
- **The base class lies**: it advertises behavior it doesn't have. If there's no meaningful "generic payment", the right tool is an `abstract` class or an `interface` (covered in the abstraction topic).

**3. Downcasting without checking**

```java
public static String refund(Payment payment, BigDecimal amount) {
    CardPayment card = (CardPayment) payment; // What if it's a PaypalPayment?
    return card.refundToCard(amount);
}

refund(new PaypalPayment(), BigDecimal.TEN); // ClassCastException
```

Problems:

- **`ClassCastException` at runtime**: the compiler accepts the cast, but it fails if the actual object isn't of the expected type.
- **A design smell**: if code needs to cast often, that behavior probably belongs in the base class and should be resolved through polymorphism.

## How to do it right

The classes in this package apply polymorphism correctly.

**Base class: [`Payment`](Payment.java)**

```java
public class Payment {

    public String process(BigDecimal amount) {
        return "Generic payment: " + amount;
    }

    public static String checkout(Payment payment, BigDecimal total) {
        return payment.process(total);
    }
}
```

**Subclass: [`CardPayment`](CardPayment.java)**

```java
public class CardPayment extends Payment {

    @Override
    public String process(BigDecimal amount) {
        return "Card: " + amount;
    }

    public String refundToCard(BigDecimal amount) {
        return "Refund to card: " + amount;
    }
}
```

[`PaypalPayment`](PaypalPayment.java) follows the same pattern: it overrides `process` and returns `"Paypal: " + amount`.

What it does well:

| Decision | Why |
|---|---|
| `checkout` takes a `Payment` | It depends only on the parent type; it works with any subclass without knowing it. |
| `checkout` has no `if` | It doesn't ask for the type: it delegates to `process` and dynamic dispatch picks the right version. |
| Each subclass overrides `process` | Each payment method's behavior lives in its own class. |
| `@Override` on `process` | The compiler checks that the parent method is really being overridden. |
| `Payment` has a valid default behavior | It can be used on its own without failing; subclasses specialize it. |
| `refundToCard` exists only in `CardPayment` | What's specific to a subclass doesn't pollute the base class; using it requires an explicit downcast. |
| `instanceof` with pattern matching | The downcast only happens after checking the type, so it never throws `ClassCastException`. |
| Adding a new payment doesn't touch existing code | Just create another subclass; `checkout` stays the same. |

Usage:

```java
Payment generic = new Payment();
Payment card    = new CardPayment();   // Upcasting: automatic and safe
Payment paypal  = new PaypalPayment();

generic.process(new BigDecimal("500"));   // "Generic payment: 500"
card.process(new BigDecimal("3100"));     // "Card: 3100"   -> same call, different result
paypal.process(new BigDecimal("5000"));   // "Paypal: 5000"

Payment.checkout(paypal, new BigDecimal("3200")); // "Paypal: 3200" -> checkout doesn't know which type it gets

List<Payment> payments = List.of(generic, card, paypal);
payments.forEach(p -> System.out.println(p.process(BigDecimal.TEN))); // Each one processes in its own way
```

**Downcasting: reaching what's specific to the subclass**

From a `Payment` reference you only see what `Payment` declares. To call `refundToCard`, the reference has to be converted to `CardPayment`:

```java
Payment payment = new CardPayment();
// payment.refundToCard(...);  // Doesn't compile: Payment doesn't know that method

// Safe way: checks the type and casts at once (Java 16+)
// Same as: if (payment instanceof CardPayment) { CardPayment card = (CardPayment) payment; ... }
if (payment instanceof CardPayment card) {
    card.refundToCard(new BigDecimal("3100")); // "Refund to card: 3100"
}

// Unchecked cast on the wrong type
Payment other = new PaypalPayment();
CardPayment wrong = (CardPayment) other; // ClassCastException
```

The cast **doesn't create a new object**: `payment` and `card` point to the same object. The only thing that changes is the reference's type, that is, which methods the compiler lets you call.
