# Entrega: Taller No. 4 Jakarta EE, arquitecturas por capas en servidores separados

| Entregable del tablero | Dónde está |
|---|---|
| Escenario 1: JSF en Tomcat → Lógica en WildFly → Datos en WildFly → BD | Código en la raíz del proyecto, perfil `escenario1` del `docker-compose.yml` |
| Escenario 2: Presentación → Servicios → Lógica → Datos | Perfil `escenario2` |
| Escenario 3: REST (por API Gateway) + SOAP + web sobre los mismos Servicios → Lógica | Perfil `escenario3` |
| Login sencillo | `login.xhtml` + `AutenticacionBean` (usuarios en `db/init.sql`) |
| Diseño de la arquitectura | `arquitectura.md` y `diagramas/` |
| Evidencias de funcionamiento | `capturas/`, `evidencias/` y `postman/` |

| Carpeta / archivo | Contenido |
|---|---|
| `tablero.webp` | Foto del enunciado (tablero de clase) |
| `arquitectura.md` | Documento de diseño: vistas lógica, de procesos, física e implementación, escenarios (+1), atributos de calidad, contratos y decisiones |
| `diagramas/` | Diagramas de la arquitectura: fuente PlantUML (`.puml`) e imagen (`.png`) |
| `postman/` | Colecciones de los escenarios 2 y 3, con aserciones del código HTTP, del header `X-Gateway` y de los SOAP Fault |
| `capturas/` | Capturas de la web JSF de cada escenario (`e1-…`, `e2-…`, `e3-…`) |
| `evidencias/` | Salidas reales: `mvnw clean package`, pruebas web, curl REST y SOAP, Newman, logs y estado de los contenedores |

## Diagramas

| Archivo | Vista |
|---|---|
| `01-vista-logica-escenario1` | Vista lógica (4 capas), escenario 1 |
| `02-vista-logica-escenario2` | Vista lógica, escenario 2 |
| `03-vista-logica-escenario3` | Vista lógica, escenario 3 |
| `04-vista-fisica-escenario1` | Vista física (contenedores, puertos y protocolos), escenario 1 |
| `05-vista-fisica-escenario2` | Vista física, escenario 2 |
| `06-vista-fisica-escenario3` | Vista física, escenario 3 |
| `07-vista-implementacion` | Vista de implementación (módulos Maven y paquetes Java) |

Para regenerar los PNG: `java -jar plantuml.jar -charset UTF-8 -tpng diagramas/*.puml`. No hace falta
Graphviz, porque los `.puml` usan el motor interno (`!pragma layout smetana`).

## Capturas (iguales para los tres escenarios)

| N.º | Prueba | Resultado esperado |
|---|---|---|
| 01 | Entrar a `/app/productos.xhtml` sin sesión | Redirige a `login.xhtml` |
| 02 | Login `admin / mala` | "Usuario o clave incorrectos" |
| 03 | Login `admin / admin123` | Lista de productos, "Hola, Administrador" |
| 04 | Crear producto válido | "Producto creado" y aparece en la tabla |
| 05 | Crear con nombre vacío | "El nombre es obligatorio" |
| 06 | Crear con precio −5 | "El precio debe ser mayor que cero" y no se agrega fila |
| 07 | Crear con precio "abc" | "El precio debe ser un número" |
| 08 | Salir | Vuelve a `login.xhtml` |
| 09 | Entrar a `/app/productos.xhtml` después de salir | Redirige a `login.xhtml` |
| 10 | Recargar la lista con el contenedor de Lógica apagado | Mensaje de error en el recuadro y la página no se cae |

En `e3-04` la tabla muestra productos creados por las tres puertas: "Impresora" por REST a través del
gateway, "Cámara web" por SOAP y "Parlante E3" por la web. "Impresora" y "Cámara web" salen dos veces
porque los crean tanto las pruebas con curl como la colección de Postman.

## Evidencias

| Archivo | Qué demuestra |
|---|---|
| `maven-build.txt` | `mvnw.cmd clean package`: BUILD SUCCESS de los 7 módulos |
| `escenarioN-web.txt` | Las 9 pruebas web de cada escenario, con [OK] o [FALLA] |
| `escenarioN-capa-caida.txt` | El id de sesión cambia al hacer login; con la Lógica apagada, la web muestra el error. En 2 y 3, además, la API responde 503 con JSON |
| `escenario2-rest.txt` | Peticiones de la colección con curl: 200, 401, 200, 201, 400, más nombre de 150 caracteres (400) y login sin cuerpo (400) |
| `escenario3-rest-gateway.txt` | Lo mismo, pero por el gateway en `:8000`, con el header `X-Gateway` en cada respuesta. Incluye una ruta no expuesta (404) y la prueba del límite de peticiones |
| `escenario3-soap.txt` | WSDL con las 3 operaciones y `soap:address` en `localhost:8081`; login, login con clave mala (Fault), listar, crear, y Faults con precio 0, NaN e INF |
| `escenario3-misma-logica.txt` | Lo creado por SOAP y por la web se ve por REST; lo creado por REST y por la web se ve por SOAP; tabla de PostgreSQL |
| `escenario2-newman.txt`, `escenario3-newman.txt` | Colecciones de Postman ejecutadas con Newman: 5/5 y 20/20 aserciones |
| `escenarioN-logs.txt`, `escenarioN-docker-ps.txt` | Logs completos y estado de los contenedores (sin líneas ERROR ni SEVERE) |

## Reproducir las pruebas

```
mvnw.cmd clean package
docker compose --profile escenario2 up --build
```

Luego, en Postman: Import → `postman/taller4-escenario2.postman_collection.json` → Run collection. Para el
escenario 3 se usa su colección, después de bajar el 2 con `docker compose --profile escenario2 down -v`.
