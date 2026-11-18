# Abstraction

**English** | [Español](README.es.md)

## What is it?

**Abstraction** is one of the pillars of Object-Oriented Programming. It consists of **exposing what an object does and hiding how it does it**, keeping only the details that matter to whoever uses it.

The key idea: **the base type defines the contract (the "what") and each subclass decides the implementation (the "how")**. Whoever calls `shipment.getCost()` doesn't know whether the shipment is standard, express or international, nor what formula is used; they only know that every shipment has a cost.

In Java, abstraction is expressed with two tools:

- **`abstract` classes**: they can't be instantiated. They can have state (fields), constructors, concrete methods and **abstract methods** (declared without a body) that subclasses are forced to implement.
- **`interface`s**: they define a contract without state. A class can implement several of them.

| | `abstract` class | `interface` |
|---|---|---|
| Can be instantiated | No | No |
| Fields / state | Yes | Only constants |
| Constructors | Yes | No |
| Methods with a body | Yes | Only `default` / `static` |
| How many can a class use | One (`extends`) | Several (`implements`) |
| Typical use | Family of related classes sharing state and logic | Capability that unrelated classes can have |

This package uses `abstract` classes, because every shipment shares state (`trackingCode`, `weightKG`), validation and part of the logic.

Concepts involved:

- **Abstract method**: `protected abstract BigDecimal calculateCost();` has no body. Each concrete subclass **must** implement it or the code doesn't compile.
- **Intermediate abstract class**: `DomesticShipment` is still abstract; it groups what's common to domestic shipments without being a complete shipment itself.
- **Template method**: a `final` method of the base class (`getCost`) defines the fixed steps of an algorithm and delegates the variable step (`calculateCost`) to the subclasses.

## Why does it matter?

- **Reduces complexity**: client code works with `Shipment` and only sees `getCost()` and `summary()`, not the formulas of each type.
- **The compiler enforces the contract**: if a new subclass forgets to implement `calculateCost`, it doesn't compile. The error appears early, not in production.
- **Prevents meaningless objects**: a "generic shipment" doesn't exist, so `new Shipment(...)` isn't allowed.
- **Avoids duplication**: validation, rounding and the summary live once in the base class.
- **Protects invariants**: `final` methods guarantee that every shipment rounds and formats its cost the same way, no matter which subclass it is.
- **Makes extension easy**: adding `SameDayShipment` only requires a new subclass that implements `calculateCost`.

## How NOT to do it

**1. A base class that can be instantiated and fails at runtime**

```java
public class Shipment {
    public BigDecimal calculateCost() {
        throw new RuntimeException("Not implemented");
    }
}

new Shipment().calculateCost(); // Compiles, but fails at runtime
```

Problems:

- **The error arrives late**: the compiler allows creating a `Shipment` and calling `calculateCost`, but the program fails at runtime.
- **Nothing forces subclasses**: a subclass can forget to override `calculateCost` and still compile.
- **The class lies**: it advertises behavior it doesn't have. If the method has no meaningful default implementation, it should be `abstract`.

**2. A single class that knows every type**

```java
public class Shipment {
    private String type; // "STANDARD", "EXPRESS", "INTERNATIONAL"
    private BigDecimal weightKG;

    public BigDecimal getCost() {
        if (type.equals("STANDARD")) {
            return new BigDecimal("5").add(new BigDecimal("3").multiply(weightKG));
        } else if (type.equals("EXPRESS")) {
            return new BigDecimal("5").add(new BigDecimal("3").multiply(weightKG)).multiply(BigDecimal.TWO);
        } else if (type.equals("INTERNATIONAL")) {
            return new BigDecimal("20").add(new BigDecimal("8").multiply(weightKG));
        }
        throw new RuntimeException("Unknown type");
    }
}
```

Problems:

- **Exposes every detail in one place**: the class grows with each type and mixes rules that have nothing to do with each other.
- **Duplicated formulas**: the domestic rate is written twice; changing it means remembering every copy.
- **Fields that only make sense for some types**: where does `countryCode` go? It would be `null` for domestic shipments.
- **Types as `String`**: a typo (`"EXPRES"`) compiles just fine.

**3. Letting subclasses break the common logic**

```java
public abstract class Shipment {
    public BigDecimal getCost() { // Not final
        return calculateCost().setScale(2, RoundingMode.HALF_UP);
    }
}

public class ExpressShipment extends Shipment {
    @Override
    public BigDecimal getCost() {
        return calculateCost(); // Skips rounding: "70.0000" instead of "70.00"
    }
}
```

Problems:

- **The invariant gets lost**: the base class promised every cost would have 2 decimals, but any subclass can override it.
- **Inconsistent behavior**: two shipments of different types format their cost differently.

## How to do it right

The classes in this package apply abstraction correctly. The hierarchy is:

```
Shipment (abstract)
├── DomesticShipment (abstract)
│   ├── StandardShipment
│   └── ExpressShipment
└── InternationalShipment
```

**Abstract base class: [`Shipment`](Shipment.java)**

```java
public abstract class Shipment {
    private final String trackingCode;
    private final BigDecimal weightKG;

    protected Shipment(String trackingCode, BigDecimal weightKG) {
        if (trackingCode == null || trackingCode.isBlank()) {
            throw new RuntimeException("Tracking code is needed");
        }
        if (weightKG == null || weightKG.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Weight must be greater than 0");
        }
        this.trackingCode = trackingCode;
        this.weightKG = weightKG;
    }

    protected abstract BigDecimal calculateCost();

    public final BigDecimal getCost() {
        return calculateCost().setScale(2, RoundingMode.HALF_UP);
    }

    public final String summary() {
        return this.trackingCode + " | " + weightKG + " kg | " + "Cost: " + getCost();
    }
}
```

**Intermediate abstract class: [`DomesticShipment`](DomesticShipment.java)**

```java
public abstract class DomesticShipment extends Shipment {
    private static final BigDecimal BASE_FEE = new BigDecimal("5");
    private static final BigDecimal FEE_PER_KG = new BigDecimal("3");

    protected BigDecimal domesticRate() {
        return BASE_FEE.add(FEE_PER_KG.multiply(getWeightKG()));
    }
}
```

**Concrete subclasses**

```java
public class StandardShipment extends DomesticShipment {
    @Override
    protected BigDecimal calculateCost() {
        return domesticRate();
    }
}

public class ExpressShipment extends DomesticShipment {
    private static final BigDecimal HEAVY_THRESHOLD_KG = new BigDecimal("20");

    @Override
    protected BigDecimal calculateCost() {
        BigDecimal extraCharge = getWeightKG().compareTo(HEAVY_THRESHOLD_KG) > 0 ? BigDecimal.TEN : BigDecimal.ZERO;
        return domesticRate().multiply(BigDecimal.TWO).add(extraCharge);
    }
}
```

[`InternationalShipment`](InternationalShipment.java) extends `Shipment` directly: it adds a `countryCode` (validated as two uppercase letters, e.g. `"US"`) and computes `20 + 8 × kg`.

| Type | Cost formula |
|---|---|
| `StandardShipment` | `5 + 3 × kg` |
| `ExpressShipment` | `(5 + 3 × kg) × 2`, plus `10` if it weighs more than 20 kg |
| `InternationalShipment` | `20 + 8 × kg` |

What it does well:

| Decision | Why |
|---|---|
| `Shipment` is `abstract` | A generic shipment doesn't exist; `new Shipment(...)` doesn't compile. |
| `calculateCost` is `abstract` | Every concrete subclass is forced to implement it; forgetting it is a compile error. |
| `calculateCost` is `protected` | It's an implementation detail; client code uses `getCost()`. |
| `getCost` and `summary` are `final` | Rounding and format are the same for every shipment and no subclass can change them (template method). |
| Validation in the `Shipment` constructor | Written once; no shipment can exist with an empty tracking code or a weight ≤ 0. |
| `protected` constructor | Only subclasses can call it; it makes clear the class exists to be extended. |
| `DomesticShipment` is also `abstract` | It groups the domestic rate without being a complete shipment; avoids duplicating the formula in `StandardShipment` and `ExpressShipment`. |
| `countryCode` only in `InternationalShipment` | Each class has only the data that makes sense for it; no `null` fields in domestic shipments. |
| `private static final` constants | Rates have a name and live in the class they belong to; there are no magic numbers. |
| `BigDecimal` for money and weight | Avoids `double` rounding errors. |

Usage:

```java
// Shipment s = new Shipment("X", BigDecimal.ONE);        // Doesn't compile: Shipment is abstract
// Shipment d = new DomesticShipment("X", BigDecimal.ONE); // Doesn't compile either

Shipment standard      = new StandardShipment("CO-001", new BigDecimal("10"));
Shipment express       = new ExpressShipment("CO-002", new BigDecimal("10"));
Shipment heavyExpress  = new ExpressShipment("CO-003", new BigDecimal("25"));
Shipment international = new InternationalShipment("US-001", new BigDecimal("10"), "US");

standard.getCost();      // 35.00  -> 5 + 3·10
express.getCost();       // 70.00  -> (5 + 3·10) · 2
heavyExpress.getCost();  // 170.00 -> (5 + 3·25) · 2 + 10
international.getCost(); // 100.00 -> 20 + 8·10

standard.summary();      // "CO-001 | 10 kg | Cost: 35.00"

List<Shipment> shipments = List.of(standard, express, international);
shipments.forEach(s -> System.out.println(s.summary())); // Client code only knows Shipment
```

Invalid input is rejected when building the object:

```java
new StandardShipment("", new BigDecimal("10"));                 // "Tracking code is needed"
new StandardShipment("CO-001", BigDecimal.ZERO);                // "Weight must be greater than 0"
new InternationalShipment("US-001", BigDecimal.ONE, "usa");     // "Country code not valid"
```

> Abstraction and polymorphism go hand in hand: abstraction **defines** the contract (`calculateCost`) and polymorphism **chooses** at runtime which implementation runs. See [`c_polymorphism`](../c_polymorphism/README.md).
