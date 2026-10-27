# Taller No. 4 — Arquitecturas por capas en servidores separados

Los tres escenarios del tablero en **un solo proyecto**: un `pom.xml` y un `docker-compose.yml` con un
**perfil por escenario**. El dominio es mínimo a propósito: **login contra la BD** y una lista de
**productos** (listar + crear).

| Escenario | Qué muestra | Contenedores (perfil) |
|---|---|---|
| `escenario1` | Cada capa en su propio servidor: **JSF en Tomcat → Lógica en WildFly → Datos en WildFly → Postgres** | postgres, datos, logica, presentacion-ejb |
| `escenario2` | Aparece la capa de **Servicios (REST)** entre la presentación y la lógica | postgres, datos, logica, servicios, presentacion-rest |
| `escenario3` | **Varios clientes** sobre los mismos Servicios: REST por un **API Gateway**, **SOAP** y la web JSF | lo del 2 + gateway (nginx) |

Usuarios de prueba (están en `db/init.sql`): **admin / admin123** y **estudiante / javeriana**.

La documentación de entrega (diagramas, capturas, evidencias y Postman) está en [`docs/entrega/`](docs/entrega/LEEME.md).

---

## Cómo correr un escenario (Windows)

Requisitos: Java 21 y Docker Desktop. Maven no hace falta (el proyecto trae `mvnw`).

```bat
mvnw.cmd clean package
docker compose --profile escenario1 up --build
```

Abrir http://localhost:8080/presentacion (los WildFly tardan unos 30 s en arrancar).

Para pasar a otro escenario, primero apaga el actual **con el mismo perfil** (todos usan el puerto 8080):

```bat
docker compose --profile escenario1 down -v
```

- Sin `--profile`, `docker compose down` deja vivos los contenedores que tienen perfil (la presentación,
  servicios y el gateway). Si no recuerdas cuál subiste: `docker compose --profile "*" down -v`.
- `-v` borra también la BD, así el siguiente escenario arranca con los datos iniciales.

### Puertos

| | Escenario 1 | Escenario 2 | Escenario 3 |
|---|---|---|---|
| Web JSF | `localhost:8080/presentacion` | igual | igual |
| REST | — | `localhost:8081/servicios/api/...` | `localhost:8000/api/...` (por el gateway) |
| SOAP | — | — | `localhost:8081/servicios/ProductosSoap?wsdl` |

Las colecciones de Postman están en `docs/entrega/postman/`. Los sobres SOAP de ejemplo están en `soap/`.

---

## Estructura

```
taller4-jee/
├── pom.xml              pom raíz: construye todos los módulos
├── docker-compose.yml   un servicio por servidor; perfiles escenario1 / escenario2 / escenario3
├── contratos/           jar con las interfaces remotas, los DTO y las excepciones (va dentro de todos los .war)
├── datos/               WildFly: entidades JPA + EJB @Remote de acceso a datos + datasource de Postgres
├── logica/              WildFly: EJB @Remote con las reglas (login, validar producto)
├── servicios/           WildFly: REST (JAX-RS) y SOAP (JAX-WS). Solo traduce HTTP ↔ llamada a la Lógica  [esc. 2 y 3]
├── presentacion-ejb/    Tomcat: JSF que llama a la Lógica por EJB remoto                                  [esc. 1]
├── presentacion-rest/   Tomcat: JSF que llama a Servicios por REST                                         [esc. 2 y 3]
├── gateway/             nginx como API Gateway                                                             [esc. 3]
├── soap/                sobres SOAP de ejemplo
├── db/init.sql          crea las tablas y los datos iniciales
└── docs/entrega/        documentación de entrega
```

Las dos presentaciones generan `presentacion.war`, por eso la URL es la misma en los tres escenarios.

## Cómo se comunican las capas

**Escenario 1**

```
Navegador ──HTTP──▶ Tomcat (JSF) ──EJB remoto──▶ WildFly (Lógica) ──EJB remoto──▶ WildFly (Datos) ──JDBC──▶ Postgres
```

- Tomcat es **solo contenedor web**: no trae JSF, CDI ni EJB. Por eso `presentacion.war` lleva adentro
  Mojarra (JSF), Weld (CDI) y el cliente EJB de WildFly (mira `presentacion-ejb/pom.xml`).
- Las llamadas remotas se hacen con un **Service Locator** (`LocalizadorLogica`, `LocalizadorDatos`). Arma
  un `InitialContext` que apunta al otro servidor y busca el EJB por `ejb:/<war>/<Bean>!<interfaz>`.
- Para aceptar llamadas remotas, cada WildFly que expone EJB tiene un usuario de aplicación
  (`ejbuser / ejbclave123`), que se crea en su `Dockerfile`.
- Las entidades JPA **no salen** del servidor de Datos: se copian a DTO (`ProductoDTO`, `UsuarioDatos`).
- El login compara el **SHA-256** de la clave con el que está en la BD (`Cifrado.sha256` en Lógica).
  `UsuarioDatos` (con hash) solo viaja de Datos a Lógica; a la Presentación le llega `UsuarioDTO` (sin hash).

**Escenario 2**

```
Navegador ──▶ Tomcat (JSF) ──REST/JSON──▶ WildFly (Servicios) ──EJB remoto──▶ Lógica ──▶ Datos ──▶ Postgres
```

- La Presentación ya **no conoce los EJB**: `ClienteServicios` usa el cliente JAX-RS (Jersey) contra la API.
- Servicios no tiene reglas de negocio. Convierte la respuesta o la excepción de la Lógica en códigos HTTP:
  `200`, `201`, `400` (dato inválido), `401` (login malo) y `503` (la Lógica no responde, ver
  `LogicaNoDisponibleMapper`).

**Escenario 3**

```
Cliente REST ──▶ nginx (API Gateway :8000) ─┐
Cliente SOAP ───────────────────────────────┼──▶ WildFly (Servicios REST + SOAP) ──▶ Lógica ──▶ Datos ──▶ Postgres
Navegador ──▶ Tomcat (JSF) ─────────────────┘
```

- `ProductosSoapService` (`@WebService`) expone **las mismas operaciones** que los recursos REST. WildFly
  genera el WSDL solo.
- La idea central es **una sola Lógica con varias puertas de entrada**: un producto creado por SOAP aparece
  en la web y en el REST.
- El gateway (`gateway/nginx.conf`) es la entrada para los clientes REST. Enruta `/api/*` hacia Servicios,
  limita las peticiones por IP (10 por segundo, ráfaga de 20) y marca las respuestas con el header
  `X-Gateway`. Los clientes no saben dónde vive Servicios.
- El puerto 8081 de Servicios queda publicado para los clientes SOAP; por ahí también se puede llegar al REST
  sin pasar por el gateway.

---

## Simplificaciones conscientes

- Las claves se guardan como SHA-256 sin sal (en un sistema real: bcrypt o PBKDF2 con sal).
- La API REST/SOAP no pide token: el login solo protege la web (`FiltroSesion`).
- El usuario `ejbuser / ejbclave123` está fijo en los Dockerfile y en el compose.
- JSF corre en modo `Development` para ver los errores mientras se prueba.

## Si algo falla

- **La web dice "No se pudo conectar con la capa de…"**: esa capa todavía está arrancando o está apagada.
  Espera unos segundos y recarga. Revisa con `docker compose --profile escenario1 logs -f logica` (o `datos`,
  `servicios`).
- **Apagué la Lógica y la primera petición se demora**: el cliente EJB de WildFly tarda entre 1 y 2 minutos
  en dar por perdida la conexión con un servidor que se apagó; las peticiones siguientes fallan al instante.
  En los escenarios 2 y 3 la web no espera más de 15 s (tiempo máximo del cliente REST).
- **Cambié `init.sql` y no se nota**: Postgres solo lo ejecuta la primera vez. Usa `down -v` y vuelve a subir.
- **Cambié código y no se nota**: vuelve a correr `mvnw.cmd clean package` antes de `docker compose ... up --build`.
