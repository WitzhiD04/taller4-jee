# Entrega: Taller No. 4 Jakarta EE, arquitectura por capas en servidores separados

| Entregable del tablero | Dónde está |
|---|---|
| Presentación JSF en Tomcat | Módulo `presentacion` (contenedor `presentacion`, puerto 8080) |
| Capa de Servicios con REST y SOAP | Módulo `servicios` (WildFly, puerto 8081) |
| Clientes REST a través de un API Gateway | `gateway/nginx.conf` (nginx, puerto 8000) |
| Lógica y Datos en servidores WildFly separados, con BD | Módulos `logica` y `datos`, más `db/init.sql` (PostgreSQL) |
| Login sencillo | `login.xhtml` + `AutenticacionBean` (usuarios en `db/init.sql`) |
| Diseño de la arquitectura | `arquitectura.md` y `diagramas/` |
| Evidencias de funcionamiento | `capturas/`, `evidencias/` y `postman/` |

| Carpeta / archivo | Contenido |
|---|---|
| `tablero.webp` | Foto del enunciado (tablero de clase) |
| `arquitectura.md` | Documento de diseño: vistas lógica, de procesos, física e implementación, escenarios (+1), atributos de calidad, contratos y decisiones |
| `diagramas/` | Diagramas de la arquitectura: fuente PlantUML (`.puml`) e imagen (`.png`) |
| `postman/` | Colección con REST por el gateway y SOAP, con aserciones del código HTTP, del header `X-Gateway` y de los SOAP Fault |
| `capturas/` | Capturas de la web JSF |
| `evidencias/` | Salidas reales: `mvnw clean package`, pruebas web, curl REST y SOAP, Newman, logs y estado de los contenedores |

## Diagramas

| Archivo | Vista |
|---|---|
| `01-vista-logica` | Vista lógica (4 capas: presentación, acceso, dominio e infraestructura) |
| `02-vista-fisica` | Vista física (contenedores, puertos y protocolos) |
| `03-vista-implementacion` | Vista de implementación (módulos Maven y paquetes Java) |

Para regenerar los PNG: `java -jar plantuml.jar -charset UTF-8 -tpng diagramas/*.puml`. No hace falta
Graphviz, porque los `.puml` usan el motor interno (`!pragma layout smetana`).

## Capturas de la web

| N.º | Prueba | Resultado esperado |
|---|---|---|
| 01 | Entrar a `/app/productos.xhtml` sin sesión | Redirige a `login.xhtml` |
| 02 | Login `admin / mala` | "Usuario o clave incorrectos" |
| 03 | Login `admin / admin123` | Lista de productos, "Hola, Administrador" |
| 04 | Crear producto válido | "Producto creado"; en la tabla también salen los productos creados antes por REST y por SOAP |
| 05 | Crear con nombre vacío | "El nombre es obligatorio" |
| 06 | Crear con precio −5 | "El precio debe ser mayor que cero" y no se agrega fila |
| 07 | Crear con precio "abc" | "El precio debe ser un número" |
| 08 | Salir | Vuelve a `login.xhtml` |
| 09 | Entrar a `/app/productos.xhtml` después de salir | Redirige a `login.xhtml` |
| 10 | Recargar la lista con el contenedor de Lógica apagado | Mensaje de error en el recuadro y la página no se cae |

En la captura 04, "Impresora" (creado por REST a través del gateway) y "Cámara web" (creado por SOAP) salen
dos veces, porque los crean tanto las pruebas con curl como la colección de Postman.

## Evidencias

| Archivo | Qué demuestra |
|---|---|
| `maven-build.txt` | `mvnw.cmd clean package`: BUILD SUCCESS del pom raíz y sus 5 módulos |
| `docker-ps.txt` | Los 6 contenedores arriba y los puertos publicados |
| `rest-gateway.txt` | REST por el gateway en `:8000`, con el header `X-Gateway` en cada respuesta: 200, 401, 200, 201, 400, nombre de 150 caracteres (400), login sin cuerpo (400) y ruta no expuesta (404). Incluye la prueba del límite de peticiones del gateway |
| `soap.txt` | WSDL con las 3 operaciones y `soap:address` en `localhost:8081`; login, login con clave mala (Fault), listar, crear, y Faults con precio 0, NaN e INF |
| `web.txt` | Las 9 pruebas de la web, con [OK] o [FALLA] |
| `misma-logica.txt` | Lo creado por SOAP y por la web se ve por REST; lo creado por REST y por la web se ve por SOAP; tabla de PostgreSQL |
| `capa-caida.txt` | El id de sesión cambia al hacer login; con la Lógica apagada, la web muestra el error y la API responde 503 con JSON |
| `newman.txt` | Colección de Postman ejecutada con Newman |
| `logs.txt` | Logs completos de los 6 contenedores. La única línea ERROR es del arranque: el script preguntó por la API mientras Datos aún cargaba, la Lógica falló y Servicios respondió 503 hasta que todo estuvo listo |

## Reproducir las pruebas

```
mvnw.cmd clean package
docker compose up --build
```

Luego, en Postman: Import → `postman/taller4.postman_collection.json` → Run collection.
