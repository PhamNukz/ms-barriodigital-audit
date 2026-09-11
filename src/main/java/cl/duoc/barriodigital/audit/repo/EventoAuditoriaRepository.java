package cl.duoc.barriodigital.audit.repo;

import cl.duoc.barriodigital.audit.domain.EventoAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventoAuditoriaRepository extends JpaRepository<EventoAuditoria, Long> {
    Optional<EventoAuditoria> findByEventId(String eventId);
}
