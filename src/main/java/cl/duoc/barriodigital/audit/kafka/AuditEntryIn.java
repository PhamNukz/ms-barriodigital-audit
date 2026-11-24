package cl.duoc.barriodigital.audit.kafka;

import java.time.Instant;

/** Misma forma que AuditEntry en ms-barriodigital-requests (topico audit.timeline). */
public record AuditEntryIn(
        String eventId,
        Instant timestamp,
        String actor,
        String accion,
        Long tramiteId,
        String vecinoUsername,
        String detalle,
        String nota,
        String origen) {
}
