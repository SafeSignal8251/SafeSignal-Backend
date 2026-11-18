SafeSignal – Backend

SafeSignal es una plataforma web de seguridad ciudadana que permite reportar incidentes, visualizar zonas de riesgo en un mapa, gestionar contactos de confianza y dispositivos IoT, activar alertas SOS y consultar estadísticas. Incluye un panel para administradores.

Este repositorio contiene el frontend en Angular. El backend (Spring Boot) está en SafeSignal-Backend.

Tecnologías:
* Angular 22 y TypeScript
* Angular Material (interfaz) y formularios reactivos
* Leaflet (mapas)
* Guards de ruta e interceptor HTTP para el token JWT
* Vitest (pruebas)

Requisitos:
* Node.js 22 o superior y npm
* El backend en ejecución (por defecto en http://localhost:8080)
* La aplicación queda en http://localhost:4200.

La URL del backend se configura en src/environments/environment.ts (desarrollo) y src/environments/environment.prod.ts (producción), en la propiedad apiURL.

Estructura
src/app/
  componentes/
    publico/      inicio, login, registro, recuperar contraseña, términos
    cliente/      dashboard, mapa de riesgo, reportes, contactos, dispositivos, notificaciones, perfil
    admin/        dashboard, usuarios, moderación, umbrales de riesgo, estadísticas, comunicación
    compartido/   diálogos, menús de navegación y mapa
  core/           guard de autenticación, interceptor, mensajes y utilidades
  model/          interfaces de datos
  services/       servicios HTTP hacia la API

Roles
* CLIENTE: usuario que reporta incidentes, usa el mapa y las alertas.
* ADMIN: gestiona usuarios, modera reportes y administra umbrales y estadísticas.
