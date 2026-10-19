-- El vecino afectado (no solo quien hizo el cambio): permite revisar todo lo que
-- le paso a una persona, incluidos los tramites que un funcionario ingreso a su nombre.
ALTER TABLE evento_auditoria ADD (
    vecino_username VARCHAR2(150),
    tipo_id         NUMBER(19),
    nota            VARCHAR2(500)
);

CREATE INDEX idx_auditoria_vecino ON evento_auditoria (vecino_username);
