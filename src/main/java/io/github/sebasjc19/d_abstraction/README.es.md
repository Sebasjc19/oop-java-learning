# Abstracción

[English](README.md) | **Español**

## ¿Qué es?

La **abstracción** es uno de los pilares de la Programación Orientada a Objetos. Consiste en **exponer qué hace un objeto y ocultar cómo lo hace**, quedándose solo con los detalles que le importan a quien lo usa.

La idea clave: **el tipo base define el contrato (el "qué") y cada subclase decide la implementación (el "cómo")**. Quien llama a `shipment.getCost()` no sabe si el envío es estándar, exprés o internacional, ni qué fórmula se usa; solo sabe que todo envío tiene un costo.

En Java la abstracción se expresa con dos herramientas:

- **Clases `abstract`**: no se pueden instanciar. Pueden tener estado (atributos), constructores, métodos concretos y **métodos abstractos** (declarados sin cuerpo) que las subclases están obligadas a implementar.
- **`interface`**: definen un contrato sin estado. Una clase puede implementar varias.

| | Clase `abstract` | `interface` |
|---|---|---|
| Se puede instanciar | No | No |
| Atributos / estado | Sí | Solo constantes |
| Constructores | Sí | No |
| Métodos con cuerpo | Sí | Solo `default` / `static` |
| Cuántas puede usar una clase | Una (`extends`) | Varias (`implements`) |
| Uso típico | Familia de clases relacionadas que comparten estado y lógica | Capacidad que pueden tener clases sin relación entre sí |

Este paquete usa clases `abstract`, porque todos los envíos comparten estado (`trackingCode`, `weightKG`), validaciones y parte de la lógica.

Conceptos que intervienen:

- **Método abstracto**: `protected abstract BigDecimal calculateCost();` no tiene cuerpo. Cada subclase concreta **debe** implementarlo o el código no compila.
- **Clase abstracta intermedia**: `DomesticShipment` sigue siendo abstracta; agrupa lo común a los envíos nacionales sin ser un envío completo.
- **Método plantilla (*template method*)**: un método `final` de la clase base (`getCost`) define los pasos fijos de un algoritmo y delega el paso variable (`calculateCost`) en las subclases.

## ¿Por qué es importante?

- **Reduce la complejidad**: el código cliente trabaja con `Shipment` y solo ve `getCost()` y `summary()`, no las fórmulas de cada tipo.
- **El compilador obliga a cumplir el contrato**: si una subclase nueva olvida implementar `calculateCost`, no compila. El error aparece pronto, no en producción.
- **Impide objetos sin sentido**: un "envío genérico" no existe, así que no se permite `new Shipment(...)`.
- **Evita duplicación**: validaciones, redondeo y resumen viven una sola vez en la clase base.
- **Protege invariantes**: los métodos `final` garantizan que todo envío redondea y formatea su costo igual, sea cual sea la subclase.
- **Facilita la extensión**: agregar `SameDayShipment` solo requiere una subclase nueva que implemente `calculateCost`.

## Cómo NO hacerlo

**1. Una clase base instanciable que falla en ejecución**

```java
public class Shipment {
    public BigDecimal calculateCost() {
        throw new RuntimeException("Not implemented");
    }
}

new Shipment().calculateCost(); // Compila, pero falla al ejecutar
```

Problemas:

- **El error llega tarde**: el compilador permite crear un `Shipment` y llamar a `calculateCost`, pero el programa falla en ejecución.
- **Nada obliga a las subclases**: una subclase puede olvidar sobrescribir `calculateCost` y aun así compilar.
- **La clase miente**: anuncia un comportamiento que no tiene. Si el método no tiene una implementación por defecto con sentido, debe ser `abstract`.

**2. Una sola clase que conoce todos los tipos**

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

Problemas:

- **Expone todos los detalles en un solo lugar**: la clase crece con cada tipo y mezcla reglas que no tienen nada que ver entre sí.
- **Fórmulas duplicadas**: la tarifa nacional está escrita dos veces; cambiarla obliga a acordarse de todas las copias.
- **Atributos que solo tienen sentido para algunos tipos**: ¿dónde va `countryCode`? Quedaría en `null` para los envíos nacionales.
- **Tipos como `String`**: un error de escritura (`"EXPRES"`) compila sin problema.

**3. Dejar que las subclases rompan la lógica común**

```java
public abstract class Shipment {
    public BigDecimal getCost() { // No es final
        return calculateCost().setScale(2, RoundingMode.HALF_UP);
    }
}

public class ExpressShipment extends Shipment {
    @Override
    public BigDecimal getCost() {
        return calculateCost(); // Se salta el redondeo: "70.0000" en vez de "70.00"
    }
}
```

Problemas:

- **Se pierde el invariante**: la clase base prometía que todo costo tendría 2 decimales, pero cualquier subclase puede sobrescribirlo.
- **Comportamiento inconsistente**: dos envíos de distinto tipo formatean su costo de forma diferente.

## Cómo SÍ hacerlo

Las clases de este paquete aplican la abstracción correctamente. La jerarquía es:

```
Shipment (abstract)
├── DomesticShipment (abstract)
│   ├── StandardShipment
│   └── ExpressShipment
└── InternationalShipment
```

**Clase base abstracta: [`Shipment`](Shipment.java)**

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

**Clase abstracta intermedia: [`DomesticShipment`](DomesticShipment.java)**

```java
public abstract class DomesticShipment extends Shipment {
    private static final BigDecimal BASE_FEE = new BigDecimal("5");
    private static final BigDecimal FEE_PER_KG = new BigDecimal("3");

    protected BigDecimal domesticRate() {
        return BASE_FEE.add(FEE_PER_KG.multiply(getWeightKG()));
    }
}
```

**Subclases concretas**

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

[`InternationalShipment`](InternationalShipment.java) extiende directamente de `Shipment`: agrega un `countryCode` (validado como dos letras mayúsculas, por ejemplo `"US"`) y calcula `20 + 8 × kg`.

| Tipo | Fórmula del costo |
|---|---|
| `StandardShipment` | `5 + 3 × kg` |
| `ExpressShipment` | `(5 + 3 × kg) × 2`, más `10` si pesa más de 20 kg |
| `InternationalShipment` | `20 + 8 × kg` |

Qué hace bien:

| Decisión | Por qué |
|---|---|
| `Shipment` es `abstract` | Un envío genérico no existe; `new Shipment(...)` no compila. |
| `calculateCost` es `abstract` | Obliga a cada subclase concreta a implementarlo; olvidarlo es un error de compilación. |
| `calculateCost` es `protected` | Es un detalle de implementación; el código cliente usa `getCost()`. |
| `getCost` y `summary` son `final` | El redondeo y el formato son iguales para todos los envíos y ninguna subclase puede cambiarlos (método plantilla). |
| Validaciones en el constructor de `Shipment` | Se escriben una sola vez; ningún envío puede existir con código vacío o peso ≤ 0. |
| Constructor `protected` | Solo las subclases pueden llamarlo; deja claro que la clase existe para ser extendida. |
| `DomesticShipment` también es `abstract` | Agrupa la tarifa nacional sin ser un envío completo; evita duplicar la fórmula en `StandardShipment` y `ExpressShipment`. |
| `countryCode` solo en `InternationalShipment` | Cada clase tiene solo los datos que tienen sentido para ella; no hay atributos en `null` en los envíos nacionales. |
| Constantes `private static final` | Las tarifas tienen nombre y viven en la clase a la que pertenecen; no hay números mágicos. |
| `BigDecimal` para dinero y peso | Evita los errores de redondeo de `double`. |

Uso:

```java
// Shipment s = new Shipment("X", BigDecimal.ONE);        // No compila: Shipment es abstracta
// Shipment d = new DomesticShipment("X", BigDecimal.ONE); // Tampoco compila

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
shipments.forEach(s -> System.out.println(s.summary())); // El código cliente solo conoce Shipment
```

Los datos inválidos se rechazan al construir el objeto:

```java
new StandardShipment("", new BigDecimal("10"));                 // "Tracking code is needed"
new StandardShipment("CO-001", BigDecimal.ZERO);                // "Weight must be greater than 0"
new InternationalShipment("US-001", BigDecimal.ONE, "usa");     // "Country code not valid"
```

> La abstracción y el polimorfismo van de la mano: la abstracción **define** el contrato (`calculateCost`) y el polimorfismo **elige** en ejecución qué implementación se ejecuta. Ver [`c_polymorphism`](../c_polymorphism/README.es.md).
