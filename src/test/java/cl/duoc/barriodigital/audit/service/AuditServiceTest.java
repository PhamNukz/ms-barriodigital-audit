package cl.duoc.barriodigital.audit.service;

import cl.duoc.barriodigital.audit.domain.AccionAuditoria;
import cl.duoc.barriodigital.audit.domain.EventoAuditoria;
import cl.duoc.barriodigital.audit.kafka.AuditEntryIn;
import cl.duoc.barriodigital.audit.kafka.TramiteEventIn;
import cl.duoc.barriodigital.audit.repo.AccionAuditoriaRepository;
import cl.duoc.barriodigital.audit.repo.EventoAuditoriaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuditServiceTest {

    private final EventoAuditoriaRepository eventos = mock(EventoAuditoriaRepository.class);
    private final AccionAuditoriaRepository acciones = mock(AccionAuditoriaRepository.class);
    private final AuditService service = new AuditService(eventos, acciones);

    private TramiteEventIn evento(String eventId) {
        return new TramiteEventIn(eventId, Instant.now(), "trace-1", 10L, 1L, "vecino1@duocuc.cl",
                "INGRESADO", "ADMITIDO", "func1@duocuc.cl");
    }

    @Test
    void guarda_un_evento_nuevo() {
        when(eventos.findByEventId("evt-1")).thenReturn(Optional.empty());

        service.registrar(evento("evt-1"));

        verify(eventos).save(any(EventoAuditoria.class));
    }

    @Test
    void ignora_un_evento_ya_auditado() {
        when(eventos.findByEventId("evt-dup")).thenReturn(Optional.of(
                new EventoAuditoria("evt-dup", 10L, "func1", "INGRESADO", "ADMITIDO", Instant.now())));

        service.registrar(evento("evt-dup"));

        verify(eventos, never()).save(any());
    }

    /** Validacion: no reintentable, va directo a la DLT (ver KafkaErrorConfigTest). */
    @Test
    void evento_sin_tramiteId_es_invalido_y_no_se_guarda() {
        var sinTramite = new TramiteEventIn("evt-2", Instant.now(), "trace-1", null, 1L, "vecino1",
                null, "INGRESADO", "vecino1");

        var ex = assertThrows(EventoInvalidoException.class, () -> service.registrar(sinTramite));

        assertEquals("falta tramiteId", ex.getMessage());
        verify(eventos, never()).save(any());
    }

    @Test
    void mensaje_vacio_es_invalido() {
        assertThrows(EventoInvalidoException.class, () -> service.registrar(null));
    }

    @Test
    void guarda_la_accion_de_audit_timeline() {
        when(acciones.findByEventId("acc-1")).thenReturn(Optional.empty());

        service.registrarAccion(new AuditEntryIn("acc-1", Instant.now(), "func1", "CAMBIAR_ESTADO", 10L,
                "vecino1", "INGRESADO -> ADMITIDO", "Revisaremos el lunes", "ms-barriodigital-requests"));

        ArgumentCaptor<AccionAuditoria> guardada = ArgumentCaptor.forClass(AccionAuditoria.class);
        verify(acciones).save(guardada.capture());
        assertEquals("func1", guardada.getValue().getActor());
        assertEquals("CAMBIAR_ESTADO", guardada.getValue().getAccion());
        assertEquals("ms-barriodigital-requests", guardada.getValue().getOrigen());
    }

    @Test
    void accion_sin_actor_es_invalida() {
        var sinActor = new AuditEntryIn("acc-2", Instant.now(), " ", "CREAR", 10L, null, null, null, null);

        assertThrows(EventoInvalidoException.class, () -> service.registrarAccion(sinActor));
        verify(acciones, never()).save(any());
    }
}
