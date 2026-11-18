SafeSignal – Backend

API REST de SafeSignal, una plataforma de seguridad ciudadana: reporte de incidentes, zonas de riesgo, alertas SOS, contactos de confianza, dispositivos IoT y estadísticas.

El frontend (Angular) está en SafeSignal-Frontend.

Tecnologías:
* Java 21 y Spring Boot
* Spring Data JPA y PostgreSQL
* Spring Security con JWT y contraseñas con BCrypt
* Bean Validation y manejo global de errores
* Documentación OpenAPI con Swagger (springdoc)

Seguridad:
* Autenticación sin estado con JWT.
* Control de acceso por rol (CLIENTE y ADMIN) con @PreAuthorize.
* Prevención de IDOR con el bean @seguridad: cada usuario solo accede a sus propios recursos.
* CORS configurado para el origen del frontend.
* Las contraseñas nunca se devuelven en las respuestas.
  
Configuración:
Los valores de conexión están en src/main/resources/application.properties. El secreto del JWT se puede cambiar con la variable de entorno JWT_SECRET.

La API queda en http://localhost:8080 y la documentación en http://localhost:8080/swagger-ui/index.html.

Estructura
src/main/java/com/upc/safesignal/
  Entities/       entidades JPA
  Repositories/   repositorios
  Dtos/           objetos de transferencia de datos
  Services/       interfaces de servicio
  ServiceImpl/    implementaciones de la lógica de negocio
  Controllers/    endpoints REST
  Security/       JWT, filtros y configuración de seguridad
