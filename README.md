# OOP Java Learning

**English** | [Español](README.es.md)

A learning project that explains and practices the **core concepts of Object-Oriented Programming (OOP)** in Java.

## Purpose

Each OOP concept gets its own package with:

- A **README** (in English and Spanish) explaining what the concept is, why it matters, how **not** to apply it and how to apply it correctly.
- A **small, focused example class** that puts the concept into practice.
- **Unit tests** that verify the class behaves as expected, including the cases where it must reject invalid input.

The goal isn't just to show syntax, but to understand **why** each concept exists and what problems it solves.

## Concepts

| # | Concept | Package | Status              |
|---|---|-|---------------------|
| a | Encapsulation | [`a_encapsulation`](src/main/java/io/github/sebasjc19/a_encapsulation/README.md) | ✅ Available         |
| b | Inheritance | [`b_inheritance`](src/main/java/io/github/sebasjc19/b_inheritance/README.md) | ✅ Available |
| c | Polymorphism | — | 🕒 Planned          |
| d | Abstraction | — | 🕒 Planned          |

The letter prefix (`a_`, `b_`, ...) shows the suggested reading order.

## Project structure

```
src/
├── main/java/io/github/sebasjc19/
│   └── a_encapsulation/        # Example + README for each concept
└── test/java/io/github/sebasjc19/
    └── a_encapsulation/        # Unit tests for each concept
```

## Requirements

- **Java 25**
- **Maven 3.9+**

## Running the tests

```bash
mvn test
```

## Tech stack

- Java 25
- Maven
- JUnit 6
