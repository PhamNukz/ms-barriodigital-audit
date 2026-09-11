package cl.duoc.barriodigital.audit.repo;

import cl.duoc.barriodigital.audit.domain.EventoAuditoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EventoAuditoriaRepositoryTest {

    @Autowired
    EventoAuditoriaRepository repo;

    @Test
    void guarda_y_busca_por_event_id() {
        repo.save(new EventoAuditoria("evt-1", 10L, "func1", "INGRESADO", "ADMITIDO", Instant.now()));

        assertThat(repo.findByEventId("evt-1")).isPresent();
        assertThat(repo.findByEventId("no-existe")).isEmpty();
    }
}
