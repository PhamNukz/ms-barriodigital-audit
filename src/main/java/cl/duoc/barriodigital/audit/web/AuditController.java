package cl.duoc.barriodigital.audit.web;

import cl.duoc.barriodigital.audit.domain.EventoAuditoria;
import cl.duoc.barriodigital.audit.repo.EventoAuditoriaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * Solo lectura: timeline de auditoria, del evento mas reciente al mas antiguo.
 *
 * <ul>
 *   <li>{@code usuario}: quien hizo el cambio (actor).</li>
 *   <li>{@code vecino}: el vecino dueno del tramite.</li>
 *   <li>{@code persona}: cualquiera de los dos. Es el filtro del funcionario que atiende
 *       a alguien: todo lo que esa persona hizo y todo lo que le paso a sus tramites.</li>
 * </ul>
 */
@RestController
@RequestMapping("/audit/timeline")
public class AuditController {

    private final EventoAuditoriaRepository repo;

    public AuditController(EventoAuditoriaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<AuditDtos.Response> listar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String vecino,
            @RequestParam(required = false) String persona,
            @RequestParam(required = false) Long tramiteId,
            @RequestParam(required = false) String tipoEvento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta) {
        // ponytail: filtro en memoria, igual que en ms-barriodigital-requests;
        // pasar a Specifications si el volumen de eventos crece.
        return repo.findAll(Sort.by(Sort.Direction.DESC, "timestamp")).stream()
                .filter(e -> usuario == null || usuario.equalsIgnoreCase(e.getUsuario()))
                .filter(e -> vecino == null || vecino.equalsIgnoreCase(e.getVecinoUsername()))
                .filter(e -> persona == null || involucra(e, persona))
                .filter(e -> tramiteId == null || tramiteId.equals(e.getTramiteId()))
                .filter(e -> tipoEvento == null || tipoEvento.equalsIgnoreCase(e.getEstadoNuevo()))
                .filter(e -> desde == null || !e.getTimestamp().isBefore(desde))
                .filter(e -> hasta == null || !e.getTimestamp().isAfter(hasta))
                .map(AuditDtos.Response::from)
                .toList();
    }

    private static boolean involucra(EventoAuditoria e, String persona) {
        return persona.equalsIgnoreCase(e.getUsuario()) || persona.equalsIgnoreCase(e.getVecinoUsername());
    }
}
