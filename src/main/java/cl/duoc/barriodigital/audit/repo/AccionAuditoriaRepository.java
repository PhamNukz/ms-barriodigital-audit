package cl.duoc.barriodigital.audit.repo;

import cl.duoc.barriodigital.audit.domain.AccionAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccionAuditoriaRepository extends JpaRepository<AccionAuditoria, Long> {
    Optional<AccionAuditoria> findByEventId(String eventId);
}
