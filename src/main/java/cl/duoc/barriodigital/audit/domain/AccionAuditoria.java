package cl.duoc.barriodigital.audit.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Una entrada de audit.timeline: quien hizo que, cuando y desde donde. Va en su propia
 * tabla porque evento_auditoria ya tiene el mismo cambio visto desde requests.events.
 */
@Entity
@Table(name = "accion_auditoria")
public class AccionAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 60)
    private String eventId;

    @Column(name = "event_timestamp", nullable = false)
    private Instant timestamp;

    @Column(nullable = false, length = 150)
    private String actor;

    @Column(nullable = false, length = 30)
    private String accion;

    @Column(name = "tramite_id")
    private Long tramiteId;

    @Column(name = "vecino_username", length = 150)
    private String vecinoUsername;

    @Column(length = 200)
    private String detalle;

    @Column(length = 500)
    private String nota;

    @Column(length = 100)
    private String origen;

    protected AccionAuditoria() {
    }

    public AccionAuditoria(String eventId, Instant timestamp, String actor, String accion, Long tramiteId,
                           String vecinoUsername, String detalle, String nota, String origen) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.actor = actor;
        this.accion = accion;
        this.tramiteId = tramiteId;
        this.vecinoUsername = vecinoUsername;
        this.detalle = detalle;
        this.nota = nota;
        this.origen = origen;
    }

    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public Instant getTimestamp() { return timestamp; }
    public String getActor() { return actor; }
    public String getAccion() { return accion; }
    public Long getTramiteId() { return tramiteId; }
    public String getVecinoUsername() { return vecinoUsername; }
    public String getDetalle() { return detalle; }
    public String getNota() { return nota; }
    public String getOrigen() { return origen; }
}
