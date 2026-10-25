# Sistema de Gestión de Productos — versión sencilla

Java Swing + JDBC + SQLite, sin MVC ni paquetes separados. El programa usa tres clases en `src/main/java`.

## Requisitos
- JDK 17 o superior
- Gradle instalado (comando `gradle`)
- Internet la primera vez para descargar la dependencia JDBC desde Maven Central

## Ejecutar
Abra una terminal en esta carpeta y ejecute:

```powershell
gradle run
```

La base de datos `productos.db` se crea automáticamente en la carpeta desde la que se ejecuta Gradle.

## Funciones
- Registrar producto (código único, nombre, descripción, precio y cantidad)
- Mostrar todos y buscar por código o nombre
- Actualizar descripción, precio y cantidad por código
- Eliminar por código con confirmación
- Generar diez productos aleatorios adicionales (códigos únicos)
- Persistencia SQLite al cerrar y reabrir el programa

## Clases
- `Main.java`: inicia el programa y contiene la interfaz Swing.
- `Producto.java`: datos de un producto.
- `BaseDatos.java`: conexión JDBC, creación de tabla y operaciones SQL con `PreparedStatement`.

`schema.sql` es una copia de la definición de la tabla para documentarla; el programa crea la tabla automáticamente desde `BaseDatos.inicializar()`.

## Pruebas recomendadas
1. Registrar un producto y comprobar que aparece.
2. Buscar por código y por nombre.
3. Seleccionar una fila, modificar precio y actualizar.
4. Eliminar una fila confirmando la operación.
5. Probar precio negativo y cantidad no entera.
6. Generar diez productos aleatorios y comprobar su cantidad.
7. Cerrar y volver a abrir para demostrar persistencia.

## Nota
Aunque no utiliza MVC, sí utiliza SQLite y JDBC como exige la guía original. No se necesita API REST.
