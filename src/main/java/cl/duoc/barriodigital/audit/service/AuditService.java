package cl.duoc.barriodigital.audit.service;

import cl.duoc.barriodigital.audit.domain.AccionAuditoria;
import cl.duoc.barriodigital.audit.domain.EventoAuditoria;
import cl.duoc.barriodigital.audit.kafka.AuditEntryIn;
import cl.duoc.barriodigital.audit.kafka.TramiteEventIn;
import cl.duoc.barriodigital.audit.repo.AccionAuditoriaRepository;
import cl.duoc.barriodigital.audit.repo.EventoAuditoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de auditoria, sin nada de Kafka: valida, descarta reentregas y guarda.
 * La idempotencia por eventId cubre el at-least-once de Kafka: un reintento o un
 * rebalanceo puede entregar dos veces el mismo mensaje.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final EventoAuditoriaRepository eventos;
    private final AccionAuditoriaRepository acciones;

    public AuditService(EventoAuditoriaRepository eventos, AccionAuditoriaRepository acciones) {
        this.eventos = eventos;
        this.acciones = acciones;
    }

    /** Hecho de requests.events: una fila del timeline de estados. */
    @Transactional
    public void registrar(TramiteEventIn e) {
        exigir(e, "payload");
        exigir(e.eventId(), "eventId");
        exigir(e.tramiteId(), "tramiteId");
        exigir(e.usuario(), "usuario");
        exigir(e.estadoNuevo(), "estadoNuevo");
        exigir(e.timestamp(), "timestamp");
        if (eventos.findByEventId(e.eventId()).isPresent()) {
            log.info("evento {} ya auditado, se ignora", e.eventId());
            return;
        }
        eventos.save(new EventoAuditoria(
                e.eventId(), e.tramiteId(), e.usuario(),
                e.estadoAnterior(), e.estadoNuevo(), e.timestamp(),
                e.vecinoUsername(), e.tipoId(), e.nota()));
    }

    /** Entrada de audit.timeline: quien hizo que, cuando y desde donde. */
    @Transactional
    public void registrarAccion(AuditEntryIn a) {
        exigir(a, "payload");
        exigir(a.eventId(), "eventId");
        exigir(a.timestamp(), "timestamp");
        exigir(a.actor(), "actor");
        exigir(a.accion(), "accion");
        if (acciones.findByEventId(a.eventId()).isPresent()) {
            log.info("accion {} ya auditada, se ignora", a.eventId());
            return;
        }
        acciones.save(new AccionAuditoria(a.eventId(), a.timestamp(), a.actor(), a.accion(), a.tramiteId(),
                a.vecinoUsername(), a.detalle(), a.nota(), a.origen()));
    }

    /** Los campos que la tabla tiene NOT NULL: sin ellos el save fallaria igual, pero tras 3 reintentos inutiles. */
    private static void exigir(Object valor, String campo) {
        if (valor == null || valor instanceof String s && s.isBlank()) {
            throw new EventoInvalidoException("falta " + campo);
        }
    }
}
