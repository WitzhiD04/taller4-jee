# Taller No. 4 — Arquitectura por capas en servidores separados

Cada capa corre en su propio servidor y **varios tipos de cliente llegan a la misma capa de Servicios**:

```
Cliente REST ──▶ nginx (API Gateway :8000) ─┐
Cliente SOAP ───────────────────────────────┼──▶ WildFly (Servicios REST + SOAP) ──▶ WildFly (Lógica) ──▶ WildFly (Datos) ──▶ Postgres
Navegador ──▶ Tomcat (JSF :8080) ───────────┘
```

El dominio es mínimo a propósito: **login contra la BD** y una lista de **productos** (listar + crear).
Usuarios de prueba (están en `db/init.sql`): **admin / admin123** y **estudiante / javeriana**.

La documentación de entrega (diagramas, capturas, evidencias y Postman) está en [`docs/entrega/`](docs/entrega/LEEME.md).

---

## Cómo correrlo (Windows)

Requisitos: Java 21 y Docker Desktop abierto. Maven no hace falta (el proyecto trae `mvnw`).

```bat
mvnw.cmd clean package
docker compose up --build
```

Espera unos 30 segundos a que arranquen los WildFly y abre:

| Qué | Dónde |
|---|---|
| Web JSF | http://localhost:8080/presentacion (entra con admin / admin123) |
| REST, por el API Gateway | http://localhost:8000/api/productos |
| SOAP, directo a Servicios | http://localhost:8081/servicios/ProductosSoap?wsdl |

La colección de Postman está en `docs/entrega/postman/` y los sobres SOAP de ejemplo en `soap/`.

Para apagar todo y borrar la BD:

```bat
docker compose down -v
```

---

## Estructura

```
taller4-jee/
├── pom.xml              pom raíz: construye todos los módulos
├── docker-compose.yml   un contenedor por servidor
├── contratos/           jar con las interfaces remotas, los DTO y las excepciones (va dentro de todos los .war)
├── datos/               WildFly: entidades JPA + EJB @Remote de acceso a datos + datasource de Postgres
├── logica/              WildFly: EJB @Remote con las reglas (login, validar producto)
├── servicios/           WildFly: REST (JAX-RS) y SOAP (JAX-WS). Solo traduce HTTP ↔ llamada a la Lógica
├── presentacion/        Tomcat: JSF (login.xhtml, app/productos.xhtml) + filtro de sesión
├── gateway/             nginx como API Gateway
├── soap/                sobres SOAP de ejemplo
├── db/init.sql          crea las tablas y los datos iniciales
└── docs/entrega/        documentación de entrega
```

## Cómo se comunican las capas

- **Presentación → Servicios (REST/JSON).** Tomcat es solo contenedor web: no trae JSF ni CDI. Por eso
  `presentacion.war` lleva adentro Mojarra (JSF), Weld (CDI) y Jersey (cliente REST). La presentación no
  conoce los EJB: `ClienteServicios` solo sabe la URL de la API.
- **Clientes REST → API Gateway → Servicios.** nginx (`gateway/nginx.conf`) es la entrada para los
  clientes REST:
  - Reenvía `/api/*` a Servicios.
  - Limita las peticiones por IP (10 por segundo, ráfaga de 20).
  - Marca las respuestas con el header `X-Gateway`.
  - Los clientes no saben dónde vive Servicios.
- **Clientes SOAP → Servicios.** `ProductosSoapService` (`@WebService`) expone **las mismas operaciones**
  que los recursos REST. WildFly genera el WSDL solo.
- **Servicios → Lógica → Datos (EJB remoto).** Las llamadas se hacen con un **Service Locator**
  (`LocalizadorLogica`, `LocalizadorDatos`). Arma un `InitialContext` que apunta al otro servidor y busca el
  EJB por `ejb:/<war>/<Bean>!<interfaz>`. Cada WildFly que expone EJB tiene un usuario de aplicación
  (`ejbuser / ejbclave123`), creado en su `Dockerfile`.
- **Reglas solo en Lógica.** Servicios solo convierte la respuesta o la excepción en un código HTTP:
  `200`, `201`, `400` (dato inválido), `401` (login malo) o `503` (la Lógica no responde). Los mismos
  errores llegan por SOAP como *Fault*.
- **Las entidades JPA no salen de Datos.** Se copian a DTO. El login compara el **SHA-256** de la clave con
  el que está en la BD (`Cifrado.sha256` en Lógica). `UsuarioDatos`, que lleva el hash, solo viaja de Datos
  a Lógica; a la presentación le llega `UsuarioDTO`, sin hash.

La idea central es **una sola Lógica con varias puertas de entrada**: un producto creado por SOAP aparece en
la web y en el REST.

## Del tablero a esta arquitectura

El tablero dibuja tres pasos (`docs/entrega/tablero.webp`):

1. JSF en Tomcat → Lógica → Datos → BD.
2. Se mete una capa de **Servicios** entre la presentación y la Lógica.
3. A esos Servicios llegan también clientes **REST** (por un API Gateway) y **SOAP**.

Este proyecto implementa el paso 3, que es el sistema completo e incluye lo de los pasos anteriores.

---

## Simplificaciones conscientes

- Las claves se guardan como SHA-256 sin sal (en un sistema real: bcrypt o PBKDF2 con sal).
- La API REST/SOAP no pide token: el login solo protege la web (`FiltroSesion`).
- El usuario `ejbuser / ejbclave123` está fijo en los Dockerfile y en el compose.
- El puerto 8081 de Servicios queda publicado para SOAP; por ahí también se puede llegar al REST sin pasar
  por el gateway.
- JSF corre en modo `Development` para ver los errores mientras se prueba.

## Si algo falla

- **"failed to connect to the docker API"**: Docker Desktop no está abierto.
- **La web dice "No se pudo conectar con la capa de Servicios"**: alguna capa todavía está arrancando o está
  apagada. Espera unos segundos y recarga. Revisa con `docker compose logs -f servicios` (o `logica`,
  `datos`).
- **Cambié `init.sql` y no se nota**: Postgres solo lo ejecuta la primera vez. Usa `docker compose down -v`
  y vuelve a subir.
- **Cambié código y no se nota**: vuelve a correr `mvnw.cmd clean package` antes de
  `docker compose up --build`.
