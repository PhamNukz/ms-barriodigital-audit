package cl.duoc.barriodigital.audit.web;

import cl.duoc.barriodigital.audit.repo.EventoAuditoriaRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/** Solo lectura -- timeline de auditoria, filtrable por usuario, fechas y tipo de evento (estado nuevo). */
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
            @RequestParam(required = false) String tipoEvento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta) {
        // ponytail: filtro en memoria, igual que en ms-barriodigital-requests;
        // pasar a Specifications si el volumen de eventos crece.
        return repo.findAll().stream()
                .filter(e -> usuario == null || usuario.equalsIgnoreCase(e.getUsuario()))
                .filter(e -> tipoEvento == null || tipoEvento.equalsIgnoreCase(e.getEstadoNuevo()))
                .filter(e -> desde == null || !e.getTimestamp().isBefore(desde))
                .filter(e -> hasta == null || !e.getTimestamp().isAfter(hasta))
                .map(AuditDtos.Response::from)
                .toList();
    }
}
