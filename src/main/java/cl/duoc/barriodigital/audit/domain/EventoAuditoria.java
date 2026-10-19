package cl.duoc.barriodigital.audit.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "evento_auditoria")
public class EventoAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 60)
    private String eventId;

    @Column(name = "tramite_id", nullable = false)
    private Long tramiteId;

    @Column(nullable = false, length = 150)
    private String usuario;

    @Column(name = "estado_anterior", length = 20)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false, length = 20)
    private String estadoNuevo;

    @Column(name = "event_timestamp", nullable = false)
    private Instant timestamp;

    /** Vecino dueno del tramite. Null en los eventos guardados antes de V2. */
    @Column(name = "vecino_username", length = 150)
    private String vecinoUsername;

    @Column(name = "tipo_id")
    private Long tipoId;

    /** Nota del funcionario, o "Ingresado por X a nombre de Y". */
    @Column(length = 500)
    private String nota;

    protected EventoAuditoria() {
    }

    public EventoAuditoria(String eventId, Long tramiteId, String usuario,
                           String estadoAnterior, String estadoNuevo, Instant timestamp) {
        this.eventId = eventId;
        this.tramiteId = tramiteId;
        this.usuario = usuario;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.timestamp = timestamp;
    }

    public EventoAuditoria(String eventId, Long tramiteId, String usuario, String estadoAnterior,
                           String estadoNuevo, Instant timestamp, String vecinoUsername, Long tipoId, String nota) {
        this(eventId, tramiteId, usuario, estadoAnterior, estadoNuevo, timestamp);
        this.vecinoUsername = vecinoUsername;
        this.tipoId = tipoId;
        this.nota = nota;
    }

    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public Long getTramiteId() { return tramiteId; }
    public String getUsuario() { return usuario; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public Instant getTimestamp() { return timestamp; }
    public String getVecinoUsername() { return vecinoUsername; }
    public Long getTipoId() { return tipoId; }
    public String getNota() { return nota; }
}
