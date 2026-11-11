# Encapsulación

[English](README.md) | **Español**

## ¿Qué es?

La **encapsulación** es uno de los pilares de la Programación Orientada a Objetos. Consiste en:

1. **Ocultar el estado interno** de un objeto (sus atributos), haciéndolo inaccesible desde fuera.
2. **Exponer solo operaciones controladas** (métodos) que permiten interactuar con ese estado de forma segura.

La idea clave: **el objeto es responsable de mantener su propio estado válido**. Nadie desde fuera debería poder dejarlo en un estado inconsistente.

En Java se logra principalmente con los modificadores de acceso (`private`, `protected`, `public`, package-private) y con métodos que validan antes de modificar.

## ¿Por qué es importante?

- **Protege las invariantes**: reglas que siempre deben cumplirse (por ejemplo, "el saldo nunca es negativo").
- **Centraliza la validación**: las reglas viven en un solo lugar, no repetidas (u olvidadas) en cada parte del código que usa la clase.
- **Permite cambiar la implementación** sin romper a quien usa la clase: mientras la interfaz pública sea la misma, el interior puede cambiar libremente.
- **Facilita el razonamiento y la depuración**: si el estado solo cambia a través de unos pocos métodos, es fácil saber dónde buscar cuando algo sale mal.
- **Hace el código más fácil de probar**: cada regla se puede verificar con un test sobre un método concreto.

## Cómo NO hacerlo

```java
public class BankAccount {
    public String owner;
    public double balance;
}
```

```java
BankAccount account = new BankAccount();
account.owner = "";              // ¿Una cuenta sin dueño?
account.balance = -500;          // ¿Saldo negativo?
account.balance -= 1_000_000;    // ¿Retiro sin verificar fondos?
account = new BankAccount();     // owner y balance quedan en null / 0
```

Problemas:

- **Atributos `public`**: cualquiera puede leer y modificar el estado sin control.
- **Sin validación**: el objeto puede quedar en estados inválidos (sin dueño, saldo negativo).
- **Reglas dispersas**: cada lugar que retire dinero tendría que acordarse de verificar el saldo. Basta con que uno lo olvide.
- **`double` para dinero**: produce errores de redondeo (`0.1 + 0.2 != 0.3`).

Otra trampa común es creer que basta con poner `private` y generar *getters y setters* para todo:

```java
public class BankAccount {
    private double balance;

    public void setBalance(double balance) {
        this.balance = balance;  // Sigue aceptando cualquier valor
    }
}
```

Esto **no es encapsulación real**: el atributo es privado, pero el setter deja la puerta igual de abierta. Encapsular no es "ocultar el campo", es **controlar cómo cambia**.

## Cómo SÍ hacerlo

La clase [`BankAccount`](BankAccount.java) de este paquete aplica la encapsulación correctamente:

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

Qué hace bien:

| Decisión | Por qué |
|---|---|
| Atributos `private` | Nadie fuera de la clase puede modificar `owner` ni `balance` directamente. |
| `owner` es `final` | El dueño se asigna una vez y nunca cambia. |
| Validación en el constructor | Es **imposible** crear una cuenta inválida: el objeto nace en un estado correcto. |
| No hay `setBalance` | El saldo solo cambia a través de operaciones con significado de negocio: `deposit` y `withdraw`. |
| `withdraw` verifica fondos | La regla "no retirar más de lo que hay" vive en un solo lugar y no se puede saltar. |
| `validateAmount` es `private` | Es un detalle interno; reutiliza la validación sin exponerla. |
| Getters de solo lectura | Se puede consultar el estado sin poder alterarlo. Como `BigDecimal` y `String` son inmutables, devolverlos directamente es seguro. |
| `BigDecimal` en vez de `double` | Precisión exacta para valores monetarios. |

Uso:

```java
BankAccount account = new BankAccount("Sebas", new BigDecimal("100"));
account.deposit(new BigDecimal("50"));    // balance = 150
account.withdraw(new BigDecimal("30"));   // balance = 120

account.withdraw(new BigDecimal("500"));  // RuntimeException: Insufficient balance
account.deposit(new BigDecimal("-10"));   // RuntimeException: Amount must be greater than zero
new BankAccount("", BigDecimal.TEN);      // RuntimeException: The owner is needed
// account.balance = ...                  // No compila: balance es private
```
