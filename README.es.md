# OOP Java Learning

[English](README.md) | **Español**

Proyecto de aprendizaje para explicar y practicar los **conceptos básicos de la Programación Orientada a Objetos (POO)** en Java.

## Finalidad

Cada concepto de POO tiene su propio paquete con:

- Un **README** (en español e inglés) que explica qué es el concepto, por qué es importante, cómo **no** aplicarlo y cómo aplicarlo correctamente.
- Una **clase de ejemplo** pequeña y enfocada que lo pone en práctica.
- **Pruebas unitarias** que verifican que la clase se comporta como se espera, incluidos los casos en que debe rechazar datos inválidos.

El objetivo no es solo mostrar la sintaxis, sino entender **por qué** existe cada concepto y qué problemas resuelve.

## Conceptos

| # | Concepto | Paquete                                                                             | Estado       |
|---|---|-------------------------------------------------------------------------------------|--------------|
| a | Encapsulación | [`a_encapsulation`](src/main/java/io/github/sebasjc19/a_encapsulation/README.es.md) | ✅ Disponible |
| b | Herencia | [`b_inheritance`](src/main/java/io/github/sebasjc19/b_inheritance/README.es.md)     | ✅ Disponible |
| c | Polimorfismo | —                                                                                   | 🕒 Planeado  |
| d | Abstracción | —                                                                                   | 🕒 Planeado  |

El prefijo de letra (`a_`, `b_`, ...) indica el orden de lectura sugerido.

## Estructura del proyecto

```
src/
├── main/java/io/github/sebasjc19/
│   └── a_encapsulation/        # Ejemplo + README de cada concepto
└── test/java/io/github/sebasjc19/
    └── a_encapsulation/        # Pruebas unitarias de cada concepto
```

## Requisitos

- **Java 25**
- **Maven 3.9+**

## Ejecutar las pruebas

```bash
mvn test
```

## Tecnologías

- Java 25
- Maven
- JUnit 6
