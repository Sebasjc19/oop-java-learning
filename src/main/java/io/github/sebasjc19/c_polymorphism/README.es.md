# Polimorfismo

[English](README.md) | **Español**

## ¿Qué es?

El **polimorfismo** ("muchas formas") es uno de los pilares de la Programación Orientada a Objetos. Permite que **un mismo mensaje** (una llamada a un método) produzca **comportamientos distintos** según el **tipo real** del objeto que lo recibe.

La idea clave: **el código cliente trabaja con el tipo padre y no necesita saber qué subclase tiene delante**. Quien llama a `payment.process(amount)` no sabe si está pagando con tarjeta o con PayPal; cada objeto responde a su manera.

En Java hay dos formas de polimorfismo:

- **En tiempo de compilación (sobrecarga / *overloading*)**: varios métodos con el mismo nombre y distintos parámetros. El compilador elige cuál usar según los argumentos.
- **En tiempo de ejecución (sobrescritura / *overriding*)**: una subclase redefine un método del padre y la JVM decide **al ejecutar** qué versión llamar. Es el polimorfismo "de verdad" en POO y el que se trabaja en este paquete.

Conceptos que intervienen:

- **Upcasting**: asignar un objeto de una subclase a una referencia del tipo padre (`Payment payment = new CardPayment();`). Es automático y siempre seguro.
- **Despacho dinámico (*dynamic dispatch*)**: al llamar a un método sobrescrito, se ejecuta la versión del **objeto real**, no la del tipo de la variable.
- **Downcasting**: convertir una referencia del tipo padre de vuelta al tipo hijo (`(CardPayment) payment`) para acceder a métodos propios de la subclase. Es explícito y puede fallar.
- **`instanceof`**: comprueba el tipo real de un objeto antes de hacer un downcast.

> Este paquete usa herencia de clases concretas. Las clases `abstract` y las `interface` también permiten polimorfismo, pero se tratan en el tema de **abstracción**.

## ¿Por qué es importante?

- **Código cliente genérico**: un solo método (`checkout`) funciona con cualquier tipo de pago, presente o futuro.
- **Elimina condicionales por tipo**: no hacen falta cadenas de `if/else` ni `switch` preguntando "¿qué tipo eres?".
- **Facilita la extensión**: agregar `CryptoPayment` solo requiere crear una nueva subclase; `checkout` no se toca (principio *abierto/cerrado*).
- **Cada clase es responsable de su comportamiento**: la lógica de pagar con tarjeta vive en `CardPayment`, no repartida por el código que la usa.
- **Colecciones heterogéneas**: una `List<Payment>` puede contener pagos de cualquier tipo y tratarlos a todos por igual.

## Cómo NO hacerlo

**1. Preguntar por el tipo en lugar de delegar**

```java
public static String checkout(Object payment, BigDecimal total) {
    if (payment instanceof CardPayment) {
        return "Card: " + total;
    } else if (payment instanceof PaypalPayment) {
        return "Paypal: " + total;
    } else if (payment instanceof CryptoPayment) { // Hay que acordarse de agregar cada tipo nuevo
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

Problemas:

- **Cada tipo nuevo obliga a modificar código existente**: `checkout` (o el `switch`) crece con cada forma de pago y se repite en todos los lugares que necesiten distinguir tipos.
- **Fácil de olvidar un caso**: si se agrega un tipo y no se actualiza una de las cadenas de `if`, el error aparece en ejecución.
- **La lógica no está donde debería**: el comportamiento de cada pago queda fuera de su clase.
- **Tipos como `String`**: un error de escritura (`"CRAD"`) compila sin problema.

**2. Una clase base que solo lanza excepciones**

```java
public class Payment {
    public String process(BigDecimal amount) {
        throw new RuntimeException("Method not valid");
    }
}

new Payment().process(BigDecimal.TEN); // Compila, pero falla al ejecutar
```

Problemas:

- **El error llega tarde**: el compilador permite crear un `Payment` y llamar a `process`, pero el programa falla en ejecución.
- **La clase base miente**: anuncia un comportamiento que no tiene. Si no existe un "pago genérico" con sentido, la herramienta correcta es una clase `abstract` o una `interface` (se verá en el tema de abstracción).

**3. Downcasting sin comprobar**

```java
public static String refund(Payment payment, BigDecimal amount) {
    CardPayment card = (CardPayment) payment; // ¿Y si es un PaypalPayment?
    return card.refundToCard(amount);
}

refund(new PaypalPayment(), BigDecimal.TEN); // ClassCastException
```

Problemas:

- **`ClassCastException` en ejecución**: el compilador acepta el cast, pero falla si el objeto real no es del tipo esperado.
- **Señal de diseño**: si el código necesita castear a menudo, probablemente ese comportamiento debería estar en la clase base y resolverse por polimorfismo.

## Cómo SÍ hacerlo

Las clases de este paquete aplican el polimorfismo correctamente.

**Clase base: [`Payment`](Payment.java)**

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

**Subclase: [`CardPayment`](CardPayment.java)**

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

[`PaypalPayment`](PaypalPayment.java) sigue el mismo patrón: sobrescribe `process` y devuelve `"Paypal: " + amount`.

Qué hace bien:

| Decisión | Por qué |
|---|---|
| `checkout` recibe un `Payment` | Depende solo del tipo padre; funciona con cualquier subclase sin conocerla. |
| `checkout` no tiene ningún `if` | No pregunta por el tipo: delega en `process` y el despacho dinámico elige la versión correcta. |
| Cada subclase sobrescribe `process` | El comportamiento de cada forma de pago vive en su propia clase. |
| `@Override` en `process` | El compilador verifica que realmente se está sobrescribiendo el método del padre. |
| `Payment` tiene un comportamiento por defecto válido | Se puede usar por sí misma sin fallar; las subclases lo especializan. |
| `refundToCard` solo existe en `CardPayment` | Lo que es específico de una subclase no contamina a la clase base; para usarlo hace falta un downcast explícito. |
| `instanceof` con *pattern matching* | El downcast solo ocurre después de comprobar el tipo, así que nunca lanza `ClassCastException`. |
| Agregar un pago nuevo no toca código existente | Basta con crear otra subclase; `checkout` sigue igual. |

Uso:

```java
Payment generic = new Payment();
Payment card    = new CardPayment();   // Upcasting: automático y seguro
Payment paypal  = new PaypalPayment();

generic.process(new BigDecimal("500"));   // "Generic payment: 500"
card.process(new BigDecimal("3100"));     // "Card: 3100"   -> misma llamada, distinto resultado
paypal.process(new BigDecimal("5000"));   // "Paypal: 5000"

Payment.checkout(paypal, new BigDecimal("3200")); // "Paypal: 3200" -> checkout no sabe qué tipo recibe

List<Payment> payments = List.of(generic, card, paypal);
payments.forEach(p -> System.out.println(p.process(BigDecimal.TEN))); // Cada uno procesa a su manera
```

**Downcasting: acceder a lo propio de la subclase**

Desde una referencia de tipo `Payment` solo se ve lo que `Payment` declara. Para llamar a `refundToCard` hay que convertir la referencia a `CardPayment`:

```java
Payment payment = new CardPayment();
// payment.refundToCard(...);  // No compila: Payment no conoce ese método

// Forma segura: comprueba el tipo y castea a la vez (Java 16+)
// Equivale a: if (payment instanceof CardPayment) { CardPayment card = (CardPayment) payment; ... }
if (payment instanceof CardPayment card) {
    card.refundToCard(new BigDecimal("3100")); // "Refund to card: 3100"
}

// Cast sin comprobar sobre el tipo equivocado
Payment other = new PaypalPayment();
CardPayment wrong = (CardPayment) other; // ClassCastException
```

El cast **no crea un objeto nuevo**: `payment` y `card` apuntan al mismo objeto. Lo único que cambia es el tipo de la referencia, es decir, qué métodos permite llamar el compilador.
