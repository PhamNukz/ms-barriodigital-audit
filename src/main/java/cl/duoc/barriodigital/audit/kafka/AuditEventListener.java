package cl.duoc.barriodigital.audit.kafka;

import cl.duoc.barriodigital.audit.domain.EventoAuditoria;
import cl.duoc.barriodigital.audit.repo.EventoAuditoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuditEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);

    private final EventoAuditoriaRepository repo;

    public AuditEventListener(EventoAuditoriaRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = "requests.events", groupId = "audit-svc",
            autoStartup = "${barriodigital.kafka.listener-auto-startup:true}")
    @Transactional
    public void onTramiteEvent(TramiteEventIn evento) {
        // idempotencia: si Kafka reentrega el mismo eventId (at-least-once), no duplicar la fila.
        if (repo.findByEventId(evento.eventId()).isPresent()) {
            log.info("evento {} ya auditado, se ignora", evento.eventId());
            return;
        }
        repo.save(new EventoAuditoria(
                evento.eventId(), evento.tramiteId(), evento.usuario(),
                evento.estadoAnterior(), evento.estadoNuevo(), evento.timestamp()));
    }
}
