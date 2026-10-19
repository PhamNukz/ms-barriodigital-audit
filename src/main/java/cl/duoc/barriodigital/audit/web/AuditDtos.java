package cl.duoc.barriodigital.audit.web;

import cl.duoc.barriodigital.audit.domain.EventoAuditoria;

import java.time.Instant;

public final class AuditDtos {

    private AuditDtos() {
    }

    public record Response(
            Long id, Long tramiteId, String usuario,
            String estadoAnterior, String estadoNuevo, Instant timestamp,
            String vecinoUsername, Long tipoId, String nota) {

        public static Response from(EventoAuditoria e) {
            return new Response(e.getId(), e.getTramiteId(), e.getUsuario(),
                    e.getEstadoAnterior(), e.getEstadoNuevo(), e.getTimestamp(),
                    e.getVecinoUsername(), e.getTipoId(), e.getNota());
        }
    }
}
