package cl.duoc.barriodigital.audit.kafka;

import cl.duoc.barriodigital.audit.service.AuditService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Solo mensajeria: recibe, deja en el log de donde vino y delega en {@link AuditService}.
 * Grupo audit-svc (spring.kafka.consumer.group-id), que lee los dos topicos con 3 hilos
 * cada uno (listener.concurrency). Los errores no se capturan aqui: los maneja
 * KafkaErrorConfig (reintentos + DLT).
 */
@Component
public class AuditEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);

    private final AuditService service;

    public AuditEventListener(AuditService service) {
        this.service = service;
    }

    @KafkaListener(topics = "${barriodigital.kafka.topics.requests-events}",
            autoStartup = "${barriodigital.kafka.listener-auto-startup:true}")
    public void onTramiteEvent(ConsumerRecord<String, TramiteEventIn> rec) {
        log.info("Recibido {}-{}@{} key={}", rec.topic(), rec.partition(), rec.offset(), rec.key());
        service.registrar(rec.value());
    }

    // El yml deserializa a TramiteEventIn; este topico trae otro DTO.
    @KafkaListener(topics = "${barriodigital.kafka.topics.audit-timeline}",
            autoStartup = "${barriodigital.kafka.listener-auto-startup:true}",
            properties = "spring.json.value.default.type=cl.duoc.barriodigital.audit.kafka.AuditEntryIn")
    public void onAuditEntry(ConsumerRecord<String, AuditEntryIn> rec) {
        log.info("Recibido {}-{}@{} key={}", rec.topic(), rec.partition(), rec.offset(), rec.key());
        service.registrarAccion(rec.value());
    }
}
