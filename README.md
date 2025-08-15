# ConversorDeMonedas

Este proyecto es una aplicación de consola en Java que permite convertir entre diferentes monedas utilizando tasas de cambio en tiempo real obtenidas desde la API pública de ExchangeRate.

## Estructura
- `src/main/java/conversor/ApiService.java`: Conexión y obtención de tasas desde la API.
- `src/main/java/conversor/Conversor.java`: Lógica de conversión de monedas.
- `src/main/java/conversor/Main.java`: Menú y flujo principal del programa.

## Requisitos
- Java 11 o superior
- Gson (para procesar JSON)

## Uso
1. Ejecuta la clase `Main`.
2. Selecciona la conversión deseada.
3. Ingresa la cantidad a convertir.
4. El resultado se mostrará en consola.

## Dependencias
Agrega la librería Gson a tu proyecto. Si usas Maven, añade esto a tu `pom.xml`:

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

Si usas otro sistema, descarga el JAR desde [https://github.com/google/gson](https://github.com/google/gson).

## Nota
Reemplaza `TU_API_KEY` en `ApiService.java` por tu clave real de ExchangeRate API.
