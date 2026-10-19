package cl.duoc.barriodigital.audit.kafka;

import java.time.Instant;

/** Misma forma que TramiteEvent en ms-barriodigital-requests (ver security para el porque duplicar). */
public record TramiteEventIn(
        String eventId,
        Instant timestamp,
        String traceId,
        Long tramiteId,
        Long tipoId,
        String vecinoUsername,
        String estadoAnterior,
        String estadoNuevo,
        String usuario,
        String nota) {

    public TramiteEventIn(String eventId, Instant timestamp, String traceId, Long tramiteId, Long tipoId,
                          String vecinoUsername, String estadoAnterior, String estadoNuevo, String usuario) {
        this(eventId, timestamp, traceId, tramiteId, tipoId, vecinoUsername, estadoAnterior, estadoNuevo, usuario, null);
    }
}
