# Herencia

[English](README.md) | **Español**

## ¿Qué es?

La **herencia** es uno de los pilares de la Programación Orientada a Objetos. Permite que una clase (**subclase** o clase hija) se construya a partir de otra (**superclase** o clase padre):

1. **Hereda** sus atributos y comportamiento, sin tener que reescribirlos.
2. **Extiende o especializa** ese comportamiento: agrega lo propio o redefine (*sobrescribe*) lo que necesita cambiar.

La idea clave: **la herencia modela una relación "es un"**. Un `Car` *es un* `Vehicle`; un `Truck` *es un* `Vehicle`. Si la frase "X es un Y" no tiene sentido, la herencia probablemente no es la herramienta correcta.

![Example](https://jenkov.com/images/java/inheritance-1.png)

En Java se logra con:

- `extends`: declara la superclase (Java solo permite heredar de **una** clase).
- `super(...)`: llama al constructor del padre; debe ser la primera instrucción del constructor hijo.
- `super.metodo()`: reutiliza la implementación del padre desde la subclase.
- `@Override`: indica que un método sobrescribe al del padre y hace que el compilador lo verifique.
- `protected`: hace un miembro visible para las subclases sin exponerlo públicamente.

## ¿Por qué es importante?

- **Reutiliza código**: lo común (nombre, salario base, validaciones) se escribe una sola vez en la superclase.
- **Evita duplicación**: una corrección en la clase padre beneficia automáticamente a todas las hijas.
- **Modela jerarquías reales**: refleja cómo se relacionan los conceptos del dominio.
- **Habilita el polimorfismo**: se puede trabajar con un `Employee` sin saber si en realidad es un `Developer` o un `Manager`, y cada uno responde a su manera.
- **Facilita la extensión**: agregar un nuevo tipo de empleado no requiere tocar el código existente, solo crear una nueva subclase.

## Cómo NO hacerlo

**1. Sin herencia: copiar y pegar**

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
    private String name;          // Repetido
    private BigDecimal baseSalary; // Repetido
    private BigDecimal fixedBonus;

    public BigDecimal calculatePay() { ... }
    public String describe() { ... } // Repetido
}
```

Problemas:

- **Código duplicado**: `name`, `baseSalary`, `describe()` y las validaciones existen en cada clase.
- **Cambios en varios lugares**: si cambia la regla para validar un nombre, hay que acordarse de cambiarla en todas. Basta con que una se olvide.
- **Sin un tipo común**: no se puede tener una `List<Employee>` y calcular la nómina de todos por igual.

**2. Herencia mal usada**

```java
// Manager NO "es un" Developer: solo se quería reutilizar código
public class Manager extends Developer { ... }

// Un empleado NO "es una" lista
public class Team extends ArrayList<Employee> { ... }
```

```java
public class Employee {
    protected BigDecimal baseSalary; // Atributo expuesto a las subclases
}

public class Developer extends Employee {
    public Developer(...) {
        this.baseSalary = new BigDecimal("-500"); // Se salta la validación del padre
    }

    @Override
    public BigDecimal calculatePay() {
        return overtimeFee.multiply(...); // Ignora el salario base del padre
    }
}
```

Problemas:

- **Romper la relación "es un"**: heredar solo para reutilizar métodos crea jerarquías sin sentido. `Manager` heredaría `overtime` y `overtimeFee`, que no le corresponden. Para reutilizar sin "es un", se usa **composición** (tener un objeto como atributo).
- **Atributos `protected`**: la subclase puede modificar el estado del padre directamente y saltarse sus validaciones. La herencia no debe romper la encapsulación.
- **Sobrescribir sin respetar el contrato**: si `calculatePay()` del hijo ignora la lógica del padre, el comportamiento deja de ser coherente con lo que se espera de un `Employee`.
- **Jerarquías profundas**: `A extends B extends C extends D...` vuelven el código frágil; un cambio arriba afecta a todo lo de abajo.

## Cómo SÍ hacerlo

Las clases de este paquete aplican la herencia correctamente.

**Superclase: [`Employee`](Employee.java)**

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

**Subclases: [`Developer`](Developer.java) y [`Manager`](Manager.java)**

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

Qué hace bien:

| Decisión | Por qué |
|---|---|
| Relación "es un" real | Un `Developer` y un `Manager` **son** empleados; la jerarquía tiene sentido en el dominio. |
| Lo común vive en `Employee` | `name`, `baseSalary`, `describe()` y la validación se escriben una sola vez. |
| Atributos `private` en el padre | Las subclases no pueden tocar `name` ni `baseSalary` directamente: la encapsulación se mantiene. |
| `super(name, baseSalary)` | El hijo delega la construcción al padre, así que **siempre** pasa por sus validaciones. |
| Cada subclase agrega solo lo suyo | `Developer` añade horas extra; `Manager`, un bono. Ninguno hereda lo que no le corresponde. |
| `@Override` en `calculatePay()` | El compilador verifica que realmente se está sobrescribiendo un método del padre. |
| `super.calculatePay()` | El hijo **extiende** el cálculo del padre en lugar de reemplazarlo: el salario base siempre se incluye. |
| `validateNoNegative` es `protected static` | Las subclases reutilizan la validación sin que quede expuesta públicamente. |
| `describe()` usa `calculatePay()` | Al llamar `describe()` sobre un `Developer`, se usa **su** versión de `calculatePay()`. Un solo método sirve para todos los tipos. |
| Jerarquía de un solo nivel | Simple y fácil de entender; nada de cadenas largas de herencia. |

Uso:

```java
Employee employee  = new Employee("Ana", new BigDecimal("850"));
Employee developer = new Developer("Sebas", new BigDecimal("1500"), 10, new BigDecimal("40"));
Employee manager   = new Manager("Luis", new BigDecimal("1000"), new BigDecimal("250"));

employee.calculatePay();   // 850
developer.calculatePay();  // 1500 + (10 × 40) = 1900
manager.calculatePay();    // 1000 + 250 = 1250

developer.describe();      // "Name: Sebas\nPay: 1900"  -> describe() viene de Employee

List<Employee> staff = List.of(employee, developer, manager);
staff.forEach(e -> System.out.println(e.calculatePay())); // Cada uno calcula a su manera

new Developer("Sebas", new BigDecimal("1500"), -5, BigDecimal.TEN);  // RuntimeException: Overtime can't be negative
new Manager("", new BigDecimal("1000"), BigDecimal.TEN);             // RuntimeException: A valid name is needed (validación del padre)
new Manager("Luis", new BigDecimal("1000"), new BigDecimal("-100")); // RuntimeException: bono negativo
```
