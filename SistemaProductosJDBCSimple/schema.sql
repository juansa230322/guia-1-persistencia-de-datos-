CREATE TABLE IF NOT EXISTS productos (
 codigo TEXT PRIMARY KEY NOT NULL,
 nombre TEXT NOT NULL CHECK(length(trim(nombre)) > 0),
 descripcion TEXT NOT NULL DEFAULT '',
 precio NUMERIC NOT NULL CHECK(precio >= 0),
 cantidad INTEGER NOT NULL CHECK(cantidad >= 0)
);
