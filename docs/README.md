# EnvíaGT

Programa de consola en Java para cotizar y registrar envíos, administrar vehículos y despachos, registrar entregas y consultar ingresos.

## Requisitos

- JDK 8 o superior.
- Todos los archivos `.java` en una misma carpeta.

Comprueba que Java y su compilador estén instalados:

```bash
java -version
javac -version
```

## Compilar

Abre una terminal dentro de la carpeta que contiene los archivos `.java` y ejecuta:

```bash
javac -encoding UTF-8 *.java
```

Esto genera los archivos `.class`.

## Ejecutar

Desde esa misma carpeta:

```bash
java Main
```

## Uso

Escribe el número de la opción del menú y presiona Enter.

Para realizar una entrega:

1. Registra un envío.
2. Registra un vehículo.
3. Crea un despacho con la placa del vehículo.
4. Asigna el envío utilizando el ID del despacho y el código del envío.
5. Da salida al despacho.
6. Registra si la entrega fue exitosa.
7. Consulta los envíos o el reporte de ingresos.

Usa punto para los decimales, por ejemplo `0.5`, y responde `s` o `n` a las preguntas de confirmación.

Para cerrar el programa, selecciona la opción `0`.

## Consideraciones

- Cotizar no registra el envío ni genera ingresos.
- Cada reintento genera un recargo de Q15 cuando sale el despacho.
- Los datos se mantienen únicamente en memoria y se pierden al cerrar el programa.
- Después de modificar el código, vuelve a compilar antes de ejecutarlo.