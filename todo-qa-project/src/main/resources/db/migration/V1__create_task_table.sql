CREATE TABLE task (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'TODO',
    fecha_creacion TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT chk_estado CHECK (estado IN ('TODO', 'IN_PROGRESS', 'DONE'))
);