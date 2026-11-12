# Inheritance

**English** | [Español](README.es.md)

## What is it?

**Inheritance** is one of the pillars of Object-Oriented Programming. It lets a class (the **subclass** or child class) be built on top of another (the **superclass** or parent class):

1. It **inherits** the parent's fields and behavior, without rewriting them.
2. It **extends or specializes** that behavior: it adds its own and redefines (*overrides*) whatever needs to change.

The key idea: **inheritance models an "is-a" relationship**. A `Car` *is an* `Vehicle`; a `Truck` *is an* `Vehicle`. If the sentence "X is a Y" doesn't make sense, inheritance is probably not the right tool.

![Example](https://jenkov.com/images/java/inheritance-1.png)

In Java this is achieved with:

- `extends`: declares the superclass (Java only allows inheriting from **one** class).
- `super(...)`: calls the parent's constructor; it must be the first statement in the child's constructor.
- `super.method()`: reuses the parent's implementation from the subclass.
- `@Override`: marks a method as overriding the parent's and makes the compiler check it.
- `protected`: makes a member visible to subclasses without exposing it publicly.

## Why does it matter?

- **Reuses code**: what's shared (name, base salary, validations) is written once in the superclass.
- **Avoids duplication**: a fix in the parent class automatically benefits every child.
- **Models real hierarchies**: it reflects how concepts in the domain relate to each other.
- **Enables polymorphism**: you can work with an `Employee` without knowing whether it's actually a `Developer` or a `Manager`, and each one responds in its own way.
- **Makes extension easy**: adding a new kind of employee doesn't require touching existing code, just creating a new subclass.

## How NOT to do it

**1. No inheritance: copy and paste**

```java
public class Developer {
    private String name;
    private BigDecimal baseSalary;
    private int overtime;
    private BigDecimal overtimeFee;

    public BigDecimal calculatePay() { ... }
    public String describe() { ... }
}

public class Manager {
    private String name;           // Repeated
    private BigDecimal baseSalary; // Repeated
    private BigDecimal fixedBonus;

    public BigDecimal calculatePay() { ... }
    public String describe() { ... } // Repeated
}
```

Problems:

- **Duplicated code**: `name`, `baseSalary`, `describe()` and the validations exist in every class.
- **Changes in many places**: if the rule for validating a name changes, you have to remember to change it everywhere. It only takes one to be forgotten.
- **No common type**: you can't have a `List<Employee>` and compute everyone's payroll the same way.

**2. Misused inheritance**

```java
// A Manager is NOT a Developer: this only exists to reuse code
public class Manager extends Developer { ... }

// A team is NOT a list
public class Team extends ArrayList<Employee> { ... }
```

```java
public class Employee {
    protected BigDecimal baseSalary; // Field exposed to subclasses
}

public class Developer extends Employee {
    public Developer(...) {
        this.baseSalary = new BigDecimal("-500"); // Bypasses the parent's validation
    }

    @Override
    public BigDecimal calculatePay() {
        return overtimeFee.multiply(...); // Ignores the parent's base salary
    }
}
```

Problems:

- **Breaking the "is-a" relationship**: inheriting just to reuse methods creates meaningless hierarchies. `Manager` would inherit `overtime` and `overtimeFee`, which don't belong to it. To reuse code without an "is-a", use **composition** (holding an object as a field).
- **`protected` fields**: the subclass can modify the parent's state directly and skip its validations. Inheritance must not break encapsulation.
- **Overriding without honoring the contract**: if the child's `calculatePay()` ignores the parent's logic, the behavior no longer matches what's expected of an `Employee`.
- **Deep hierarchies**: `A extends B extends C extends D...` makes code fragile; a change at the top affects everything below.

## How to do it right

The classes in this package apply inheritance correctly.

**Superclass: [`Employee`](Employee.java)**

```java
public class Employee {

    private final String name;
    private final BigDecimal baseSalary;

    public Employee(String name, BigDecimal baseSalary) {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("A valid name is needed");
        }
        validateNoNegative(baseSalary, "Base salary");
        this.name = name;
        this.baseSalary = baseSalary;
    }

    public BigDecimal calculatePay() {
        return this.baseSalary;
    }

    public String describe() {
        return "Name: " + name + "\nPay: " + calculatePay();
    }

    protected static void validateNoNegative(BigDecimal value, String field) { ... }
}
```

**Subclasses: [`Developer`](Developer.java) and [`Manager`](Manager.java)**

```java
public class Developer extends Employee {

    private final int overtime;
    private final BigDecimal overtimeFee;

    public Developer(String name, BigDecimal baseSalary, int overtime, BigDecimal overtimeFee) {
        super(name, baseSalary);
        if (overtime < 0) {
            throw new RuntimeException("Overtime can't be negative");
        }
        validateNoNegative(overtimeFee, "Overtime fee");
        this.overtime = overtime;
        this.overtimeFee = overtimeFee;
    }

    @Override
    public BigDecimal calculatePay() {
        return super.calculatePay()
                .add(overtimeFee.multiply(BigDecimal.valueOf(overtime)));
    }
}
```

```java
public class Manager extends Employee {

    private final BigDecimal fixedBonus;

    public Manager(String name, BigDecimal baseSalary, BigDecimal fixedBonus) {
        super(name, baseSalary);
        validateNoNegative(fixedBonus, "Fixed bonus");
        this.fixedBonus = fixedBonus;
    }

    @Override
    public BigDecimal calculatePay() {
        return super.calculatePay().add(fixedBonus);
    }
}
```

What it does well:

| Decision | Why |
|---|---|
| A real "is-a" relationship | A `Developer` and a `Manager` **are** employees; the hierarchy makes sense in the domain. |
| Shared logic lives in `Employee` | `name`, `baseSalary`, `describe()` and the validation are written only once. |
| `private` fields in the parent | Subclasses can't touch `name` or `baseSalary` directly: encapsulation is preserved. |
| `super(name, baseSalary)` | The child delegates construction to the parent, so it **always** goes through its validations. |
| Each subclass adds only its own | `Developer` adds overtime; `Manager`, a bonus. Neither inherits what doesn't belong to it. |
| `@Override` on `calculatePay()` | The compiler checks that a parent method is really being overridden. |
| `super.calculatePay()` | The child **extends** the parent's calculation instead of replacing it: the base salary is always included. |
| `validateNoNegative` is `protected static` | Subclasses reuse the validation without it being publicly exposed. |
| `describe()` uses `calculatePay()` | Calling `describe()` on a `Developer` uses **its** version of `calculatePay()`. One method works for every type. |
| A single-level hierarchy | Simple and easy to follow; no long inheritance chains. |

Usage:

```java
Employee employee  = new Employee("Ana", new BigDecimal("850"));
Employee developer = new Developer("Sebas", new BigDecimal("1500"), 10, new BigDecimal("40"));
Employee manager   = new Manager("Luis", new BigDecimal("1000"), new BigDecimal("250"));

employee.calculatePay();   // 850
developer.calculatePay();  // 1500 + (10 × 40) = 1900
manager.calculatePay();    // 1000 + 250 = 1250

developer.describe();      // "Name: Sebas\nPay: 1900"  -> describe() comes from Employee

List<Employee> staff = List.of(employee, developer, manager);
staff.forEach(e -> System.out.println(e.calculatePay())); // Each one calculates in its own way

new Developer("Sebas", new BigDecimal("1500"), -5, BigDecimal.TEN);  // RuntimeException: Overtime can't be negative
new Manager("", new BigDecimal("1000"), BigDecimal.TEN);             // RuntimeException: A valid name is needed (parent's validation)
new Manager("Luis", new BigDecimal("1000"), new BigDecimal("-100")); // RuntimeException: negative bonus
```
