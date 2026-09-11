package cl.duoc.barriodigital.audit.kafka;

import cl.duoc.barriodigital.audit.domain.EventoAuditoria;
import cl.duoc.barriodigital.audit.repo.EventoAuditoriaRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuditEventListenerTest {

    private TramiteEventIn evento(String eventId) {
        return new TramiteEventIn(eventId, Instant.now(), "trace-1", 10L, 1L, "vecino1@duocuc.cl",
                "INGRESADO", "ADMITIDO", "func1@duocuc.cl");
    }

    @Test
    void guarda_un_evento_nuevo() {
        EventoAuditoriaRepository repo = mock(EventoAuditoriaRepository.class);
        when(repo.findByEventId("evt-1")).thenReturn(Optional.empty());
        var listener = new AuditEventListener(repo);

        listener.onTramiteEvent(evento("evt-1"));

        verify(repo).save(any(EventoAuditoria.class));
    }

    @Test
    void ignora_un_evento_ya_auditado() {
        EventoAuditoriaRepository repo = mock(EventoAuditoriaRepository.class);
        when(repo.findByEventId("evt-dup")).thenReturn(Optional.of(
                new EventoAuditoria("evt-dup", 10L, "func1", "INGRESADO", "ADMITIDO", Instant.now())));
        var listener = new AuditEventListener(repo);

        listener.onTramiteEvent(evento("evt-dup"));

        verify(repo, never()).save(any());
    }
}
