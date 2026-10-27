-- Lo ejecuta Postgres automáticamente la PRIMERA vez que arranca el contenedor.
CREATE TABLE usuarios (
    id         SERIAL PRIMARY KEY,
    usuario    VARCHAR(50)  NOT NULL UNIQUE,
    nombre     VARCHAR(100) NOT NULL,
    clave_hash VARCHAR(64)  NOT NULL   -- SHA-256 de la clave, en hexadecimal
);

CREATE TABLE productos (
    id     SERIAL PRIMARY KEY,
    nombre VARCHAR(100)     NOT NULL,
    precio DOUBLE PRECISION NOT NULL
);

-- Usuarios de prueba: admin / admin123  y  estudiante / javeriana
INSERT INTO usuarios (usuario, nombre, clave_hash) VALUES
    ('admin',      'Administrador', encode(sha256('admin123'::bytea), 'hex')),
    ('estudiante', 'Estudiante',    encode(sha256('javeriana'::bytea), 'hex'));

INSERT INTO productos (nombre, precio) VALUES
    ('Teclado', 85000),
    ('Mouse',   45000);
