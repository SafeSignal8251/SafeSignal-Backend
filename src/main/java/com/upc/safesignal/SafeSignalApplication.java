package com.upc.safesignal;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "SafeSignal API",
                version = "1.0",
                description = "API REST de SafeSignal, plataforma de seguridad ciudadana: cuentas, alertas SOS, seguimiento en tiempo real, "
                        + "rutas seguras, mapa de riesgo, reportes comunitarios, dispositivos IoT y estadísticas. "
                        + "Para probar los endpoints protegidos: 1) ejecuta POST /api/authenticate con tu correo y contraseña, "
                        + "2) copia el valor de jwt de la respuesta, 3) pulsa Authorize, pega el token y confirma. "
                        + "Cada endpoint indica en su descripción qué valida y qué devuelve, y los cuerpos de ejemplo se pueden ejecutar tal cual."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class SafeSignalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafeSignalApplication.class, args);
    }

}