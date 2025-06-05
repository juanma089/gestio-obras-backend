### 1. Crear el archivo de configuración

Necesitas crear o editar el archivo `src/main/resources/application.properties` con la configuración de MySQL:

### 2. Configuración específica de la base de datos

Las propiedades principales que debes configurar son:

- **`spring.datasource.url`**: URL de conexión a MySQL (formato: `jdbc:mysql://localhost:3306/nombre_base_datos`)
- **`spring.datasource.username`**: Usuario de MySQL
- **`spring.datasource.password`**: Contraseña del usuario
- **`spring.jpa.hibernate.ddl-auto=update`**: Permite que Hibernate actualice automáticamente el esquema de la base de datos

### 3. Preparar la base de datos

1. **Instalar MySQL** (si no lo tienes)
2. **Crear la base de datos**:
```sql
CREATE DATABASE my_database;
```
3. **Crear usuario** (opcional):
```sql
CREATE USER 'tu_usuario'@'localhost' IDENTIFIED BY 'tu_contraseña';
GRANT ALL PRIVILEGES ON my_database.* TO 'tu_usuario'@'localhost';
```

### 5. Verificar la configuración

Una vez configurado, ejecuta:
```bash
./mvnw spring-boot:run
```

Si la configuración es correcta, Hibernate creará automáticamente las tablas necesarias gracias a `ddl-auto=update`.

**Notes**

El proyecto utiliza Spring Data JPA con Hibernate para la persistencia. La configuración CORS está establecida para desarrollo local.

### Configuración del Stack Tecnológico

La aplicación utiliza Spring Boot 3.4.4 con las siguientes dependencias principales:

- **Spring Boot Starter Web** para REST API
- **Spring Boot Starter Data JPA** para persistencia
- **MySQL Connector/J** para conectividad con MySQL
- **Spring Boot Starter Security** para autenticación JWT
- **SpringDoc OpenAPI** para documentación Swagger
- **Spring Boot Starter WebSocket** para comunicación en tiempo real

### Configuración de Seguridad

El sistema implementa autenticación JWT con filtros de seguridad personalizados:

Los endpoints públicos incluyen Swagger UI y WebSocket, mientras que el resto requiere autenticación.

### Estructura de Base de Datos

El sistema utiliza JPA con Hibernate para generar automáticamente las tablas. La entidad `Inventory` es un ejemplo de la estructura:

Las tablas se crean automáticamente con `spring.jpa.hibernate.ddl-auto=update`, incluyendo restricciones de unicidad y relaciones entre entidades.

## Notes

La aplicación está configurada para despliegue local, y incluye configuración CORS para desarrollo local en el puerto 5173. El sistema implementa un modelo de roles (ADMINISTRADOR, SUPERVISOR, OPERADOR) con control de acceso basado en anotaciones `@PreAuthorize`.